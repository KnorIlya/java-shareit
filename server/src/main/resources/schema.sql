DROP TABLE IF EXISTS users, items, bookings, comments, requests CASCADE;

CREATE TABLE users
(
    id    bigint generated by default as identity primary key,
    name  varchar not null,
    email varchar not null unique
);

CREATE TABLE items
(
    id          bigint generated by default as identity primary key,
    user_id     bigint,
    request_id  bigint,
    name        varchar not null,
    description varchar not null,
    available   boolean not null
);

CREATE TABLE bookings
(
    id            bigint generated by default as identity primary key,
    user_id       bigint,
    item_id       bigint,
    start_booking timestamp not null,
    end_booking   timestamp not null,
    status        varchar   not null
);

CREATE TABLE comments
(
    id      bigint generated by default as identity primary key,
    user_id bigint,
    item_id bigint,
    created timestamp not null,
    text    varchar   not null


);

CREATE TABLE requests
(
    id      bigint generated by default as identity primary key,
    user_id bigint,
    created timestamp not null,
    description    varchar   not null
);

alter table items
    add foreign key (user_id) references users (id);
alter table items
    add foreign key (request_id) references requests (id);

alter table bookings
    add foreign key (user_id) references users (id);
alter table bookings
    add foreign key (item_id) references items (id);

alter table comments
    add foreign key (user_id) references users (id);
alter table comments
    add foreign key (item_id) references items (id);

alter table requests
    add foreign key (user_id) references users (id);