USE TeamXCDB
GO

ALTER PROCEDURE get_meets(@page int, @page_size int)
AS
	SELECT m.meet_id, m.[name], m.[year], m.course_id, c.[name]
	FROM Meet m
	JOIN Course c ON m.course_id = c.course_id
	ORDER BY [year] DESC, m.[name]
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_meet_count
AS
	SELECT COUNT(*) FROM Meet
GO

ALTER PROCEDURE get_meet(@id int)
AS
	SELECT m.meet_id, m.[name], m.[year], m.course_id, c.[name]
	FROM Meet m
	JOIN Course c ON c.course_id = m.course_id
	WHERE m.meet_id = @id