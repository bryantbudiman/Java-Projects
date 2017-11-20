drop database if exists bbbudimaA4;
create database bbbudimaA4; 
use bbbudimaA4; 

create table loginInformation (
   username varchar(50) not null primary key, 
   password varchar(50) not null
);