USE TeamXCDB
GO

CREATE PROCEDURE get_race_levels
AS
	SELECT race_level_id, [name]
	FROM RaceLevel