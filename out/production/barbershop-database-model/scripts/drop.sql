-- triggers
drop trigger if exists max_customer_services_per_day on customer_service cascade;
drop trigger if exists service_til_closing_time on customer_service cascade;
drop trigger if exists is_booking_time_available on booking cascade;
drop trigger if exists check_contact_info on customer cascade;
drop trigger if exists check_discount on booking cascade;
drop trigger if exists is_service_time_available on customer_service cascade;

-- functions
drop function if exists max_customer_services_per_day();
drop function if exists service_til_closing_time();
drop function if exists is_master_free();
drop function if exists contact_info();
drop function if exists discount_validity();
drop function if exists is_service_time_available();

-- views
drop view if exists vip_customer;
drop view if exists outdated_discount;
drop materialized view if exists booking_archive;

-- indices
drop index if exists index_for_master;
drop index if exists index_for_booking_service;

-- deletes
delete from customer_service;
delete from customer_discount;
delete from service;
delete from booking;
delete from customer;
delete from discount;
delete from master;

-- tables
drop table if exists master cascade;
drop table if exists customer cascade;
drop table if exists discount cascade;
drop table if exists customer_discount cascade;
drop table if exists booking cascade;
drop table if exists service cascade;
drop table if exists customer_service cascade;
