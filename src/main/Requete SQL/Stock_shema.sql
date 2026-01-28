create type movement_type as enum ('IN', 'OUT');

create table if not exists stock_movement (
    id serial primary key,
    quantity numeric(10,2),
    type movement_type,
    unit unit_type,
    creation_datetime timestamp
    id_ingredient integer constraint fk_ingredient references ingredient(id),
);
