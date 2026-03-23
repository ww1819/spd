-- 用户界面个性化配置（如报表列显隐），按 user_id + config_key 唯一
CREATE TABLE IF NOT EXISTS sys_user_ui_config (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value LONGTEXT COMMENT '配置值JSON',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_config (user_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户界面个性化配置';
