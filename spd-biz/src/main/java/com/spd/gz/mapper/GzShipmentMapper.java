package com.spd.gz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;

/**
 * 高值出库Mapper接口
 *
 * @author spd
 * @date 2024-12-08
 */
public interface GzShipmentMapper
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
     * 删除高值出库
     *
     * @param id 高值出库主键
     * @return 结果
     */
    public int deleteGzShipmentById(Long id);

    /**
     * 批量新增高值出库明细
     *
     * @param gzShipmentEntryList 高值出库明细列表
     * @return 结果
     */
    public int batchGzShipmentEntry(List<GzShipmentEntry> gzShipmentEntryList);

    /**
     * 通过高值出库主键删除高值出库明细信息
     *
     * @param id 高值出库ID
     * @return 结果
     */
    public int deleteGzShipmentEntryByParenId(Long id);

    /**
     * 查询当天最大的单号
     * @param prefix 单号前缀（GZCK）
     * @param date 日期
     * @return
     */
    String selectMaxBillNo(@Param("prefix") String prefix, @Param("date") String date);

    /**
     * 逻辑删除
     * @param entry
     * @return
     */
    int updateGzShipmentEntry(GzShipmentEntry entry);
}

