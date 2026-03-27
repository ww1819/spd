-- =============================================================================
-- 依据导出 sys_menu_202603212150 分析的重复项清理（请在备份后执行）
-- 依赖：已创建 sp_hc_merge_sys_menu（见 dedupe_hc_material_sys_menu.sql）
-- =============================================================================

-- ---------- 0) perms 修正（非合并，消除误报重复） ----------
-- 盈亏目录 M：不应与报表页共用 list
UPDATE sys_menu SET perms = '' WHERE menu_id = 1239 AND menu_type = 'M';

-- 发票录入页 C：与「发票新增」F 的 add 冲突；列表页用 list
UPDATE sys_menu SET perms = 'finance:invoice:list' WHERE menu_id = 2207 AND menu_type = 'C';

-- 客户菜单功能管理：F「列表」与 C 重复 list，改为 query（与 material/menu.sql 设计一致）
UPDATE sys_menu SET perms = 'hc:system:customerMenuManage:query' WHERE menu_id = 2103 AND menu_type = 'F';

-- ---------- 1) 删除「与父级 C 同 perms」的多余 F（科室模块；权限仍由父级 list 生效） ----------
-- 如前端强依赖这些按钮行，可改为 UPDATE 为 *:query 并同步接口 @PreAuthorize
DELETE FROM sys_role_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);
DELETE FROM sys_user_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);
DELETE FROM hc_customer_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);
DELETE FROM sys_post_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);
DELETE FROM hc_user_permission_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);
DELETE FROM sys_menu WHERE menu_id IN (1568, 1573, 1577, 1581, 1583, 1589);

-- ---------- 2) 合并「订单发布 / 到货验收」下重复 F（与采购计划审核下按钮 perms 相同） ----------
CALL sp_hc_merge_sys_menu(1270, 3103);
CALL sp_hc_merge_sys_menu(1274, 3104);
CALL sp_hc_merge_sys_menu(1146, 3106);
CALL sp_hc_merge_sys_menu(1150, 3107);
CALL sp_hc_merge_sys_menu(1148, 3108);

-- ---------- 3) 基础资料重复：先合并 F 子按钮，再合并 C 主菜单 ----------
-- 科室
CALL sp_hc_merge_sys_menu(1111, 2231);
CALL sp_hc_merge_sys_menu(1115, 2232);
CALL sp_hc_merge_sys_menu(1116, 2233);
CALL sp_hc_merge_sys_menu(1117, 2234);
CALL sp_hc_merge_sys_menu(1118, 2235);
CALL sp_hc_merge_sys_menu(1092, 2230);

-- 厂家
CALL sp_hc_merge_sys_menu(1129, 2251);
CALL sp_hc_merge_sys_menu(1130, 2252);
CALL sp_hc_merge_sys_menu(1131, 2253);
CALL sp_hc_merge_sys_menu(1132, 2254);
CALL sp_hc_merge_sys_menu(1133, 2255);
CALL sp_hc_merge_sys_menu(1102, 2250);

-- 供应商
CALL sp_hc_merge_sys_menu(1124, 2241);
CALL sp_hc_merge_sys_menu(1125, 2242);
CALL sp_hc_merge_sys_menu(1126, 2243);
CALL sp_hc_merge_sys_menu(1127, 2244);
CALL sp_hc_merge_sys_menu(1128, 2245);
CALL sp_hc_merge_sys_menu(1095, 2240);

-- 财务分类
CALL sp_hc_merge_sys_menu(1139, 2291);
CALL sp_hc_merge_sys_menu(1140, 2292);
CALL sp_hc_merge_sys_menu(1141, 2293);
CALL sp_hc_merge_sys_menu(1142, 2294);
CALL sp_hc_merge_sys_menu(1143, 2295);
CALL sp_hc_merge_sys_menu(1104, 2290);

-- 单位
CALL sp_hc_merge_sys_menu(1174, 2261);
CALL sp_hc_merge_sys_menu(1175, 2262);
CALL sp_hc_merge_sys_menu(1176, 2263);
CALL sp_hc_merge_sys_menu(1177, 2264);
CALL sp_hc_merge_sys_menu(1178, 2265);
CALL sp_hc_merge_sys_menu(1173, 2260);

-- 库房分类：2280 带全量按钮；1179 为旧版无子节点，删除 1179
CALL sp_hc_merge_sys_menu(2280, 1179);

-- 货位：保留 perms=foundation:location:list 的 2270（与 Controller 一致），删除旧行 1550（曾为 view）
CALL sp_hc_merge_sys_menu(2270, 1550);

-- ---------- 4) 科室退库重复 C 菜单（同 component） ----------
CALL sp_hc_merge_sys_menu(1169, 1406);

-- ---------- 5) 可选：gzOrder 两页同 perms（需业务确认后再执行） ----------
-- CALL sp_hc_merge_sys_menu(1197, 1198);
-- 或手工修改 1197/1198 其一的 perms 并同步 Controller

-- ---------- 6) 可选：若依缓存监控与缓存列表同 perms（平台菜单，是否改 perms 自行决定） ----------
-- UPDATE sys_menu SET perms = 'monitor:cache:list' WHERE menu_id = 114;
-- 仍可能与 113 重复；建议查阅所用若依版本对「缓存列表」的约定
