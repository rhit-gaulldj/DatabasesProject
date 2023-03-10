USE [TeamXCDB]
GO
ALTER Procedure [dbo].[insert_race]
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







