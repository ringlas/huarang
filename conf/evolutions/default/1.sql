# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table character_sheet (
  id                        integer auto_increment not null,
  current_episode           integer,
  date_created              varchar(255),
  text                      text,
  user_id                   integer,
  gamebook_id               integer,
  constraint pk_character_sheet primary key (id))
;

create table character_sheet_huarang (
  id                        integer auto_increment not null,
  energy                    integer,
  wisdom                    integer,
  mind                      integer,
  stamina                   integer,
  specialty                 varchar(255),
  mastery                   varchar(255),
  legendary                 varchar(255),
  bonus_received            tinyint(1) default 0,
  current_episode           integer,
  date_created              varchar(255),
  text                      text,
  user_id                   integer,
  constraint pk_character_sheet_huarang primary key (id))
;

create table episode (
  id                        integer auto_increment not null,
  text                      text,
  number                    integer(4),
  gamebook_id               integer,
  constraint pk_episode primary key (id))
;

create table episode_link (
  id                        integer auto_increment not null,
  episode_id                integer,
  text                      varchar(255),
  go_to_episode_id          integer,
  constraint pk_episode_link primary key (id))
;

create table gamebook (
  id                        integer auto_increment not null,
  title                     varchar(255),
  author                    varchar(255),
  year                      integer(4),
  intro                     varchar(255),
  date_created              varchar(255),
  user_id                   integer,
  constraint pk_gamebook primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  role                      varchar(255),
  constraint pk_user primary key (id))
;

alter table character_sheet add constraint fk_character_sheet_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_character_sheet_user_1 on character_sheet (user_id);
alter table character_sheet add constraint fk_character_sheet_gamebook_2 foreign key (gamebook_id) references gamebook (id) on delete restrict on update restrict;
create index ix_character_sheet_gamebook_2 on character_sheet (gamebook_id);
alter table character_sheet_huarang add constraint fk_character_sheet_huarang_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_character_sheet_huarang_user_3 on character_sheet_huarang (user_id);
alter table episode add constraint fk_episode_gamebook_4 foreign key (gamebook_id) references gamebook (id) on delete restrict on update restrict;
create index ix_episode_gamebook_4 on episode (gamebook_id);
alter table episode_link add constraint fk_episode_link_episode_5 foreign key (episode_id) references episode (id) on delete restrict on update restrict;
create index ix_episode_link_episode_5 on episode_link (episode_id);
alter table episode_link add constraint fk_episode_link_goToEpisode_6 foreign key (go_to_episode_id) references episode (id) on delete restrict on update restrict;
create index ix_episode_link_goToEpisode_6 on episode_link (go_to_episode_id);
alter table gamebook add constraint fk_gamebook_user_7 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_gamebook_user_7 on gamebook (user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table character_sheet;

drop table character_sheet_huarang;

drop table episode;

drop table episode_link;

drop table gamebook;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

