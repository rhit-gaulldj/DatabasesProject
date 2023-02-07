USE TeamXCDB
GO

ALTER FUNCTION get_splits(@ResultId int)
RETURNS varchar(100)
AS
BEGIN
	DECLARE @Splits varchar(100)
	SELECT @Splits = COALESCE(@Splits + ', ', '') + SUBSTRING(CONVERT(varchar, DATEADD(ms, rs.[time] * 1000, 0), 108), 4, 5)
		FROM ResultSplit rs
		WHERE result_id = @ResultId
		ORDER BY rs.[index]

	RETURN @Splits
END