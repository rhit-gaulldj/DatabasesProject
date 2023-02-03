USE TeamXCDB
GO

CREATE PROCEDURE get_meets(@page int, @page_size int)
AS
	SELECT meet_id, [name], [year], course_id
	FROM Meet
	ORDER BY [year] DESC, [name]
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_meet_count
AS
	SELECT COUNT(*) FROM Meet
GO

CREATE PROCEDURE get_meet(@id int)
AS
	SELECT meet_id, [name], [year], course_id
	FROM Meet
	WHERE meet_id = @id