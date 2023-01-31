USE [TeamXCDB]
GO

CREATE PROCEDURE insert_athlete(@first_name nvarchar(200), @last_name nvarchar(200), @grad_year int, @gender char)
AS
BEGIN
	IF (@first_name is null OR @last_name is null OR @grad_year is null OR @gender is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF (@gender not like '[MFO]') BEGIN
		PRINT('Invalid gender')
		RETURN 2
	END

	IF (@grad_year < 1900) BEGIN
		PRINT('Grad year must be greater than 1900')
		RETURN 3
	END

	INSERT INTO Athlete(first_name, last_name, grad_year, gender)
		VALUES(@first_name, @last_name, @grad_year, @gender)
END