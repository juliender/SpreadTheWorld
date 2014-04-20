# --- !Ups
alter table identity_id add column picture_url text;

# --- !Downs
ALTER TABLE identity_id DROP picture_url;