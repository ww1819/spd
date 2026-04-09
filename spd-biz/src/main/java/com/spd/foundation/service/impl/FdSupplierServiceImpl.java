package com.spd.foundation.service.impl;

import java.math.BigDecimal;
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
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdSupplierChangeLog;
import com.spd.foundation.mapper.FdSupplierChangeLogMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.service.IFdSupplierService;

/**
 * 供应商Service业务层处理
 *
 * @author spd
 * @date 2023-12-05
 */
@Service
public class FdSupplierServiceImpl implements IFdSupplierService
{
    @Autowired
    private FdSupplierMapper fdSupplierMapper;

    @Autowired
    private FdSupplierChangeLogMapper fdSupplierChangeLogMapper;

    @Autowired
    protected Validator validator;

    @Override
    public FdSupplier selectFdSupplierById(Long id)
    {
        FdSupplier s = fdSupplierMapper.selectFdSupplierById(id);
        if (s != null)
        {
            SecurityUtils.ensureTenantAccess(s.getTenantId());
        }
        return s;
    }

    @Override
    public List<FdSupplier> selectFdSupplierList(FdSupplier fdSupplier)
    {
        if (fdSupplier != null && StringUtils.isEmpty(fdSupplier.getTenantId()))
        {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid))
            {
                fdSupplier.setTenantId(tid);
            }
        }
        return fdSupplierMapper.selectFdSupplierList(fdSupplier);
    }

    @Override
    public int insertFdSupplier(FdSupplier fdSupplier)
    {
        if (fdSupplier.getCode() != null)
        {
            fdSupplier.setCode(fdSupplier.getCode().trim());
        }
        if (fdSupplier.getName() != null)
        {
            fdSupplier.setName(fdSupplier.getName().trim());
        }
        if (StringUtils.isEmpty(fdSupplier.getCode()))
        {
            String tenantId = StringUtils.isNotEmpty(fdSupplier.getTenantId()) ? fdSupplier.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            fdSupplier.setCode(generateSupplierCode(tenantId));
        }
        fdSupplier.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdSupplier.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdSupplier.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(fdSupplier.getTenantId()))
        {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid))
            {
                fdSupplier.setTenantId(tid);
            }
        }
        if (StringUtils.isEmpty(fdSupplier.getName()))
        {
            throw new ServiceException("供应商名称不能为空");
        }
        if (fdSupplierMapper.countSupplierByTenantAndName(fdSupplier.getTenantId(), fdSupplier.getName(), null) > 0)
        {
            throw new ServiceException("供应商名称「" + fdSupplier.getName() + "」已存在，不能重复");
        }
        fdSupplier.setHisId(normalizeHisId(fdSupplier.getHisId()));
        if (isHisIdBlank(fdSupplier.getHisId()))
        {
            fdSupplier.setHisId(null);
        }
        else
        {
            assertHisIdUnique(fdSupplier.getTenantId(), fdSupplier.getHisId(), null);
        }
        return fdSupplierMapper.insertFdSupplier(fdSupplier);
    }

    @Override
    public int updateFdSupplier(FdSupplier fdSupplier)
    {
        if (fdSupplier.getId() == null)
        {
            throw new ServiceException("供应商主键不能为空");
        }
        if (fdSupplier.getName() != null)
        {
            fdSupplier.setName(fdSupplier.getName().trim());
        }
        FdSupplier before = fdSupplierMapper.selectFdSupplierById(fdSupplier.getId());
        if (before == null)
        {
            throw new ServiceException("供应商不存在");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        if (StringUtils.isNotEmpty(fdSupplier.getName()))
        {
            if (fdSupplierMapper.countSupplierByTenantAndName(before.getTenantId(), fdSupplier.getName(), fdSupplier.getId()) > 0)
            {
                throw new ServiceException("供应商名称「" + fdSupplier.getName() + "」已存在，不能重复");
            }
            fdSupplier.setReferredCode(PinyinUtils.getPinyinInitials(fdSupplier.getName()));
        }
        fdSupplier.setHisId(before.getHisId());
        fdSupplier.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdSupplier.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdSupplier.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        int n = fdSupplierMapper.updateFdSupplier(fdSupplier);
        if (n > 0)
        {
            FdSupplier after = fdSupplierMapper.selectFdSupplierById(fdSupplier.getId());
            String logOp = StringUtils.isNotEmpty(fdSupplier.getUpdateBy()) ? fdSupplier.getUpdateBy() : SecurityUtils.getUserIdStr();
            recordSupplierFieldChanges(before, after, logOp);
        }
        return n;
    }

    @Override
    public int deleteFdSupplierById(Long id)
    {
        checkSupplierIsExist(id);
        FdSupplier fdSupplier = fdSupplierMapper.selectFdSupplierById(id);
        if (fdSupplier == null)
        {
            throw new ServiceException(String.format("供应商：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(fdSupplier.getTenantId());
        return fdSupplierMapper.deleteFdSupplierById(id, SecurityUtils.getUserIdStr());
    }

    private void checkSupplierIsExist(Long id)
    {
        int count = fdSupplierMapper.selectSupplierIsExist(id);
        if (count > 0)
        {
            throw new ServiceException("已存在出入库业务的供应商不能进行删除!");
        }
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
            FdSupplier before = fdSupplierMapper.selectFdSupplierById(id);
            if (before == null)
            {
                continue;
            }
            SecurityUtils.ensureTenantAccess(before.getTenantId());
            String name = before.getName();
            if (StringUtils.isEmpty(name))
            {
                continue;
            }
            FdSupplier supplier = fdSupplierMapper.selectFdSupplierById(id);
            supplier.setReferredCode(PinyinUtils.getPinyinInitials(name));
            supplier.setUpdateBy(op);
            supplier.setUpdateTime(DateUtils.getNowDate());
            fdSupplierMapper.updateFdSupplier(supplier);
            FdSupplier after = fdSupplierMapper.selectFdSupplierById(id);
            recordSupplierFieldChanges(before, after, op);
        }
    }

    @Override
    public List<FdSupplierChangeLog> selectSupplierChangeLog(Long supplierId)
    {
        if (supplierId == null)
        {
            return Collections.emptyList();
        }
        List<FdSupplierChangeLog> list = fdSupplierChangeLogMapper.selectBySupplierId(supplierId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public Map<String, Object> validateFdSupplierImport(List<FdSupplier> list, Boolean isUpdateSupport)
    {
        clearSupplierImportValidationColumn(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requiresHisId", importRequiresMandatoryHisId());
        ImportRowErrorCollector collector = collectSupplierImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        if (list != null)
        {
            result.put("totalRows", list.size());
        }
        else
        {
            result.put("totalRows", 0);
        }
        if (valid && list != null && !list.isEmpty())
        {
            String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
            int insertCount = 0;
            int updateCount = 0;
            for (FdSupplier row : list)
            {
                if (row == null)
                {
                    continue;
                }
                normalizeImportRow(row);
                if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName()))
                {
                    continue;
                }
                if (StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName()))
                {
                    continue;
                }
                FdSupplier existing = fdSupplierMapper.selectFdSupplierByCodeAndTenantId(row.getCode(), tenantId);
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
        fillSupplierValidationTexts(list, collector, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdSupplier.class, list));
        return result;
    }

    @Override
    public String importFdSupplier(List<FdSupplier> list, Boolean isUpdateSupport, String operName, boolean confirmed)
    {
        if (!confirmed)
        {
            throw new ServiceException("请先完成校验并在确认后再导入");
        }
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("导入供应商数据不能为空！");
        }
        clearSupplierImportValidationColumn(list);
        ImportRowErrorCollector collector = collectSupplierImportErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        if (!errors.isEmpty())
        {
            throw new ServiceException("数据已变更或校验未通过，请重新校验后再导入。详情：" + String.join("；", errors));
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (FdSupplier row : list)
        {
            if (row == null || StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName()))
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
            row.setReferredCode(PinyinUtils.getPinyinInitials(row.getName()));
            FdSupplier existing = fdSupplierMapper.selectFdSupplierByCodeAndTenantId(row.getCode(), tenantId);
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
                        throw new ServiceException("衡水市第三人民医院导入新增行必须填写HIS供应商ID");
                    }
                    assertHisIdUnique(tenantId, row.getHisId(), null);
                }
                else if (isHisIdBlank(row.getHisId()))
                {
                    row.setHisId(null);
                }
                fdSupplierMapper.insertFdSupplier(row);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、供应商 ").append(row.getName()).append(" 导入成功");
            }
            else if (Boolean.TRUE.equals(isUpdateSupport))
            {
                applyImportUpdate(existing, row, operName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、供应商 ").append(row.getName()).append(" 更新成功");
            }
        }
        successMsg.insert(0, "增量导入完成（仅新增或按选项更新已有编码；更新时不修改HIS供应商ID）。共处理 " + successNum + " 条，明细如下：");
        markSupplierImportSuccessTexts(list);
        return successMsg.toString();
    }

    /**
     * 衡水市第三人民医院：HIS 供应商 ID 必填且租户内唯一
     */
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

    /**
     * 自动生成供应商编码：6位数字（100000-999999），按当前租户递增。
     */
    private String generateSupplierCode(String tenantId)
    {
        FdSupplier query = new FdSupplier();
        query.setTenantId(tenantId);
        List<FdSupplier> allSuppliers = fdSupplierMapper.selectFdSupplierList(query);
        int maxCode = 99999;
        for (FdSupplier s : allSuppliers)
        {
            String code = s.getCode();
            if (StringUtils.isNotEmpty(code) && code.matches("^\\d{6}$"))
            {
                try
                {
                    int val = Integer.parseInt(code);
                    if (val >= 100000 && val <= 999999 && val > maxCode)
                    {
                        maxCode = val;
                    }
                }
                catch (NumberFormatException ignored)
                {
                }
            }
        }
        int nextCode = maxCode + 1;
        if (nextCode > 999999)
        {
            nextCode = 100000;
        }
        String candidate = String.format("%06d", nextCode);
        // 并发兜底：若被抢占，则顺延重试
        for (int i = 0; i < 20; i++)
        {
            if (fdSupplierMapper.selectFdSupplierByCodeAndTenantId(candidate, tenantId) == null)
            {
                return candidate;
            }
            nextCode++;
            if (nextCode > 999999)
            {
                nextCode = 100000;
            }
            candidate = String.format("%06d", nextCode);
        }
        throw new ServiceException("自动生成供应商编码失败，请稍后重试");
    }

    private void assertHisIdUnique(String tenantId, String hisId, Long excludeId)
    {
        if (isHisIdBlank(hisId))
        {
            return;
        }
        if (fdSupplierMapper.countSupplierByTenantAndHisId(tenantId, hisId, excludeId) > 0)
        {
            throw new ServiceException("HIS供应商ID「" + hisId + "」在本租户下已存在，不能重复");
        }
    }

    private void normalizeImportRow(FdSupplier row)
    {
        if (row.getCode() != null)
        {
            row.setCode(row.getCode().trim());
        }
        if (row.getName() != null)
        {
            row.setName(row.getName().trim());
        }
        if (row.getHisId() != null)
        {
            row.setHisId(normalizeHisId(row.getHisId()));
        }
    }

    private ImportRowErrorCollector collectSupplierImportErrors(List<FdSupplier> list, Boolean isUpdateSupport)
    {
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入供应商数据不能为空");
            return c;
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        Map<String, Integer> codeFirstRow = new LinkedHashMap<>();
        Map<String, Integer> hisFirstRow = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++)
        {
            FdSupplier row = list.get(i);
            int excelRow = i + 2;
            if (row == null)
            {
                c.addRow(excelRow, "数据为空");
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName()))
            {
                continue;
            }
            if (StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName()))
            {
                c.addRow(excelRow, "供应商编码与名称均不能为空");
                continue;
            }
            String code = row.getCode();
            if (codeFirstRow.containsKey(code))
            {
                c.addRow(excelRow, "供应商编码「" + code + "」与第" + codeFirstRow.get(code) + "行重复");
            }
            else
            {
                codeFirstRow.put(code, excelRow);
            }
            FdSupplier existing = fdSupplierMapper.selectFdSupplierByCodeAndTenantId(code, tenantId);
            if (existing == null)
            {
                if (importRequiresMandatoryHisId() && isHisIdBlank(row.getHisId()))
                {
                    c.addRow(excelRow, "HIS供应商ID不能为空（衡水市第三人民医院新增时必填）");
                }
                if (!isHisIdBlank(row.getHisId()))
                {
                    String hid = row.getHisId();
                    if (hisFirstRow.containsKey(hid))
                    {
                        c.addRow(excelRow, "HIS供应商ID「" + hid + "」与第" + hisFirstRow.get(hid) + "行重复");
                    }
                    else
                    {
                        hisFirstRow.put(hid, excelRow);
                    }
                    if (fdSupplierMapper.countSupplierByTenantAndHisId(tenantId, hid, null) > 0)
                    {
                        c.addRow(excelRow, "HIS供应商ID「" + hid + "」在租户下已存在");
                    }
                }
            }
            else
            {
                if (!Boolean.TRUE.equals(isUpdateSupport))
                {
                    c.addRow(excelRow, "供应商编码「" + code + "」在租户下已存在，未勾选「更新已存在」则无法导入");
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

    private void clearSupplierImportValidationColumn(List<FdSupplier> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdSupplier r : list)
        {
            if (r != null)
            {
                r.setValidationResult(null);
            }
        }
    }

    private void fillSupplierValidationTexts(List<FdSupplier> list, ImportRowErrorCollector collector, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FdSupplier row = list.get(i);
            if (row == null)
            {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName()))
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

    private void markSupplierImportSuccessTexts(List<FdSupplier> list)
    {
        if (list == null)
        {
            return;
        }
        for (FdSupplier row : list)
        {
            if (row == null)
            {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName()))
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
     * 导入更新已存在供应商：仅更新名称与名称简码；不修改 HIS 供应商 ID 与其它字段
     */
    private void applyImportUpdate(FdSupplier existing, FdSupplier row, String operName)
    {
        FdSupplier before = fdSupplierMapper.selectFdSupplierById(existing.getId());
        existing.setName(row.getName());
        existing.setReferredCode(PinyinUtils.getPinyinInitials(row.getName()));
        existing.setUpdateBy(operName);
        existing.setUpdateTime(DateUtils.getNowDate());
        fdSupplierMapper.updateFdSupplier(existing);
        FdSupplier after = fdSupplierMapper.selectFdSupplierById(existing.getId());
        recordSupplierFieldChanges(before, after, operName);
    }

    private static String nz(String v)
    {
        return v == null ? "" : v;
    }

    private static String nzMoney(BigDecimal b)
    {
        return b == null ? "" : b.stripTrailingZeros().toPlainString();
    }

    private static String nzDate(Date d)
    {
        return d == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, d);
    }

    private void recordSupplierFieldChanges(FdSupplier before, FdSupplier after, String operator)
    {
        if (before == null || after == null || after.getId() == null)
        {
            return;
        }
        Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        Long sid = after.getId();
        pushSupplierChange(sid, op, now, "code", "供应商编码", nz(before.getCode()), nz(after.getCode()));
        pushSupplierChange(sid, op, now, "name", "供应商名称", nz(before.getName()), nz(after.getName()));
        pushSupplierChange(sid, op, now, "referred_code", "名称简码", nz(before.getReferredCode()), nz(after.getReferredCode()));
        pushSupplierChange(sid, op, now, "tax_number", "税号", nz(before.getTaxNumber()), nz(after.getTaxNumber()));
        pushSupplierChange(sid, op, now, "reg_money", "注册资金", nzMoney(before.getRegMoney()), nzMoney(after.getRegMoney()));
        pushSupplierChange(sid, op, now, "valid_time", "资质有效期", nzDate(before.getValidTime()), nzDate(after.getValidTime()));
        pushSupplierChange(sid, op, now, "contacts", "联系人", nz(before.getContacts()), nz(after.getContacts()));
        pushSupplierChange(sid, op, now, "contacts_phone", "联系电话", nz(before.getContactsPhone()), nz(after.getContactsPhone()));
        pushSupplierChange(sid, op, now, "website", "网址", nz(before.getWebsite()), nz(after.getWebsite()));
        pushSupplierChange(sid, op, now, "legal_person", "法人", nz(before.getLegalPerson()), nz(after.getLegalPerson()));
        pushSupplierChange(sid, op, now, "zip_code", "邮编", nz(before.getZipCode()), nz(after.getZipCode()));
        pushSupplierChange(sid, op, now, "email", "邮箱", nz(before.getEmail()), nz(after.getEmail()));
        pushSupplierChange(sid, op, now, "address", "地址", nz(before.getAddress()), nz(after.getAddress()));
        pushSupplierChange(sid, op, now, "company_person", "公司负责人", nz(before.getCompanyPerson()), nz(after.getCompanyPerson()));
        pushSupplierChange(sid, op, now, "phone", "电话", nz(before.getPhone()), nz(after.getPhone()));
        pushSupplierChange(sid, op, now, "cert_number", "证件号", nz(before.getCertNumber()), nz(after.getCertNumber()));
        pushSupplierChange(sid, op, now, "fax", "传真", nz(before.getFax()), nz(after.getFax()));
        pushSupplierChange(sid, op, now, "bank_account", "银行账号", nz(before.getBankAccount()), nz(after.getBankAccount()));
        pushSupplierChange(sid, op, now, "company_referred", "公司简称", nz(before.getCompanyReferred()), nz(after.getCompanyReferred()));
        pushSupplierChange(sid, op, now, "supplier_range", "经营范围", nz(before.getSupplierRange()), nz(after.getSupplierRange()));
        pushSupplierChange(sid, op, now, "supplier_status", "状态", nz(before.getSupplierStatus()), nz(after.getSupplierStatus()));
        pushSupplierChange(sid, op, now, "supplier_type", "供应商类型", nz(before.getSupplierType()), nz(after.getSupplierType()));
        pushSupplierChange(sid, op, now, "remark", "备注", nz(before.getRemark()), nz(after.getRemark()));
    }

    private void pushSupplierChange(Long supplierId, String operator, Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue)
    {
        if (Objects.equals(oldValue, newValue))
        {
            return;
        }
        FdSupplierChangeLog rec = new FdSupplierChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setSupplierId(supplierId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdSupplierChangeLogMapper.insert(rec);
    }
}
