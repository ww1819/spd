package com.spd.hc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.hc.domain.HcBarcodeFlow;
import com.spd.hc.domain.HcBarcodeMaster;
import com.spd.hc.domain.StkLvIoInhospitalBarcode;
import com.spd.gz.domain.GzDepFlow;
import com.spd.gz.domain.GzWhFlow;

public interface HcBarcodeTraceMapper {

    int insertStkLvIoInhospitalBarcode(StkLvIoInhospitalBarcode row);

    int insertHcBarcodeMaster(HcBarcodeMaster row);

    /** 同步当前持有方/状态（appendFlow 后回写） */
    int updateHcBarcodeMasterCurrentState(HcBarcodeMaster row);

    int insertHcBarcodeFlow(HcBarcodeFlow row);

    int insertGzWhFlow(GzWhFlow row);

    int insertGzDepFlow(GzDepFlow row);

    List<HcBarcodeMaster> selectHcBarcodeMasterList(@Param("tenantId") String tenantId,
        @Param("barcodeValue") String barcodeValue,
        @Param("valueLevel") String valueLevel,
        @Param("businessTypeCode") String businessTypeCode,
        @Param("billNo") String billNo,
        @Param("materialName") String materialName);

    List<HcBarcodeFlow> selectHcBarcodeFlowList(@Param("tenantId") String tenantId,
        @Param("barcodeValue") String barcodeValue,
        @Param("hcBarcodeMasterId") String hcBarcodeMasterId);

    HcBarcodeMaster selectHcBarcodeMasterByTenantAndBarcode(@Param("tenantId") String tenantId,
        @Param("barcodeValue") String barcodeValue);

    HcBarcodeMaster selectHcBarcodeMasterById(@Param("tenantId") String tenantId, @Param("id") String id);

    Integer selectNextFlowSeq(@Param("tenantId") String tenantId, @Param("hcBarcodeMasterId") String hcBarcodeMasterId);
}
