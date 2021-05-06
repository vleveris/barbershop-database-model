set client_min_messages=warning;
-- tables
create table master( 
    id serial primary key,
    national_id char(11) not null,
    name varchar(20) not null,
    sername varchar(30) not null,
    qualification varchar(20) default 'barber',
    employment_date date
);

create table customer(
    id serial primary key,
    name varchar(20) not null,
    phone varchar(20) unique,
    email varchar(40) unique,
    vip boolean default false
);

create table discount(
    code char(6) primary key,
    value decimal(5, 2) not null,
    is_fixed boolean default false
);

create table customer_discount(
    code char(6),
    id serial,
    active_from date,
    expiration date
        constraint at_least_day check (expiration >=active_from),
    primary key(code, id),
    foreign key (code) references discount (code) on delete cascade on update cascade,
    foreign key (id) references customer (id) on delete cascade on update cascade
);

create table booking(
    id serial primary key,
    date date not null,
    time time not null,
    master_id serial not null,
    customer_id serial not null,
    discount_code char(6),
    constraint future check(date >= current_date),
    constraint working_hours check(time='10:00:00' or time='10:30:00' or time='11:00:00' or time='11:30:00' or time='12:00:00' or time='12:30:00' or time='13:00:00' or time='13:30:00' or time='14:00:00' or time='14:30:00' or time='15:00:00' or time='15:30:00' or time='16:00:00' or time='16:30:00' or time='17:00:00' or time='17:30:00' or time='18:00:00' or time='18:30:00' or time='19:00:00' or time='19:30:00'),
    foreign key (master_id) references master (id) on delete restrict on update cascade,
    foreign key (customer_id) references customer (id) on delete restrict on update cascade,
    foreign key (discount_code) references discount (code) on delete cascade on update restrict
);

create table service(
    id serial primary key,
    name varchar(200) not null unique,
    price decimal(5,2) not null,
    duration interval not null
        constraint possible_duration check (duration='15m' or duration='30m' or duration='45m' or duration='1h' or duration='90m' or duration='2h')
);

create table customer_service(
    booking_id serial,
    service_id serial,
    primary key(booking_id, service_id),
    foreign key (booking_id) references booking (id) on delete cascade on update cascade,
    foreign key (service_id) references service (id) on delete cascade on update cascade
);

-- views
create view vip_customer
(id, name, phone, email)
as select id, name, phone, email from customer where vip=true;
create view outdated_discount
(code, id)
as select code, id from customer_discount where expiration<current_date;

create materialized view booking_archive
as select * from booking where date<current_date with data;
