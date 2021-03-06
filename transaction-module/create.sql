create table operation_type (id bigint generated by default as identity, charge_order bigint check (charge_order>=0), description varchar(20), primary key (id))
create table payments_tracking (id bigint generated by default as identity, amount decimal(19,2), credit_id bigint, debit_id bigint, primary key (id))
create table transaction (id bigint generated by default as identity, account_id bigint not null, amount decimal(19,2) not null, balance decimal(19,2) not null, due_date bigint not null, event_date bigint not null, operation_type_id bigint not null, primary key (id))
alter table payments_tracking add constraint FKpcw7u4xotms3ujrlaid3m7a0d foreign key (credit_id) references transaction
alter table payments_tracking add constraint FKf7mswjfbnnwcejtxcbkv71umc foreign key (debit_id) references transaction
alter table transaction add constraint FKj2jvxwruf2hchy3jj25ne5m07 foreign key (operation_type_id) references operation_type
