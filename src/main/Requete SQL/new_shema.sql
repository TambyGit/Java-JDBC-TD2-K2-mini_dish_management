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

UPDATE dish SET price = 3500.00  WHERE id = 1;
UPDATE dish SET price = 12000.00 WHERE id = 2;
UPDATE dish SET price = NULL     WHERE id = 3;
UPDATE dish SET price = 8000.00  WHERE id = 4;
UPDATE dish SET price = NULL     WHERE id = 5;