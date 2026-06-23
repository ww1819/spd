package com.spd.his.domain.dto;

import lombok.Data;

/**
 * 历史计费镜像补全执行科室结果。
 */
@Data
public class HisExecDeptBackfillResultVo
{
    /** 镜像表成功补全条数 */
    private int updatedCount;
    /** 本地已有执行科室或未匹配到可更新行 */
    private int skippedCount;
    /** HIS 源数据本身无执行科室 */
    private int hisMissingExecCount;
    /** HIS 有数据但本地镜像不存在 */
    private int notFoundCount;
    /** 统一表从镜像同步补全条数（兜底） */
    private int unifiedSyncedCount;
}
