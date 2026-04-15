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

    /**
     * 删除高值退货
     *
     * @param id 高值退货主键
     * @return 结果
     */
    public int deleteGzRefundGoodsById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

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


    /**
     * 通过高值退货主键删除高值退货明细信息
     *
     * @param id 高值退货ID
     * @return 结果
     */
    public int deleteGzRefundGoodsEntryByParenId(@Param("parenId") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 查询当天最大的单号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);

    /** 按单号前缀 + 日期查询当天最大单号（避免 GZTH 与 GZTK 流水互相干扰） */
    String selectMaxBillNoByPrefix(@Param("prefix") String prefix, @Param("date") String date);

    /**
     * 逻辑删除
     * @param entry
     * @return
     */
    int updateGzRefundGoodsEntry(GzRefundGoodsEntry entry);
}
