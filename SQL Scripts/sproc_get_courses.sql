USE TeamXCDB
GO

CREATE PROCEDURE get_courses(@page int, @page_size int)
AS
	SELECT course_id, [name]
	FROM Course
	ORDER BY [name] ASC
	OFFSET(@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_course_count
AS
	SELECT COUNT(*) FROM Course
GO

ALTER PROCEDURE get_all_courses
AS
	SELECT course_id, [name]
	FROM Course
	ORDER BY [name] ASC
GO