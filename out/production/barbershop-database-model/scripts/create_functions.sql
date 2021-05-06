create or replace function max_customer_services_per_day()
returns trigger as $$
begin
    if (select count(*) from customer_service where booking_id=new.booking_id) > 5
    then raise exception 'Limit of customer services per day exceeded';
    end if;
    return new;
end; $$
language plpgsql;

create or replace function service_til_closing_time()
returns trigger as $$
declare
    booking_time time;
    service_duration interval;
begin
    booking_time=(select time from booking where id=new.booking_id);
    service_duration=(select duration from service where id=new.service_id);
    if booking_time+service_duration >'20:00:00'
    then raise exception 'Too late!';
    end if;
    return new;
end; $$
language plpgsql;

create or replace function is_master_free()
returns trigger as $$
declare
    booking_starts record;
    booking_services record;
    service_list record;
    total_service_duration interval := '0s';
begin
    for booking_starts in
        select time, id from booking where date = new.date and master_id = new.master_id
    loop
        if booking_starts.time <= new.time then
            for booking_services in
                select service_id from customer_service where booking_id = booking_starts.id
            loop
                for service_list in
                    select duration from service where id = booking_services.service_id
                loop
                    total_service_duration = total_service_duration+service_list.duration;
                end loop;
                if booking_starts.time+total_service_duration > new.time
                then raise exception 'Master is busy on that time';
                end if;
            end loop;
        end if;
    end loop;
    return new;
end; $$
language plpgsql;

create or replace function contact_info()
returns trigger as $$
begin
    raise exception 'Phone or email should be provided';
    return new;
end; $$
language plpgsql;

create or replace function discount_validity()
returns trigger as $$
declare
    discount_expiration date;
begin
    if (select count(*) from customer_discount where code=new.discount_code and id=new.customer_id) =0
    then raise exception 'This discount is not set for you';
    else
        discount_expiration :=(select expiration from customer_discount where code=new.discount_code and id=new.customer_id);
        if discount_expiration < new.date then raise exception 'Discount will end before booking';
            delete from booking where id = new.id;
        end if;
    end if;
    return new;
end;$$
language plpgsql;

create or replace function is_service_time_available()
returns trigger as $$
declare
    new_service_duration interval;
    service_duration interval;
    booking_info record;
    services record;
    time_after_services time;
    master_booking_time record;
begin
    new_service_duration := (select duration from service where id=new.service_id);
    select date, time, master_id into booking_info from booking where id=new.booking_id;
    time_after_services = booking_info.time+new_service_duration;
    for services in
        select service_id from customer_service where booking_id=new.booking_id
    loop
        service_duration := (select duration from service where id=services.service_id);
        time_after_services = time_after_services+service_duration;
    end loop;
    for master_booking_time in
        select time from booking where date=booking_info.date and master_id=booking_info.master_id
    loop
        if booking_info.time<master_booking_time.time and master_booking_time.time<time_after_services then raise exception 'Master will be busy at that time';
        end if;
    end loop;
    return new;
end; $$
language plpgsql;

create or replace function calculate_booking_price
(b_id integer, out initial_price decimal(5,2), out price_with_discount decimal(5,2))
as $$
declare
    d_code char(6);
    service_ids record;
    total_price decimal(5,2) =0.00;
    discount_info record;
begin
    for service_ids in
        select service_id from customer_service where booking_id= b_id
    loop
        total_price = total_price+(select price from service where id=service_ids.service_id);
    end loop;
    d_code = (select discount_code from booking where id=b_id);
    initial_price = total_price;
    if d_code is not null then
        select value, is_fixed into discount_info from discount where code=d_code;
        if discount_info.is_fixed = false then
            total_price = (100-discount_info.value)*total_price/100;
        else total_price = total_price-discount_info.value;
        end if;
        if total_price <0 then total_price=0.00;
        end if;
    end if;
    price_with_discount = total_price;
end;$$
language plpgsql;
