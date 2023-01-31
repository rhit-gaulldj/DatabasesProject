USE TeamXCDB
GO

CREATE PROCEDURE get_athlete(@id int)
AS
	SELECT athlete_id, first_name, last_name, grad_year, gender
	FROM Athlete
	WHERE athlete_id = @id