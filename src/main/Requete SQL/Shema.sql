CREATE TYPE ingredient_category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    dish_type dish_type_enum NOT NUL;
);

CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    price NUMERIC NOT NULL,
    category ingredient_category_enum NOT NULL,
    id_dish INTEGER,
    CONSTRAINT fk_dish FOREIGN KEY (id_dish)
    REFERENCES dish(id)ON DELETE SET NULL
);

ALTER TABLE dish ADD COLUMN IF NOT EXISTS price NUMERIC;

UPDATE dish SET price = 2000.00 WHERE name = 'Salade fraîche';
UPDATE dish SET price = 6000.00 WHERE name = 'Poulet grillé';
UPDATE dish SET price = NULL    WHERE name = 'Riz aux légumes';
UPDATE dish SET price = NULL    WHERE name = 'Gâteau au chocolat';
UPDATE dish SET price = NULL    WHERE name = 'Salade de fruits';