USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[get_athlete_pbs]    Script Date: 2/3/2023 6:48:01 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
Alter Procedure [dbo].[get_athlete_progress] (
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


