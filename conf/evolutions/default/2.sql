# Users schema
 
# --- !Ups
alter table identity_id add column firstname text;
alter table identity_id add column lastname text;
alter table identity_id add column fullname text;
alter table identity_id add column email text;

 
# --- !Downs
