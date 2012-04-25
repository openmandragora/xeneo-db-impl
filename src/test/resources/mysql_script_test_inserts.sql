SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `flower` ;
CREATE SCHEMA IF NOT EXISTS `flower` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `flower` ;

-- -----------------------------------------------------
-- Table `flower`.`CaseType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`CaseType` ;

CREATE  TABLE IF NOT EXISTS `flower`.`CaseType` (
  `CaseTypeURI` VARCHAR(255) NOT NULL ,
  `Title` VARCHAR(100) NOT NULL ,
  `Description` TEXT NULL ,
  PRIMARY KEY (`CaseTypeURI`) ,
  UNIQUE INDEX `CaseTypeURI_UNIQUE` (`CaseTypeURI` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Case`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Case` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Case` (
  `CaseURI` VARCHAR(255) NOT NULL ,
  `Title` VARCHAR(100) NOT NULL ,
  `Description` TEXT NULL ,
  `CreationDate` DATETIME NOT NULL ,
  `CaseTypeURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`CaseURI`) ,
  UNIQUE INDEX `CaseURI_UNIQUE` (`CaseURI` ASC) ,
  INDEX `CaseTypeURI` (`CaseTypeURI` ASC) ,
  CONSTRAINT `CaseTypeURI`
    FOREIGN KEY (`CaseTypeURI` )
    REFERENCES `flower`.`CaseType` (`CaseTypeURI` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`User` ;

CREATE  TABLE IF NOT EXISTS `flower`.`User` (
  `UserURI` VARCHAR(255) NOT NULL ,
  `UserName` VARCHAR(100) NOT NULL ,
  `FirstName` VARCHAR(55) NOT NULL ,
  `LastName` VARCHAR(55) NOT NULL ,
  `Password` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`UserURI`) ,
  UNIQUE INDEX `UserURI_UNIQUE` (`UserURI` ASC) ,
  UNIQUE INDEX `UserName_UNIQUE` (`UserName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Task`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Task` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Task` (
  `TaskURI` VARCHAR(255) NOT NULL ,
  `Title` VARCHAR(100) NOT NULL ,
  `Description` TEXT NULL ,
  PRIMARY KEY (`TaskURI`) ,
  UNIQUE INDEX `TaskURI_UNIQUE` (`TaskURI` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Object`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Object` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Object` (
  `ObjectURI` VARCHAR(255) NOT NULL ,
  `Name` VARCHAR(255) NOT NULL ,
  `ObjectTypeURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`ObjectURI`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Activity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Activity` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Activity` (
  `ActivityURI` VARCHAR(255) NOT NULL ,
  `CreationDate` DATETIME NOT NULL ,
  `UserURI` VARCHAR(255) NOT NULL ,
  `ActionURI` VARCHAR(255) NOT NULL ,
  `ObjectURI` VARCHAR(255) NOT NULL ,
  `TargetURI` VARCHAR(255) NULL ,
  `Description` TEXT NULL ,
  `summary` VARCHAR(255) NULL ,
  PRIMARY KEY (`ActivityURI`) ,
  UNIQUE INDEX `ActivityURI_UNIQUE` (`ActivityURI` ASC) ,
  INDEX `UserURI` (`UserURI` ASC) ,
  INDEX `ActivityObject` (`ObjectURI` ASC) ,
  INDEX `ActivityTarget` (`TargetURI` ASC) ,
  CONSTRAINT `UserURI`
    FOREIGN KEY (`UserURI` )
    REFERENCES `flower`.`User` (`UserURI` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ActivityObject`
    FOREIGN KEY (`ObjectURI` )
    REFERENCES `flower`.`Object` (`ObjectURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `ActivityTarget`
    FOREIGN KEY (`TargetURI` )
    REFERENCES `flower`.`Object` (`ObjectURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`TaskContext`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`TaskContext` ;

CREATE  TABLE IF NOT EXISTS `flower`.`TaskContext` (
  `ActivityURI` VARCHAR(255) NOT NULL ,
  `TaskURI` VARCHAR(255) NOT NULL ,
  `CaseURI` VARCHAR(255) NOT NULL ,
  INDEX `ActivityURI` (`ActivityURI` ASC) ,
  INDEX `TaskURI` (`TaskURI` ASC) ,
  INDEX `CaseURI` (`CaseURI` ASC) ,
  PRIMARY KEY (`ActivityURI`, `TaskURI`, `CaseURI`) ,
  CONSTRAINT `ActivityURI`
    FOREIGN KEY (`ActivityURI` )
    REFERENCES `flower`.`Activity` (`ActivityURI` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `TaskURI`
    FOREIGN KEY (`TaskURI` )
    REFERENCES `flower`.`Task` (`TaskURI` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `CaseURI`
    FOREIGN KEY (`CaseURI` )
    REFERENCES `flower`.`Case` (`CaseURI` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Role` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Role` (
  `RoleURI` VARCHAR(255) NOT NULL ,
  `Title` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`RoleURI`) ,
  UNIQUE INDEX `RoleURI_UNIQUE` (`RoleURI` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Operation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Operation` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Operation` (
  `OperationID` INT NOT NULL AUTO_INCREMENT ,
  `Title` VARCHAR(150) NOT NULL ,
  `CaseTypeURI` VARCHAR(255) NULL ,
  `TaskURI` VARCHAR(255) NULL ,
  PRIMARY KEY (`OperationID`) ,
  INDEX `Operation_RightAssignment` (`CaseTypeURI` ASC) ,
  INDEX `Operation_Task` (`TaskURI` ASC) ,
  CONSTRAINT `Operation_RightAssignment`
    FOREIGN KEY (`CaseTypeURI` )
    REFERENCES `flower`.`CaseType` (`CaseTypeURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `Operation_Task`
    FOREIGN KEY (`TaskURI` )
    REFERENCES `flower`.`Task` (`TaskURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Participant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Participant` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Participant` (
  `ParticipantID` INT NOT NULL AUTO_INCREMENT ,
  `UserURI` VARCHAR(255) NOT NULL ,
  `CaseURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`ParticipantID`) ,
  INDEX `User_Participant` (`UserURI` ASC) ,
  INDEX `Case_Participant` (`CaseURI` ASC) ,
  CONSTRAINT `User_Participant`
    FOREIGN KEY (`UserURI` )
    REFERENCES `flower`.`User` (`UserURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `Case_Participant`
    FOREIGN KEY (`CaseURI` )
    REFERENCES `flower`.`Case` (`CaseURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`RightAssignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`RightAssignment` ;

CREATE  TABLE IF NOT EXISTS `flower`.`RightAssignment` (
  `OperationID` INT NOT NULL ,
  `RoleURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`OperationID`, `RoleURI`) ,
  INDEX `OperationID` (`OperationID` ASC) ,
  INDEX `RoleURI` (`RoleURI` ASC) ,
  CONSTRAINT `OperationID`
    FOREIGN KEY (`OperationID` )
    REFERENCES `flower`.`Operation` (`OperationID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `RoleURI`
    FOREIGN KEY (`RoleURI` )
    REFERENCES `flower`.`Role` (`RoleURI` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`UserAssignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`UserAssignment` ;

CREATE  TABLE IF NOT EXISTS `flower`.`UserAssignment` (
  `UserURI` VARCHAR(255) NOT NULL ,
  `RoleURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`UserURI`, `RoleURI`) ,
  INDEX `User_UserAssignment` (`UserURI` ASC) ,
  INDEX `Role_UserAssignment` (`RoleURI` ASC) ,
  CONSTRAINT `User_UserAssignment`
    FOREIGN KEY (`UserURI` )
    REFERENCES `flower`.`User` (`UserURI` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Role_UserAssignment`
    FOREIGN KEY (`RoleURI` )
    REFERENCES `flower`.`Role` (`RoleURI` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`URIGenerator`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`URIGenerator` ;

CREATE  TABLE IF NOT EXISTS `flower`.`URIGenerator` (
  `BaseURI` VARCHAR(255) NOT NULL ,
  `Number` MEDIUMTEXT NOT NULL ,
  PRIMARY KEY (`BaseURI`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`Plugin`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`Plugin` ;

CREATE  TABLE IF NOT EXISTS `flower`.`Plugin` (
  `PluginURI` VARCHAR(255) NOT NULL ,
  `Title` VARCHAR(100) NOT NULL ,
  `Description` TEXT NULL ,
  `Classname` VARCHAR(255) NOT NULL ,
  `Active` TINYINT(1) NOT NULL ,
  `PluginType` VARCHAR(255) NOT NULL ,
  `BundleID` MEDIUMTEXT NOT NULL ,
  PRIMARY KEY (`PluginURI`) ,
  UNIQUE INDEX `PluginURI_UNIQUE` (`PluginURI` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`PluginInstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`PluginInstance` ;

CREATE  TABLE IF NOT EXISTS `flower`.`PluginInstance` (
  `PluginInstanceID` INT NOT NULL AUTO_INCREMENT ,
  `PluginURI` VARCHAR(255) NOT NULL ,
  `Active` TINYINT(1) NOT NULL ,
  `CronString` VARCHAR(255) NOT NULL ,
  `OwnerURI` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`PluginInstanceID`) ,
  INDEX `PluginType` (`PluginURI` ASC) ,
  CONSTRAINT `PluginType`
    FOREIGN KEY (`PluginURI` )
    REFERENCES `flower`.`Plugin` (`PluginURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`PluginConfiguration`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`PluginConfiguration` ;

CREATE  TABLE IF NOT EXISTS `flower`.`PluginConfiguration` (
  `PluginConfigurationID` INT NOT NULL AUTO_INCREMENT ,
  `PluginInstanceID` INT NOT NULL ,
  PRIMARY KEY (`PluginConfigurationID`) ,
  INDEX `pip` (`PluginInstanceID` ASC) ,
  CONSTRAINT `pip`
    FOREIGN KEY (`PluginInstanceID` )
    REFERENCES `flower`.`PluginInstance` (`PluginInstanceID` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`PluginConfigurationContext`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`PluginConfigurationContext` ;

CREATE  TABLE IF NOT EXISTS `flower`.`PluginConfigurationContext` (
  `TaskURI` VARCHAR(255) NOT NULL ,
  `CaseURI` VARCHAR(255) NOT NULL ,
  `PluginConfigurationID` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`PluginConfigurationID`, `CaseURI`, `TaskURI`) ,
  INDEX `Context2Task` (`TaskURI` ASC) ,
  INDEX `Context2Case` (`CaseURI` ASC) ,
  INDEX `PluginConfigurationContext` (`PluginConfigurationID` ASC) ,
  CONSTRAINT `Context2Task`
    FOREIGN KEY (`TaskURI` )
    REFERENCES `flower`.`Task` (`TaskURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `Context2Case`
    FOREIGN KEY (`CaseURI` )
    REFERENCES `flower`.`Case` (`CaseURI` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `PluginConfigurationContext`
    FOREIGN KEY (`PluginConfigurationID` )
    REFERENCES `flower`.`PluginConfiguration` (`PluginConfigurationID` )
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `flower`.`PluginConfigurationProperties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `flower`.`PluginConfigurationProperties` ;

CREATE  TABLE IF NOT EXISTS `flower`.`PluginConfigurationProperties` (
  `Key` VARCHAR(255) NOT NULL ,
  `PluginConfigurationID` INT NOT NULL ,
  `Value` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`Key`, `PluginConfigurationID`) ,
  INDEX `PConfig` (`PluginConfigurationID` ASC) ,
  CONSTRAINT `PConfig`
    FOREIGN KEY (`PluginConfigurationID` )
    REFERENCES `flower`.`PluginConfiguration` (`PluginConfigurationID` )
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `flower`.`CaseType`
-- -----------------------------------------------------
START TRANSACTION;
USE `flower`;
INSERT INTO `flower`.`CaseType` (`CaseTypeURI`, `Title`, `Description`) VALUES ('http://stefanhuber.at/flower/test/ecommerce_case', 'Ecommerce Project', 'This CaseType represents an Ecommerce Project');

COMMIT;

-- -----------------------------------------------------
-- Data for table `flower`.`Case`
-- -----------------------------------------------------
START TRANSACTION;
USE `flower`;
INSERT INTO `flower`.`Case` (`CaseURI`, `Title`, `Description`, `CreationDate`, `CaseTypeURI`) VALUES ('http://stefanhuber.at/flower/test/case_1', 'My Case title', 'Case 1 Description', '2001-10-10 23:59:59', 'http://stefanhuber.at/flower/test/ecommerce_case');

COMMIT;

-- -----------------------------------------------------
-- Data for table `flower`.`User`
-- -----------------------------------------------------
START TRANSACTION;
USE `flower`;
INSERT INTO `flower`.`User` (`UserURI`, `UserName`, `FirstName`, `LastName`, `Password`) VALUES ('http://stefanhuber.at/user/stefan', 'Stefan.Huber', 'Stefan', 'Huber', 'blub');

COMMIT;
