USE TeamXCDB
GO

ALTER PROCEDURE insert_full_line(@first_name nvarchar(200), @last_name nvarchar(200), @gender char(1), -- 3
	@time float, @course varchar(100), @meet_name varchar(100), @meet_year int, -- 4
	@race_level varchar(100), @grade int, @distance float, @distance_unit varchar(5), -- 4
	@first_split_time float = null, @first_split_dist float = null, @first_split_unit varchar(5) = null, -- 3
	@second_split_time float = null, @second_split_dist float = null, @second_split_unit varchar(5) = null, -- 3
	@third_split_time float = null, @third_split_dist float = null, @third_split_unit varchar(5) = null, -- 3
	@fourth_split_time float = null, @fourth_split_dist float = null, @fourth_split_unit varchar(5) = null) -- 3
AS
BEGIN
	-- We'll assume there are no nulls (except for result split fields)
	-- Just continue along, inserting as needed, and selecting IDs where they exist
	DECLARE @a_id int
	SELECT @a_id = athlete_id FROM Athlete WHERE first_name = @first_name AND last_name = @last_name AND gender = @gender
	IF (@a_id is null) BEGIN
		-- Athlete doesn't exist, must insert them
		-- grade = meet_yr - grad_yr + 13
		-- grad_yr = meet_yr + 13 - grade
		DECLARE @grad_yr int
		SET @grad_yr = @meet_year + 13 - @grade
		EXEC insert_athlete @first_name = @first_name, @last_name = @last_name, @grad_year = @grad_yr,
			@gender = @gender, @id = @a_id OUTPUT
	END

	DECLARE @c_id int
	SELECT @c_id = course_id FROM Course WHERE [name] = @course
	IF (@c_id is null) BEGIN
		EXEC insert_course @CourseName = @course, @id = @c_id OUTPUT
	END

	DECLARE @m_id int
	SELECT @m_id = meet_id FROM Meet WHERE [name] = @meet_name AND [year] = @meet_year
	IF (@m_id is null) BEGIN
		EXEC insert_meet @MeetName = @meet_name, @MeetYear = @meet_year, @CourseId = @c_id, @id = @m_id OUTPUT
	END

	DECLARE @rl_id int
	SELECT @rl_id = race_level_id FROM RaceLevel WHERE [name] = @race_level
	IF (@rl_id is null) BEGIN
		INSERT INTO RaceLevel([name])
			VALUES(@race_level)
		SELECT @rl_id = @@IDENTITY
	END

	-- While races have different units, we don't look at those since a meet can only have one race per level per gender anyway
	DECLARE @r_id int
	SELECT @r_id = race_id FROM Race WHERE meet_id = @m_id AND race_level_id = @rl_id AND gender = @gender
	IF (@r_id is null) BEGIN
		EXEC insert_race @RaceDistance = @distance, @RaceUnit = @distance_unit, @RaceLevelID = @rl_id, @MeetID = @m_id,
			@Gender = @gender, @id = @r_id OUTPUT
	END

	-- Now we've got the race ID and athlete ID, and everything else has been resolved.
	-- Check if this athlete has run in this race already. If they have a result, then don't add a new one
	DECLARE @rs_id int
	SELECT @rs_id = result_id FROM Result WHERE athlete_id = @a_id AND race_id = @r_id
	IF (@rs_id is null) BEGIN
		-- Let's insert the result and the splits
		-- Splits can be null, which indicates that they should not be added
		EXEC insert_result @RaceId = @r_id, @Time = @time, @AthleteId = @a_id, @ResultId = @rs_id OUTPUT
	END

	-- Try to insert each split
	-- @first_split_time float, @first_split_dist float, @first_split_unit varchar(5), 
	IF (@first_split_time is not null) BEGIN
		IF (NOT EXISTS (SELECT * FROM ResultSplit WHERE result_id = @rs_id AND [index] = 0)) BEGIN
			EXEC insert_split @ResultId = @rs_id, @Index = 0, @Time = @first_split_time, @Distance = @first_split_dist,
				@Unit = @first_split_unit
		END
	END
	IF (@second_split_time is not null) BEGIN
		IF (NOT EXISTS (SELECT * FROM ResultSplit WHERE result_id = @rs_id AND [index] = 1)) BEGIN
			EXEC insert_split @ResultId = @rs_id, @Index = 1, @Time = @second_split_time, @Distance = @second_split_dist,
				@Unit = @second_split_unit
		END
	END
	IF (@third_split_time is not null) BEGIN
		IF (NOT EXISTS (SELECT * FROM ResultSplit WHERE result_id = @rs_id AND [index] = 2)) BEGIN
			EXEC insert_split @ResultId = @rs_id, @Index = 2, @Time = @third_split_time, @Distance = @third_split_dist,
				@Unit = @third_split_unit
		END
	END
	IF (@fourth_split_time is not null) BEGIN
		IF (NOT EXISTS (SELECT * FROM ResultSplit WHERE result_id = @rs_id AND [index] = 3)) BEGIN
			EXEC insert_split @ResultId = @rs_id, @Index = 3, @Time = @fourth_split_time, @Distance = @fourth_split_dist,
				@Unit = @fourth_split_unit
		END
	END
END