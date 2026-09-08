-- 高值核销确认列表（状态+日期）加速索引
-- 已在开发库 aspt 执行；生产可按需执行（先 SHOW INDEX 确认是否已存在）。

ALTER TABLE his_mirror_consume_link
  ADD INDEX idx_hmcl_tenant_confirm_trace (
    tenant_id, confirm_status, del_flag, gz_dep_inventory_id, traceability_id
  );

ALTER TABLE his_mirror_consume_link
  ADD INDEX idx_hmcl_tenant_confirm_legacy (
    tenant_id, confirm_status, del_flag, gz_dep_inventory_id, dept_batch_consume_id
  );

ALTER TABLE gz_traceability
  ADD INDEX idx_gz_tr_list_ctime (
    tenant_id, trace_source, order_status, del_flag, create_time
  );
