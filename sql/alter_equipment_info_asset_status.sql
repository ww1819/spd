-- 修改 equipment_info 表的 asset_status 列长度，以支持更长的状态文本
-- 将 char(1) 改为 varchar(50) 以容纳中文状态文本（如：正常使用、待报废使用中等）

ALTER TABLE `equipment_info` 
MODIFY COLUMN `asset_status` varchar(50) NOT NULL DEFAULT '正常使用' COMMENT '资产状态（正常使用、待维修、待拆分、报废、待报废、已损坏、待报废使用中、待报废未使用）';

