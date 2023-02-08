USE TeamXCDB
GO

CREATE PROCEDURE insert_result(@RaceId int, @Time float, @AthleteId int, @ResultId int OUTPUT)
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
GO

CREATE PROCEDURE insert_split(@ResultId int, @Index int, @Time float, @Distance float, @Unit varchar(5))
AS
BEGIN
	IF (@ResultId is null OR @Index is null OR @Time is null OR @Distance is null OR @Unit is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF (@Index < 0) BEGIN
		PRINT('Invalid index; must be >= 0')
		RETURN 2
	END

	IF (@Distance <= 0) BEGIN
		PRINT('Invalid distance; must be > 0')
		RETURN 3
	END

	IF (not @Unit = 'mi' and not @Unit = 'km' and not @Unit = 'm') BEGIN
		PRINT('Invalid unit; must be mi/km/m')
		RETURN 4
	END

	IF ((SELECT COUNT(*) FROM Result WHERE result_id = @ResultId) < 1) BEGIN
		PRINT('Result does not exist')
		RETURN 5
	END

	INSERT INTO ResultSplit(result_id, [index], [time], distance, distance_unit)
		VALUES(@ResultId, @Index, @Time, @Distance, @Unit)
END
GO