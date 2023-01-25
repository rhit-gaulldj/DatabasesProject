USE TeamXCDB
GO

-- Login and Session tables
CREATE TABLE [Login](
	email varchar(100) PRIMARY KEY NOT NULL,
	password_hash varchar(50) NOT NULL,
	password_salt varchar(50) NOT NULL,
	created_at datetime NOT NULL DEFAULT GETDATE(),
	last_login datetime NOT NULL DEFAULT GETDATE(),

	CHECK(email LIKE '%@%.%')
)

CREATE TABLE [Session](
	session_id uniqueidentifier PRIMARY KEY NOT NULL DEFAULT NEWID(),
	expires_at datetime NOT NULL,
	user_email varchar(100) NOT NULL,

	FOREIGN KEY(user_email) REFERENCES [Login](email)
)