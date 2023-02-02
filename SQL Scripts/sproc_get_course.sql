USE TeamXCDB
GO

CREATE PROCEDURE get_course(@id int)
AS
	SELECT [course_id], [name]
	FROM Course
	WHERE course_id = @id