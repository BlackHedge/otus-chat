create DATABASE auth
    with
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION
LIMIT = 20
    IS_TEMPLATE = FALSE;

create TABLE public.users (
	login varchar NOT NULL
	, "password" varchar NOT NULL
	, nickname varchar NOT NULL
	, "role" varchar NOT NULL
	, is_active bool DEFAULT TRUE NULL
	, CONSTRAINT users_unique UNIQUE (login)
	, CONSTRAINT nick_unique UNIQUE (nickname)
);

--DROP TABLE users;

create TABLE public.roles (
	id serial NOT NULL
	, "role" varchar NOT NULL
	, "desc" varchar NULL
	, CONSTRAINT roles_pk PRIMARY KEY (id)
	, CONSTRAINT roles_unique UNIQUE ("role")
);

insert into roles ("role")
values
('ADMIN'),('USER');

insert into public.users
(login, "password", nickname, "role", is_active)
VALUES
('admin', 'admin', 'Ugly_Odmen', 'ADMIN', true),
('login', 'pass', 'User123', 'USER', true),
('login1', 'pass', 'User124', 'USER', true),
('login2', 'pass', 'Tom', 'USER', true),
('login11', 'pass', 'Bobby', 'USER', true);


select * from users;

--DELETE FROM users WHERE login = 'login1';