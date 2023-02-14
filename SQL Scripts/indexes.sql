USE TeamXCDB
GO

CREATE INDEX AthleteName ON Athlete(last_name);

CREATE INDEX CourseName ON Course([name]);

CREATE INDEX MeetYear ON Meet([year]);
CREATE INDEX MeetName ON Meet([name]);

CREATE INDEX ResultTime ON Result([time]);