USE TeamXCDB
GO

ALTER PROCEDURE perform_search(@Query nvarchar(200))
AS
BEGIN
	-- Returns race results in which the names of the athlete/course/meet matches the provided query
	IF (@Query is null) BEGIN
		PRINT('Query must contain text')
		RETURN 1
	END

	SELECT TOP(100) Athlete, FormattedTime, Meet, [Year], c.[name] as Course,
			Grade, Splits, CAST(distance as varchar(10)) + distance_unit AS Distance
		FROM all_times_view atv
		JOIN Course c ON c.course_id = atv.course_id
		WHERE Athlete like '%' + @Query + '%' OR
				(Meet + ' (' + CAST([Year] as varchar(5)) + ')') like '%' + @Query + '%' OR
				c.[name] like '%' + @Query + '%'
		ORDER BY atv.[time] ASC
END