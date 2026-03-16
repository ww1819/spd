package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbCustomerAssetLedger;

public interface ISbCustomerAssetLedgerService {

    List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q);
    SbCustomerAssetLedger selectById(String id);
    int insert(SbCustomerAssetLedger row);
    int update(SbCustomerAssetLedger row);
    int deleteById(String id);
}
