package com.spd.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.department.domain.BasApplyTemplate;
import com.spd.department.domain.BasApplyTemplateEntry;
import com.spd.department.mapper.BasApplyTemplateMapper;
import com.spd.department.service.IBasApplyTemplateService;

/**
 * 科室申领/申购制单模板Service业务层处理
 * （明细含 warehouseId，申购模板 SG 引用时带入申购单）
 *
 * @author spd
 */
@Service
public class BasApplyTemplateServiceImpl implements IBasApplyTemplateService {

    @Autowired
    private BasApplyTemplateMapper basApplyTemplateMapper;

    @Override
    public List<BasApplyTemplate> selectBasApplyTemplateList(BasApplyTemplate template) {
        if (template == null) {
            template = new BasApplyTemplate();
        }
        if (StringUtils.isEmpty(template.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            template.setTenantId(SecurityUtils.getCustomerId());
        }
        // 未传类型时默认只查申领，避免 SL/SG 混出
        if (StringUtils.isEmpty(template.getTemplateType())) {
            template.setTemplateType("SL");
        }
        return basApplyTemplateMapper.selectBasApplyTemplateList(template);
    }

    @Override
    public BasApplyTemplate selectBasApplyTemplateById(Long id) {
        return basApplyTemplateMapper.selectBasApplyTemplateById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasApplyTemplate(BasApplyTemplate template) {
        template.setDelFlag(0);
        // 未传类型时默认申领模板，避免与申购(SG)混库
        if (StringUtils.isEmpty(template.getTemplateType())) {
            template.setTemplateType("SL");
        }
        int rows = basApplyTemplateMapper.insertBasApplyTemplate(template);
        List<BasApplyTemplateEntry> entryList = template.getEntryList();
        if (entryList != null && !entryList.isEmpty()) {
            for (int i = 0; i < entryList.size(); i++) {
                BasApplyTemplateEntry e = entryList.get(i);
                e.setParenId(template.getId());
                e.setSortOrder(i + 1);
            }
            basApplyTemplateMapper.batchInsertEntry(entryList);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasApplyTemplateById(Long id) {
        String deleteBy = SecurityUtils.getUserIdStr();
        return basApplyTemplateMapper.deleteBasApplyTemplateById(id, deleteBy != null ? deleteBy : "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasApplyTemplate(BasApplyTemplate template) {
        int rows = basApplyTemplateMapper.updateBasApplyTemplate(template);
        if (template.getId() != null) {
            String deleteBy = SecurityUtils.getUserIdStr();
            basApplyTemplateMapper.deleteEntryByParenId(template.getId(), deleteBy != null ? deleteBy : "");
            List<BasApplyTemplateEntry> entryList = template.getEntryList();
            if (entryList != null && !entryList.isEmpty()) {
                for (int i = 0; i < entryList.size(); i++) {
                    BasApplyTemplateEntry e = entryList.get(i);
                    e.setParenId(template.getId());
                    e.setSortOrder(i + 1);
                }
                basApplyTemplateMapper.batchInsertEntry(entryList);
            }
        }
        return rows;
    }
}
