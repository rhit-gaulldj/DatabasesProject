USE TeamXCDB
GO

CREATE PROCEDURE get_meets(@page int, @page_size int)
AS
	SELECT meet_id, [name], [year]
	FROM Meet
	ORDER BY [year], [name] ASC
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_meet_count
AS
	SELECT COUNT(*) FROM Meet
GO