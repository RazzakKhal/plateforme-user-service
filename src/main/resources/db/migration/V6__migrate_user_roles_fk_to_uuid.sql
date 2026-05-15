DO $$
BEGIN
    IF to_regclass('public.user_roles') IS NULL THEN
        RETURN;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'user_roles'
          AND column_name = 'user_id'
          AND data_type <> 'uuid'
    ) THEN
        ALTER TABLE user_roles ADD COLUMN IF NOT EXISTS new_user_id UUID;

        UPDATE user_roles ur
        SET new_user_id = u.id
        FROM user_id_map u
        WHERE ur.user_id = u.legacy_id;

        ALTER TABLE user_roles DROP COLUMN user_id;
        ALTER TABLE user_roles RENAME COLUMN new_user_id TO user_id;
        ALTER TABLE user_roles
            ADD CONSTRAINT fk_user_roles_users
                FOREIGN KEY (user_id) REFERENCES users (id);
    END IF;
END $$;
