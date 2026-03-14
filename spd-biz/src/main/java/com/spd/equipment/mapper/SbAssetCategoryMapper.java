package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.SbAssetCategory;

public interface SbAssetCategoryMapper {

    List<SbAssetCategory> selectList(SbAssetCategory q);

    SbAssetCategory selectById(String id);

    int insert(SbAssetCategory row);

    int update(SbAssetCategory row);
}
