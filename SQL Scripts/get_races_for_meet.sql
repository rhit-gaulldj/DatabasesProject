USE TeamXCDB
GO

CREATE PROCEDURE get_races_for_meet(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Meet id cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Meet WHERE meet_id = @id) < 1) BEGIN
		RAISERROR('Meet does not exist', 14, 1)
		RETURN 2
	END

	SELECT r.race_id, r.distance, r.distance_unit, r.race_level_id, rl.[name], r.meet_id
		FROM Race r
		JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id
		JOIN Meet m ON r.meet_id = m.meet_id
		WHERE m.meet_id = @id
END