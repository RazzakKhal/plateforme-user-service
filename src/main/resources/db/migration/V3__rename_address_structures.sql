DO $$
BEGIN
    IF to_regclass('public.adress') IS NOT NULL
        AND to_regclass('public.addresses') IS NULL THEN
        ALTER TABLE adress RENAME TO addresses;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'adress_id'
    ) THEN
        ALTER TABLE users RENAME COLUMN adress_id TO address_id;
    END IF;
END $$;

ALTER INDEX IF EXISTS idx_users_adress_id RENAME TO idx_users_address_id;
