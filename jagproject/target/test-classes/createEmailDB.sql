-- Best practice MySQL
--
-- The EMAILAPP database must exist before running this script
-- and before running the fx_desktop_standard_project because the unit
-- test runs this script. Only the root user can create a MySQL database
-- but you do not want to use the root user and password in your code.
--
-- This script needs to run only once

DROP DATABASE IF EXISTS EMAILAPP;
CREATE DATABASE EMAILAPP;

USE EMAILAPP;

DROP USER IF EXISTS userxj@localhost;
CREATE USER userxj@'localhost' IDENTIFIED BY 'dawson2';
GRANT ALL ON EMAILAPP.* TO userxj@'localhost';

FLUSH PRIVILEGES;