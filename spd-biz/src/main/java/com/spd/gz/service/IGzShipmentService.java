package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzShipment;

/**
 * 高值出库Service接口
 *
 * @author spd
 * @date 2024-12-08
 */
public interface IGzShipmentService
{
    /**
     * 查询高值出库
     *
     * @param id 高值出库主键
     * @return 高值出库
     */
    public GzShipment selectGzShipmentById(Long id);

    /**
     * 查询高值出库列表
     *
     * @param gzShipment 高值出库
     * @return 高值出库集合
     */
    public List<GzShipment> selectGzShipmentList(GzShipment gzShipment);

    /**
     * 新增高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    public int insertGzShipment(GzShipment gzShipment);

    /**
     * 修改高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    public int updateGzShipment(GzShipment gzShipment);

    /**
     * 删除高值出库信息
     *
     * @param id 高值出库主键
     * @return 结果
     */
    public int deleteGzShipmentById(Long id);

    /**
     * 审核高值出库
     * @param id
     * @return
     */
    int auditGzShipment(String id);
}

