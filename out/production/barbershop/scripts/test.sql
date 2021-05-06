-- discount is active at least 1 day
insert into customer_discount
values ('VIP026', 1, '2020-12-03', '2020-12-02');
-- bookings cannot be set in the past
insert into booking
(date, time, master_id, customer_id, discount_code)
values ('2020-12-06', '12:00', 4, 4, 'VIP026');
-- booking time should be compatible with working hours
insert into booking
(date, time, master_id, customer_id, discount_code)
values ('2020-12-28', '21:00', 4, 4, 'VIP026');
-- service duration limits
insert into service values (default, 'Haircut_bad', 15.00, '10m');
-- service til closing time
insert into booking
(id, date, time, master_id, customer_id, discount_code)
values (10, '2020-12-28', '19:30', 4, 4, 'VIP026');
insert into customer_service values (10, 1);
-- check if master is free
insert into booking
(id, date, time, master_id, customer_id, discount_code)
values (11, '2020-12-28', '15:00', 4, 4, 'VIP026');
insert into customer_service values (11, 1);
insert into booking
(date, time, master_id, customer_id, discount_code)
values ('2020-12-28', '15:00', 4, 2, null);
-- at least phone or email should be provided
insert into customer
(name)
values ('Alma');
-- discount validity
insert into booking
values (default, '2021-01-07', '12:00', 4, 5, 'VIP026');
insert into booking
values (default, '2021-01-06', '12:00', 4, 4, 'VIP026');
-- service time availability
insert into booking
(id, date, time, master_id, customer_id, discount_code)
values (12, '2020-12-28', '19:00', 4, 2, null);
insert into customer_service
values (12, 1);
