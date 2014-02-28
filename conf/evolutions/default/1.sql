# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table episode (
  id                        integer auto_increment not null,
  text                      text,
  constraint pk_episode primary key (id))
;

create table episode_link (
  id                        integer auto_increment not null,
  episode_id                integer,
  text                      varchar(255),
  go_to_episode_id          integer,
  constraint pk_episode_link primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;

alter table episode_link add constraint fk_episode_link_episode_1 foreign key (episode_id) references episode (id) on delete restrict on update restrict;
create index ix_episode_link_episode_1 on episode_link (episode_id);
alter table episode_link add constraint fk_episode_link_goToEpisode_2 foreign key (go_to_episode_id) references episode (id) on delete restrict on update restrict;
create index ix_episode_link_goToEpisode_2 on episode_link (go_to_episode_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table episode;

drop table episode_link;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

