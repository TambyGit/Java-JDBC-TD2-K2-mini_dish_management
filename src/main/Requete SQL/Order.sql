CREATE TABLE "order"(
    id SERIAL PRIMARY KEY,
    reference varchar(8) NOT NULL,
    creation_date timestamp Default now()
);

CREATE TABLE dish_order (
    id SERIAL PRIMARY KEY,
    id_order Integer constraint fk_order references "order"(id),
    id_dish Integer constraint  fk_dish references dish(id),
    quantity Integer not null
);