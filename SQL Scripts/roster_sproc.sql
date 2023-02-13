USE TeamXCDB
GO

ALTER PROCEDURE get_roster(@Year int)
AS
BEGIN
	if (@Year is null) BEGIN
		PRINT('Year cannot be null')
		RETURN 1
	END

	-- Returns all athletes with a result for this given year
	SELECT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender, @Year - a.grad_year + 13 AS Grade
	FROM Athlete a
	JOIN Result rs ON rs.athlete_id = a.athlete_id
	JOIN Race r ON rs.race_id = r.race_id
	JOIN Meet m ON r.meet_id = m.meet_id AND m.[year] = @Year
END