USE TeamXCDB
GO

-- Returns 0 if success or 1 otherwise
CREATE PROCEDURE log_in_session(@session_id uniqueidentifier, @can_log_in bit OUTPUT)
AS
BEGIN
	DECLARE @canLogin bit
	SELECT @canLogin = dbo.can_log_in(@session_id)
	IF (@canLogin = 1) BEGIN
		SET @can_log_in = 1
		RETURN 0
	END

	PRINT('Cannot log in with this session ID')
	SET @can_log_in = 0
	RETURN 1

END
GO

CREATE FUNCTION can_log_in(@session_id uniqueidentifier)
RETURNS bit
AS
BEGIN
	IF ((SELECT COUNT(*) FROM [Session] WHERE session_id = @session_id) <> 1) BEGIN
		-- Email and session do not exist
		RETURN 0
	END

	DECLARE @expire_date datetime
	SELECT @expire_date = expires_at FROM [Session] WHERE session_id = @session_id
	IF (GETDATE() >= @expire_date) BEGIN
		-- Session has expired
		RETURN 0
	END

	RETURN 1
END