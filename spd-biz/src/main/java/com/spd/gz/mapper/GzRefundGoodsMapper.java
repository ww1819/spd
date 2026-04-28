package com.spd.gz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.domain.GzRefundGoodsEntry;

/**
 * 高值退货Mapper接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface GzRefundGoodsMapper
{
    /**
     * 查询高值退货
     *
     * @param id 高值退货主键
     * @return 高值退货
     */
    public GzRefundGoods selectGzRefundGoodsById(Long id);

    /**
     * 查询高值退货列表
     *
     * @param gzRefundGoods 高值退货
     * @return 高值退货集合
     */
    public List<GzRefundGoods> selectGzRefundGoodsList(GzRefundGoods gzRefundGoods);

    /** 查询高值退库列表（gz_refund_stock） */
    public List<GzRefundGoods> selectGzRefundStockList(GzRefundGoods gzRefundGoods);

    /**
     * 新增高值退货
     *
     * @param gzRefundGoods 高值退货
     * @return 结果
     */
    public int insertGzRefundGoods(GzRefundGoods gzRefundGoods);

    /**
     * 修改高值退货
     *
     * @param gzRefundGoods 高值退货
     * @return 结果
     */
    public int updateGzRefundGoods(GzRefundGoods gzRefundGoods);

    /** 新增高值退库（gz_refund_stock） */
    public int insertGzRefundStock(GzRefundGoods gzRefundGoods);

    /** 修改高值退库（gz_refund_stock） */
    public int updateGzRefundStock(GzRefundGoods gzRefundGoods);

    /**
     * 删除高值退货
     *
     * @param id 高值退货主键
     * @return 结果
     */
    public int deleteGzRefundGoodsById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 删除高值退库（gz_refund_stock） */
    public int deleteGzRefundStockById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 批量删除高值退货明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzRefundGoodsEntryByParenIds(@Param("array") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 批量新增高值退货明细
     *
     * @param gzRefundGoodsEntryList 高值退货明细列表
     * @return 结果
     */
    public int batchGzRefundGoodsEntry(List<GzRefundGoodsEntry> gzRefundGoodsEntryList);

    /** 批量新增高值退库明细（gz_refund_stock_entry） */
    public int batchGzRefundStockEntry(List<GzRefundGoodsEntry> gzRefundGoodsEntryList);


    /**
     * 通过高值退货主键删除高值退货明细信息
     *
     * @param id 高值退货ID
     * @return 结果
     */
    public int deleteGzRefundGoodsEntryByParenId(@Param("parenId") Long id, @Param("deleteBy") String deleteBy);

    /** 通过主表ID逻辑删除高值退库明细 */
    public int deleteGzRefundStockEntryByParenId(@Param("parenId") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 查询当天最大的单号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);

    /** 按单号前缀 + 日期查询当天最大单号（避免 GZTH 与 GZTK 流水互相干扰） */
    String selectMaxBillNoByPrefix(@Param("prefix") String prefix, @Param("date") String date);

    /** 按退库表查询当天最大单号 */
    String selectMaxStockBillNoByPrefix(@Param("prefix") String prefix, @Param("date") String date);

    /**
     * 逻辑删除
     * @param entry
     * @return
     */
    int updateGzRefundGoodsEntry(GzRefundGoodsEntry entry);

    /**
     * 查询主单下有效明细ID列表
     */
    List<Long> selectActiveRefundGoodsEntryIdsByParenId(@Param("parenId") Long parenId);

    /** 查询主单下有效退库明细ID列表 */
    List<Long> selectActiveRefundStockEntryIdsByParenId(@Param("parenId") Long parenId);

    /**
     * 查询主单下有效明细
     */
    List<GzRefundGoodsEntry> selectActiveRefundGoodsEntriesByParenId(@Param("parenId") Long parenId);

    /** 查询主单下有效退库明细 */
    List<GzRefundGoodsEntry> selectActiveRefundStockEntriesByParenId(@Param("parenId") Long parenId);

    /**
     * 按明细ID更新退货/退库明细（增量更新）
     */
    int updateGzRefundGoodsEntryById(GzRefundGoodsEntry entry);

    /** 按明细ID更新退库明细 */
    int updateGzRefundStockEntryById(GzRefundGoodsEntry entry);

    /** 查询高值退库主单 */
    GzRefundGoods selectGzRefundStockById(Long id);
}
