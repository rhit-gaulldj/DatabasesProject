USE TeamXCDB
GO

CREATE PROCEDURE log_in(@email varchar(100), @password_hash varchar(50), @session_id uniqueidentifier OUTPUT)
AS
BEGIN
	IF (@email is null OR @password_hash is null) BEGIN
		RAISERROR('Email and password cannot be null', 14, 1)
		-- Return 1 to indicate failure for all errors so client cannot differentiate
		RETURN 1
	END

	IF (NOT EXISTS (SELECT * FROM [Login] WHERE email = @email)) BEGIN
		RAISERROR('User does not exist', 14, 2)
		RETURN 1
	END

	IF (NOT EXISTS (SELECT * FROM [Login] WHERE email = @email AND password_hash = @password_hash)) BEGIN
		RAISERROR('Incorrect password provided', 14, 3)
		RETURN 1
	END

	-- User is allowed to log in. We'll create a session and send the session token
	DECLARE @new_id uniqueidentifier
	EXEC add_session @email = @email, @new_id = @new_id OUTPUT
	SET @session_id = @new_id

	UPDATE [Login]
		SET last_login = GETDATE()
		WHERE email = @email

END

CREATE PROCEDURE add_session(@email varchar(100), @new_id uniqueidentifier OUTPUT)
AS
BEGIN
	DECLARE @result uniqueidentifier
	SET @result = NEWID()
	INSERT INTO [Session](session_id, expires_at, user_email)
		VALUES(@result, GETDATE()+7, @email)
	SET @new_id = @result
END

CREATE PROCEDURE get_salt(@email varchar(100), @salt varchar(50) OUTPUT)
AS
BEGIN
	SELECT @salt = password_salt FROM [Login] WHERE email = @email
END