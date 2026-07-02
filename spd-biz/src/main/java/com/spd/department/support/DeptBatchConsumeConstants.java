package com.spd.department.support;

/**
 * 科室批量消耗业务常量。
 */
public final class DeptBatchConsumeConstants
{
    /**
     * 历史数据：早期自动核销写入 create_by/audit_by 的占位文案（新数据改写 sys_user.user_id，如 3105）。
     * 列表查询仍兼容此值展示为「自动核销」。
     */
    public static final String AUTO_WRITE_OFF_OPERATOR = "自动核销";

    private DeptBatchConsumeConstants()
    {
    }
}
