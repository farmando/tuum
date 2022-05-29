create table customer (
    id bigserial primary key,
    name varchar(50) not null
);

insert into customer(name) values
    ('Peter'),
    ('Jhoe'),
    ('Mary');

create table account (
    id bigserial primary key ,
    customer_id int not null,
    country varchar(50) not null,
    foreign key (customer_id) references customer(id)
);

create table currency (
    id varchar(3) primary key,
    name varchar(30) not null
);

insert into currency(id,name) values
    ('EUR', 'EURO'),
    ('SEK', 'KRONA SEK'),
    ('GBP', 'POUND STERLING'),
    ('USD', 'AMERICAN DOLLAR');

create table balance (
     id bigserial primary key,
     account_id int not null,
     currency_id varchar(3) not null,
     balance numeric(20,2) not null,
     foreign key (account_id) references account (id),
     foreign key (currency_id) references currency (id),
     unique (account_id, currency_id)
);

create table transaction (
    id bigserial primary key,
    account_id int not null,
    trx_controller varchar(50) not null,
    amount numeric(20,2) not null,
    direction varchar(3) not null,
    currency_id varchar(3) not null,
    description varchar(50) not null,
    foreign key (account_id) references account (id),
    foreign key (currency_id) references currency (id)
);

ALTER TABLE transaction ADD CONSTRAINT check_types CHECK (direction IN ('IN', 'OUT'));

CREATE OR REPLACE FUNCTION do_not_allow_transaction_when_insuficient_balance()
RETURNS trigger AS
$$
    BEGIN
        if (NEW.balance < 0) then
            raise exception 'Insuficient balance';
        end if;
        RETURN NEW;
    END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER check_balance
    BEFORE UPDATE ON balance
    FOR EACH ROW EXECUTE PROCEDURE do_not_allow_transaction_when_insuficient_balance();