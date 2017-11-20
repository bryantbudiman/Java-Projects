DROP DATABASE if exists Bubblr;

CREATE DATABASE Bubblr;

USE Bubblr;


CREATE TABLE Users (
  username varchar(50) not null,
  password varchar(50) not null,
  highscore int(4) not null
);