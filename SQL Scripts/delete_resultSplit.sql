USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_athlete]    Script Date: 1/19/2023 11:15:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER Procedure [dbo].[delete_resultSplit]
(@resultSplit int
)
As
BEGIN

if(@resultSplit is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [result_split_id]
From ResultSplit
Where [result_split_id] = @resultSplit) is NULL)
begin
	Raiserror('resultSplit is not in table',14,1)
	Return(2)
end

Delete From ResultSplit
Where [result_split_id] = @resultSplit

Return(0)

END

