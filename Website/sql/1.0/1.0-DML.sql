
USE `manascape`;



-- Testing


INSERT INTO `user_accounts` (
	`id`, `username`, `passwordHash`, `passwordSalt`, `dob`, `countryCode`, `creationDate`, `creationIP`, `currentIP`
) VALUES (
	1, 'Test', '', '', '1995-07-21', '225', '2015-08-20', '127.0.0.1', '127.0.0.1'
);

