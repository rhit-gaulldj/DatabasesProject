USE [TeamXCDB]
GO
/****** Object:  UserDefinedFunction [dbo].[func_athlete_pbs]    Script Date: 2/6/2023 1:33:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER FUNCTION [dbo].[func_athlete_pbs] (@AthleteID int
)
returns TABLE 
AS 
    RETURN 
		Select athlete_id,first_name,last_name,grad_year,gender, meet_name,[year],course_name,p1.distance,p1.distance_unit,
		CONVERT(varchar, DATEADD(ms, p1.best_time * 1000, 0), 108) as best_time,
		Case 
			When p1.distance_unit = 'mi' THEN CONVERT(varchar, DATEADD(ms, (best_time/p1.distance) * 1000, 0), 108)
			When p1.distance_unit = 'km' THEN CONVERT(varchar, DATEADD(ms, (best_time/p1.distance) * 1.60934 * 1000, 0), 108) 
        Else null
		End	AS per_mile, splits
	From [dbo].[Performances] as p2
	Join (Select distance, distance_unit, MIN([seconds]) as 'best_time'
		From [dbo].[Performances]
		Where @AthleteID = athlete_id
		Group By distance, distance_unit) AS p1
	On p1.distance = p2.distance and p1.distance_unit = p2.distance_unit and p1.best_time = p2.[seconds] and  @AthleteID = p2.athlete_id