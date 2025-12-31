-- ============================================
-- 创建68分类表
-- ============================================

DROP TABLE IF EXISTS fd_category68;
CREATE TABLE fd_category68 (
    category68_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '68分类ID',
    parent_id BIGINT(20) DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    category68_code VARCHAR(50) DEFAULT '' COMMENT '68分类编码',
    category68_name VARCHAR(100) NOT NULL COMMENT '68分类名称',
    del_flag INT(1) DEFAULT 0 COMMENT '删除标识（0存在 1删除）',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    PRIMARY KEY (category68_id),
    KEY idx_parent_id (parent_id)
) ENGINE=INNODB AUTO_INCREMENT=1 COMMENT='68分类表';

