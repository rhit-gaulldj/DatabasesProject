USE TeamXCDB
GO

CREATE PROCEDURE perform_search(@Query nvarchar(200))
AS
BEGIN
	-- Returns IDs of matching athletes, courses, and meets
	-- Also returns the type of each result
	IF (@Query is null) BEGIN
		PRINT('Query must contain text')
		RETURN 1
	END

	(SELECT a.athlete_id as Id, 'Athlete' as [Type]
	 FROM Athlete a
	 WHERE first_name like '%' + @Query + '%'
			OR last_name like '%' + @Query + '%')
	UNION
	(SELECT c.course_id as Id, 'Course' as [Type]
	 FROM Course c
	 WHERE c.[name] like '%' + @Query + '%')
	UNION
	(SELECT m.meet_id as Id, 'Meet' as [Type]
	 FROM Meet m
	 WHERE m.[name] like '%' + @Query + '%')
END