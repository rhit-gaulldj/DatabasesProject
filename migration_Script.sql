-----------------------------------
-------------PHASE 1---------------
-----------------------------------

/*
 Database creation script
 Creates the database (@databaseName) and grants the db_owner permission to all members
*/

/*
USE TeamXCDB
GO
DROP DATABASE [_XCDBTest3]
*/

:setvar database_name "_XCDBTest3"
:setvar user_name "TeamXCDB"

CREATE DATABASE $(database_name) ON
PRIMARY (
	NAME=DbData,
	FILENAME='D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\$(database_name).mdf',
	SIZE=6MB,
	FILEGROWTH=12%) 
LOG ON (
	NAME=DbLog,
	FILENAME='D:\Database\MSSQL15.MSSQLSERVER\MSSQL\DATA\$(database_name).ldf',
	SIZE=3MB,
	FILEGROWTH=10%)
GO

USE $(database_name)
GO

CREATE USER $(user_name)
FOR LOGIN TeamXCDB
-----------------------------------
-------------PHASE 2---------------
-----------------------------------
--+Tables

-- Base tables (i.e. no foreign keys - this has since changed as Meet has an FK):
--		Athlete, RaceLevel, Course, Meet

CREATE TABLE Athlete(
	athlete_id int IDENTITY(1,1) PRIMARY KEY,
	first_name nvarchar(100) NOT NULL,
	last_name nvarchar(100),
	grad_year int NOT NULL,
	gender char(1),

	CHECK(grad_year > 1900),
	CHECK(gender LIKE '[MFO]') -- Male, female, other
)

CREATE TABLE RaceLevel(
	race_level_id int IDENTITY(1,1) PRIMARY KEY,
	name varchar(100) NOT NULL
)

CREATE TABLE Course(
	course_id int IDENTITY(1,1) PRIMARY KEY,
	name varchar(100) NOT NULL
)

CREATE TABLE Meet(
	meet_id int IDENTITY(1,1) PRIMARY KEY,
	name varchar(100) NOT NULL,
	[year] int,
	course_id int,

	FOREIGN KEY(course_id) REFERENCES Course(course_id)
)

Go

-- "Secondary" tables (i.e. tables w/ foreign keys):
--		Race, Result, ResultSplit

CREATE TABLE Race(
	race_id int IDENTITY(1,1) PRIMARY KEY,
	distance float NOT NULL,
	distance_unit varchar(2) NOT NULL,
	race_level_id int NOT NULL,
	meet_id int NOT NULL,
	gender char(1) NOT NULL,

	FOREIGN KEY(race_level_id) REFERENCES RaceLevel(race_level_id),
	FOREIGN KEY(meet_id) REFERENCES Meet(meet_id),
	CHECK(distance_unit = 'km' OR distance_unit = 'mi' OR distance_unit = 'm'),
	CHECK(gender LIKE '[MFO]')
)

CREATE TABLE Result(
	result_id int IDENTITY(1,1) PRIMARY KEY,
	[time] float NOT NULL, -- Times are in seconds
	athlete_id int NOT NULL,
	race_id int NOT NULL,

	FOREIGN KEY(athlete_id) REFERENCES Athlete(athlete_id),
	FOREIGN KEY(race_id) REFERENCES Race(race_id)
)

CREATE TABLE ResultSplit(
	result_split_id int IDENTITY(1,1) PRIMARY KEY,
	result_id int NOT NULL,
	[index] int NOT NULL,
	[time] float NOT NULL, -- Times are in seconds
	distance float NOT NULL,
	distance_unit varchar(2) NOT NULL,

	FOREIGN KEY(result_id) REFERENCES Result(result_id),
	CHECK(distance_unit = 'km' OR distance_unit = 'mi' OR distance_unit = 'm')
)

GO

-- Login and Session tables
CREATE TABLE [Login](
	email varchar(100) PRIMARY KEY NOT NULL,
	password_hash varchar(50) NOT NULL,
	password_salt varchar(50) NOT NULL,
	created_at datetime NOT NULL DEFAULT GETDATE(),
	last_login datetime NOT NULL DEFAULT GETDATE(),

	CHECK(email LIKE '%@%.%')
)

CREATE TABLE [Session](
	session_id uniqueidentifier PRIMARY KEY NOT NULL DEFAULT NEWID(),
	expires_at datetime NOT NULL,
	user_email varchar(100) NOT NULL,

	FOREIGN KEY(user_email) REFERENCES [Login](email)
)
GO

-----------------------------------
-------------PHASE 3---------------
-----------------------------------
--+Indexes

CREATE INDEX AthleteName ON Athlete(last_name);

CREATE INDEX CourseName ON Course([name]);

CREATE INDEX MeetYear ON Meet([year]);
CREATE INDEX MeetName ON Meet([name]);

CREATE INDEX ResultTime ON Result([time]);
Go


-----------------------------------
-------------PHASE 4---------------
-----------------------------------

-- + Function that views use
CREATE FUNCTION get_splits(@ResultId int)
RETURNS varchar(100)
AS
BEGIN
	DECLARE @Splits varchar(100)
	SELECT @Splits = COALESCE(@Splits + ', ', '') + SUBSTRING(CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108), 4, 5)
		FROM ResultSplit rs
		WHERE result_id = @ResultId
		ORDER BY rs.[index]

	RETURN @Splits
END
GO

--+views

