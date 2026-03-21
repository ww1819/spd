package com.spd.foundation.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.enums.TenantEnum;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.service.IFdFinanceCategoryService;

/**
 * 财务分类维护Service业务层处理
 *
 * @author spd
 * @date 2024-03-04
 */
@Service
public class FdFinanceCategoryServiceImpl implements IFdFinanceCategoryService
{
    @Autowired
    private FdFinanceCategoryMapper fdFinanceCategoryMapper;

    @Autowired
    protected Validator validator;

    /**
     * 查询财务分类维护
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 财务分类维护
     */
    @Override
    public FdFinanceCategory selectFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId)
    {
        FdFinanceCategory row = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    /**
     * 查询财务分类维护列表
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 财务分类维护
     */
    @Override
    public List<FdFinanceCategory> selectFdFinanceCategoryList(FdFinanceCategory fdFinanceCategory)
    {
        if (fdFinanceCategory != null && StringUtils.isEmpty(fdFinanceCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdFinanceCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdFinanceCategoryMapper.selectFdFinanceCategoryList(fdFinanceCategory);
    }

    /**
     * 新增财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    @Override
    public int insertFdFinanceCategory(FdFinanceCategory fdFinanceCategory)
    {
        fdFinanceCategory.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFinanceCategory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFinanceCategory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(fdFinanceCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdFinanceCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        if (fdFinanceCategory.getDelFlag() == null)
        {
            fdFinanceCategory.setDelFlag(0);
        }
        if (StringUtils.isEmpty(fdFinanceCategory.getIsUse()))
        {
            fdFinanceCategory.setIsUse("1");
        }
        fdFinanceCategory.setHisId(normalizeHisId(fdFinanceCategory.getHisId()));
        if (importRequiresMandatoryHisId())
        {
            if (isHisIdBlank(fdFinanceCategory.getHisId()))
            {
                throw new ServiceException("衡水市第三人民医院新增财务分类时必须填写HIS财务分类ID");
            }
            assertFinanceHisIdUnique(fdFinanceCategory.getTenantId(), fdFinanceCategory.getHisId(), null);
        }
        else if (isHisIdBlank(fdFinanceCategory.getHisId()))
        {
            fdFinanceCategory.setHisId(null);
        }
        return fdFinanceCategoryMapper.insertFdFinanceCategory(fdFinanceCategory);
    }

    /**
     * 修改财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    @Override
    public int updateFdFinanceCategory(FdFinanceCategory fdFinanceCategory)
    {
        if (fdFinanceCategory.getFinanceCategoryId() == null)
        {
            throw new ServiceException("财务分类主键不能为空");
        }
        FdFinanceCategory before = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(fdFinanceCategory.getFinanceCategoryId());
        if (before == null)
        {
            throw new ServiceException("财务分类不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdFinanceCategory.setTenantId(before.getTenantId());
        fdFinanceCategory.setHisId(before.getHisId());
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFinanceCategory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFinanceCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return fdFinanceCategoryMapper.updateFdFinanceCategory(fdFinanceCategory);
    }

//    /**
//     * 批量删除财务分类维护
//     *
//     * @param financeCategoryIds 需要删除的财务分类维护主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdFinanceCategoryByFinanceCategoryIds(Long[] financeCategoryIds)
//    {
//        return fdFinanceCategoryMapper.deleteFdFinanceCategoryByFinanceCategoryIds(financeCategoryIds);
//    }

    /**
     * 删除财务分类维护信息
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 结果
     */
    @Override
    public int deleteFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId)
    {
        FdFinanceCategory fdFinanceCategory = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId);
        if (fdFinanceCategory == null)
        {
            throw new ServiceException(String.format("财务分类：%s，不存在!", financeCategoryId));
        }
        SecurityUtils.ensureTenantAccess(fdFinanceCategory.getTenantId());
        fdFinanceCategory.setDelFlag(1);
        fdFinanceCategory.setDeleteBy(SecurityUtils.getUserIdStr());
        fdFinanceCategory.setDeleteTime(DateUtils.getNowDate());
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
        fdFinanceCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdFinanceCategoryMapper.updateFdFinanceCategory(fdFinanceCategory);
    }

    /**
     * 批量更新财务分类名称简码（根据名称生成拼音首字母）
     */
    @Override
    public void updateReferred(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            FdFinanceCategory category = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(id);
            if (category == null) {
                continue;
            }
            SecurityUtils.ensureTenantAccess(category.getTenantId());
            String name = category.getFinanceCategoryName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            category.setReferredName(PinyinUtils.getPinyinInitials(name));
            fdFinanceCategoryMapper.updateFdFinanceCategory(category);
        }
    }

    @Override
    public Map<String, Object> validateFinanceCategoryImport(List<FdFinanceCategory> list, Boolean isUpdateSupport)
    {
        clearFinanceImportValidationColumn(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requiresHisId", importRequiresMandatoryHisId());
        ImportRowErrorCollector collector = collectFinanceCategoryImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list != null ? list.size() : 0);
        if (valid && list != null && !list.isEmpty())
        {
            String tenantId = SecurityUtils.getCustomerId();
            int insertCount = 0;
            int updateCount = 0;
            for (FdFinanceCategory row : list)
            {
                if (row == null)
                {
                    continue;
                }
                normalizeFinanceImportRow(row);
                if (StringUtils.isEmpty(row.getFinanceCategoryCode()) && StringUtils.isEmpty(row.getFinanceCategoryName()))
                {
                    continue;
                }
                if (StringUtils.isEmpty(row.getFinanceCategoryCode()) || StringUtils.isEmpty(row.getFinanceCategoryName()))
                {
                    continue;
                }
                FdFinanceCategory existing = fdFinanceCategoryMapper.selectFdFinanceCategoryByCodeAndTenantId(
                    row.getFinanceCategoryCode(), tenantId);
                if (existing == null)
                {
                    insertCount++;
                }
                else if (Boolean.TRUE.equals(isUpdateSupport))
                {
                    updateCount++;
                }
            }
            result.put("insertCount", insertCount);
            result.put("updateCount", updateCount);
        }
        else
        {
            result.put("insertCount", 0);
            result.put("updateCount", 0);
        }
        fillFinanceValidationTexts(list, collector, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdFinanceCategory.class, list));
        return result;
    }

    @Override
    public String importFinanceCategory(List<FdFinanceCategory> list, Boolean isUpdateSupport, String operName, boolean confirmed)
    {
        if (!confirmed)
        {
            throw new ServiceException("请先完成校验并在确认后再导入");
        }
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("导入财务分类数据不能为空！");
        }
        clearFinanceImportValidationColumn(list);
        ImportRowErrorCollector collector = collectFinanceCategoryImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        if (!errors.isEmpty())
        {
            throw new ServiceException("数据已变更或校验未通过，请重新校验后再导入。详情：" + String.join("；", errors));
        }
        String tenantId = SecurityUtils.getCustomerId();
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (FdFinanceCategory row : list)
        {
            if (row == null || StringUtils.isEmpty(row.getFinanceCategoryCode()) || StringUtils.isEmpty(row.getFinanceCategoryName()))
            {
                continue;
            }
            normalizeFinanceImportRow(row);
            try
            {
                BeanValidators.validateWithException(validator, row);
            }
            catch (Exception e)
            {
                throw new ServiceException("导入校验异常：" + e.getMessage());
            }
            row.setReferredName(PinyinUtils.getPinyinInitials(row.getFinanceCategoryName()));
            if (StringUtils.isEmpty(row.getIsUse()))
            {
                row.setIsUse("1");
            }
            FdFinanceCategory existing = fdFinanceCategoryMapper.selectFdFinanceCategoryByCodeAndTenantId(
                row.getFinanceCategoryCode(), tenantId);
            if (existing == null)
            {
                row.setCreateBy(operName);
                row.setDelFlag(0);
                if (StringUtils.isNotEmpty(tenantId))
                {
                    row.setTenantId(tenantId);
                }
                row.setHisId(normalizeHisId(row.getHisId()));
                if (importRequiresMandatoryHisId())
                {
                    if (isHisIdBlank(row.getHisId()))
                    {
                        throw new ServiceException("衡水市第三人民医院导入新增行必须填写HIS财务分类ID");
                    }
                    assertFinanceHisIdUnique(tenantId, row.getHisId(), null);
                }
                else if (isHisIdBlank(row.getHisId()))
                {
                    row.setHisId(null);
                }
                row.setCreateTime(DateUtils.getNowDate());
                fdFinanceCategoryMapper.insertFdFinanceCategory(row);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、财务分类 ").append(row.getFinanceCategoryName()).append(" 导入成功");
            }
            else if (Boolean.TRUE.equals(isUpdateSupport))
            {
                applyFinanceImportUpdate(existing, row, operName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、财务分类 ").append(row.getFinanceCategoryName()).append(" 更新成功");
            }
        }
        successMsg.insert(0, "增量导入完成（仅新增或按选项更新已有编码；更新时不修改HIS财务分类ID）。共处理 " + successNum + " 条，明细如下：");
        markFinanceImportSuccessTexts(list);
        return successMsg.toString();
    }

    private void applyFinanceImportUpdate(FdFinanceCategory existing, FdFinanceCategory row, String operName)
    {
        existing.setFinanceCategoryName(row.getFinanceCategoryName());
        existing.setReferredName(PinyinUtils.getPinyinInitials(row.getFinanceCategoryName()));
        existing.setUpdateBy(operName);
        existing.setUpdateTime(DateUtils.getNowDate());
        fdFinanceCategoryMapper.updateFdFinanceCategory(existing);
    }

    private static boolean importRequiresMandatoryHisId()
    {
        return TenantEnum.HS_003 == TenantEnum.fromCustomerId(SecurityUtils.getCustomerId());
    }

    private static String normalizeHisId(String raw)
    {
        if (raw == null)
        {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty())
        {
            return "";
        }
        if (s.matches("\\d+\\.0+"))
        {
            return s.substring(0, s.indexOf('.'));
        }
        return s;
    }

    private static boolean isHisIdBlank(String v)
    {
        return v == null || v.trim().isEmpty();
    }

    private void assertFinanceHisIdUnique(String tenantId, String hisId, Long excludeId)
    {
        if (isHisIdBlank(hisId))
        {
            return;
        }
        if (fdFinanceCategoryMapper.countFinanceCategoryByTenantAndHisId(tenantId, hisId, excludeId) > 0)
        {
            throw new ServiceException("HIS财务分类ID「" + hisId + "」在本租户下已存在，不能重复");
        }
    }

    private void normalizeFinanceImportRow(FdFinanceCategory row)
    {
        if (row.getFinanceCategoryCode() != null)
        {
            row.setFinanceCategoryCode(row.getFinanceCategoryCode().trim());
        }
        if (row.getFinanceCategoryName() != null)
        {
            row.setFinanceCategoryName(row.getFinanceCategoryName().trim());
        }
        if (row.getHisId() != null)
        {
            row.setHisId(normalizeHisId(row.getHisId()));
        }
    }

    private ImportRowErrorCollector collectFinanceCategoryImportErrors(List<FdFinanceCategory> list, Boolean isUpdateSupport)
    {
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入财务分类数据不能为空");
            return c;
        }
        String tenantId = SecurityUtils.getCustomerId();
        Map<String, Integer> codeFirstRow = new LinkedHashMap<>();
        Map<String, Integer> hisFirstRow = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++)
        {
            FdFinanceCategory row = list.get(i);
            int excelRow = i + 2;
            if (row == null)
            {
                c.addRow(excelRow, "数据为空");
                continue;
            }
            normalizeFinanceImportRow(row);
            if (StringUtils.isEmpty(row.getFinanceCategoryCode()) && StringUtils.isEmpty(row.getFinanceCategoryName()))
            {
                continue;
            }
            if (StringUtils.isEmpty(row.getFinanceCategoryCode()) || StringUtils.isEmpty(row.getFinanceCategoryName()))
            {
                c.addRow(excelRow, "财务分类编码与名称均不能为空");
                continue;
            }
            String code = row.getFinanceCategoryCode();
            if (codeFirstRow.containsKey(code))
            {
                c.addRow(excelRow, "财务分类编码「" + code + "」与第" + codeFirstRow.get(code) + "行重复");
            }
            else
            {
                codeFirstRow.put(code, excelRow);
            }
            FdFinanceCategory existing = fdFinanceCategoryMapper.selectFdFinanceCategoryByCodeAndTenantId(code, tenantId);
            if (existing == null)
            {
                if (importRequiresMandatoryHisId() && isHisIdBlank(row.getHisId()))
                {
                    c.addRow(excelRow, "HIS财务分类ID不能为空（衡水市第三人民医院新增时必填）");
                }
                if (!isHisIdBlank(row.getHisId()))
                {
                    String hid = row.getHisId();
                    if (hisFirstRow.containsKey(hid))
                    {
                        c.addRow(excelRow, "HIS财务分类ID「" + hid + "」与第" + hisFirstRow.get(hid) + "行重复");
                    }
                    else
                    {
                        hisFirstRow.put(hid, excelRow);
                    }
                    if (fdFinanceCategoryMapper.countFinanceCategoryByTenantAndHisId(tenantId, hid, null) > 0)
                    {
                        c.addRow(excelRow, "HIS财务分类ID「" + hid + "」在租户下已存在");
                    }
                }
            }
            else
            {
                if (!Boolean.TRUE.equals(isUpdateSupport))
                {
                    c.addRow(excelRow, "财务分类编码「" + code + "」在租户下已存在，未勾选「更新已存在」则无法导入");
                    continue;
                }
            }
            try
            {
                BeanValidators.validateWithException(validator, row);
            }
            catch (Exception e)
            {
                c.addRow(excelRow, e.getMessage());
            }
        }
        return c;
    }

    private void clearFinanceImportValidationColumn(List<FdFinanceCategory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdFinanceCategory r : list)
        {
            if (r != null)
            {
                r.setValidationResult(null);
            }
        }
    }

    private void fillFinanceValidationTexts(List<FdFinanceCategory> list, ImportRowErrorCollector collector, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FdFinanceCategory row = list.get(i);
            if (row == null)
            {
                continue;
            }
            normalizeFinanceImportRow(row);
            if (StringUtils.isEmpty(row.getFinanceCategoryCode()) && StringUtils.isEmpty(row.getFinanceCategoryName()))
            {
                row.setValidationResult("空行（已跳过）");
                continue;
            }
            List<String> msgs = collector.getRowMessages(excelRow);
            if (!msgs.isEmpty())
            {
                row.setValidationResult(String.join("；", msgs));
            }
            else if (fileValid)
            {
                row.setValidationResult("校验通过");
            }
            else
            {
                row.setValidationResult("本行未单独报错；文件因其他数据未通过校验");
            }
        }
    }

    private void markFinanceImportSuccessTexts(List<FdFinanceCategory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdFinanceCategory row : list)
        {
            if (row == null)
            {
                continue;
            }
            normalizeFinanceImportRow(row);
            if (StringUtils.isEmpty(row.getFinanceCategoryCode()) && StringUtils.isEmpty(row.getFinanceCategoryName()))
            {
                row.setValidationResult("空行（已跳过）");
            }
            else
            {
                row.setValidationResult("导入成功");
            }
        }
    }
}
