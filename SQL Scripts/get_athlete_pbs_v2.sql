USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[get_athlete_pbs]    Script Date: 2/3/2023 6:48:01 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
Alter Procedure [dbo].[get_athlete_pbs] (
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




