CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

CREATE TABLE dish_ingredient (
    id SERIAL PRIMARY KEY,
    id_dish INTEGER NOT NULL,
    id_ingredient INTEGER NOT NULL,
    quantity_required NUMERIC NOT NULL,
    unit_type unit_type NOT NULL,
        CONSTRAINT fk_dish FOREIGN KEY (id_dish) REFERENCES dish(id) ON DELETE CASCADE,
        CONSTRAINT fk_ingredient FOREIGN KEY (id_ingredient) REFERENCES ingredient(id) ON DELETE CASCADE
);

ALTER TABLE ingredient DROP CONSTRAINT IF EXISTS fk_dish;
ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;

UPDATE dish SET price = 3500.00  WHERE id = 1;
UPDATE dish SET price = 12000.00 WHERE id = 2;
UPDATE dish SET price = NULL     WHERE id = 3;
UPDATE dish SET price = 8000.00  WHERE id = 4;
UPDATE dish SET price = NULL     WHERE id = 5;

CREATE TABLE IF NOT EXISTS "table" (
                                       id SERIAL PRIMARY KEY,
                                       number INTEGER NOT NULL UNIQUE,
    );