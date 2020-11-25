-- Xin Jia Cao
-- Script to create the database

DROP DATABASE IF EXISTS EMAILAPP;
CREATE DATABASE EMAILAPP;

USE EMAILAPP;

DROP USER IF EXISTS userxj@localhost;
CREATE USER userxj@'localhost' IDENTIFIED BY 'dawson2';
GRANT ALL ON EMAILAPP.* TO userxj@'localhost';

FLUSH PRIVILEGES;