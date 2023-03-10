USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_race]    Script Date: 1/19/2023 11:08:44 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE Procedure [dbo].[delete_result]
(@ResultID int
)
As
BEGIN

if(@ResultID is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [result_id]
From Result
Where [result_id] = @ResultID) is NULL)
begin
	Raiserror('result is not in table',14,1)
	Return(2)
end

Delete From Result
Where [result_id] = @ResultID

Return(0)

END

