USE TeamXCDB
GO

-- "Secondary" tables (i.e. tables w/ foreign keys):
--		Race, Result, ResultSplit

CREATE TABLE Race(
	race_id int IDENTITY(1,1) PRIMARY KEY,
	distance float NOT NULL,
	distance_unit varchar(2) NOT NULL,
	race_level_id int NOT NULL,
	meet_id int NOT NULL

	FOREIGN KEY(race_level_id) REFERENCES RaceLevel(race_level_id),
	FOREIGN KEY(meet_id) REFERENCES Meet(meet_id),
	CHECK(distance_unit = 'km' OR distance_unit = 'mi' OR distance_unit = 'm')
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