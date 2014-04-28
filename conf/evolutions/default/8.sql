# add count for apps

# --- !Ups
alter table app add column count_messages int;


# --- !Downs
ALTER TABLE app DROP count_messages;
