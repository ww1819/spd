package com.spd.system.service;

import com.spd.system.domain.SysPrintDocRows;

/**
 * 单据打印每页行数 服务层
 *
 * @author spd
 */
public interface ISysPrintDocRowsService
{
    /**
     * 按单据类型查询；无记录时返回 null（由控制层返回默认 6）。
     */
    SysPrintDocRows selectByDocKind(String docKind);

    /**
     * 更新或插入
     */
    int upsert(SysPrintDocRows row);
}
