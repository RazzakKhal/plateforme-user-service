BEGIN;
    ALTER TABLE IF EXISTS addresses
    RENAME COLUMN adress_line1 to address_line1;
COMMIT;
