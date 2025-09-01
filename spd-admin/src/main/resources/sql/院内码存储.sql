USE spd;

DROP PROCEDURE IF EXISTS pro_gz_bill_entry_in_hospital_code_by_parentId;

DELIMITER $$

CREATE
DEFINER = 'root'@'localhost'
PROCEDURE pro_gz_bill_entry_in_hospital_code_by_parentId(IN parentId bigint, OUT msg varchar(200))
BEGIN
    DECLARE sheetIdCount INT DEFAULT 0;
    DECLARE stockCount INT DEFAULT 0;
    DECLARE sheetId BIGINT DEFAULT 0;
    DECLARE entryId BIGINT;
    DECLARE orderNo VARCHAR(100);
    DECLARE wareHouseId BIGINT;
    DECLARE supplierId BIGINT;
    DECLARE materialId BIGINT;
    DECLARE price DECIMAL(18, 6);
    DECLARE qty DECIMAL(18, 6);
    DECLARE batchNo VARCHAR(100);
    DECLARE batchNumber VARCHAR(200);
    DECLARE endTime DATETIME;
    DECLARE intQty BIGINT DEFAULT 1;
    DECLARE inHospitalCode VARCHAR(200);
    DECLARE masterBarcode VARCHAR(200);
    DECLARE secondaryBarcode VARCHAR(200);
    declare cur_entry cursor for
SELECT b.id, a.suppler_id, a.order_no, a.warehouse_id, b.material_id, b.price, b.qty, b.batch_no, b.batch_number, b.end_time, b.master_barcode, b.secondary_barcode
FROM `gz_order` a INNER JOIN `gz_order_entry` b ON a.id = b.paren_id
WHERE a.id = parentId;


IF ISNULL(parentId) THEN
        SET msg = '单号为空！';
ELSE
SELECT COUNT(*) INTO stockCount FROM gz_order_entry_inhospitalcode_list WHERE parent_id = parentId;
IF stockCount > 0 THEN
            SET msg = '当前单据已经生成条码库存！';
ELSE
SELECT count(*) INTO sheetIdCount FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm';
if sheetIdCount = 0 then
                INSERT INTO sys_sheet_id(business_type, sheet_type, stock_id, dept_id, sheet_id) VALUES
                    ('高值', 'gzynm', NULL, NULL, 0);
                    SET sheetId = 0;
ELSE
SELECT sheet_id INTO sheetId FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm';
IF isnull(sheetId) THEN
                  SET sheetId = 0;
END IF;
end if;




OPEN cur_entry;
read_loop: LOOP
                FETCH cur_entry INTO entryId, supplierId, orderNo, wareHouseId, materialId, price, qty, batchNo, batchNumber, endTime, masterBarcode, secondaryBarcode;

                IF qty > 0 THEN
                    set intQty = 0;
                    WHILE intQty <= qty DO
                            SET intQty = intQty + 1;
                            set sheetId = sheetId + 1;
                            SET inHospitalCode = CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10), '', right(CAST(sheetId + 1000000 AS CHAR),6));


INSERT INTO gz_order_entry_inhospitalcode_list (parent_id, code, detail_id, material_id, price, qty, batch_no, batch_number, master_barcode, secondary_barcode, end_date, in_hospital_code, create_date)
VALUES (parentId, orderNo, entryId, materialId, price, 1, batchNo, batchNumber, masterBarcode, secondaryBarcode, endTime, inHospitalCode, NOW());

INSERT INTO gz_depot_inventory(qty, material_id, warehouse_id, unit_price, amt, batch_no, material_no, material_date, warehouse_date, supplier_id, master_barcode, secondary_barcode, in_hospital_code)
VALUES (1, materialId, wareHouseId, price, price, batchNo, batchNumber, NULL, NOW(), supplierId, masterBarcode, secondaryBarcode, inHospitalCode);
UPDATE sys_sheet_id SET sheet_id = sheetId WHERE business_type = '高值' AND sheet_type = 'gzynm';


END WHILE;
END IF;
END LOOP read_loop;

END IF;
END IF;
END
$$

DELIMITER ;