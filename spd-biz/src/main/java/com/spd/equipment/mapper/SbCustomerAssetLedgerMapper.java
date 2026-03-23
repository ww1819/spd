package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbCustomerAssetLedger;

public interface SbCustomerAssetLedgerMapper {

    List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q);

    /** 按科室查询未删除台账（用于按科室盘点生成明细） */
    List<SbCustomerAssetLedger> selectListByDeptId(@Param("customerId") String customerId, @Param("deptId") String deptId);

    /** 按68分类ID集合查询未删除台账（用于按68分类盘点，含下级分类） */
    List<SbCustomerAssetLedger> selectListByCategory68Ids(@Param("customerId") String customerId, @Param("category68Ids") List<String> category68Ids);

    /** 按存放地点查询未删除台账（用于按存放地点盘点） */
    List<SbCustomerAssetLedger> selectListByStoragePlace(@Param("customerId") String customerId, @Param("storagePlace") String storagePlace);

    /** 查询客户下台账中不重复的存放地点列表（用于下拉） */
    List<String> selectDistinctStoragePlace(@Param("customerId") String customerId);

    SbCustomerAssetLedger selectById(String id);

    /** 按客户ID+设备流水号精确匹配一条未删除台账（用于导入时按流水号更新） */
    SbCustomerAssetLedger selectByCustomerIdAndEquipmentSerialNo(@Param("customerId") String customerId, @Param("equipmentSerialNo") String equipmentSerialNo);

    /** 取客户下设备流水号最大值(纯数字部分)，用于生成下一流水号 */
    Integer selectMaxSerialNoNumeric(@Param("customerId") String customerId);

    /** 取客户下某68分类的档案号最大序号(XXXX部分) */
    Integer selectMaxArchiveNoSeq(@Param("customerId") String customerId, @Param("category68Id") String category68Id);

    int insert(SbCustomerAssetLedger row);

    int update(SbCustomerAssetLedger row);
}
