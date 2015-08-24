
USE `manascape`;



-- Testing


INSERT INTO `user_accounts` (
	`id`, `username`, `passwordHash`, `passwordSalt`, `dob`, `countryCode`, `creationDate`, `creationIP`, `currentIP`, `staff`
) VALUES (
	1, 'a', '34140a25f2834124a33a03c04c7876a9c5c73a4838a402c30c35f6966ab9b1377ca21cbc7a8dc322f2164e40334a14d7878d9cedd461d24688b941044a83ae3b', 'ee1500156f6a88c547006d0bba7f6129da3ce872e5ed8e030feb14fc9bf3d16da2994dddde36f8687fade3ff2d3db49a2fe49e3cba60c45deb80dcdc20905418', '1995-07-21', '225', '2015-08-20', '127.0.0.1', '127.0.0.1', 1
);

