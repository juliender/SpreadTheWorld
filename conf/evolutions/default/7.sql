# Picture schema

# --- !Ups
create table picture (
  id					varchar(40) not null,
  content_type          text,
  version               text,
  constraint pk_picture_id primary key (id)
);

alter table app add column background_picture_id varchar(40);
alter table app add column middle_picture_id varchar(40);
alter table app add column message text;


# --- !Downs
DROP TABLE picture CASCADE;
ALTER TABLE app DROP background_picture_id;
ALTER TABLE app DROP middle_picture_id;
ALTER TABLE app DROP message;