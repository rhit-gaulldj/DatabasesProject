USE [TeamXCDB]
GO

CREATE VIEW ThreeMileResults AS
SELECT a.first_name AS [First Name], a.last_name AS [Last Name], 
	CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108) AS [Time],
	c.[name] AS Course, m.[name] AS Meet, m.[year] as [Year], rl.[name] AS [Race Level],
	m.[year] - a.grad_year + 13 AS [Grade]
FROM Result rs
JOIN Athlete a ON rs.athlete_id = a.athlete_id
JOIN Race r ON rs.race_id = r.race_id
JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id
JOIN Meet m ON r.meet_id = m.meet_id
JOIN Course c ON m.course_id = c.course_id
WHERE r.distance = 3 AND r.distance_unit = 'mi'
--ORDER BY rs.[time]