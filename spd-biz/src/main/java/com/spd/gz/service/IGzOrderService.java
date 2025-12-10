package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzOrder;

/**
 * 高值入库Service接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface IGzOrderService
{
    /**
     * 查询高值入库
     *
     * @param id 高值入库主键
     * @return 高值入库
     */
    public GzOrder selectGzOrderById(Long id);

    /**
     * 查询高值入库列表
     *
     * @param gzOrder 高值入库
     * @return 高值入库集合
     */
    public List<GzOrder> selectGzOrderList(GzOrder gzOrder);

    /**
     * 新增高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    public int insertGzOrder(GzOrder gzOrder);

    /**
     * 修改高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    public int updateGzOrder(GzOrder gzOrder);

    /**
     * 删除高值入库信息
     *
     * @param id 高值入库主键
     * @return 结果
     */
    public int deleteGzOrderById(Long id);

    /**
     * 审核高值入库
     * @param id
     * @return
     */
    int auditGzOrder(String id);

    /**
     * 根据院内码查询是否有未出库的出库单
     * @param inHospitalCode 院内码
     * @return 出库单号列表
     */
    List<String> selectOutboundOrderNosByInHospitalCode(String inHospitalCode);
}
