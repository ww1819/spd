package com.spd.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.domain.BasApplyTemplate;
import com.spd.department.mapper.BasApplyTemplateMapper;
import com.spd.department.service.IBasApplyTemplateService;

/**
 * 科室申领制单模板Service业务层处理
 *
 * @author spd
 */
@Service
public class BasApplyTemplateServiceImpl implements IBasApplyTemplateService {

    @Autowired
    private BasApplyTemplateMapper basApplyTemplateMapper;

    @Override
    public List<BasApplyTemplate> selectBasApplyTemplateList(BasApplyTemplate template) {
        return basApplyTemplateMapper.selectBasApplyTemplateList(template);
    }

    @Override
    public BasApplyTemplate selectBasApplyTemplateById(Long id) {
        return basApplyTemplateMapper.selectBasApplyTemplateById(id);
    }
}
