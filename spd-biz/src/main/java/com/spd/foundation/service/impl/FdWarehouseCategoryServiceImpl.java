package com.spd.foundation.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.foundation.service.FoundationSnapshotRecorder;
import com.spd.foundation.service.IFdWarehouseCategoryService;

/**
 * 库房分类Service业务层处理
 *
 * @author spd
 */
@Service
public class FdWarehouseCategoryServiceImpl implements IFdWarehouseCategoryService
{
    @Autowired
    private FdWarehouseCategoryMapper fdWarehouseCategoryMapper;

    @Autowired
    private FoundationSnapshotRecorder foundationSnapshotRecorder;

    @Autowired
    protected Validator validator;

    /**
     * 查询库房分类
     *
     * @param warehouseCategoryId 库房分类主键
     * @return 库房分类
     */
    @Override
    public FdWarehouseCategory selectFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId)
    {
        FdWarehouseCategory row = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    /**
     * 查询库房分类列表
     *
     * @param fdWarehouseCategory 库房分类
     * @return 库房分类
     */
    @Override
    public List<FdWarehouseCategory> selectFdWarehouseCategoryList(FdWarehouseCategory fdWarehouseCategory)
    {
        if (fdWarehouseCategory != null && StringUtils.isEmpty(fdWarehouseCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdWarehouseCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdWarehouseCategoryMapper.selectFdWarehouseCategoryList(fdWarehouseCategory);
    }

    /**
     * 查询库房分类树形列表
     *
     * @return 库房分类集合
     */
    @Override
    public List<FdWarehouseCategory> selectFdWarehouseCategoryTree()
    {
        return fdWarehouseCategoryMapper.selectFdWarehouseCategoryTree(SecurityUtils.getCustomerId());
    }

    /**
     * 新增库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    @Override
    public int insertFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory)
    {
        fdWarehouseCategory.setCreateTime(DateUtils.getNowDate());
        if (fdWarehouseCategory.getWarehouseCategoryName() != null)
        {
            fdWarehouseCategory.setWarehouseCategoryName(fdWarehouseCategory.getWarehouseCategoryName().trim());
        }
        if (StringUtils.isEmpty(fdWarehouseCategory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            fdWarehouseCategory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(fdWarehouseCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdWarehouseCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        if (fdWarehouseCategory.getDelFlag() == null) {
            fdWarehouseCategory.setDelFlag(0);
        }
        fdWarehouseCategory.setHisId(normalizeHisId(fdWarehouseCategory.getHisId()));
        if (importRequiresMandatoryHisId())
        {
            if (isHisIdBlank(fdWarehouseCategory.getHisId()))
            {
                throw new ServiceException("衡水市第三人民医院新增库房分类时必须填写HIS库房分类ID");
            }
            assertWarehouseHisIdUnique(fdWarehouseCategory.getTenantId(), fdWarehouseCategory.getHisId(), null);
        }
        else if (isHisIdBlank(fdWarehouseCategory.getHisId()))
        {
            fdWarehouseCategory.setHisId(null);
        }
        if (StringUtils.isEmpty(fdWarehouseCategory.getWarehouseCategoryName()))
        {
            throw new ServiceException("库房分类名称不能为空");
        }
        if (fdWarehouseCategoryMapper.countWarehouseCategoryByTenantAndName(fdWarehouseCategory.getTenantId(), fdWarehouseCategory.getWarehouseCategoryName(), null) > 0)
        {
            throw new ServiceException("库房分类名称「" + fdWarehouseCategory.getWarehouseCategoryName() + "」已存在，不能重复");
        }
        fdWarehouseCategory.setReferredName(PinyinUtils.getPinyinInitials(fdWarehouseCategory.getWarehouseCategoryName()));
        return fdWarehouseCategoryMapper.insertFdWarehouseCategory(fdWarehouseCategory);
    }

    /**
     * 修改库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    @Override
    public int updateFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory)
    {
        if (fdWarehouseCategory.getWarehouseCategoryId() == null) {
            throw new ServiceException("库房分类主键不能为空");
        }
        if (fdWarehouseCategory.getWarehouseCategoryName() != null)
        {
            fdWarehouseCategory.setWarehouseCategoryName(fdWarehouseCategory.getWarehouseCategoryName().trim());
        }
        FdWarehouseCategory before = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(fdWarehouseCategory.getWarehouseCategoryId());
        if (before == null) {
            throw new ServiceException("库房分类不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        if (StringUtils.isNotEmpty(fdWarehouseCategory.getWarehouseCategoryName()))
        {
            if (fdWarehouseCategoryMapper.countWarehouseCategoryByTenantAndName(before.getTenantId(), fdWarehouseCategory.getWarehouseCategoryName(), fdWarehouseCategory.getWarehouseCategoryId()) > 0)
            {
                throw new ServiceException("库房分类名称「" + fdWarehouseCategory.getWarehouseCategoryName() + "」已存在，不能重复");
            }
            fdWarehouseCategory.setReferredName(PinyinUtils.getPinyinInitials(fdWarehouseCategory.getWarehouseCategoryName()));
        }
        fdWarehouseCategory.setTenantId(before.getTenantId());
        fdWarehouseCategory.setHisId(before.getHisId());
        fdWarehouseCategory.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdWarehouseCategory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            fdWarehouseCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        int n = fdWarehouseCategoryMapper.updateFdWarehouseCategory(fdWarehouseCategory);
        if (n > 0)
        {
            FdWarehouseCategory after = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(fdWarehouseCategory.getWarehouseCategoryId());
            String logOp = StringUtils.isNotEmpty(fdWarehouseCategory.getUpdateBy()) ? fdWarehouseCategory.getUpdateBy() : SecurityUtils.getUserIdStr();
            foundationSnapshotRecorder.record(before.getTenantId(), "WAREHOUSE_CATEGORY",
                String.valueOf(fdWarehouseCategory.getWarehouseCategoryId()), before, after, logOp);
        }
        return n;
    }

    /**
     * 批量删除库房分类
     *
     * @param warehouseCategoryIds 需要删除的库房分类主键
     * @return 结果
     */
    @Override
    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long warehouseCategoryIds)
    {
        FdWarehouseCategory fdWarehouseCategory = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryIds);
        if (fdWarehouseCategory == null) {
            throw new ServiceException(String.format("库房分类：%s，不存在!", warehouseCategoryIds));
        }
        SecurityUtils.ensureTenantAccess(fdWarehouseCategory.getTenantId());
        fdWarehouseCategory.setDelFlag(1);
        fdWarehouseCategory.setDeleteBy(SecurityUtils.getUserIdStr());
        fdWarehouseCategory.setDeleteTime(DateUtils.getNowDate());
        fdWarehouseCategory.setUpdateTime(new Date());
        fdWarehouseCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdWarehouseCategoryMapper.updateFdWarehouseCategory(fdWarehouseCategory);
    }

    /**
     * 批量更新库房分类名称简码（根据名称生成拼音首字母）
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
            FdWarehouseCategory category = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(id);
            if (category == null) {
                continue;
            }
            SecurityUtils.ensureTenantAccess(category.getTenantId());
            String name = category.getWarehouseCategoryName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            category.setReferredName(PinyinUtils.getPinyinInitials(name));
            fdWarehouseCategoryMapper.updateFdWarehouseCategory(category);
        }
    }

    @Override
    public Map<String, Object> validateWarehouseCategoryImport(List<FdWarehouseCategory> list, Boolean isUpdateSupport)
    {
        clearWarehouseImportValidationColumn(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requiresHisId", importRequiresMandatoryHisId());
        ImportRowErrorCollector collector = collectWarehouseCategoryImportErrors(list, isUpdateSupport);
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
            for (FdWarehouseCategory row : list)
            {
                if (row == null)
                {
                    continue;
                }
                normalizeWarehouseImportRow(row);
                if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) && StringUtils.isEmpty(row.getWarehouseCategoryName()))
                {
                    continue;
                }
                if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) || StringUtils.isEmpty(row.getWarehouseCategoryName()))
                {
                    continue;
                }
                FdWarehouseCategory existing = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByCodeAndTenantId(
                    row.getWarehouseCategoryCode(), tenantId);
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
        fillWarehouseValidationTexts(list, collector, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdWarehouseCategory.class, list));
        return result;
    }

    @Override
    public String importWarehouseCategory(List<FdWarehouseCategory> list, Boolean isUpdateSupport, String operName, boolean confirmed)
    {
        if (!confirmed)
        {
            throw new ServiceException("请先完成校验并在确认后再导入");
        }
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("导入库房分类数据不能为空！");
        }
        clearWarehouseImportValidationColumn(list);
        ImportRowErrorCollector collector = collectWarehouseCategoryImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        if (!errors.isEmpty())
        {
            throw new ServiceException("数据已变更或校验未通过，请重新校验后再导入。详情：" + String.join("；", errors));
        }
        String tenantId = SecurityUtils.getCustomerId();
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (FdWarehouseCategory row : list)
        {
            if (row == null || StringUtils.isEmpty(row.getWarehouseCategoryCode()) || StringUtils.isEmpty(row.getWarehouseCategoryName()))
            {
                continue;
            }
            normalizeWarehouseImportRow(row);
            try
            {
                BeanValidators.validateWithException(validator, row);
            }
            catch (Exception e)
            {
                throw new ServiceException("导入校验异常：" + e.getMessage());
            }
            row.setReferredName(PinyinUtils.getPinyinInitials(row.getWarehouseCategoryName()));
            if (row.getParentId() == null)
            {
                row.setParentId(0L);
            }
            FdWarehouseCategory existing = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByCodeAndTenantId(
                row.getWarehouseCategoryCode(), tenantId);
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
                        throw new ServiceException("衡水市第三人民医院导入新增行必须填写HIS库房分类ID");
                    }
                    assertWarehouseHisIdUnique(tenantId, row.getHisId(), null);
                }
                else if (isHisIdBlank(row.getHisId()))
                {
                    row.setHisId(null);
                }
                row.setCreateTime(DateUtils.getNowDate());
                fdWarehouseCategoryMapper.insertFdWarehouseCategory(row);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、库房分类 ").append(row.getWarehouseCategoryName()).append(" 导入成功");
            }
            else if (Boolean.TRUE.equals(isUpdateSupport))
            {
                applyWarehouseImportUpdate(existing, row, operName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、库房分类 ").append(row.getWarehouseCategoryName()).append(" 更新成功");
            }
        }
        successMsg.insert(0, "增量导入完成（仅新增或按选项更新已有编码；更新时不修改HIS库房分类ID）。共处理 " + successNum + " 条，明细如下：");
        markWarehouseImportSuccessTexts(list);
        return successMsg.toString();
    }

    private void applyWarehouseImportUpdate(FdWarehouseCategory existing, FdWarehouseCategory row, String operName)
    {
        existing.setWarehouseCategoryName(row.getWarehouseCategoryName());
        existing.setReferredName(PinyinUtils.getPinyinInitials(row.getWarehouseCategoryName()));
        existing.setUpdateBy(operName);
        existing.setUpdateTime(DateUtils.getNowDate());
        fdWarehouseCategoryMapper.updateFdWarehouseCategory(existing);
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

    private void assertWarehouseHisIdUnique(String tenantId, String hisId, Long excludeId)
    {
        if (isHisIdBlank(hisId))
        {
            return;
        }
        if (fdWarehouseCategoryMapper.countWarehouseCategoryByTenantAndHisId(tenantId, hisId, excludeId) > 0)
        {
            throw new ServiceException("HIS库房分类ID「" + hisId + "」在本租户下已存在，不能重复");
        }
    }

    private void normalizeWarehouseImportRow(FdWarehouseCategory row)
    {
        if (row.getWarehouseCategoryCode() != null)
        {
            row.setWarehouseCategoryCode(row.getWarehouseCategoryCode().trim());
        }
        if (row.getWarehouseCategoryName() != null)
        {
            row.setWarehouseCategoryName(row.getWarehouseCategoryName().trim());
        }
        if (row.getHisId() != null)
        {
            row.setHisId(normalizeHisId(row.getHisId()));
        }
    }

    private ImportRowErrorCollector collectWarehouseCategoryImportErrors(List<FdWarehouseCategory> list, Boolean isUpdateSupport)
    {
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入库房分类数据不能为空");
            return c;
        }
        String tenantId = SecurityUtils.getCustomerId();
        Map<String, Integer> codeFirstRow = new LinkedHashMap<>();
        Map<String, Integer> hisFirstRow = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++)
        {
            FdWarehouseCategory row = list.get(i);
            int excelRow = i + 2;
            if (row == null)
            {
                c.addRow(excelRow, "数据为空");
                continue;
            }
            normalizeWarehouseImportRow(row);
            if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) && StringUtils.isEmpty(row.getWarehouseCategoryName()))
            {
                continue;
            }
            if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) || StringUtils.isEmpty(row.getWarehouseCategoryName()))
            {
                c.addRow(excelRow, "库房分类编码与名称均不能为空");
                continue;
            }
            String code = row.getWarehouseCategoryCode();
            if (codeFirstRow.containsKey(code))
            {
                c.addRow(excelRow, "库房分类编码「" + code + "」与第" + codeFirstRow.get(code) + "行重复");
            }
            else
            {
                codeFirstRow.put(code, excelRow);
            }
            FdWarehouseCategory existing = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByCodeAndTenantId(code, tenantId);
            if (existing == null)
            {
                if (importRequiresMandatoryHisId() && isHisIdBlank(row.getHisId()))
                {
                    c.addRow(excelRow, "HIS库房分类ID不能为空（衡水市第三人民医院新增时必填）");
                }
                if (!isHisIdBlank(row.getHisId()))
                {
                    String hid = row.getHisId();
                    if (hisFirstRow.containsKey(hid))
                    {
                        c.addRow(excelRow, "HIS库房分类ID「" + hid + "」与第" + hisFirstRow.get(hid) + "行重复");
                    }
                    else
                    {
                        hisFirstRow.put(hid, excelRow);
                    }
                    if (fdWarehouseCategoryMapper.countWarehouseCategoryByTenantAndHisId(tenantId, hid, null) > 0)
                    {
                        c.addRow(excelRow, "HIS库房分类ID「" + hid + "」在租户下已存在");
                    }
                }
            }
            else
            {
                if (!Boolean.TRUE.equals(isUpdateSupport))
                {
                    c.addRow(excelRow, "库房分类编码「" + code + "」在租户下已存在，未勾选「更新已存在」则无法导入");
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

    private void clearWarehouseImportValidationColumn(List<FdWarehouseCategory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdWarehouseCategory r : list)
        {
            if (r != null)
            {
                r.setValidationResult(null);
            }
        }
    }

    private void fillWarehouseValidationTexts(List<FdWarehouseCategory> list, ImportRowErrorCollector collector, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FdWarehouseCategory row = list.get(i);
            if (row == null)
            {
                continue;
            }
            normalizeWarehouseImportRow(row);
            if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) && StringUtils.isEmpty(row.getWarehouseCategoryName()))
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

    private void markWarehouseImportSuccessTexts(List<FdWarehouseCategory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdWarehouseCategory row : list)
        {
            if (row == null)
            {
                continue;
            }
            normalizeWarehouseImportRow(row);
            if (StringUtils.isEmpty(row.getWarehouseCategoryCode()) && StringUtils.isEmpty(row.getWarehouseCategoryName()))
            {
                row.setValidationResult("空行（已跳过）");
            }
            else
            {
                row.setValidationResult("导入成功");
            }
        }
    }

//    /**
//     * 删除库房分类信息
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId)
//    {
//        return fdWarehouseCategoryMapper.deleteFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId);
//    }
}
