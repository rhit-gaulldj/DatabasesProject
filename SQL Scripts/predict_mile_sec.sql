USE [TeamXCDB]
GO
/****** Object:  UserDefinedFunction [dbo].[func_athlete_pbs]    Script Date: 2/6/2023 1:33:23 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--t2 = t1 * POWER((d2 / d1),1.06)
Alter FUNCTION [dbo].[predict_mile_sec] (
	@Time int,
	@Dist float,
	@Unit varchar(8) = 'mi'
)
RETURNS int 
AS  
BEGIN
	Return (
		@Time * POWER(1/
		(CASE
			WHEN @Unit = 'mi' THEN @Dist
			WHEN @Unit = 'km' THEN @Dist / 1.60934
		END), 1.06)
	)
END
