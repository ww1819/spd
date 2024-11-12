package com.spd.gz.mapper;

import java.util.List;
import com.spd.gz.domain.GzRefundStock;
import com.spd.gz.domain.GzRefundStockEntry;

/**
 * 高值退库Mapper接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface GzRefundStockMapper
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
     * 删除高值退库
     *
     * @param id 高值退库主键
     * @return 结果
     */
    public int deleteGzRefundStockById(Long id);

    /**
     * 批量删除高值退库
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzRefundStockByIds(Long[] ids);

    /**
     * 批量删除高值退货明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzRefundStockEntryByParenIds(Long[] ids);

    /**
     * 批量新增高值退货明细
     *
     * @param gzRefundStockEntryList 高值退货明细列表
     * @return 结果
     */
    public int batchGzRefundStockEntry(List<GzRefundStockEntry> gzRefundStockEntryList);


    /**
     * 通过高值退库主键删除高值退货明细信息
     *
     * @param id 高值退库ID
     * @return 结果
     */
    public int deleteGzRefundStockEntryByParenId(Long id);

    /**
     * 查询当天最大的流水号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);

    /**
     * 逻辑删除
     * @param entry
     * @return
     */
    int updateGzRefundStockEntry(GzRefundStockEntry entry);
}
