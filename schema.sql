create table hangman
(
	id serial not null,
	user_id int not null,
	coins bigint not null,
	bet bigint,
	word varchar,
	letters varchar,
	profit bigint,
	wins int,
	settings json
);

create unique index hangman_id_uindex
	on hangman (id);

create unique index hangman_user_id_uindex
	on hangman (user_id);

alter table hangman
	add constraint hangman_pk
		primary key (id);

create table hangman_transactions
(
    id serial not null,
    from_id int not null,
    to_id int not null,
    amount bigint not null,
    created_at timestamp not null,
    tid bigint not null
);

create unique index hangman_transactions_id_uindex
    on hangman_transactions (id);

alter table hangman_transactions
    add constraint hangman_transactions_pk
        primary key (id);

