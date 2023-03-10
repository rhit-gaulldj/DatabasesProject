USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[athlete_race_results]    Script Date: 1/19/2023 11:17:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--@athlete_id is mandatory, race_id is optional. If there isn't a race_id, it just shows all races that athlete has
ALTER PROCEDURE [dbo].[athlete_race_results] 
(@athlete_id int,
@race_id int = -1)
AS
SELECT 
	[Athlete].first_name,[Athlete].last_name,[Athlete].grad_year,[Athlete].gender,
	[Meet].name,[Meet].year,[Course].name,[Race].distance,[Race].distance_unit,[Result].time,rs.splits
FROM [Result] as result
JOIN [Athlete] as athlete
ON @athlete_id = athlete.athlete_id
JOIN [Race] as race
ON race.race_id = [Result].race_id
JOIN [Course] as course
ON race.course_id = course.course_id
JOIN [Meet] as meet
ON race.meet_id = meet.meet_id
JOIN [result_splits] as rs
ON result.result_id = rs.result_id
WHERE @athlete_id = [Result].athlete_id AND (@race_id = -1 OR @race_id = [Result].race_id)

