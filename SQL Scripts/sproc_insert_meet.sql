USE [TeamXCDB]
GO
ALTER Procedure [dbo].[insert_meet]
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