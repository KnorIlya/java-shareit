DROP TABLE IF EXISTS users, items, booking CASCADE;

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
    name        varchar not null,
    description varchar not null,
    available   boolean not null,
    foreign key (user_id) references users (id)
);