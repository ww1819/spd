DELIMITER $$
 
CREATE OR REPLACE PROCEDURE `AddColumnWithComment`(
    IN tableName VARCHAR(255),
    IN columnName VARCHAR(255),
    IN columnComment VARCHAR(255),
    IN dataType VARCHAR(255)
)
BEGIN
    SET @stmt = CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN `', columnName, '` ', dataType, ' COMMENT "', columnComment, '"');
 
    PREPARE stmt FROM @stmt;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$
 
DELIMITER ;