CREATE VIEW all_times_view
AS
SELECT CONCAT(a.first_name, ' ', a.last_name) AS Athlete, 
			SUBSTRING(CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108), 4, 5) AS [FormattedTime],
			m.[name] as Meet, m.[year] AS [Year], m.[year] - a.grad_year + 13 AS [Grade],
			[dbo].get_splits(rs.result_id) as Splits,
			rs.result_id, r.race_id, rl.race_level_id, a.athlete_id, m.meet_id, c.course_id, rs.[time], r.distance,
			r.distance_unit
		FROM Result rs
		JOIN Race r ON rs.race_id = r.race_id
		JOIN Athlete a ON rs.athlete_id = a.athlete_id
		JOIN Meet m ON r.meet_id = m.meet_id
		JOIN Course c ON c.course_id = m.course_id
		JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id

GO

CREATE VIEW ThreeMileResults AS
SELECT a.first_name AS [First Name], a.last_name AS [Last Name], 
	SUBSTRING(CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108), 4, 5) AS [Time],
	c.[name] AS Course, m.[name] AS Meet, m.[year] as [Year], rl.[name] AS [Race Level],
	m.[year] - a.grad_year + 13 AS [Grade]
FROM Result rs
JOIN Athlete a ON rs.athlete_id = a.athlete_id
JOIN Race r ON rs.race_id = r.race_id
JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id
JOIN Meet m ON r.meet_id = m.meet_id
JOIN Course c ON m.course_id = c.course_id
WHERE r.distance = 3 AND r.distance_unit = 'mi'

GO

CREATE VIEW result_splits AS
SELECT [ResultSplit].result_id, STRING_AGG(Concat(time, '/', distance,distance_unit),' | ') as splits
FROM [ResultSplit]
GROUP BY result_id;

GO

CREATE VIEW Performances 
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

GO

-----------------------------------
-------------PHASE 5---------------
-----------------------------------
--+functions

CREATE FUNCTION func_athlete_pbs (@AthleteID int
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
Go

CREATE FUNCTION predict_mile_sec (
	@Time int,
	@Dist float,
	@Unit varchar(8) = 'mi'
)
RETURNS int 
AS  
BEGIN
	Return (
		@Time * POWER(1/
		(CASE
			WHEN @Unit = 'mi' THEN @Dist
			WHEN @Unit = 'km' THEN @Dist / 1.60934
		END), 1.06)
	)
END
GO

-----------------------------------
-------------PHASE 6---------------
-----------------------------------
--+Stored Procedures

CREATE PROCEDURE get_athletes_not_in_race(@RaceId int)
AS
BEGIN
	IF (@RaceId is null) BEGIN
		PRINT('Race ID cannot be null')
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		PRINT('Race does not exist')
		RETURN 2
	END

	-- Gives athletes who were in school during the year of this meet
	-- Also matches athlete gender to the race
	DECLARE @gender char(1)
	DECLARE @race_year int
	SELECT @gender = Race.gender, @race_year = Meet.[year]
		FROM Race
		JOIN Meet ON Race.meet_id = Meet.meet_id
		WHERE Race.race_id = @RaceId

	-- Get all athletes except for those with results in this race
	(SELECT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender
		FROM Athlete a
		WHERE a.gender = @gender AND @race_year BETWEEN (a.grad_year - 4) AND a.grad_year)
	EXCEPT
	(SELECT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender
		FROM Athlete a
		JOIN Result rs ON rs.athlete_id = a.athlete_id
		JOIN Race r ON rs.race_id = r.race_id
		WHERE r.race_id = @RaceId)
	ORDER BY a.last_name, a.first_name
END
GO
--==--
CREATE PROCEDURE [dbo].[athlete_race_results] 
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
JOIN [Meet] as meet
ON race.meet_id = meet.meet_id
JOIN [Course] as course
ON meet.course_id = course.course_id
JOIN [result_splits] as rs
ON result.result_id = rs.result_id
WHERE @athlete_id = [Result].athlete_id AND (@race_id = -1 OR @race_id = [Result].race_id)
GO
--==--
CREATE Procedure [dbo].[delete_athlete]
(@AthleteID int
)
As
BEGIN

if(@AthleteID is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [athlete_id]
From Athlete
Where [athlete_id] = @AthleteID) is NULL)
begin
	Raiserror('athlete is not in table',14,1)
	Return(2)
end

Delete From Athlete
Where [athlete_id] = @AthleteID

Return(0)

END
GO
--==--
CREATE Procedure [dbo].[delete_meet]
(@id int)
AS
BEGIN
if(@id is NULL)
begin
	raiserror('ID can not be null', 14,1)
	return(1)
end

If((Select [name]
From Meet
Where meet_id = @id) is NULL)
begin
	Raiserror('Meet is not in table',14,1)
	Return(2)
end

BEGIN TRANSACTION

-- Must delete all splits, results, and races associated with this meet
DELETE rspl
	FROM ResultSplit rspl
	JOIN Result rs on rspl.result_id = rs.result_id
	JOIN Race r ON rs.race_id = r.race_id
	WHERE r.meet_id = @id

DELETE rs
	FROM Result rs
	JOIN Race r ON rs.race_id = r.race_id
	WHERE r.meet_id = @id

DELETE r
	FROM Race r
	WHERE r.meet_id = @id

Delete From Meet
Where meet_id = @id

COMMIT TRANSACTION

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[delete_race]
(@RaceID int
)
As
BEGIN

