package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzRefundGoods;

/**
 * 高值退货Service接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface IGzRefundGoodsService
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
     * 删除高值退货信息
     *
     * @param id 高值退货主键
     * @return 结果
     */
    public int deleteGzRefundGoodsById(Long id);

    /**
     * 审核高值退货信息
     * @param id
     * @return
     */
    int auditGoods(String id);

    /**
     * 查询高值备货退库
     */
    GzRefundGoods selectGzRefundStockById(Long id);

    /**
     * 查询高值备货退库列表
     */
    List<GzRefundGoods> selectGzRefundStockList(GzRefundGoods gzRefundGoods);

    /**
     * 新增高值备货退库
     */
    int insertGzRefundStock(GzRefundGoods gzRefundGoods);

    /**
     * 修改高值备货退库
     */
    int updateGzRefundStock(GzRefundGoods gzRefundGoods);

    /**
     * 删除高值备货退库
     */
    int deleteGzRefundStockById(Long id);

    /**
     * 审核高值备货退库
     */
    int auditStock(String id);
}
