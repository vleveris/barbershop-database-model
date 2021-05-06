create trigger max_customer_services_per_day
before insert on customer_service
for each row
execute procedure max_customer_services_per_day();

create trigger service_til_closing_time
before insert or update on customer_service
for each row
execute procedure service_til_closing_time();

create trigger is_booking_time_available
before insert or update on booking
for each row
execute procedure is_master_free();

create trigger check_contact_info
before insert or update of phone, email on customer
for each row
when (new.phone is null and new.email is null)
execute procedure contact_info();

create trigger check_discount
after insert or update of discount_code, date on booking
for each row
when (new.discount_code is not null)
execute procedure discount_validity();

create trigger is_service_time_available
before insert or update on customer_service
for each row
execute procedure is_service_time_available();
