insert into master
(national_id, name, sername, employment_date)
values ('38909154686', 'Jonas', 'Andriulis', '2015-05-13'),
('37810138832', 'Gediminas', 'Starkus', '2020-03-13'),
('37102060584', 'Paulius', 'Erelis', '2017-11-17'),
('36912155637', 'Stasys', 'Paulauskas', '2018-12-05'),
('38003052259', 'Artūras', 'Ulionovas', '2016-02-10');

insert into master
(national_id, name, sername, qualification, employment_date)
values ('46702059894', 'Julija', 'Antanaityte', 'hairdresser', '2011-09-10'),
('48604303513', 'Irena', 'Stukaite', 'hairstylist', '2016-06-28'),
('49501160340', 'Jadvyga', 'Markiene', 'hairstylist', '2019-08-02'),
('48111281372', 'Renata', 'Simkunaite', 'hairdresser', '2016-11-08');

insert into customer
(name, phone, email, vip)
values ('Stase', '+37062676247', 'stasele123@gmail.com', default),
('Audrius', null, 'audrius.juodis@mif.stud.vu.lt', false),
('Gintaras', null, 'gintasne@gmail.com', default),
('Jonas', null, 'jodraug@gmail.com', true);

insert into discount
values ('CHR001', 15.00, default),
('VIP026', 10.00, true),
('HAP559', 40.00, false);

insert into customer_discount
values('VIP026', 4, null, '2020-12-31');

insert into booking
(date, time, master_id, customer_id, discount_code)
values ('2020-12-20', '12:00', 4, 4, 'VIP026'),
('2020-12-18', '19:30', 7, 1, null);

insert into service values (default, 'Haircut', 15.00, '1h');
insert into service values (default, 'Shave', 20.00, '1h');
insert into service values (default, 'Beard modeling', 30.00, '90m');

insert into customer_service values (1, 3);
