USE TeamXCDB
GO

ALTER PROCEDURE get_results_for_race(@RaceId int)
AS
BEGIN
	IF (@RaceId is null) BEGIN
		RAISERROR('race Id cannot be null', 14, 1)
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		RAISERROR('Race does not exist', 14, 1)
		RETURN 2
	END

	SELECT race_id, [Athlete], [FormattedTime], [Grade], [Splits]
		FROM all_times_view
		WHERE race_id = @RaceId
		ORDER BY [time] ASC

END