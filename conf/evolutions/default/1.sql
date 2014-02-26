# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bar (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_bar primary key (id))
;

create table episode (
  id                        integer auto_increment not null,
  number                    integer,
  text                      text,
  constraint pk_episode primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table bar;

drop table episode;

SET FOREIGN_KEY_CHECKS=1;

