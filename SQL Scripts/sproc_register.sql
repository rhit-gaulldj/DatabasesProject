USE TeamXCDB
GO

CREATE PROCEDURE register(@email varchar(100), @pass_hash varchar(50), @pass_salt varchar(50))
AS
BEGIN
	-- Again, we always return 1 so that the client cannot differentiate between errors at all
	IF (@email IS NULL OR @email = '') BEGIN
		RAISERROR('Email cannot be null/empty', 14, 1)
		RETURN 1
	END

	IF (@pass_hash IS NULL OR @pass_hash = '') BEGIN
		RAISERROR('Password hash cannot be null/empty', 14, 2)
		RETURN 1
	END

	IF (@pass_salt IS NULL OR @pass_salt = '') BEGIN
		RAISERROR('Password salt cannot be null/empty', 14, 3)
		RETURN 1
	END

	-- Check if user exists
	IF (EXISTS (SELECT * FROM [Login] WHERE email = @email)) BEGIN
		RAISERROR('User already exists', 14, 4)
		RETURN 1
	END

	INSERT INTO [Login](email, password_hash, password_salt)
		VALUES(@email, @pass_hash, @pass_salt)
END