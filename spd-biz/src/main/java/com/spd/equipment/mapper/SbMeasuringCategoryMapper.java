package com.spd.equipment.mapper;

import java.util.List;
import com.spd.equipment.domain.SbMeasuringCategory;

public interface SbMeasuringCategoryMapper {

    List<SbMeasuringCategory> selectList(SbMeasuringCategory q);

    SbMeasuringCategory selectById(String id);

    int insert(SbMeasuringCategory row);

    int update(SbMeasuringCategory row);
}
