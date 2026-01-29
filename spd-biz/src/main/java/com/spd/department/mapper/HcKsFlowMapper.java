package com.spd.department.mapper;

import com.spd.department.domain.HcKsFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 科室流水Mapper接口 t_hc_ks_flow
 *
 * @author spd
 */
@Mapper
@Repository
public interface HcKsFlowMapper {

    /**
     * 新增科室流水
     */
    int insertHcKsFlow(HcKsFlow flow);
}
