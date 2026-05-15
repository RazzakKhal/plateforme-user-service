CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS user_id_map
(
    legacy_id BIGINT PRIMARY KEY,
    id        UUID NOT NULL UNIQUE
);

DO $$
DECLARE
    fk_record RECORD;
    formula_id_data_type TEXT;
BEGIN
    IF to_regclass('public.users') IS NULL THEN
        RETURN;
    END IF;

    FOR fk_record IN
        SELECT con.conname, rel.relname
        FROM pg_constraint con
            JOIN pg_class rel ON rel.oid = con.conrelid
        WHERE con.contype = 'f'
          AND rel.relname IN ('users', 'user_roles')
    LOOP
        EXECUTE format('ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', fk_record.relname, fk_record.conname);
    END LOOP;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'id'
          AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE users ADD COLUMN IF NOT EXISTS legacy_id BIGINT;
        UPDATE users SET legacy_id = id WHERE legacy_id IS NULL;

        ALTER TABLE users ADD COLUMN IF NOT EXISTS new_id UUID;
        UPDATE users SET new_id = gen_random_uuid() WHERE new_id IS NULL;

        INSERT INTO user_id_map (legacy_id, id)
        SELECT legacy_id, new_id
        FROM users
        ON CONFLICT (legacy_id) DO UPDATE SET id = EXCLUDED.id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'address_id'
          AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE users ADD COLUMN IF NOT EXISTS new_address_id UUID;
        UPDATE users u
        SET new_address_id = a.id
        FROM address_id_map a
        WHERE u.address_id IS NOT NULL
          AND u.address_id = a.legacy_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'formula_id'
          AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE users ADD COLUMN IF NOT EXISTS new_formula_id UUID;

        SELECT data_type
        INTO formula_id_data_type
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'formula_id';

        IF to_regclass('public.formula_id_map') IS NOT NULL THEN
            UPDATE users u
            SET new_formula_id = f.id
            FROM formula_id_map f
            WHERE u.formula_id IS NOT NULL
              AND u.formula_id = f.legacy_id;
        ELSIF formula_id_data_type IN ('character varying', 'character', 'text') THEN
            UPDATE users
            SET new_formula_id = NULLIF(BTRIM(formula_id::TEXT), '')::UUID
            WHERE formula_id IS NOT NULL
              AND BTRIM(formula_id::TEXT) ~* '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$';
        ELSE
            RAISE NOTICE 'Skipping legacy users.formula_id backfill because formula_id_map is unavailable in user-service database';
        END IF;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'new_id'
    ) THEN
        ALTER TABLE users DROP CONSTRAINT IF EXISTS user_pkey;
        ALTER TABLE users DROP CONSTRAINT IF EXISTS users_pkey;
        ALTER TABLE users DROP COLUMN id;
        ALTER TABLE users RENAME COLUMN new_id TO id;
        ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (id);
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'new_address_id'
    ) THEN
        ALTER TABLE users DROP COLUMN address_id;
        ALTER TABLE users RENAME COLUMN new_address_id TO address_id;
        ALTER TABLE users
            ADD CONSTRAINT fk_users_address
                FOREIGN KEY (address_id) REFERENCES addresses (id);
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'new_formula_id'
    ) THEN
        ALTER TABLE users DROP COLUMN formula_id;
        ALTER TABLE users RENAME COLUMN new_formula_id TO formula_id;
    END IF;
END $$;
