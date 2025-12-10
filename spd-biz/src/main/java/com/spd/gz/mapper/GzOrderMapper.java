package com.spd.gz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzOrderEntry;

/**
 * 高值入库Mapper接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface GzOrderMapper
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
     * 删除高值入库
     *
     * @param id 高值入库主键
     * @return 结果
     */
    public int deleteGzOrderById(Long id);

    /**
     * 批量新增高值退货明细
     *
     * @param gzOrderEntryList 高值退货明细列表
     * @return 结果
     */
    public int batchGzOrderEntry(List<GzOrderEntry> gzOrderEntryList);


    /**
     * 通过高值入库主键删除高值退货明细信息
     *
     * @param id 高值入库ID
     * @return 结果
     */
    public int deleteGzOrderEntryByParenId(Long id);

    /**
     * 查询当天最大的单号
     * @param prefix 单号前缀（GZRK或GZCK）
     * @param date 日期
     * @return
     */
    String selectMaxBillNo(@Param("prefix") String prefix, @Param("date") String date);

    /**
     * 逻辑删除
     * @param entry
     * @return
     */
    int updateGzOrderEntry(GzOrderEntry entry);

    /**
     * 根据院内码查询是否有未出库的出库单
     * @param inHospitalCode 院内码
     * @return 出库单号列表
     */
    List<String> selectOutboundOrderNosByInHospitalCode(String inHospitalCode);
}
