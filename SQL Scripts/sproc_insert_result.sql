USE [TeamXCDB]
GO

ALTER PROCEDURE [dbo].[insert_result](@RaceId int, @Time float, @AthleteId int, @ResultId int = null OUTPUT)
AS
BEGIN
	IF (@RaceId is null OR @Time is null OR @AthleteId is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		PRINT('Race does not exist')
		RETURN 2
	END

	IF ((SELECT COUNT(*) FROM Athlete WHERE athlete_id = @AthleteId) < 1) BEGIN
		PRINT('Athlete does not exist')
		RETURN 3
	END

	IF (@Time <= 0) BEGIN
		PRINT('Invalid time; must be > 0')
		RETURN 4
	END

	INSERT INTO Result([time], athlete_id, race_id)
		VALUES(@Time, @AthleteId, @RaceId)

	SELECT @ResultId = @@IDENTITY
END
