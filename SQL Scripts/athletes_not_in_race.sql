USE TeamXCDB
GO

ALTER PROCEDURE get_athletes_not_in_race(@RaceId int)
AS
BEGIN
	IF (@RaceId is null) BEGIN
		PRINT('Race ID cannot be null')
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		PRINT('Race does not exist')
		RETURN 2
	END

	-- Gives athletes who were in school during the year of this meet
	-- Also matches athlete gender to the race
	DECLARE @gender char(1)
	DECLARE @race_year int
	SELECT @gender = Race.gender, @race_year = Meet.[year]
		FROM Race
		JOIN Meet ON Race.meet_id = Meet.meet_id
		WHERE Race.race_id = @RaceId

	-- Get all athletes except for those with results in this race
	(SELECT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender
		FROM Athlete a
		WHERE a.gender = @gender AND @race_year BETWEEN (a.grad_year - 4) AND a.grad_year)
	EXCEPT
	(SELECT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender
		FROM Athlete a
		JOIN Result rs ON rs.athlete_id = a.athlete_id
		JOIN Race r ON rs.race_id = r.race_id
		WHERE r.race_id = @RaceId)

END