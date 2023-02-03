USE TeamXCDB
GO

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