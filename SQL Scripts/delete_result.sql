USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_result]    Script Date: 2/8/2023 3:03:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER Procedure [dbo].[delete_result]
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

BEGIN TRANSACTION

DELETE FROM ResultSplit
WHERE result_id = @ResultID

Delete From Result
Where [result_id] = @ResultID

COMMIT TRANSACTION

Return(0)

END

