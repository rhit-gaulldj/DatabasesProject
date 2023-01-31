USE TeamXCDB
GO

CREATE PROCEDURE update_athlete(@id int, @first_name nvarchar(200), @last_name nvarchar(200),
	@grad_year int, @gender char(1))
AS
BEGIN
	IF (@id is null) BEGIN
		PRINT('Athlete ID must not be null')
		RETURN 1
	END
	IF (NOT EXISTS (SELECT * FROM Athlete WHERE athlete_id = @id)) BEGIN
		PRINT('Athlete does not exist')
		RETURN 2
	END

	IF (@gender is not null AND @gender not like '[MFO]') BEGIN
		PRINT('Invalid gender')
		RETURN 3
	END

	-- If any fields are null, then we'll just not modify them
	BEGIN TRANSACTION
		IF (@first_name is not null) BEGIN	
			UPDATE Athlete SET first_name = @first_name WHERE athlete_id = @id
		END
		IF (@last_name is not null) BEGIN	
			UPDATE Athlete SET last_name = @last_name WHERE athlete_id = @id
		END
		IF (@grad_year is not null) BEGIN	
			UPDATE Athlete SET grad_year = @grad_year WHERE athlete_id = @id
		END
		IF (@gender is not null) BEGIN	
			UPDATE Athlete SET gender = @gender WHERE athlete_id = @id
		END
	COMMIT TRANSACTION
END