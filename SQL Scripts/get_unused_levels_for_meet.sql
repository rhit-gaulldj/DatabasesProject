USE TeamXCDB
GO

CREATE PROCEDURE get_unused_levels_for_meet(@MeetId int)
AS
BEGIN
	IF (@MeetId is null) BEGIN
		RAISERROR('Meet ID cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Meet WHERE meet_id = @MeetId) < 1) BEGIN
		RAISERROR('Meet does not exist', 14, 1)
		RETURN 2
	END

	SELECT rl.race_level_id, rl.[name]
	FROM RaceLevel rl
	WHERE NOT EXISTS (SELECT * FROM Race
						WHERE meet_id = @MeetId AND race_level_id = rl.race_level_id)
END