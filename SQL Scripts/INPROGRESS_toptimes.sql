USE TeamXCDB
GO

select min([time]), first_name, last_name from Result
JOIN Athlete ON Result.athlete_id = Athlete.athlete_id
JOIN Race r ON Result.race_id = r.race_id
WHERE r.distance = 3
GROUP BY Athlete.athlete_id, first_name, last_name
ORDER BY min([time]) ASC