-- 效能分析报表：原 menu_type=M 且无 component，前端路由无页面组件导致空白
-- 绑定本地页面 datacenter/efficiencyAnalysis/index

UPDATE sys_menu
SET menu_type = 'C',
    path = 'efficiencyAnalysis',
    component = 'datacenter/efficiencyAnalysis/index',
    is_frame = '1',
    update_time = NOW()
WHERE menu_id = 1502
  AND menu_name = '效能分析报表';
