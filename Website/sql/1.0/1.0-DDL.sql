
DROP DATABASE IF EXISTS `manascape`;

CREATE DATABASE `manascape` 
	DEFAULT CHARACTER SET utf8 
	DEFAULT COLLATE utf8_general_ci;
	
USE `manascape`;


DROP TABLE IF EXISTS `user_accounts`;
CREATE TABLE `user_accounts` (
	`id`				INT(10)			UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE, 
	`username`			VARCHAR(12)		NOT NULL UNIQUE, 
	`passwordHash`		CHAR(128)		NOT NULL, 
	`passwordSalt`		CHAR(128)		NOT NULL, 
	`dob`				DATE			NOT NULL, 
	`countryCode`		TINYINT(3)		UNSIGNED NOT NULL, 
	
	`creationDate`		DATETIME		NOT NULL, 
	`creationIP`		VARCHAR(128)	NOT NULL,
	
	`staff`				BIT				NOT NULL DEFAULT 0, 
	`pmod`				BIT				NOT NULL DEFAULT 0, 
	`fmod`				BIT				NOT NULL DEFAULT 0,
	
	`lastLoginDate`		DATETIME		NULL, 
	`currentIP`			VARCHAR(128)	NOT NULL, 
	
	`supportDisabled`	BIT				NOT NULL DEFAULT 0,
	`forumsDisabled`	BIT				NOT NULL DEFAULT 0,
	
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


DROP TABLE IF EXISTS `user_passwordChanges`;
CREATE TABLE `user_passwordChanges` (
	`userId`		INT(10)			UNSIGNED NOT NULL, 
	`ip`			VARCHAR(128)	NOT NULL, 
	`date`			DATETIME		NOT NULL, 
	`oldHash`		CHAR(128)		NOT NULL, 
	`oldSalt`		CHAR(128)		NOT NULL,
	`newHash`		CHAR(128)		NOT NULL, 
	`newSalt`		CHAR(128)		NOT NULL, 
	
	PRIMARY KEY (`userId`, `date`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `user_bans`;
CREATE TABLE `user_bans` (
	`id`			BIGINT(20)		UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
	`userId`		INT(10)			UNSIGNED NOT NULL, 
	`date`			DATETIME		NOT NULL, 
	`addedBy`		VARCHAR(12)		NOT NULL, 
	`type`			VARCHAR(20)		NOT NULL, 
	`reason`		TEXT			NOT NULL,
	`active`		BIT				NOT NULL DEFAULT 1,
	`liftDate`		DATETIME		NULL,
	`liftor`		VARCHAR(12)		NULL, 
	`liftReason`	TEXT			NULL,
	
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `user_ticketThreads`;
CREATE TABLE `user_ticketThreads` (
	`id`				INT(10)			UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
	`title`				VARCHAR(50)		NOT NULL, 
	`messageCount`		SMALLINT(5)		UNSIGNED NOT NULL DEFAULT 1,
	`lastMessageId`		INT(10)			NULL,
	`canReply`			BIT				NOT NULL DEFAULT 1,
	`authorDel`			BIT				NOT NULL DEFAULT 0,
	`receiverDel`		BIT				NOT NULL DEFAULT 0,
	
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `user_ticketMessages`;
CREATE TABLE `user_ticketMessages` (
	`id`			INT(10)			UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
	`threadId`		INT(10)			UNSIGNED NOT NULL, 
	`date`			DATETIME		NOT NULL, 
	`author`		VARCHAR(12)		NULL, 
	`authorStaff`	BIT				NOT NULL DEFAULT 0,
	`authorId`		INT(10)			UNSIGNED NOT NULL,
	`authorIP`		VARCHAR(128)	NOT NULL, 
	`receiver`		VARCHAR(12)		NULL, 
	`message`		TEXT			NOT NULL, 
	`readOn`		DATETIME		NULL,
	
	PRIMARY KEY (`id`),
	FOREIGN KEY (`threadId`) REFERENCES `user_ticketThreads` (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `media_news`;
CREATE TABLE `media_news` (
	`id`			MEDIUMINT(8)	UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE, 
	`authorId`		INT(10)			UNSIGNED NOT NULL, 
	`title`			VARCHAR(50)		NOT NULL, 
	`date`			DATETIME		NOT NULL, 
	`category`		TINYINT(2)		UNSIGNED NOT NULL, 
	`description`	VARCHAR(1024)	NOT NULL, 
	`body`			TEXT			NOT NULL, 
	`deleted`		BIT				NOT NULL DEFAULT 0,
	`lastEditor`	VARCHAR(12)		NULL, 
	`lastEditDate`	DATETIME		NULL, 
	
	PRIMARY KEY (`id`), 
	FOREIGN KEY (`authorId`) REFERENCES `user_accounts` (`id`)
) ENGINE=InnoDB;



DELIMITER $$



-- -------------------------------------------------------------------------------------------
--
-- Utilities
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `getPageInfo` $$
CREATE PROCEDURE `util_getPageInfo` (
	IN `in_count`		BIGINT(20) UNSIGNED,
	IN `in_page`	  	MEDIUMINT(8) UNSIGNED, 
	IN `in_limit`	  	SMALLINT(5) UNSIGNED,
	OUT `out_pageCount`	MEDIUMINT(8) UNSIGNED,
	OUT `out_realPage`	MEDIUMINT(8) UNSIGNED,
	OUT `out_start`		BIGINT(20) UNSIGNED
) 
BEGIN
	SET `out_pageCount` = CEIL(`in_count` / `in_limit`);
	
	IF (`out_pageCount` = 0) THEN
		SET `out_pageCount` = 1;
	END IF;
	
	IF (`in_page` > `out_pageCount`) THEN 
		SET `out_realPage` = `out_pageCount`;
	ELSE 
		SET `out_realPage` = `in_page`;
	END IF;
	
	SET `out_start` = (`out_realPage` * `in_limit`) - `in_limit`;
END $$



-- -------------------------------------------------------------------------------------------
--
-- Staff Center
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `staff_getUserBanForType` $$
CREATE PROCEDURE `staff_getUserBanForType` (
	IN `in_userId`	INT(10) UNSIGNED,
	IN `in_type` 	VARCHAR(20)
) 
BEGIN 
	SELECT `id` 
	FROM `user_bans` 
	WHERE `userId` = `in_userId` 
		AND `type` = `in_type` 
		AND `active` = 1 
	LIMIT 1;
END $$ 


DROP PROCEDURE IF EXISTS `staff_getUserBan` $$
CREATE PROCEDURE `staff_getUserBan` (
	IN `in_id`	BIGINT(20) UNSIGNED
) 
BEGIN 
	SELECT `b`.`id`, `b`.`userId`, `b`.`date`, `b`.`addedBy`, `b`.`type`, `b`.`reason`, `b`.`active`, `b`.`liftDate`, `b`.`liftor`, `b`.`liftReason`, 
		`a`.`username` 
	FROM `user_bans` AS `b` 
		JOIN `user_accounts` AS `a` 
			ON `b`.`userId` = `a`.`id` 
	WHERE `b`.`id` = `in_id` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `staff_getUserBans` $$
CREATE PROCEDURE `staff_getUserBans` (
	IN `in_userId`	INT(10) UNSIGNED
) 
BEGIN 
	SELECT `id`, `date`, `addedBy`, `type`, `active`  
	FROM `user_bans` 
	WHERE `userId` = `in_userId` 
	ORDER BY `date` DESC;
END $$


DROP PROCEDURE IF EXISTS `staff_submitUserBan` $$
CREATE PROCEDURE `staff_submitUserBan` (
	IN `in_userId`	INT(10) UNSIGNED,
	IN `in_date`	DATETIME, 
	IN `in_addedBy`	VARCHAR(12), 
	IN `in_type`	VARCHAR(20), 
	IN `in_reason`	TEXT
	
) 
BEGIN
	INSERT INTO `user_bans` (
		`userId`, `date`, `addedBy`, `type`, `reason`
	) VALUES (
		`in_userId`, `in_date`, `in_addedBy`, `in_type`, `in_reason` 
	);
END $$


DROP PROCEDURE IF EXISTS `staff_liftUserBan` $$
CREATE PROCEDURE `staff_liftUserBan` (
	IN `in_banId`	BIGINT(20) UNSIGNED,
	IN `in_date`	DATETIME, 
	IN `in_liftor`	VARCHAR(12), 
	IN `in_reason`	TEXT
	
) 
BEGIN
	UPDATE `user_bans` 
	SET `active` = 0,
		`liftDate` = `in_date`, 
		`liftor` = `in_liftor`, 
		`liftReason` = `in_reason` 
	WHERE `id` = `in_banId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `staff_applyOrLiftForumBan` $$
CREATE PROCEDURE `staff_applyOrLiftForumBan` (
	IN `in_userId`	INT(10) UNSIGNED,
	IN `in_date`	DATETIME, 
	IN `in_addedBy`	VARCHAR(12), 
	IN `in_reason`	TEXT,
	IN `in_banId`	BIGINT(20) UNSIGNED
) 
BEGIN 
	IF (`in_banID` = 0) THEN 
		CALL `staff_submitUserBan` (`in_userId`, `in_date`, `in_addedBy`, 'forums', `in_reason`);
		
		UPDATE `user_accounts` 
		SET `forumsDisabled` = 1 
		WHERE `id` = `in_userId` 
		LIMIT 1;
	ELSE
		CALL `staff_liftUserBan`(`in_banId`, `in_date`, `in_addedBy`, `in_reason`);
		
		UPDATE `user_accounts` 
		SET `forumsDisabled` = 0 
		WHERE `id` = `in_userId` 
		LIMIT 1;
	END IF;
END $$


DROP PROCEDURE IF EXISTS `staff_applyOrLiftSupportBan` $$
CREATE PROCEDURE `staff_applyOrLiftSupportBan` (
	IN `in_userId`	INT(10) UNSIGNED,
	IN `in_date`	DATETIME, 
	IN `in_addedBy`	VARCHAR(12), 
	IN `in_reason`	TEXT,
	IN `in_banId`	BIGINT(20) UNSIGNED
) 
BEGIN 
	IF (`in_banID` = 0) THEN 
		CALL `staff_submitUserBan` (`in_userId`, `in_date`, `in_addedBy`, 'support', `in_reason`);
		
		UPDATE `user_accounts` 
		SET `supportDisabled` = 1 
		WHERE `id` = `in_userId` 
		LIMIT 1;
	ELSE
		CALL `staff_liftUserBan`(`in_banId`, `in_date`, `in_addedBy`, `in_reason`);
		
		UPDATE `user_accounts` 
		SET `supportDisabled` = 0 
		WHERE `id` = `in_userId` 
		LIMIT 1;
	END IF;
END $$


DROP PROCEDURE IF EXISTS `staff_getUserPasswordChanges` $$
CREATE PROCEDURE `staff_getUserPasswordChanges` (
	IN `in_userId`		INT(10) UNSIGNED,
	IN `in_page`	  	MEDIUMINT(8) UNSIGNED, 
	IN `in_limit`	  	SMALLINT(5) UNSIGNED,
	OUT `out_pageCount`	MEDIUMINT(8) UNSIGNED,
	OUT `out_realPage`	MEDIUMINT(8) UNSIGNED,
	OUT `out_count`		BIGINT(20) UNSIGNED
) 
BEGIN 
	DECLARE `start`	BIGINT(20);
	
	SELECT COUNT(`date`) INTO `out_count` 
	FROM `user_passwordChanges` 
	WHERE `userId` = `in_userId`;
	
	CALL `util_getPageInfo`(`out_count`, `in_page`, `in_limit`, `out_pageCount`, `out_realPage`, `start`);
	
	SELECT `ip`, `date` 
	FROM `user_passwordChanges` 
	WHERE `userId` = `in_userId` 
	ORDER BY `date` DESC 
	LIMIT `start`,`in_limit`;
END $$


DROP PROCEDURE IF EXISTS `staff_getUserLoginSessions` $$
CREATE PROCEDURE `staff_getUserLoginSessions` (
	IN `in_userId`		INT(10) UNSIGNED,
	IN `in_page`	  	MEDIUMINT(8) UNSIGNED, 
	IN `in_limit`	  	SMALLINT(5) UNSIGNED,
	OUT `out_pageCount`	MEDIUMINT(8) UNSIGNED,
	OUT `out_realPage`	MEDIUMINT(8) UNSIGNED,
	OUT `out_count`		BIGINT(20) UNSIGNED
) 
BEGIN 
	DECLARE `start`	BIGINT(20);
	
	SELECT COUNT(`id`) INTO `out_count` 
	FROM `user_sessions` 
	WHERE `userId` = `in_userId`;
	
	CALL `util_getPageInfo`(`out_count`, `in_page`, `in_limit`, `out_pageCount`, `out_realPage`, `start`);
	
	SELECT `ip`, `startDate`, `endDate`, `secure`, `startMod`, `currentMod`, `startDest`, `currentDest` 
	FROM `user_sessions` 
	WHERE `userId` = `in_userId` 
	ORDER BY `endDate` DESC 
	LIMIT `start`,`in_limit`;
END $$


DROP PROCEDURE IF EXISTS `staff_getUserLoginAttempts` $$
CREATE PROCEDURE `staff_getUserLoginAttempts` (
	IN `in_username`  	VARCHAR(12),
	IN `in_page`	  	MEDIUMINT(8) UNSIGNED, 
	IN `in_limit`	  	SMALLINT(5) UNSIGNED,
	OUT `out_pageCount`	MEDIUMINT(8) UNSIGNED,
	OUT `out_realPage`	MEDIUMINT(8) UNSIGNED,
	OUT `out_count`		BIGINT(20) UNSIGNED
) 
BEGIN 
	DECLARE `start`	BIGINT(20);
	
	SELECT COUNT(`date`) INTO `out_count` 
	FROM `user_loginAttempts` 
	WHERE `username` = `in_username`;
	
	CALL `util_getPageInfo`(`out_count`, `in_page`, `in_limit`, `out_pageCount`, `out_realPage`, `start`);
	
	SELECT `a`.`date`, `a`.`ip`, (
			SELECT COUNT(`s`.`id`) 
			FROM `user_sessions` AS `s` 
				JOIN `user_loginAttempts` AS `aa` 
					ON `s`.`startDate` = `aa`.`date` 
			WHERE `aa`.`username` = `in_username` 
				AND `aa`.`date` = `a`.`date` 
			LIMIT 1
		) AS `successful` 
	FROM `user_loginAttempts` AS `a` 
	WHERE `a`.`username` = `in_username` 
	ORDER BY `a`.`date` DESC
	LIMIT `start`,`in_limit`;
END $$


DROP PROCEDURE IF EXISTS `staff_getUserDetails` $$
CREATE PROCEDURE `staff_getUserDetails` (
	IN `in_id`	INT(10) UNSIGNED
) 
BEGIN 
	SELECT `id`, `username`, `dob`, `countryCode`, `creationDate`, `creationIP`, `currentIP`, `staff`, `fmod`, `pmod`, `supportDisabled`, `forumsDisabled` 
	FROM `user_accounts`
	WHERE `id` = `in_id`
	LIMIT 1;
END $$



-- -------------------------------------------------------------------------------------------
--
-- Ticketing
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `user_ticketReceiverDelete` $$
CREATE PROCEDURE `user_ticketReceiverDelete` (
	IN `in_id`			INT(10) UNSIGNED
) 
BEGIN 
	UPDATE `user_ticketThreads` 
	SET `receiverDel` = 1 
	WHERE `id` = `in_id` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_ticketAuthorDelete` $$
CREATE PROCEDURE `user_ticketAuthorDelete` (
	IN `in_id`			INT(10) UNSIGNED
) 
BEGIN 
	UPDATE `user_ticketThreads` 
	SET `authorDel` = 1 
	WHERE `id` = `in_id` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_ticketReply` $$
CREATE PROCEDURE `user_ticketReply` (
	IN `in_id`			INT(10) UNSIGNED,
	IN `in_userId`		INT(10) UNSIGNED,
	IN `in_username`	VARCHAR(12),
	IN `in_userIP`		VARCHAR(128),
	IN `in_userStaff`	BIT,
	IN `in_receiver`	VARCHAR(12),
	IN `in_date`		DATETIME,
	IN `in_message`		TEXT,
	IN `in_canReply`	BIT,
	OUT `out_messageId`	BIGINT(20) UNSIGNED
) 
BEGIN
	INSERT INTO `user_ticketMessages` (
		`threadId`, `date`, `author`, `authorStaff`, `authorId`, `authorIP`, `receiver`, `message` 
	) VALUES (
		`in_id`, `in_date`, `in_username`, `in_userStaff`, `in_userId`, `in_userIP`, `in_receiver`, `in_message` 
	);
	
	SET `out_messageId` = LAST_INSERT_ID();
	
	UPDATE `user_ticketThreads` 
	SET `lastMessageId` = `out_messageId`, 
		`canReply` = `in_canReply`, 
		`authorDel` = 0, 
		`receiverDel` = 0, 
		`messageCount` = (`messageCount` + 1) 
	WHERE `id` = `in_id` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_ticketSetMessageRead` $$
CREATE PROCEDURE `user_ticketSetMessageRead` (
	IN `in_id`		INT(10) UNSIGNED,
	IN `in_date`	DATETIME
) 
BEGIN 
	UPDATE `user_ticketMessages` 
	SET `readOn` = `in_date` 
	WHERE `id` = `in_id`;
END $$


DROP PROCEDURE IF EXISTS `user_ticketGetThread` $$
CREATE PROCEDURE `user_ticketGetThread` (
	IN `in_id`				INT(10) UNSIGNED,
	OUT `out_title`			VARCHAR(50),
	OUT `out_canReply`		BIT,
	OUT `out_receiverDel`	BIT,
	OUT `out_authorDel`		BIT
) 
BEGIN 
	SELECT `title`, `canReply`, `receiverDel`, `authorDel` 
		INTO `out_title`, `out_canReply`, `out_receiverDel`, `out_authorDel`
	FROM `user_ticketThreads` 
	WHERE `id` = `in_id` 
	LIMIT 1;
	
	SELECT `id`, `date`, `author`, `authorStaff`, `authorId`, `authorIP`, `receiver`, `message`, `readOn`  
	FROM `user_ticketMessages` 
	WHERE `threadId` = `in_id` 
	ORDER BY `date` ASC;
END $$


DROP PROCEDURE IF EXISTS `user_ticketUnreadMessages` $$
CREATE PROCEDURE `user_ticketUnreadMessages` (
	IN `in_username` VARCHAR(12)
) 
BEGIN 
	SELECT `t`.`id`, `t`.`title`, `t`.`messageCount`, 
		`m`.`date` AS `lastMessageDate` 
	FROM `user_ticketThreads` AS `t` 
		JOIN `user_ticketMessages` AS `m` 
			ON `t`.`lastMessageId` = `m`.`id` 
	WHERE `m`.`readOn` IS NULL 
		AND `m`.`receiver` = `in_username` 
		AND `t`.`receiverDel` = 0 
	ORDER BY `m`.`date` DESC;
END $$


DROP PROCEDURE IF EXISTS `user_ticketReadMessages` $$
CREATE PROCEDURE `user_ticketReadMessages` (
	IN `in_username` VARCHAR(12)
) 
BEGIN 
	SELECT `t`.`id`, `t`.`title`, `t`.`messageCount`, 
		`m`.`date` AS `lastMessageDate` 
	FROM `user_ticketThreads` AS `t` 
		JOIN `user_ticketMessages` AS `m` 
			ON `t`.`lastMessageId` = `m`.`id` 
	WHERE `m`.`readOn` IS NOT NULL 
		AND `m`.`receiver` = `in_username` 
		AND `t`.`receiverDel` = 0 
	ORDER BY `m`.`date` DESC;
END $$


DROP PROCEDURE IF EXISTS `user_ticketSentMessages` $$
CREATE PROCEDURE `user_ticketSentMessages` (
	IN `in_username` VARCHAR(12)
) 
BEGIN 
	SELECT `t`.`id`, `t`.`title`, `t`.`messageCount`, 
		`m`.`date` AS `lastMessageDate` 
	FROM `user_ticketThreads` AS `t` 
		JOIN `user_ticketMessages` AS `m` 
			ON `t`.`lastMessageId` = `m`.`id` 
	WHERE `m`.`author` = `in_username` 
		AND `t`.`authorDel` = 0 
	ORDER BY `m`.`date` DESC;
END $$



-- -------------------------------------------------------------------------------------------
--
-- Account Management
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `user_getPassword` $$
CREATE PROCEDURE `user_getPassword` (
	IN `in_userId`	INT(10)
)
BEGIN
	SELECT `passwordHash`, `passwordSalt` 
	FROM `user_accounts` 
	WHERE `id` = `in_userId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `user_changePassword` $$
CREATE PROCEDURE `user_changePassword` (
	IN `in_userId`			INT(10) UNSIGNED,
	IN `in_ip`				VARCHAR(128),
	IN `in_date`			DATETIME,
	IN `in_newHash`			CHAR(128),
	IN `in_newSalt`			CHAR(128),
	OUT `out_successful`	BIT
) 
BEGIN 
	DECLARE `var_oldHash` CHAR(128);
	DECLARE `var_oldSalt` CHAR(128);
	
	SELECT `passwordHash`, `passwordSalt` 
		INTO `var_oldHash`, `var_oldSalt` 
	FROM `user_accounts` 
	WHERE `id` = `in_userId` 
	LIMIT 1;
	
	INSERT INTO `user_passwordChanges` (
		`userId`, `ip`, `date`, `oldHash`, `oldSalt`, `newHash`, `newSalt`
	) VALUES (
		`in_userId`, `in_ip`, `in_date`, `var_oldHash`, `var_oldSalt`, `in_newHash`, `in_newSalt`
	);
	
	UPDATE `user_accounts` 
	SET `passwordHash` = `in_newHash`, 
		`passwordSalt` = `in_newSalt` 
	WHERE `id` = `in_userId` 
	LIMIT 1;
	
	IF (ROW_COUNT() > 0) THEN
		SET `out_successful` = 1;
	ELSE
		SET `out_successful` = 0;
	END IF;
END $$



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
	
	SELECT `a`.`id`, `a`.`username`, `a`.`staff`, `a`.`fmod`, `a`.`pmod`, `a`.`currentIP`, `a`.`supportDisabled`, `a`.`forumsDisabled` 
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
	IN `in_passwordSalt`	CHAR(128),
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



-- -------------------------------------------------------------------------------------------
--
-- News Articles
--
-- -------------------------------------------------------------------------------------------


DROP PROCEDURE IF EXISTS `media_deleteNewsArticle` $$
CREATE PROCEDURE `media_deleteNewsArticle` (
	IN `in_articleId`	MEDIUMINT(8) UNSIGNED,
	IN `in_username`	VARCHAR(12),
	IN `in_date`		DATETIME
) 
BEGIN
	UPDATE `media_news` 
	SET `deleted` = 1, 
		`lastEditor` = `in_username`, 
		`lastEditDate` = `in_date`
	WHERE `id` = `in_articleId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `media_updateNewsArticle` $$
CREATE PROCEDURE `media_updateNewsArticle` (
	IN `in_articleId`	MEDIUMINT(8) UNSIGNED,
	IN `in_username`	VARCHAR(12),
	IN `in_date`		DATETIME, 
	IN `in_title`		VARCHAR(50),
	IN `in_category`	TINYINT(2) UNSIGNED,
	IN `in_description`	VARCHAR(1024),
	IN `in_body`		TEXT
) 
BEGIN 
	UPDATE `media_news` 
	SET `title` = `in_title`, 
		`category` = `in_category`, 
		`description` = `in_description`, 
		`body` = `in_body`, 
		`lastEditor` = `in_username`, 
		`lastEditDate` = `in_date`
	WHERE `id` = `in_articleId` 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `media_postNewsArticle` $$
CREATE PROCEDURE `media_postNewsArticle` (
	IN `in_authorId`	INT(10) UNSIGNED, 
	IN `in_title`		VARCHAR(50),
	IN `in_date`		DATETIME, 
	IN `in_category`	TINYINT(2) UNSIGNED,
	IN `in_description`	VARCHAR(1024),
	IN `in_body`		TEXT,
	OUT `out_articleId`	MEDIUMINT(8) UNSIGNED
) 
BEGIN 
	INSERT INTO `media_news` (
		`authorId`, `title`, `date`, `category`, `description`, `body`
	) VALUES (
		`in_authorId`, `in_title`, `in_date`, `in_category`, `in_description`, `in_body`
	);
	
	SET `out_articleId` = LAST_INSERT_ID();
END $$


DROP PROCEDURE IF EXISTS `media_getNewsArchive` $$
CREATE PROCEDURE `media_getNewsArchive` (
	IN `in_category`	TINYINT(2)
) 
BEGIN 
	SELECT `id`, `title`, `date`, `category` 
	FROM `media_news` 
	WHERE ((`in_category` = 0) OR (`in_category` = `category`)) 
		AND `deleted` = 0 
	ORDER BY `date` DESC;
END $$


DROP PROCEDURE IF EXISTS `media_getNewsArticle` $$
CREATE PROCEDURE `media_getNewsArticle` (
	IN `in_id`	MEDIUMINT(8) UNSIGNED
) 
BEGIN 
	SELECT `id`, `title`, `date`, `category`, `description`, `body`, `lastEditor`, `lastEditDate` 
	FROM `media_news` 
	WHERE `id` = `in_id` 
		AND `deleted` = 0 
	LIMIT 1;
END $$


DROP PROCEDURE IF EXISTS `media_getNewsFeed` $$
CREATE PROCEDURE `media_getNewsFeed` (
	IN `in_limit`	TINYINT(2) UNSIGNED
) 
BEGIN
	SELECT `id`, `title`, `date`, `description` 
	FROM `media_news` 
	WHERE `deleted` = 0 
	ORDER BY `date` DESC 
	LIMIT `in_limit`;
END $$


DELIMITER ;




