create table company
(
    uuid        BINARY(16) not null,
    description varchar(255),
    name        varchar(255),
    primary key (uuid)
);

create table order_table
(
    uuid               BINARY(16)  not null,
    created_date       timestamp,
    last_modified_date timestamp,
    scoreboardnumber   integer     not null,
    state              varchar(11) not null,
    title              varchar(255),
    company_uuid       BINARY(16),
    primary key (uuid)
);

alter table order_table
    add constraint FKqs133gjeq2cup0qtyl9660d0j foreign key (company_uuid) references company