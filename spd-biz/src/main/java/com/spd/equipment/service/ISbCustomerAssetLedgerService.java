package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbCustomerAssetLedger;

public interface ISbCustomerAssetLedgerService {

    List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q);
    SbCustomerAssetLedger selectById(String id);
    int insert(SbCustomerAssetLedger row);
    int update(SbCustomerAssetLedger row);
    int deleteById(String id);

    /**
     * 导入资产台账：校验所属科室必须在客户科室列表中，生产厂家/供应商可自动新增，资产分类可为空
     * @param list 从Excel解析的列表（含名称、所属科室、生产厂家、供应商等）
     * @return 导入结果消息
     */
    String importAssetLedger(List<SbCustomerAssetLedger> list);
}
