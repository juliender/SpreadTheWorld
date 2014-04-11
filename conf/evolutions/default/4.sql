# Apps schema

# --- !Ups
create table app (
  id					varchar(40) not null,
  name      			text,
  constraint pk_app_id primary key (id)
);

create table membership (
  app_id			    varchar(40) not null,
  app_user_id			varchar(40) not null,
  constraint fk_membership_app_id foreign key (app_id) references app,
  constraint fk_membership_app_user_id foreign key (app_user_id) references app_user
);



# --- !Downs
DROP TABLE app CASCADE;
DROP TABLE membership CASCADE;