USE [TeamXCDB]
GO
Alter VIEW Performances 
AS
SELECT 
	[Athlete].athlete_id,[Athlete].first_name,[Athlete].last_name,[Athlete].grad_year,[Athlete].gender,
	[Meet].name as 'meet_name',[Meet].year,[Course].name as 'course_name',[Race].distance,[Race].distance_unit,
	CONVERT(varchar, DATEADD(ms, [Result].[time] * 1000, 0), 108) as [time],
	rs.splits,[Result].[time] as seconds
FROM [Result] as result
JOIN [Athlete] as athlete
ON result.athlete_id = athlete.athlete_id
JOIN [Race] as race
ON race.race_id = [Result].race_id
JOIN [Meet] as meet
ON race.meet_id = meet.meet_id
JOIN [Course] as course
ON Meet.course_id = course.course_id
JOIN [result_splits] as rs
ON result.result_id = rs.result_id

