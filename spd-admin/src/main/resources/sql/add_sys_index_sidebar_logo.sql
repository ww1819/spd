-- 侧边栏 Logo 切换参数（公共）
-- 键名：sys.index.sidebarLogo
-- 存放目录：spd-ui\src\assets\logo\
-- 参数值：1=默认 aisipute-wide.png；2=aisipute-wide2.png
-- 修改后请在「参数设置」点「刷新缓存」，并刷新浏览器页面

-- 纠正误建键名（如 config_key='1' 且名称含首页 log/logo）
UPDATE sys_config
SET config_key   = 'sys.index.sidebarLogo',
    config_name  = '设置首页logo',
    config_value = CASE WHEN config_value IN ('1', '2') THEN config_value ELSE '1' END,
    config_type  = IFNULL(NULLIF(config_type, ''), 'Y'),
    remark       = '存放地址: spd-ui\\src\\assets\\logo\\aisipute-wide.png。图片名称 aisipute-wide2.png。参数值 1=默认 aisipute-wide.png；2=aisipute-wide2.png。修改后请刷新参数缓存并刷新页面。',
    update_time  = sysdate()
WHERE config_key = '1'
  AND (config_name LIKE '%首页%log%' OR config_name LIKE '%首页%logo%' OR config_name LIKE '%设置首页%');

-- 已存在正式键则补全备注/名称
UPDATE sys_config
SET config_name  = '设置首页logo',
    config_value = CASE WHEN config_value IN ('1', '2') THEN config_value ELSE '1' END,
    remark       = '存放地址: spd-ui\\src\\assets\\logo\\aisipute-wide.png。图片名称 aisipute-wide2.png。参数值 1=默认 aisipute-wide.png；2=aisipute-wide2.png。修改后请刷新参数缓存并刷新页面。',
    update_time  = sysdate()
WHERE config_key = 'sys.index.sidebarLogo';

-- 不存在则新增（默认 1）
INSERT INTO sys_config (
  config_name, config_key, config_value, config_type, create_by, create_time, remark
)
SELECT
  '设置首页logo',
  'sys.index.sidebarLogo',
  '1',
  'Y',
  'admin',
  sysdate(),
  '存放地址: spd-ui\\src\\assets\\logo\\aisipute-wide.png。图片名称 aisipute-wide2.png。参数值 1=默认 aisipute-wide.png；2=aisipute-wide2.png。修改后请刷新参数缓存并刷新页面。'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_config WHERE config_key = 'sys.index.sidebarLogo'
);
