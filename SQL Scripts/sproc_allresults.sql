USE [TeamXCDB]
GO

CREATE PROCEDURE view_all_results
AS
BEGIN
	SELECT * FROM ThreeMileResults
	ORDER BY [Time] ASC
END