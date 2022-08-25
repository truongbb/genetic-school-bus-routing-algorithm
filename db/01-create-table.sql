create table bus_stop
(
    id                integer primary key,
    latitude          number(10,7),
    longitude         number(10,7),
    number_of_student number not null
);

create table distance_matrix
(
    id                integer primary key,
    start_bus_stop_id number not null,
    end_bus_stop_id   number not null,
    distance          number not null,
    durations         number not null,
    constraint fk_start_bus_stop foreign key (start_bus_stop_id) references bus_stop (id),
    constraint fk_end_bus_stop foreign key (end_bus_stop_id) references bus_stop (id)
);
