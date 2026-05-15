CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS user_id_map
(
    legacy_id BIGINT PRIMARY KEY,
    id        UUID NOT NULL UNIQUE
);

DO $$
DECLARE
    fk_record RECORD;
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
        IF to_regclass('public.formula_id_map') IS NULL THEN
            RAISE EXCEPTION 'Missing formula_id_map required to migrate users.formula_id to UUID';
        END IF;

        ALTER TABLE users ADD COLUMN IF NOT EXISTS new_formula_id UUID;
        UPDATE users u
        SET new_formula_id = f.id
        FROM formula_id_map f
        WHERE u.formula_id IS NOT NULL
          AND u.formula_id = f.legacy_id;
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
