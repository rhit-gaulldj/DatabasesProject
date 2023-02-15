USE [TeamXCDB]
GO

ALTER Procedure [dbo].[delete_athlete]
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

DELETE rsp
	FROM ResultSplit rsp
	JOIN Result rs ON rs.result_id = rsp.result_id
	WHERE rs.athlete_id = @AthleteID

DELETE FROM Result
WHERE [athlete_id] = @AthleteID

Delete From Athlete
Where [athlete_id] = @AthleteID

Return(0)

END

