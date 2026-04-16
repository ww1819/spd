package com.spd.gz.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.spd.gz.domain.GzDepotInventory;
import org.apache.ibatis.annotations.Param;

/**
 * 高值备货库存明细Mapper接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface GzDepotInventoryMapper
{
    /**
     * 查询高值备货库存明细
     *
     * @param id 高值备货库存明细主键
     * @return 高值备货库存明细
     */
    public GzDepotInventory selectGzDepotInventoryById(Long id);

    /**
     * 查询高值备货库存明细列表
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 高值备货库存明细集合
     */
    public List<GzDepotInventory> selectGzDepotInventoryList(GzDepotInventory gzDepotInventory);

    /**
     * 新增高值备货库存明细
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    public int insertGzDepotInventory(GzDepotInventory gzDepotInventory);

    /**
     * 修改高值备货库存明细
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    public int updateGzDepotInventory(GzDepotInventory gzDepotInventory);

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteGzDepotInventoryById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteGzDepotInventoryByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 按批次查询库存实际数量
     * @param batchNo
     * @return
     */
    BigDecimal selectGzDepotInventoryByBatchNo(String batchNo);

    /**
     * 按批次号查询高值库存明细
     * @param batchNo
     * @return
     */
    GzDepotInventory selectGzDepotInventoryOne(String batchNo);

    /** 按批次号 + 仓库取一条备货库存（优先 qty&gt;0 行，避免 id 最大但已扣为 0 的误命中） */
    GzDepotInventory selectGzDepotInventoryOneByBatchNoAndWarehouse(@Param("batchNo") String batchNo, @Param("warehouseId") Long warehouseId);

    /**
     * 同批次、同仓、指定供应商下所有 qty&gt;0 的备货行（id 升序，用于备货退货 FIFO 扣减）
     */
    List<GzDepotInventory> selectPositiveDepotByBatchWarehouseSupplierAsc(
        @Param("batchNo") String batchNo,
        @Param("warehouseId") Long warehouseId,
        @Param("supplierId") Long supplierId);

    /**
     * 按院内码 + 仓库精确查询一条可用备货库存（数量大于0）
     */
    GzDepotInventory selectByInHospitalCodeAndWarehouse(@Param("inHospitalCode") String inHospitalCode, @Param("warehouseId") Long warehouseId);

    /**
     * 按院内码 + 仓库取最新一条备货库存（含数量为 0，用于退库回写累加）
     */
    GzDepotInventory selectLatestDepotByInHospitalCodeAndWarehouse(@Param("inHospitalCode") String inHospitalCode, @Param("warehouseId") Long warehouseId);
}
