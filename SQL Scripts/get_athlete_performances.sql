USE [TeamXCDB]
GO
/****** Object:  StoredProcedure [dbo].[get_athlete_race_results]    Script Date: 2/6/2023 1:01:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--@athlete_id is mandatory, race_id is optional. If there isn't a race_id, it just shows all races that athlete has
Create PROCEDURE [dbo].[get_athlete_performances] 
(@athlete_id int,
@distance int = -1,
@distance_unit varchar(10) = 'mi')
AS
SELECT *
FROM Performances
WHERE @athlete_id = athlete_id  AND (@distance = -1 OR (@distance = distance AND @distance_unit = distance_unit))


