CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS address_id_map
(
    legacy_id BIGINT PRIMARY KEY,
    id        UUID NOT NULL UNIQUE
);

DO $$
DECLARE
    fk_record RECORD;
BEGIN
    IF to_regclass('public.addresses') IS NULL THEN
        RETURN;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'addresses'
          AND column_name = 'id'
          AND data_type <> 'uuid'
    ) THEN
        FOR fk_record IN
            SELECT con.conname
            FROM pg_constraint con
                JOIN pg_class rel ON rel.oid = con.conrelid
            WHERE con.contype = 'f'
              AND rel.relname = 'users'
        LOOP
            EXECUTE format('ALTER TABLE users DROP CONSTRAINT IF EXISTS %I', fk_record.conname);
        END LOOP;

        ALTER TABLE addresses ADD COLUMN IF NOT EXISTS legacy_id BIGINT;
        UPDATE addresses SET legacy_id = id WHERE legacy_id IS NULL;

        ALTER TABLE addresses ADD COLUMN IF NOT EXISTS new_id UUID;
        UPDATE addresses SET new_id = gen_random_uuid() WHERE new_id IS NULL;

        INSERT INTO address_id_map (legacy_id, id)
        SELECT legacy_id, new_id
        FROM addresses
        ON CONFLICT (legacy_id) DO UPDATE SET id = EXCLUDED.id;

        ALTER TABLE addresses DROP CONSTRAINT IF EXISTS adress_pkey;
        ALTER TABLE addresses DROP CONSTRAINT IF EXISTS address_pkey;
        ALTER TABLE addresses DROP CONSTRAINT IF EXISTS addresses_pkey;
        ALTER TABLE addresses DROP COLUMN id;
        ALTER TABLE addresses RENAME COLUMN new_id TO id;
        ALTER TABLE addresses ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);
    END IF;
END $$;
