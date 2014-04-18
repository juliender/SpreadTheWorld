# Users schema
 
# --- !Ups
alter table app add column owner_id varchar(40);
alter table app add column description text;
alter table app add column picture_url text;

ALTER TABLE app ADD constraint fk_owner_id foreign key (owner_id)
   references app_user (id) on delete cascade;
 
# --- !Downs
