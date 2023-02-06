USE TeamXCDB
GO

ALTER PROCEDURE top_results_by_course
	(@CourseID int, @NumResults int, @AllowDupAthletes bit, @RaceLevelId int, @Distance int, @DistanceUnit char(2))
AS
BEGIN
	IF (@CourseId is null OR @NumResults is null OR @AllowDupAthletes is null
			OR @RaceLevelId  is null OR @Distance is null OR @DistanceUnit is null) BEGIN
		RAISERROR('Args cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Course WHERE course_id = @CourseID) <= 0) BEGIN
		RAISERROR('Course does not exist', 14, 1)
		RETURN 2
	END

	-- Race level can be < 0 to allow for any race level
	IF (@RaceLevelId >= 0 AND (SELECT COUNT(*) FROM RaceLevel WHERE race_level_id = @RaceLevelId) < 1) BEGIN
		RAISERROR('Race level does not exist', 14, 1)
		RETURN 3
	END

	IF (@AllowDupAthletes = 1)
		SELECT TOP(@NumResults) v1.Athlete, v1.[FormattedTime], v1.[Meet], v1.[Year], v1.[Grade]
			FROM all_times_view v1
			WHERE v1.course_id = @CourseID AND (race_level_id = @RaceLevelId OR @RaceLevelId < 0) AND
				v1.distance = @Distance AND v1.distance_unit = @DistanceUnit
			ORDER BY v1.[time] ASC
	ELSE
		SELECT TOP(@NumResults) v1.Athlete, v1.[FormattedTime], v1.[Meet], v1.[Year], v1.[Grade]
			FROM all_times_view v1
			JOIN (SELECT athlete_id, MIN([time]) as min_time
					FROM all_times_view
					WHERE course_id = @CourseID AND (race_level_id = @RaceLevelId OR @RaceLevelId < 0) AND
						distance = @Distance AND distance_unit = @DistanceUnit
					GROUP BY athlete_id
					) v2 ON v1.athlete_id = v2.athlete_id AND v1.[time] = v2.[min_time]
			ORDER BY v1.[time] ASC
END