if(@RaceID is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [race_id]
From Race
Where [race_id] = @RaceID) is NULL)
begin
	Raiserror('Race is not in table',14,1)
	Return(2)
end

BEGIN TRANSACTION

DELETE rsp
	FROM ResultSplit rsp
	JOIN Result rs ON rsp.result_id = rs.result_id
	WHERE rs.race_id = @RaceID

DELETE rs
	FROM Result rs
	WHERE rs.race_id = @RaceID

Delete From Race
Where race_id = @RaceID

COMMIT TRANSACTION

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[delete_result]
(@ResultID int
)
As
BEGIN

if(@ResultID is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [result_id]
From Result
Where [result_id] = @ResultID) is NULL)
begin
	Raiserror('result is not in table',14,1)
	Return(2)
end

Delete From Result
Where [result_id] = @ResultID

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[delete_resultSplit]
(@resultSplit int
)
As
BEGIN

if(@resultSplit is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [result_split_id]
From ResultSplit
Where [result_split_id] = @resultSplit) is NULL)
begin
	Raiserror('resultSplit is not in table',14,1)
	Return(2)
end

Delete From ResultSplit
Where [result_split_id] = @resultSplit

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[get_athlete_pbs] (
@AthleteID int
)
AS

IF(@AthleteID is NULL)
BEGIN
	Raiserror('AthleteID cannot be null', 14,1)
	Return(1)
END

