package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbMeasuringCategory;

public interface ISbMeasuringCategoryService {

    List<SbMeasuringCategory> selectList(SbMeasuringCategory q);
    SbMeasuringCategory selectById(String id);
    int insert(SbMeasuringCategory row);
    int update(SbMeasuringCategory row);
    int deleteById(String id);
}
