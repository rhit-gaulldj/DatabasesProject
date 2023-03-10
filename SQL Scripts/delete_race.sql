USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_race]    Script Date: 2/8/2023 2:42:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER Procedure [dbo].[delete_race]
(@RaceID int
)
As
BEGIN

if(@RaceID is NULL)
begin
	raiserror('id can not be null', 14,1)
	return(1)
end

If((Select [race_id]
From Race
Where [race_id] = @RaceID) is NULL)
begin
	Raiserror('Race is not in table',14,1)
	Return(2)
end

BEGIN TRANSACTION

DELETE rsp
	FROM ResultSplit rsp
	JOIN Result rs ON rsp.result_id = rs.result_id
	WHERE rs.race_id = @RaceID

DELETE rs
	FROM Result rs
	WHERE rs.race_id = @RaceID

Delete From Race
Where race_id = @RaceID

COMMIT TRANSACTION

Return(0)

END

