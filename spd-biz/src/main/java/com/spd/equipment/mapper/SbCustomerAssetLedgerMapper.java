package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbCustomerAssetLedger;

public interface SbCustomerAssetLedgerMapper {

    List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q);

    SbCustomerAssetLedger selectById(String id);

    /** 取客户下设备流水号最大值(纯数字部分)，用于生成下一流水号 */
    Integer selectMaxSerialNoNumeric(@Param("customerId") String customerId);

    /** 取客户下某68分类的档案号最大序号(XXXX部分) */
    Integer selectMaxArchiveNoSeq(@Param("customerId") String customerId, @Param("category68Id") String category68Id);

    int insert(SbCustomerAssetLedger row);

    int update(SbCustomerAssetLedger row);
}
