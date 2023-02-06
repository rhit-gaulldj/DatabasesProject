USE TeamXCDB
GO

ALTER PROCEDURE get_racelevels_for_course(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Course id cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Course WHERE course_id = @id) < 1) BEGIN
		RAISERROR('Course does not exist', 14, 1)
		RETURN 2
	END

	SELECT rl.race_level_id, rl.[name]
		FROM RaceLevel rl
		JOIN Race r ON r.race_level_id = rl.race_level_id
		JOIN Meet m ON r.meet_id = m.meet_id
		JOIN Course c ON m.course_id = c.course_id
		WHERE c.course_id = @id
		GROUP BY rl.race_level_id, rl.[name]
END