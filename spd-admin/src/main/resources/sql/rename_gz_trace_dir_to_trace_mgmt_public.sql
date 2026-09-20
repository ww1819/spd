-- 公共：高值分栏二级目录 3960 标题「高值追溯」→「追溯管理」（叶子页 1238 仍名「高值追溯」）
UPDATE sys_menu
SET menu_name = '追溯管理',
    remark = '高值核销确认/高值追溯',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 3960
  AND menu_type = 'M'
  AND path = 'gzTraceMgmt';
