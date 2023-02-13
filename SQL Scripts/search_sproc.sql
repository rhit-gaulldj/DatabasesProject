USE TeamXCDB
GO

ALTER PROCEDURE perform_search(@Query nvarchar(200))
AS
BEGIN
	-- Returns IDs of matching athletes, courses, and meets
	-- Also returns the type of each result
	IF (@Query is null) BEGIN
		PRINT('Query must contain text')
		RETURN 1
	END

	(SELECT athlete_id as Id, 'Athlete' as [Type], first_name + ' ' + last_name as [Name]
	 FROM Athlete
	 WHERE first_name like '%' + @Query + '%'
			OR last_name like '%' + @Query + '%')
	UNION
	(SELECT course_id as Id, 'Course' as [Type], [name] as [Name]
	 FROM Course
	 WHERE [name] like '%' + @Query + '%')
	UNION
	(SELECT meet_id as Id, 'Meet' as [Type], [name] + ' (' + CAST([year] as varchar(5)) + ')' as [Name]
	 FROM Meet
	 WHERE [name] like '%' + @Query + '%')
END