USE TeamXCDB
GO

ALTER PROCEDURE get_athletes(@page int, @page_size int)
AS
	SELECT athlete_id, first_name, last_name, grad_year, gender
	FROM Athlete
	ORDER BY last_name ASC
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_athlete_count
AS
	SELECT COUNT(*) FROM Athlete
GO