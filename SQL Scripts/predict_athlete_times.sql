Alter PROCEDURE predict_athlete_times (
    @AthleteID int,
    @Distance float,
	@DistanceUnit varchar(8) ='mi',
	@Pred_Time varchar(8)  = 'None' OUTPUT
) AS
--Avg Top 3 times
--Last time
--Weighted Season Times, 
--DATEDIFF(second,0,cast(@OptTime as datetime))
--DATEPART(SECOND, @OptTime) + 60 * DATEPART(MINUTE, @OptTime)  + 3600 * DATEPART(HOUR, @OptTime)
--Sum(Left(WorkHrs,2) * 3600 + substring(WorkHrs, 4,2) * 60 + substring(WorkHrs, 7,2))
--CONVERT(varchar, DATEADD(ms, <PUT TIME HERE> * 1000, 0), 108)

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
		