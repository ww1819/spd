package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetCategory;

public interface ISbAssetCategoryService {

    List<SbAssetCategory> selectList(SbAssetCategory q);
    SbAssetCategory selectById(String id);
    int insert(SbAssetCategory row);
    int update(SbAssetCategory row);
    int deleteById(String id);
}
