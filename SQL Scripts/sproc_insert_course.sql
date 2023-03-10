USE [TeamXCDB]
GO
ALTER Procedure [dbo].[insert_course]
(@CourseName varchar(100), @id int = null OUTPUT)
as
BEGIN

if(@CourseName is NULL)
begin
	raiserror('name can not be null', 14,1)
	return(1)
end

If((Select [name]
From Course
Where [name] = @CourseName) is NOT  NULL)
begin
	Raiserror('name is already in table - insert course',14,1)
	Return(2)
end

Insert Into Course
Values(@CourseName)

SELECT @id = @@IDENTITY

Return(0)

END
