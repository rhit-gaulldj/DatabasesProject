USE TeamXCDB
GO

ALTER PROCEDURE get_athletes(@page int)
AS
	SELECT athlete_id, first_name, last_name, grad_year, gender FROM Athlete