# StepFightJava
create table users (
id integer not null default nextval('users_id_seq') primary key,
login varchar(40) not null unique,
password varchar(40) not null,
game_count integer default 0,
game_count_win integer default 0,
crystal_green integer default 0,
crystal_blue integer default 0,
crystal_red integer default 0,
crystal_purple integer default 0
);
