package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzRefundStock;

/**
 * 高值退库Service接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface IGzRefundStockService
{
    /**
     * 查询高值退库
     *
     * @param id 高值退库主键
     * @return 高值退库
     */
    public GzRefundStock selectGzRefundStockById(Long id);

    /**
     * 查询高值退库列表
     *
     * @param gzRefundStock 高值退库
     * @return 高值退库集合
     */
    public List<GzRefundStock> selectGzRefundStockList(GzRefundStock gzRefundStock);

    /**
     * 新增高值退库
     *
     * @param gzRefundStock 高值退库
     * @return 结果
     */
    public int insertGzRefundStock(GzRefundStock gzRefundStock);

    /**
     * 修改高值退库
     *
     * @param gzRefundStock 高值退库
     * @return 结果
     */
    public int updateGzRefundStock(GzRefundStock gzRefundStock);

    /**
     * 批量删除高值退库
     *
     * @param id 需要删除的高值退库主键集合
     * @return 结果
     */
    public int deleteGzRefundStockById(Long id);

    /**
     * 审核高值退库信息
     * @param id
     * @return
     */
    int auditStock(String id);
}
