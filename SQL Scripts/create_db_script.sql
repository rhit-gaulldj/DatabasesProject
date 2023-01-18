/*
 Database creation script
 Creates the database (TeamXCDB) and grants the db_owner permission to all members
*/

CREATE DATABASE [TeamXCDB]
ON
PRIMARY (
	NAME=DbData,
	FILENAME='D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\TeamXCDB.mdf',
	SIZE=6MB,
	FILEGROWTH=12%) 
LOG ON (
	NAME=DbLog,
	FILENAME='D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\TeamXCDB.ldf',
	SIZE=3MB,
	FILEGROWTH=10%)


-- Must USE to add any users
USE [TeamXCDB]
GO

-- Add user scripts, call one at a time
CREATE USER calesnm FROM LOGIN calesnm
EXEC sp_addrolemember 'db_owner', calesnm
GO

CREATE USER demakijp FROM LOGIN demakijp
EXEC sp_addrolemember 'db_owner', demakijp
GO