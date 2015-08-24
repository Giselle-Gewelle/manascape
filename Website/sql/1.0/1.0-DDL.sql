
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
	`passwordSalt`	CHAR(128)		NOT NULL, 
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


DROP TABLE IF EXISTS `user_sessions`;
CREATE TABLE `user_sessions` (
	`id`			BIGINT(20)		UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE, 
	`userId`		INT(10)			UNSIGNED NOT NULL, 
	`ip`			VARCHAR(128)	NOT NULL, 
	`hash`			CHAR(128)		NOT NULL, 
	`startDate`		DATETIME		NOT NULL, 
	`endDate`		DATETIME		NOT NULL, 
	`secure`		BIT				NOT NULL DEFAULT 1,
	
	`startMod`		VARCHAR(30)		NOT NULL, 
	`currentMod`	VARCHAR(30)		NOT NULL, 
	`startDest`		VARCHAR(128)	NOT NULL, 
	`currentDest`	VARCHAR(128)	NOT NULL, 
	
	PRIMARY KEY (`id`), 
	FOREIGN KEY (`userId`) REFERENCES `user_accounts` (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `user_loginAttempts`;
CREATE TABLE `user_loginAttempts` (
	`username`		VARCHAR(12)		NOT NULL, 
	`date`			DATETIME		NOT NULL, 
	`ip`			VARCHAR(128)	NOT NULL, 
	
	PRIMARY KEY (`username`, `date`)
) ENGINE=InnoDB;



DELIMITER $$



-- -------------------------------------------------------------------------------------------
--
-- Login Sessions
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `user_loginFloodCheck` $$
CREATE PROCEDURE `user_loginFloodCheck` (
	IN `in_ip`		VARCHAR(128),
	IN `in_date`	DATETIME, 
	IN `in_max`		SMALLINT(5) UNSIGNED,
	OUT `out_count`	SMALLINT(5) UNSIGNED
) 
BEGIN 
	SELECT COUNT(`date`) INTO `out_count` 
	FROM `user_loginAttempts` 
	WHERE `ip` = `in_ip` 
		AND `date` >= `in_date` 
	ORDER BY `date` DESC 
	LIMIT `in_max`;
END $$


DROP PROCEDURE IF EXISTS `user_getLoginSessionDetails` $$ 
CREATE PROCEDURE `user_getLoginSessionDetails` (
	`in_sessionId`	BIGINT(20) UNSIGNED, 
	`in_secure`		BIT,
	`in_newMod`		VARCHAR(30),
	`in_newDest`	VARCHAR(50),
	`in_newHash`	CHAR(128),
	`in_endDate`	DATETIME
) 
BEGIN 
	UPDATE `user_sessions` 
	SET `hash` = `in_newHash`, 
		`secure` = `in_secure`, 
		`currentMod` = `in_newMod`, 
		`currentDest` = `in_newDest`, 
		`endDate` = `in_endDate` 
	WHERE `id` = `in_sessionId` 
	LIMIT 1;
	
	SELECT `a`.`id`, `a`.`username`, `a`.`staff`, `a`.`fmod`, `a`.`pmod`, `a`.`currentIP`
	FROM `user_sessions` AS `s` 
		JOIN `user_accounts` AS `a` 
			ON `s`.`userId` = `a`.`id` 
	WHERE `s`.`id` = `in_sessionId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_killLoginSession` $$ 
CREATE PROCEDURE `user_killLoginSession` (
	IN `in_id`		BIGINT(20) UNSIGNED,
	IN `in_date`	DATETIME
) 
BEGIN 
	UPDATE `user_sessions` 
	SET `endDate` = `in_date` 
	WHERE `id` = `in_id` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_findLoginSession` $$ 
CREATE PROCEDURE `user_findLoginSession` (
	IN `in_hash`		CHAR(128),
	IN `in_ip`			VARCHAR(128),
	IN `in_timeRange`	DATETIME
) 
BEGIN 
	SELECT `id`, `secure`, `endDate` 
	FROM `user_sessions` 
	WHERE `hash` = `in_hash` 
		AND `ip` = `in_ip` 
		AND `endDate` > `in_timeRange` 
	ORDER BY `endDate` DESC 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_submitLoginSession` $$ 
CREATE PROCEDURE `user_submitLoginSession` (
	IN `in_userId`		INT(10) UNSIGNED,
	IN `in_ip`			VARCHAR(128),
	IN `in_sessionHash`	CHAR(128),
	IN `in_date`		DATETIME, 
	IN `in_endDate`		DATETIME, 
	IN `in_mod`			VARCHAR(30),
	IN `in_dest`		VARCHAR(128),
	IN `in_secure`		BIT
) 
BEGIN 
	INSERT INTO `user_sessions` (
		`userId`, `ip`, `hash`, `secure`, `startDate`, `endDate`, `startMod`, `currentMod`, `startDest`, `currentDest`
	) VALUES (
		`in_userId`, `in_ip`, `in_sessionHash`, `in_secure`, `in_date`, `in_endDate`, `in_mod`, `in_mod`, `in_dest`, `in_dest`
	);
	
	UPDATE `user_accounts` 
	SET `lastLoginDate` = `in_date`, 
		`currentIP` = `in_ip` 
	WHERE `id` = `in_userId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_getInfoForLogin` $$
CREATE PROCEDURE `user_getInfoForLogin` (
	IN `in_username`	VARCHAR(12),
	IN `in_date`		DATETIME, 
	IN `in_ip`			VARCHAR(128)
)
BEGIN
	INSERT INTO `user_loginAttempts` (
		`username`, `date`, `ip`
	) VALUES (
		`in_username`, `in_date`, `in_ip`
	);
	
	SELECT `id`, `passwordHash`, `passwordSalt` 
	FROM `user_accounts` 
	WHERE `username` = `in_username` 
	LIMIT 1;
END $$



-- -------------------------------------------------------------------------------------------
--
-- Account Creation
--
-- -------------------------------------------------------------------------------------------


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




