USE TeamXCDB
GO

CREATE PROCEDURE log_out(@session_id uniqueidentifier)
AS
BEGIN
	DELETE FROM [Session]
		WHERE session_id = @session_id
END