USE TeamXCDB
GO

ALTER PROCEDURE get_roster(@Year int, @Gender char)
AS
BEGIN
	if (@Year is null OR @Gender is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END
	if (@Gender not like '[MFO]') BEGIN
		PRINT('Gender must be M/F/O')
		RETURN 2
	END

	-- Returns all athletes with a result for this given year
	SELECT DISTINCT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender, @Year - a.grad_year + 13 AS Grade
	FROM Athlete a
	JOIN Result rs ON rs.athlete_id = a.athlete_id
	JOIN Race r ON rs.race_id = r.race_id
	JOIN Meet m ON r.meet_id = m.meet_id AND m.[year] = @Year
	WHERE a.gender = @Gender
END