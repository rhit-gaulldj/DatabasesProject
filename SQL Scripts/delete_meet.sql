USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[delete_meet]    Script Date: 2/8/2023 12:58:34 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER Procedure [dbo].[delete_meet]
(@id int)
AS
BEGIN
if(@id is NULL)
begin
	raiserror('ID can not be null', 14,1)
	return(1)
end

If((Select [name]
From Meet
Where meet_id = @id) is NULL)
begin
	Raiserror('Meet is not in table',14,1)
	Return(2)
end

BEGIN TRANSACTION

-- Must delete all splits, results, and races associated with this meet
DELETE rspl
	FROM ResultSplit rspl
	JOIN Result rs on rspl.result_id = rs.result_id
	JOIN Race r ON rs.race_id = r.race_id
	WHERE r.meet_id = @id

DELETE rs
	FROM Result rs
	JOIN Race r ON rs.race_id = r.race_id
	WHERE r.meet_id = @id

DELETE r
	FROM Race r
	WHERE r.meet_id = @id

Delete From Meet
Where meet_id = @id

COMMIT TRANSACTION

Return(0)

END
