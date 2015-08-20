
DROP DATABASE IF EXISTS `manascape`;

CREATE DATABASE `manascape` 
	DEFAULT CHARACTER SET utf8 
	DEFAULT COLLATE utf8_general_ci;
	
USE `manascape`;


DROP TABLE IF EXISTS `user_accounts`;
CREATE TABLE `user_accounts` (
	`id`			INT(10)			UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE, 
	`username`		VARCHAR(12)		NOT NULL UNIQUE, 
	`passwordHash`	CHAR(128)		NOT NULL, 
	`passwordSalt`	CHAR(50)		NOT NULL, 
	`dob`			DATE			NOT NULL, 
	`countryCode`	TINYINT(3)		UNSIGNED NOT NULL, 
	
	`creationDate`	DATETIME		NOT NULL, 
	`creationIP`	VARCHAR(128)	NOT NULL,
	
	`staff`			BIT				NOT NULL DEFAULT 0, 
	`pmod`			BIT				NOT NULL DEFAULT 0, 
	`fmod`			BIT				NOT NULL DEFAULT 0,
	
	`lastLoginDate`	DATETIME		NULL, 
	`currentIP`		VARCHAR(128)	NOT NULL, 
	
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;



DELIMITER $$


DROP PROCEDURE IF EXISTS `user_createAccount` $$
CREATE PROCEDURE `user_createAccount` (
	IN `in_username`		VARCHAR(12),
	IN `in_passwordHash`	CHAR(128),
	IN `in_passwordSalt`	VARCHAR(50),
	IN `in_dob`				DATE,
	IN `in_countryCode`		TINYINT(3) UNSIGNED, 
	IN `in_date`			DATETIME, 
	IN `in_ip`				VARCHAR(128),
	OUT `out_returnCode`	TINYINT(1) UNSIGNED
) 
BEGIN 
	INSERT INTO `user_accounts` (
		`username`, `passwordHash`, `passwordSalt`, `dob`, `countryCode`, `creationDate`, `creationIP`, `currentIP` 
	) VALUES (
		`in_username`, `in_passwordHash`, `in_passwordSalt`, `in_dob`, `in_countryCode`, `in_date`, `in_ip`, `in_ip` 
	);
	
	SELECT ROW_COUNT() INTO `out_returnCode`;
END $$


DROP PROCEDURE IF EXISTS `user_creationFloodCheck` $$
CREATE PROCEDURE `user_creationFloodCheck` (
	IN `in_ip`		VARCHAR(128),
	IN `in_date`	DATETIME, 
	IN `in_max`		SMALLINT(5) UNSIGNED,
	OUT `out_count`	SMALLINT(5) UNSIGNED
) 
BEGIN 
	SELECT COUNT(`id`) INTO `out_count` 
	FROM `user_accounts` 
	WHERE `creationIP` = `in_ip` 
		AND `creationDate` >= `in_date` 
	ORDER BY `creationDate` DESC 
	LIMIT `in_max`;
END $$


DROP PROCEDURE IF EXISTS `user_checkUsername` $$
CREATE PROCEDURE `user_checkUsername` (
	IN `in_username`	VARCHAR(12),
	OUT `out_exists`	BIT
) 
BEGIN 
	SELECT COUNT(`id`) INTO `out_exists` 
	FROM `user_accounts` 
	WHERE `username` = `in_username` 
	LIMIT 1;
END $$


DELIMITER ;




