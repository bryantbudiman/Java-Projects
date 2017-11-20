drop database if exists jeopardy;
create database jeopardy; 
use jeopardy; 

create table loginInformation (
   username varchar(50) not null primary key, 
   password varchar(50) not null
);