IF((Select [athlete_id]
FROM Athlete
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	Raiserror('athlete is not in table',14,1)
	Return(2)
END

IF((Select Top 1 [athlete_id]
FROM Performances
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	select TOP 0 *
	From Performances--athlete hasn't run any races
	Return(0)
END

Select *
From [dbo].[func_athlete_pbs](@AthleteID)
Order By (Case 
			When distance_unit = 'mi' THEN distance
			When distance_unit = 'km' THEN distance / 1.60934
        End)
Return(0)
GO
--==--

Create PROCEDURE [dbo].[get_athlete_performances] 
(@athlete_id int,
@distance int = -1,
@distance_unit varchar(10) = 'mi')
AS
SELECT *
FROM Performances
WHERE @athlete_id = athlete_id  AND (@distance = -1 OR (@distance = distance AND @distance_unit = distance_unit))

GO
--==--

CREATE Procedure [dbo].[get_athlete_progress] (
@AthleteID int
)
AS

IF(@AthleteID is NULL)
BEGIN
	Raiserror('AthleteID cannot be null', 14,1)
	Return(1)
END

IF((Select [athlete_id]
FROM Athlete
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	Raiserror('athlete is not in table',14,1)
	Return(2)
END

IF((Select Top 1 [athlete_id]
FROM Performances
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	select TOP 0 *
	From Performances--athlete hasn't run any races
	Return(0)
END

SELECT [year], CONVERT(varchar, DATEADD(ms, MIN([dbo].[predict_mile_sec](DATEDIFF(SECOND, '1/1/1900', [best_time]),[distance],[distance_unit])) * 1000, 0), 108) AS best_predicted_mile
INTO #mytemp
FROM [dbo].[func_athlete_pbs](@AthleteID) 
GROUP BY [year]

SELECT [year], mt1.[best_predicted_mile], 
	CONVERT(varchar, (100*(Select top 1(DATEDIFF(SECOND, '1/1/1900', mt2.[best_predicted_mile]))
		from #mytemp as mt2
		where mt2.[year]+1 = mt1.[year]) / DATEDIFF(SECOND, '1/1/1900', mt1.[best_predicted_mile]))-100) + '%'
	 as performance_delta
FROM #mytemp as mt1
ORDER BY [year] DESC;


Return(0)

GO
--==--

CREATE PROCEDURE get_dists_for_course(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Id cannot be null', 14, 1)
		RETURN 1
	END
	IF (NOT EXISTS (SELECT * FROM Course WHERE course_id = @id)) BEGIN
		RAISERROR('Course does not exist', 14, 2)
		RETURN 2
	END

	SELECT R.distance, R.distance_unit
		FROM Race R
		JOIN Meet M on R.meet_id = M.meet_id
		JOIN Course C on M.course_id = C.course_id
		WHERE C.course_id = @id
		GROUP BY R.distance, R.distance_unit
END
GO
--==--

CREATE PROCEDURE get_racelevels_for_course(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Course id cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Course WHERE course_id = @id) < 1) BEGIN
		RAISERROR('Course does not exist', 14, 1)
		RETURN 2
	END

	SELECT rl.race_level_id, rl.[name]
		FROM RaceLevel rl
		JOIN Race r ON r.race_level_id = rl.race_level_id
		JOIN Meet m ON r.meet_id = m.meet_id
		JOIN Course c ON m.course_id = c.course_id
		WHERE c.course_id = @id
		GROUP BY rl.race_level_id, rl.[name]
END
GO
--==--

CREATE PROCEDURE get_race_levels
AS
	SELECT race_level_id, [name]
	FROM RaceLevel
GO
--==--

CREATE PROCEDURE get_races_for_meet(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Meet id cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Meet WHERE meet_id = @id) < 1) BEGIN
		RAISERROR('Meet does not exist', 14, 1)
		RETURN 2
	END

	SELECT r.race_id, r.distance, r.distance_unit, r.race_level_id, rl.[name], r.meet_id, r.gender
		FROM Race r
		JOIN RaceLevel rl ON r.race_level_id = rl.race_level_id
		JOIN Meet m ON r.meet_id = m.meet_id
		WHERE m.meet_id = @id
END
GO
--==--

CREATE PROCEDURE get_results_for_race(@RaceId int)
AS
BEGIN
	IF (@RaceId is null) BEGIN
		RAISERROR('race Id cannot be null', 14, 1)
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		RAISERROR('Race does not exist', 14, 1)
		RETURN 2
	END

	SELECT result_id, [Athlete], [FormattedTime], [Grade], [Splits]
		FROM all_times_view
		WHERE race_id = @RaceId
		ORDER BY [time] ASC

END
GO
--==--

CREATE PROCEDURE get_unused_levels_for_meet(@MeetId int)
AS
BEGIN
	IF (@MeetId is null) BEGIN
		RAISERROR('Meet ID cannot be null', 14, 1)
		RETURN 1
	END
	IF ((SELECT COUNT(*) FROM Meet WHERE meet_id = @MeetId) < 1) BEGIN
		RAISERROR('Meet does not exist', 14, 1)
		RETURN 2
	END

	SELECT rl.race_level_id, rl.[name]
	FROM RaceLevel rl
	WHERE NOT EXISTS (SELECT * FROM Race
						WHERE meet_id = @MeetId AND race_level_id = rl.race_level_id)
END
GO
--==--

CREATE PROCEDURE insert_full_line(@first_name nvarchar(200), @last_name nvarchar(200), @gender char(1), -- 3
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
GO
--==--

Create Procedure [dbo].[update_course]
(@CourseID int,
@CourseName varchar(100)
)
As
BEGIN
	IF(@CourseID is NULL)
	begin
		Raiserror('course_id can not be null',14,1)
		return(1)
	end

	IF(@CourseName is NULL)
	begin
		Raiserror('name can not be null',14,1)
		return(2)
	end

	If((Select [course_id]
	From Course
	Where [course_id] = @CourseID) is NULL)
	begin
		raiserror('id is not in table', 14, 1)
		return(3)
	end

	Declare @oldCourseName varchar(100)
	Set @oldCourseName = (Select Course.[name]
	From Course
	Where Course.[name] = @CourseName)


	IF(@CourseName = @oldCourseName)
	begin
		raiserror('course is already in the table',14,1)
		return(4)
	end


	Update Course 
	Set [name] = @CourseName
	Where [course_id] = @CourseID

	Return(0)
END
GO
--==--
CREATE Procedure [dbo].[delete_course](@id int)
as
BEGIN

	if(@id is NULL)
	begin
		raiserror('Id can not be null', 14,1)
		return(1)
	end




	If((SELECT COUNT(*) FROM Course WHERE course_id = @id) <= 0)
	begin
		Raiserror('Course is not in table',14,1)
		Return(2)
	end

	-- Need to delete everything
	DELETE rsp
	FROM ResultSplit rsp
	JOIN Result rs ON rsp.result_id = rs.result_id
	JOIN Race r ON rs.race_id = r.race_id
	JOIN Meet m ON r.meet_id = m.meet_id AND m.course_id = @id

	DELETE rs
	FROM Result rs
	JOIN Race r ON rs.race_id = r.race_id
	JOIN Meet m ON r.meet_id = m.meet_id AND m.course_id = @id

	DELETE r
	FROM Race r
	JOIN Meet m ON r.meet_id = m.meet_id AND m.course_id = @id

	DELETE FROM Meet
	WHERE course_id = @id

	Delete From Course
	Where course_id = @id
	Return(0)

END
GO
--==--

CREATE PROCEDURE insert_result(@RaceId int, @Time float, @AthleteId int, @ResultId int OUTPUT)
AS
BEGIN
	IF (@RaceId is null OR @Time is null OR @AthleteId is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		PRINT('Race does not exist')
		RETURN 2
	END

	IF ((SELECT COUNT(*) FROM Athlete WHERE athlete_id = @AthleteId) < 1) BEGIN
		PRINT('Athlete does not exist')
		RETURN 3
	END

	IF (@Time <= 0) BEGIN
		PRINT('Invalid time; must be > 0')
		RETURN 4
	END

	INSERT INTO Result([time], athlete_id, race_id)
		VALUES(@Time, @AthleteId, @RaceId)

	SELECT @ResultId = @@IDENTITY
END
GO

--==--

CREATE Procedure [dbo].[update_meet]
(@MeetID int,
@MeetName varchar(100),
@MeetYear int = NULL,
@CourseId int
)
As
BEGIN
IF(@MeetID is NULL)
begin
	Raiserror('meet_id can not be null',14,1)
	return(1)
end

IF(@MeetName is NULL)
begin
	Raiserror('name can not be null',14,1)
	return(2)
end

if (@CourseId is null)
begin
	raiserror('course ID cannot be null', 14, 2)
	return 4
end

If((Select meet_id
From Meet
Where meet_id = @MeetID) is NULL)
begin
	raiserror('meet_id is not in table', 14, 1)
	return(3)
end

if ((SELECT COUNT(*) FROM Course WHERE course_id = @CourseId) <= 0)
begin
	raiserror('course does not exist', 14, 4)
	return 5
end

/*Declare @OldMeetName varchar(100)
Set @OldMeetName = (Select [name]
From Meet
Where [name] = @MeetName)

IF(@MeetName = @OldMeetName)
begin
	raiserror('meet is already in the table',14,1)
	return(4)
end*/

Update Meet
Set [name] = @MeetName, [year] = @MeetYear, course_id = @CourseId
Where meet_id = @MeetID

Return(0)

END
GO

--==--

CREATE PROCEDURE insert_split(@ResultId int, @Index int, @Time float, @Distance float, @Unit varchar(5))
AS
BEGIN
	IF (@ResultId is null OR @Index is null OR @Time is null OR @Distance is null OR @Unit is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF (@Index < 0) BEGIN
		PRINT('Invalid index; must be >= 0')
		RETURN 2
	END

	IF (@Distance <= 0) BEGIN
		PRINT('Invalid distance; must be > 0')
		RETURN 3
	END

	IF (not @Unit = 'mi' and not @Unit = 'km' and not @Unit = 'm') BEGIN
		PRINT('Invalid unit; must be mi/km/m')
		RETURN 4
	END

	IF ((SELECT COUNT(*) FROM Result WHERE result_id = @ResultId) < 1) BEGIN
		PRINT('Result does not exist')
		RETURN 5
	END

	INSERT INTO ResultSplit(result_id, [index], [time], distance, distance_unit)
		VALUES(@ResultId, @Index, @Time, @Distance, @Unit)
END
GO
--==--

CREATE PROCEDURE predict_athlete_times (
    @AthleteID int,
    @Distance float,
	@DistanceUnit varchar(8) ='mi',
	@Pred_Time varchar(8)  = 'None' OUTPUT
) AS
--Avg Top 3 times


--Start Validating Inputs--
IF(@AthleteID is NULL)
BEGIN
	Raiserror('AthleteID cannot be null', 14,1)
	Return(1)
END
IF((Select [athlete_id]
FROM Athlete
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	Raiserror('athlete is not in table',14,1)
	Return(2)
END
IF((Select Top 1 [athlete_id]
FROM Performances
WHERE [athlete_id] = @AthleteID) is NULL)
BEGIN
	Raiserror('athlete has not ran any races',14,1)
	Return(3)
END
IF(@Distance !>0)
BEGIN
	Raiserror('Distance must be > 0', 14,1)
	Return(4)
END
IF(@DistanceUnit != 'mi' and @DistanceUnit != 'km')
BEGIN
	Raiserror('Invalid DistanceUnit', 14,1)
	Return(4)
END

--End Validating Inputs--
Print 'Predicting the time for AthleteID=' + CAST(@AthleteID AS VARCHAR)+ ' running '  + CAST(@Distance AS VARCHAR) + CAST(@DistanceUnit AS VARCHAR)
Declare @LastTime varchar(8)
Declare @LastDist float = -1;
Declare @LastUnit varchar(8)

Declare @LastSec int
Declare @best_predict_mile_sec int
--Get the best converted to mile time from the athletes last 3 runs

Select @best_predict_mile_sec = Min([dbo].[predict_mile_sec]([time_sec],distance,distance_unit))
From	(Select Top 3 DATEDIFF(SECOND, '1/1/1900', [Performances].[time]) as [time_sec],[distance] as distance,[distance_unit] as distance_unit
		From [Performances]
		Where @AthleteID = [Performances].[athlete_id]) as top3
Print 'Calculating with time=' + CAST(@LastTime AS VARCHAR)+ ' distance= '  + CAST(@LastDist AS VARCHAR) + CAST(@LastUnit AS VARCHAR)

--Convert the time above into the input specified distance (account for units also)
Declare @Pred_LastSec int 
Set @Pred_LastSec = @best_predict_mile_sec * POWER(
(CASE
    WHEN @DistanceUnit = 'mi' THEN @Distance
    WHEN @DistanceUnit = 'km' THEN @Distance / 1.60934
END)/
1, 1.06)
--Print 'Pred_LastSec=' + CAST(@Pred_LastSec AS VARCHAR)
Set @Pred_Time = CONVERT(varchar, DATEADD(ms, @Pred_LastSec * 1000, 0), 108)

Return 0
GO
--==--

CREATE PROCEDURE get_roster(@Year int, @Gender char)
AS
BEGIN
	if (@Year is null OR @Gender is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END
	if (@Gender not like '[MFO]') BEGIN
		PRINT('Gender must be M/F/O')
		RETURN 2
	END

	-- Returns all athletes with a result for this given year
	SELECT DISTINCT a.athlete_id, a.first_name, a.last_name, a.grad_year, a.gender, @Year - a.grad_year + 13 AS Grade
	FROM Athlete a
	JOIN Result rs ON rs.athlete_id = a.athlete_id
	JOIN Race r ON rs.race_id = r.race_id
	JOIN Meet m ON r.meet_id = m.meet_id AND m.[year] = @Year
	WHERE a.gender = @Gender
END
GO
--==--

CREATE PROCEDURE perform_search(@Query nvarchar(200))
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
GO
--==--

CREATE PROCEDURE view_all_results
AS
BEGIN
	SELECT * FROM ThreeMileResults
	ORDER BY [Time] ASC
END
GO
--==--

CREATE PROCEDURE get_athlete(@id int)
AS
	SELECT athlete_id, first_name, last_name, grad_year, gender
	FROM Athlete
	WHERE athlete_id = @id
GO
--==--

CREATE PROCEDURE get_course(@id int)
AS
	SELECT [course_id], [name]
	FROM Course
	WHERE course_id = @id
GO
--==--

CREATE PROCEDURE get_courses(@page int, @page_size int)
AS
	SELECT course_id, [name]
	FROM Course
	ORDER BY [name] ASC
	OFFSET(@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_course_count
AS
	SELECT COUNT(*) FROM Course
GO

CREATE PROCEDURE get_all_courses
AS
	SELECT course_id, [name]
	FROM Course
	ORDER BY [name] ASC
GO
--==--

CREATE PROCEDURE get_meets(@page int, @page_size int)
AS
	SELECT m.meet_id, m.[name], m.[year], m.course_id, c.[name]
	FROM Meet m
	JOIN Course c ON m.course_id = c.course_id
	ORDER BY [year] DESC, m.[name]
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_meet_count
AS
	SELECT COUNT(*) FROM Meet
GO

CREATE PROCEDURE get_meet(@id int)
AS
	SELECT m.meet_id, m.[name], m.[year], m.course_id, c.[name]
	FROM Meet m
	JOIN Course c ON c.course_id = m.course_id
	WHERE m.meet_id = @id
GO
--==--

CREATE PROCEDURE get_roster_years
AS
BEGIN
	SELECT DISTINCT [year] FROM Meet
	ORDER BY [year]
END
GO
--==--

CREATE PROCEDURE get_athletes(@page int, @page_size int)
AS
	SELECT athlete_id, first_name, last_name, grad_year, gender
	FROM Athlete
	ORDER BY last_name ASC
	OFFSET (@page * @page_size) ROWS FETCH NEXT @page_size ROWS ONLY
GO

CREATE PROCEDURE get_athlete_count
AS
	SELECT COUNT(*) FROM Athlete
GO
--==--

CREATE PROCEDURE insert_athlete(@first_name nvarchar(200), @last_name nvarchar(200), @grad_year int, @gender char, 
	@id int = null OUTPUT)
AS
BEGIN
	IF (@first_name is null OR @last_name is null OR @grad_year is null OR @gender is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF (@gender not like '[MFO]') BEGIN
		PRINT('Invalid gender')
		RETURN 2
	END

	IF (@grad_year < 1900) BEGIN
		PRINT('Grad year must be greater than 1900')
		RETURN 3
	END

	INSERT INTO Athlete(first_name, last_name, grad_year, gender)
		VALUES(@first_name, @last_name, @grad_year, @gender)

	SELECT @id = @@IDENTITY
END
GO
--==--

CREATE Procedure [dbo].[insert_course]
(@CourseName varchar(100), @id int = null OUTPUT)
as
BEGIN

if(@CourseName is NULL)
begin
	raiserror('name can not be null', 14,1)
	return(1)
end

If((Select [name]
From Course
Where [name] = @CourseName) is NOT  NULL)
begin
	Raiserror('name is already in table - insert course',14,1)
	Return(2)
end

Insert Into Course
Values(@CourseName)

SELECT @id = @@IDENTITY

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[insert_meet]
(@MeetName varchar(100),
@MeetYear int,
@CourseId int,
@id int = null OUTPUT
)
AS
BEGIN
if(@MeetName is NULL)
begin
	raiserror('name can not be null', 14,1)
	return(1)
end

if (@CourseId is null)
begin
	raiserror('course id cannot be null', 14, 2)
	return 3
end

If((Select COUNT(*) From Meet
	Where [name] = @MeetName AND [year] = @MeetYear) > 0)
begin
	Raiserror('name+year is already in table - insert meet',14,1)
	Return(2)
end

if ((select count(*) from Course where Course.course_id = @CourseId) <= 0)
begin
	raiserror('course does not exist', 14, 3)
	return 4
end

Insert Into Meet
Values(@MeetName, @MeetYear, @CourseId)

SELECT @id = @@IDENTITY

Return(0)

END
GO
--==--

CREATE Procedure [dbo].[insert_race]
(@RaceDistance float,
@RaceUnit varchar(2),
@RaceLevelID int,
@MeetID int,
@Gender char(1),
@id int = null OUTPUT
)
AS
BEGIN

if(@RaceDistance is NULL)
begin
	print('distance can not be null')
	return(1)
end

if(@RaceUnit is NULL)
begin
	print('unit can not be null')
	return(2)
end

if(@RaceLevelID is NULL)
begin
	print('race_level_id can not be null')
	return(3)
end

if(@MeetID is NULL)
begin
	print('meet_id can not be null')
	return(5)
end

if (@Gender is null) begin
	print('Gender cannot be null')
	return 12
end

if (@Gender not like '[MFO]') begin
	print('Gender must be M/F/O')
	Return 13
end

IF(@RaceDistance < 0)
begin
	print('invalid input for distance')
	return(6)
end

IF(@RaceUnit != 'm' AND @RaceUnit != 'km' AND @RaceUnit != 'mi' )
begin
	print('invalid input for unit')
	return(7)
end

If((Select [race_level_id]
From RaceLevel
Where [race_level_id] = @RaceLevelID) is NULL)
begin
	Raiserror('race_level does not exist',14,1)
	Return(8)
end

If((Select [meet_id]
From Meet
Where [meet_id] = @MeetID) is NULL)
begin
	Print('meet does not exist')
	Return(10)
end

If((Select race_id
From Race
Where race_level_id = @RaceLevelID AND meet_id = @MeetID AND gender = @Gender)
is not NULL)
begin
	Print('race already exists')
	return(11)
end

Insert Into Race(distance, distance_unit, race_level_id, meet_id, gender)
Values(@RaceDistance,@RaceUnit,@RaceLevelID,@MeetID,@Gender)

SELECT @id = @@IDENTITY

Return(0)

END
GO
--==--

/*CREATE PROCEDURE [dbo].[insert_result](@RaceId int, @Time float, @AthleteId int, @ResultId int = null OUTPUT)
AS
BEGIN
	IF (@RaceId is null OR @Time is null OR @AthleteId is null) BEGIN
		PRINT('Args cannot be null')
		RETURN 1
	END

	IF ((SELECT COUNT(*) FROM Race WHERE race_id = @RaceId) < 1) BEGIN
		PRINT('Race does not exist')
		RETURN 2
	END

	IF ((SELECT COUNT(*) FROM Athlete WHERE athlete_id = @AthleteId) < 1) BEGIN
		PRINT('Athlete does not exist')
		RETURN 3
	END

	IF (@Time <= 0) BEGIN
		PRINT('Invalid time; must be > 0')
		RETURN 4
	END

	INSERT INTO Result([time], athlete_id, race_id)
		VALUES(@Time, @AthleteId, @RaceId)

	SELECT @ResultId = @@IDENTITY
END
GO*/
--==--

CREATE PROCEDURE log_in(@email varchar(100), @password_hash varchar(50), @session_id uniqueidentifier OUTPUT)
AS
BEGIN
	IF (@email is null OR @password_hash is null) BEGIN
		RAISERROR('Email and password cannot be null', 14, 1)
		-- Return 1 to indicate failure for all errors so client cannot differentiate
		RETURN 1
	END

	IF (NOT EXISTS (SELECT * FROM [Login] WHERE email = @email)) BEGIN
		RAISERROR('User does not exist', 14, 2)
		RETURN 1
	END

	IF (NOT EXISTS (SELECT * FROM [Login] WHERE email = @email AND password_hash = @password_hash)) BEGIN
		RAISERROR('Incorrect password provided', 14, 3)
		RETURN 1
	END

	-- User is allowed to log in. We'll create a session and send the session token
	DECLARE @new_id uniqueidentifier
	EXEC add_session @email = @email, @new_id = @new_id OUTPUT
	SET @session_id = @new_id

	UPDATE [Login]
		SET last_login = GETDATE()
		WHERE email = @email

END
GO

CREATE PROCEDURE add_session(@email varchar(100), @new_id uniqueidentifier OUTPUT)
AS
BEGIN
	DECLARE @result uniqueidentifier
	SET @result = NEWID()
	INSERT INTO [Session](session_id, expires_at, user_email)
		VALUES(@result, GETDATE()+7, @email)
	SET @new_id = @result
END
GO

CREATE PROCEDURE get_salt(@email varchar(100), @salt varchar(50) OUTPUT)
AS
BEGIN
	SELECT @salt = password_salt FROM [Login] WHERE email = @email
END
GO
--==--

CREATE PROCEDURE log_out(@session_id uniqueidentifier)
AS
BEGIN
	DELETE FROM [Session]
		WHERE session_id = @session_id
END
GO
--==--

CREATE PROCEDURE register(@email varchar(100), @pass_hash varchar(50), @pass_salt varchar(50))
AS
BEGIN
	-- Again, we always return 1 so that the client cannot differentiate between errors at all
	IF (@email IS NULL OR @email = '') BEGIN
		RAISERROR('Email cannot be null/empty', 14, 1)
		RETURN 1
	END

	IF (@pass_hash IS NULL OR @pass_hash = '') BEGIN
		RAISERROR('Password hash cannot be null/empty', 14, 2)
		RETURN 1
	END

	IF (@pass_salt IS NULL OR @pass_salt = '') BEGIN
		RAISERROR('Password salt cannot be null/empty', 14, 3)
		RETURN 1
	END

	-- Check if user exists
	IF (EXISTS (SELECT * FROM [Login] WHERE email = @email)) BEGIN
		RAISERROR('User already exists', 14, 4)
		RETURN 1
	END

	INSERT INTO [Login](email, password_hash, password_salt)
		VALUES(@email, @pass_hash, @pass_salt)
END
GO
--==--

CREATE PROCEDURE log_in_session(@session_id uniqueidentifier, @can_log_in bit OUTPUT)
AS
BEGIN
	DECLARE @canLogin bit
	SELECT @canLogin = dbo.can_log_in(@session_id)
	IF (@canLogin = 1) BEGIN
		SET @can_log_in = 1
		RETURN 0
	END

	PRINT('Cannot log in with this session ID')
	SET @can_log_in = 0
	RETURN 1

END
GO

CREATE FUNCTION can_log_in(@session_id uniqueidentifier)
RETURNS bit
AS
BEGIN
	IF ((SELECT COUNT(*) FROM [Session] WHERE session_id = @session_id) <> 1) BEGIN
		-- Email and session do not exist
		RETURN 0
	END

	DECLARE @expire_date datetime
	SELECT @expire_date = expires_at FROM [Session] WHERE session_id = @session_id
	IF (GETDATE() >= @expire_date) BEGIN
		-- Session has expired
		RETURN 0
	END

	RETURN 1
END
GO
--==--

CREATE PROCEDURE update_athlete(@id int, @first_name nvarchar(200), @last_name nvarchar(200),
	@grad_year int, @gender char(1))
AS
BEGIN
	IF (@id is null) BEGIN
		PRINT('Athlete ID must not be null')
		RETURN 1
	END
	IF (NOT EXISTS (SELECT * FROM Athlete WHERE athlete_id = @id)) BEGIN
		PRINT('Athlete does not exist')
		RETURN 2
	END

	IF (@gender is not null AND @gender not like '[MFO]') BEGIN
		PRINT('Invalid gender')
		RETURN 3
	END

	-- If any fields are null, then we'll just not modify them
	BEGIN TRANSACTION
		IF (@first_name is not null) BEGIN	
			UPDATE Athlete SET first_name = @first_name WHERE athlete_id = @id
		END
		IF (@last_name is not null) BEGIN	
			UPDATE Athlete SET last_name = @last_name WHERE athlete_id = @id
		END
		IF (@grad_year is not null) BEGIN	
			UPDATE Athlete SET grad_year = @grad_year WHERE athlete_id = @id
		END
		IF (@gender is not null) BEGIN	
			UPDATE Athlete SET gender = @gender WHERE athlete_id = @id
		END
	COMMIT TRANSACTION
END
GO
--==--

CREATE PROCEDURE top_results_by_course
	(@CourseID int, @NumResults int, @AllowDupAthletes bit, @RaceLevelId int, @Distance float, @DistanceUnit varchar(5))
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
		SELECT TOP(@NumResults) v1.Athlete, v1.[FormattedTime], v1.[Meet], v1.[Year], v1.[Grade], v1.Splits
			FROM all_times_view v1
			WHERE v1.course_id = @CourseID AND (race_level_id = @RaceLevelId OR @RaceLevelId < 0) AND
				v1.distance = @Distance AND v1.distance_unit = @DistanceUnit
			ORDER BY v1.[time] ASC
	ELSE
		SELECT TOP(@NumResults) v1.Athlete, v1.[FormattedTime], v1.[Meet], v1.[Year], v1.[Grade], v1.Splits
			FROM all_times_view v1
			JOIN (SELECT athlete_id, MIN([time]) as min_time
					FROM all_times_view
					WHERE course_id = @CourseID AND (race_level_id = @RaceLevelId OR @RaceLevelId < 0) AND
						distance = @Distance AND distance_unit = @DistanceUnit
					GROUP BY athlete_id
					) v2 ON v1.athlete_id = v2.athlete_id AND v1.[time] = v2.[min_time]
			ORDER BY v1.[time] ASC
END
GO
--==--

-----------------------------------
-------------PHASE 7---------------
-----------------------------------
--+Permissions

-- Add user scripts, call one at a time
CREATE USER calesnm FROM LOGIN calesnm
EXEC sp_addrolemember 'db_owner', calesnm
GO

CREATE USER demakijp FROM LOGIN demakijp
EXEC sp_addrolemember 'db_owner', demakijp
GO


GRANT EXECUTE ON view_all_results TO $(user_name)
GRANT EXECUTE ON get_salt TO $(user_name)
GRANT EXECUTE ON add_session TO $(user_name)
GRANT EXECUTE ON log_in TO $(user_name)
GRANT EXECUTE ON register TO $(user_name)
GRANT EXECUTE ON log_in_session TO $(user_name)
GRANT EXECUTE ON log_out TO $(user_name)
GRANT EXECUTE ON get_athletes TO $(user_name)
GRANT EXECUTE ON get_athlete_count TO $(user_name)
GRANT EXECUTE ON insert_athlete TO $(user_name)
GRANT EXECUTE ON get_athlete TO $(user_name)
GRANT EXECUTE ON update_athlete TO $(user_name)
GRANT EXECUTE ON delete_athlete TO $(user_name)
GRANT EXECUTE ON get_courses TO $(user_name)
GRANT EXECUTE ON get_course_count TO $(user_name)
GRANT EXECUTE ON get_meets TO $(user_name)
GRANT EXECUTE ON get_meet_count TO $(user_name)
GRANT EXECUTE ON insert_course TO $(user_name)
GRANT EXECUTE ON update_course TO $(user_name)
GRANT EXECUTE ON delete_course TO $(user_name)
GRANT EXECUTE ON get_course TO $(user_name)
GRANT EXECUTE ON delete_meet TO $(user_name)
GRANT EXECUTE ON get_meet TO $(user_name)
GRANT EXECUTE ON update_meet TO $(user_name)
GRANT EXECUTE ON insert_meet TO $(user_name)
GRANT EXECUTE ON get_all_courses TO $(user_name)
GRANT EXECUTE ON get_race_levels TO $(user_name)
GRANT EXECUTE ON get_dists_for_course TO $(user_name)
GRANT EXECUTE ON get_racelevels_for_course TO $(user_name)
GRANT EXECUTE ON top_results_by_course TO $(user_name)
GRANT EXECUTE ON get_splits TO $(user_name)
GRANT EXECUTE ON get_races_for_meet TO $(user_name)
GRANT EXECUTE ON get_results_for_race TO $(user_name)
GRANT EXECUTE ON get_unused_levels_for_meet TO $(user_name)
GRANT EXECUTE ON insert_race TO $(user_name)
GRANT EXECUTE ON delete_race TO $(user_name)
GRANT EXECUTE ON get_athletes_not_in_race TO $(user_name)
GRANT EXECUTE ON insert_result TO $(user_name)
GRANT EXECUTE ON insert_split TO $(user_name)
GRANT EXECUTE ON delete_result TO $(user_name)
GRANT EXECUTE ON perform_search TO $(user_name)
GRANT EXECUTE ON get_roster TO $(user_name)
GRANT EXECUTE ON get_roster_years TO $(user_name)
GRANT EXECUTE ON insert_full_line TO $(user_name)
Go