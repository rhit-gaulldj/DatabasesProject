USE TeamXCDB
GO

ALTER VIEW all_times_view
AS
SELECT CONCAT(a.first_name, ' ', a.last_name) AS Athlete, CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108) AS [FormattedTime],
				m.[name] as Meet, m.[year] AS [Year], m.[year] - a.grad_year + 13 AS [Grade], 
				rs.result_id, r.race_id, rl.race_level_id, a.athlete_id, m.meet_id, c.course_id, rs.[time], r.distance,
				r.distance_unit
		FROM Result rs
		JOIN Race r ON rs.race_id = r.race_id
		JOIN Athlete a ON rs.athlete_id = a.athlete_id
		JOIN Meet m ON r.meet_id = m.meet_id
		JOIN Course c ON c.course_id = m.course_id
		JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id