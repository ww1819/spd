package com.spd.foundation.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.spd.foundation.domain.FdMaterialHisChargeBind;

@Mapper
@Repository
public interface FdMaterialHisChargeBindMapper
{
    int insert(FdMaterialHisChargeBind row);

    /** 结束该档案当前开放履历 */
    int closeOpenByMaterialId(@Param("tenantId") String tenantId,
        @Param("materialId") Long materialId,
        @Param("effectiveTo") Date effectiveTo,
        @Param("unbindBy") String unbindBy);

    List<FdMaterialHisChargeBind> selectByMaterialId(@Param("tenantId") String tenantId,
        @Param("materialId") Long materialId,
        @Param("currentOnly") Boolean currentOnly);

    List<FdMaterialHisChargeBind> selectByChargeItemId(@Param("tenantId") String tenantId,
        @Param("chargeItemId") String chargeItemId,
        @Param("currentOnly") Boolean currentOnly);

    List<FdMaterialHisChargeBind> selectList(@Param("tenantId") String tenantId,
        @Param("materialId") Long materialId,
        @Param("chargeItemId") String chargeItemId,
        @Param("currentOnly") Boolean currentOnly,
        @Param("materialKeyword") String materialKeyword,
        @Param("chargeKeyword") String chargeKeyword);
}
