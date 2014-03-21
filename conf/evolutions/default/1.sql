# Users schema
 
# --- !Ups
create table app_user (
  id				varchar(40) not null,
  constraint pk_fb_user primary key (id)
);


create table identity_id (
  id					varchar(40) not null,
  app_user_id			varchar(40) not null,
  user_id				varchar(40) not null,
  provider_id			varchar(40) not null,
  constraint pk_identity_id primary key (id),
  constraint fk_app_user_id foreign key (app_user_id) references app_user

);

 
# --- !Downs
DROP TABLE app_user;
DROP TABLE identity_id;
