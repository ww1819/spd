package com.spd.foundation.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFactoryChangeLog;
import com.spd.foundation.mapper.FdFactoryChangeLogMapper;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.service.IFdFactoryService;

/**
 * 厂家维护Service业务层处理
 *
 * @author spd
 * @date 2024-03-04
 */
@Service
public class FdFactoryServiceImpl implements IFdFactoryService
{
    @Autowired
    private FdFactoryMapper fdFactoryMapper;

    @Autowired
    private FdFactoryChangeLogMapper fdFactoryChangeLogMapper;

    @Autowired
    protected Validator validator;

    @Override
    public FdFactory selectFdFactoryByFactoryId(Long factoryId)
    {
        FdFactory f = fdFactoryMapper.selectFdFactoryByFactoryId(factoryId);
        if (f != null)
        {
            SecurityUtils.ensureTenantAccess(f.getTenantId());
        }
        return f;
    }

    @Override
    public List<FdFactory> selectFdFactoryList(FdFactory fdFactory)
    {
        if (fdFactory != null && StringUtils.isEmpty(fdFactory.getTenantId()))
        {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid))
            {
                fdFactory.setTenantId(tid);
            }
        }
        return fdFactoryMapper.selectFdFactoryList(fdFactory);
    }

    @Override
    public int insertFdFactory(FdFactory fdFactory)
    {
        if (StringUtils.isEmpty(fdFactory.getTenantId()))
        {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid))
            {
                fdFactory.setTenantId(tid);
            }
        }
        fdFactory.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFactory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFactory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        fdFactory.setHisId(normalizeHisId(fdFactory.getHisId()));
        if (importRequiresMandatoryHisId())
        {
            if (isHisIdBlank(fdFactory.getHisId()))
            {
                throw new ServiceException("衡水市第三人民医院新增生产厂家时必须填写HIS生产厂家ID");
            }
            assertHisIdUnique(fdFactory.getTenantId(), fdFactory.getHisId(), null);
        }
        else if (isHisIdBlank(fdFactory.getHisId()))
        {
            fdFactory.setHisId(null);
        }
        return fdFactoryMapper.insertFdFactory(fdFactory);
    }

    @Override
    public int updateFdFactory(FdFactory fdFactory)
    {
        if (fdFactory.getFactoryId() == null)
        {
            throw new ServiceException("生产厂家主键不能为空");
        }
        FdFactory before = fdFactoryMapper.selectFdFactoryByFactoryId(fdFactory.getFactoryId());
        if (before == null)
        {
            throw new ServiceException("生产厂家不存在");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdFactory.setHisId(before.getHisId());
        fdFactory.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFactory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFactory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        int n = fdFactoryMapper.updateFdFactory(fdFactory);
        if (n > 0)
        {
            FdFactory after = fdFactoryMapper.selectFdFactoryByFactoryId(fdFactory.getFactoryId());
            String logOp = StringUtils.isNotEmpty(fdFactory.getUpdateBy()) ? fdFactory.getUpdateBy() : SecurityUtils.getUserIdStr();
            recordFactoryFieldChanges(before, after, logOp);
        }
        return n;
    }

    @Override
    public int deleteFdFactoryByFactoryId(Long factoryId)
    {
        FdFactory fdFactory = fdFactoryMapper.selectFdFactoryByFactoryId(factoryId);
        if (fdFactory == null)
        {
            throw new ServiceException(String.format("厂家：%s，不存在!", factoryId));
        }
        SecurityUtils.ensureTenantAccess(fdFactory.getTenantId());
        return fdFactoryMapper.deleteFdFactoryByFactoryId(factoryId, SecurityUtils.getUserIdStr());
    }

    @Override
    public void updateReferred(List<Long> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return;
        }
        String op = SecurityUtils.getUserIdStr();
        for (Long id : ids)
        {
            if (id == null)
            {
                continue;
            }
            FdFactory before = fdFactoryMapper.selectFdFactoryByFactoryId(id);
            if (before == null)
            {
                continue;
            }
            SecurityUtils.ensureTenantAccess(before.getTenantId());
            String name = before.getFactoryName();
            if (StringUtils.isEmpty(name))
            {
                continue;
            }
            FdFactory factory = fdFactoryMapper.selectFdFactoryByFactoryId(id);
            factory.setFactoryReferredCode(PinyinUtils.getPinyinInitials(name));
            factory.setUpdateBy(op);
            factory.setUpdateTime(DateUtils.getNowDate());
            fdFactoryMapper.updateFdFactory(factory);
            FdFactory after = fdFactoryMapper.selectFdFactoryByFactoryId(id);
            recordFactoryFieldChanges(before, after, op);
        }
    }

    @Override
    public List<FdFactoryChangeLog> selectFactoryChangeLog(Long factoryId)
    {
        if (factoryId == null)
        {
            return Collections.emptyList();
        }
        List<FdFactoryChangeLog> list = fdFactoryChangeLogMapper.selectByFactoryId(factoryId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public Map<String, Object> validateFdFactoryImport(List<FdFactory> list, Boolean isUpdateSupport)
    {
        clearFactoryImportValidationColumn(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requiresHisId", importRequiresMandatoryHisId());
        ImportRowErrorCollector collector = collectFactoryImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list != null ? list.size() : 0);
        if (valid && list != null && !list.isEmpty())
        {
            String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
            int insertCount = 0;
            int updateCount = 0;
            for (FdFactory row : list)
            {
                if (row == null)
                {
                    continue;
                }
                normalizeImportRow(row);
                if (StringUtils.isEmpty(row.getFactoryCode()) && StringUtils.isEmpty(row.getFactoryName()))
                {
                    continue;
                }
                if (StringUtils.isEmpty(row.getFactoryCode()) || StringUtils.isEmpty(row.getFactoryName()))
                {
                    continue;
                }
                FdFactory existing = fdFactoryMapper.selectFdFactoryByCodeAndTenantId(row.getFactoryCode(), tenantId);
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
        fillFactoryValidationTexts(list, collector, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdFactory.class, list));
        return result;
    }

    @Override
    public String importFdFactory(List<FdFactory> list, Boolean isUpdateSupport, String operName, boolean confirmed)
    {
        if (!confirmed)
        {
            throw new ServiceException("请先完成校验并在确认后再导入");
        }
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("导入生产厂家数据不能为空！");
        }
        clearFactoryImportValidationColumn(list);
        ImportRowErrorCollector collector = collectFactoryImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        if (!errors.isEmpty())
        {
            throw new ServiceException("数据已变更或校验未通过，请重新校验后再导入。详情：" + String.join("；", errors));
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (FdFactory row : list)
        {
            if (row == null || StringUtils.isEmpty(row.getFactoryCode()) || StringUtils.isEmpty(row.getFactoryName()))
            {
                continue;
            }
            normalizeImportRow(row);
            try
            {
                BeanValidators.validateWithException(validator, row);
            }
            catch (Exception e)
            {
                throw new ServiceException("导入校验异常：" + e.getMessage());
            }
            row.setFactoryReferredCode(PinyinUtils.getPinyinInitials(row.getFactoryName()));
            FdFactory existing = fdFactoryMapper.selectFdFactoryByCodeAndTenantId(row.getFactoryCode(), tenantId);
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
                        throw new ServiceException("衡水市第三人民医院导入新增行必须填写HIS生产厂家ID");
                    }
                    assertHisIdUnique(tenantId, row.getHisId(), null);
                }
                else if (isHisIdBlank(row.getHisId()))
                {
                    row.setHisId(null);
                }
                fdFactoryMapper.insertFdFactory(row);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、生产厂家 ").append(row.getFactoryName()).append(" 导入成功");
            }
            else if (Boolean.TRUE.equals(isUpdateSupport))
            {
                applyImportUpdate(existing, row, operName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、生产厂家 ").append(row.getFactoryName()).append(" 更新成功");
            }
        }
        successMsg.insert(0, "增量导入完成（仅新增或按选项更新已有编码；更新时不修改HIS生产厂家ID）。共处理 " + successNum + " 条，明细如下：");
        markFactoryImportSuccessTexts(list);
        return successMsg.toString();
    }

    private static boolean importRequiresMandatoryHisId()
    {
        return TenantEnum.HS_003 == TenantEnum.fromCustomerId(SecurityUtils.requiredScopedTenantIdForSql());
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

    private void assertHisIdUnique(String tenantId, String hisId, Long excludeFactoryId)
    {
        if (isHisIdBlank(hisId))
        {
            return;
        }
        if (fdFactoryMapper.countFactoryByTenantAndHisId(tenantId, hisId, excludeFactoryId) > 0)
        {
            throw new ServiceException("HIS生产厂家ID「" + hisId + "」在本租户下已存在，不能重复");
        }
    }

    private void normalizeImportRow(FdFactory row)
    {
        if (row.getFactoryCode() != null)
        {
            row.setFactoryCode(row.getFactoryCode().trim());
        }
        if (row.getFactoryName() != null)
        {
            row.setFactoryName(row.getFactoryName().trim());
        }
        if (row.getHisId() != null)
        {
            row.setHisId(normalizeHisId(row.getHisId()));
        }
    }

    private ImportRowErrorCollector collectFactoryImportErrors(List<FdFactory> list, Boolean isUpdateSupport)
    {
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入生产厂家数据不能为空");
            return c;
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        Map<String, Integer> codeFirstRow = new LinkedHashMap<>();
        Map<String, Integer> hisFirstRow = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++)
        {
            FdFactory row = list.get(i);
            int excelRow = i + 2;
            if (row == null)
            {
                c.addRow(excelRow, "数据为空");
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getFactoryCode()) && StringUtils.isEmpty(row.getFactoryName()))
            {
                continue;
            }
            if (StringUtils.isEmpty(row.getFactoryCode()) || StringUtils.isEmpty(row.getFactoryName()))
            {
                c.addRow(excelRow, "厂家编码与名称均不能为空");
                continue;
            }
            String code = row.getFactoryCode();
            if (codeFirstRow.containsKey(code))
            {
                c.addRow(excelRow, "厂家编码「" + code + "」与第" + codeFirstRow.get(code) + "行重复");
            }
            else
            {
                codeFirstRow.put(code, excelRow);
            }
            FdFactory existing = fdFactoryMapper.selectFdFactoryByCodeAndTenantId(code, tenantId);
            if (existing == null)
            {
                if (importRequiresMandatoryHisId() && isHisIdBlank(row.getHisId()))
                {
                    c.addRow(excelRow, "HIS生产厂家ID不能为空（衡水市第三人民医院新增时必填）");
                }
                if (!isHisIdBlank(row.getHisId()))
                {
                    String hid = row.getHisId();
                    if (hisFirstRow.containsKey(hid))
                    {
                        c.addRow(excelRow, "HIS生产厂家ID「" + hid + "」与第" + hisFirstRow.get(hid) + "行重复");
                    }
                    else
                    {
                        hisFirstRow.put(hid, excelRow);
                    }
                    if (fdFactoryMapper.countFactoryByTenantAndHisId(tenantId, hid, null) > 0)
                    {
                        c.addRow(excelRow, "HIS生产厂家ID「" + hid + "」在租户下已存在");
                    }
                }
            }
            else
            {
                if (!Boolean.TRUE.equals(isUpdateSupport))
                {
                    c.addRow(excelRow, "厂家编码「" + code + "」在租户下已存在，未勾选「更新已存在」则无法导入");
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

    private void clearFactoryImportValidationColumn(List<FdFactory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdFactory r : list)
        {
            if (r != null)
            {
                r.setValidationResult(null);
            }
        }
    }

    private void fillFactoryValidationTexts(List<FdFactory> list, ImportRowErrorCollector collector, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FdFactory row = list.get(i);
            if (row == null)
            {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getFactoryCode()) && StringUtils.isEmpty(row.getFactoryName()))
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

    private void markFactoryImportSuccessTexts(List<FdFactory> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdFactory row : list)
        {
            if (row == null)
            {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getFactoryCode()) && StringUtils.isEmpty(row.getFactoryName()))
            {
                row.setValidationResult("空行（已跳过）");
            }
            else
            {
                row.setValidationResult("导入成功");
            }
        }
    }

    /**
     * 导入更新已存在厂家：仅更新名称与厂家简码；不修改 HIS ID 与其它字段
     */
    private void applyImportUpdate(FdFactory existing, FdFactory row, String operName)
    {
        FdFactory before = fdFactoryMapper.selectFdFactoryByFactoryId(existing.getFactoryId());
        existing.setFactoryName(row.getFactoryName());
        existing.setFactoryReferredCode(PinyinUtils.getPinyinInitials(row.getFactoryName()));
        existing.setUpdateBy(operName);
        existing.setUpdateTime(DateUtils.getNowDate());
        fdFactoryMapper.updateFdFactory(existing);
        FdFactory after = fdFactoryMapper.selectFdFactoryByFactoryId(existing.getFactoryId());
        recordFactoryFieldChanges(before, after, operName);
    }

    private static String nz(String v)
    {
        return v == null ? "" : v;
    }

    private void recordFactoryFieldChanges(FdFactory before, FdFactory after, String operator)
    {
        if (before == null || after == null || after.getFactoryId() == null)
        {
            return;
        }
        Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        Long fid = after.getFactoryId();
        pushFactoryChange(fid, op, now, "factory_code", "厂家编码", nz(before.getFactoryCode()), nz(after.getFactoryCode()));
        pushFactoryChange(fid, op, now, "factory_name", "厂家名称", nz(before.getFactoryName()), nz(after.getFactoryName()));
        pushFactoryChange(fid, op, now, "factory_address", "厂家地址", nz(before.getFactoryAddress()), nz(after.getFactoryAddress()));
        pushFactoryChange(fid, op, now, "factory_contact", "厂家联系方式", nz(before.getFactoryContact()), nz(after.getFactoryContact()));
        pushFactoryChange(fid, op, now, "factory_referred_code", "厂家简码", nz(before.getFactoryReferredCode()), nz(after.getFactoryReferredCode()));
        pushFactoryChange(fid, op, now, "factory_status", "状态", nz(before.getFactoryStatus()), nz(after.getFactoryStatus()));
        pushFactoryChange(fid, op, now, "remark", "备注", nz(before.getRemark()), nz(after.getRemark()));
    }

    private void pushFactoryChange(Long factoryId, String operator, Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue)
    {
        if (Objects.equals(oldValue, newValue))
        {
            return;
        }
        FdFactoryChangeLog rec = new FdFactoryChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setFactoryId(factoryId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdFactoryChangeLogMapper.insert(rec);
    }
}
