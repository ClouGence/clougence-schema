create table proc_table
(
    c_id   varchar(50) not null,
    c_name varchar(50) not null,
    primary key (c_id, c_name)
);

create table proc_table_ref
(
    r_int   int not null primary key,
    r_k1    varchar(50) null,
    r_k2    varchar(50) null,
    r_name  varchar(100) null,
    r_index int null,
    r_data  int null,
    constraint proc_table_ref_uk
        unique (r_name),
    constraint ptr
        foreign key (r_k1, r_k2) references proc_table (c_name, c_id)
);

create
index proc_table_ref_index
    on proc_table_ref (r_index);

create table t1
(
    t1_s1 int null,
    t1_s2 int null,
    t1_s3 int not null primary key
);

create
index t1_s1_index on t1 (t1_s1);

create table t3
(
    t3_s1 int null,
    t3_s2 int null,
    t3_s3 int null,
    constraint CO
        foreign key (t3_s2) references t1 (t1_s3)
);

create
index normal_index_t3_s1 on t3 (t3_s1);

create table tb_user
(
    userUUID      varchar(50) not null primary key,
    name          varchar(100) null,
    loginName     varchar(100) default 'abc' null,
    loginPassword varchar(100) null,
    email         varchar(50) null,
    index         int null,
    registerTime  timestamp null,
    constraint tb_user_email_userUUID_uindex
        unique (email, userUUID),
    constraint tb_user_userUUID_uindex
        unique (userUUID)
);

create
index normal_index_tb_user
    on tb_user (loginPassword, loginName);

create view tb_user_view as
select *
from tb_user;

create
materialized view tb_user_view_m as
select *
from tb_user;