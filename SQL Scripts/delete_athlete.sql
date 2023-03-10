USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_athlete]    Script Date: 1/19/2023 11:15:10 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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

