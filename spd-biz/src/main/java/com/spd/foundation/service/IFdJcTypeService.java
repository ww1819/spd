package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdJcType;

public interface IFdJcTypeService
{
    FdJcType selectFdJcTypeById(Long id);

    List<FdJcType> selectFdJcTypeList(FdJcType query);

    int insertFdJcType(FdJcType row);

    int updateFdJcType(FdJcType row);

    int deleteFdJcTypeById(Long id);
}
