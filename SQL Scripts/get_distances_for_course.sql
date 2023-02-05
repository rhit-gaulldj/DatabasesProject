USE TeamXCDB
GO

ALTER PROCEDURE get_dists_for_course(@id int)
AS
BEGIN
	IF (@id is null) BEGIN
		RAISERROR('Id cannot be null', 14, 1)
		RETURN 1
	END
	IF (NOT EXISTS (SELECT * FROM Course WHERE course_id = @id)) BEGIN
		RAISERROR('Course does not exist', 14, 2)
		RETURN 2
	END

	SELECT R.distance, R.distance_unit
		FROM Race R
		JOIN Meet M on R.meet_id = M.meet_id
		JOIN Course C on M.course_id = C.course_id
		WHERE C.course_id = @id
		GROUP BY R.distance, R.distance_unit
END