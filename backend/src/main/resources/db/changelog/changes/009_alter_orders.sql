ALTER TABLE orders
    ADD kind VARCHAR(4) NOT NULL DEFAULT 'Авто';

ALTER TABLE orders
    ALTER COLUMN kind DROP DEFAULT;