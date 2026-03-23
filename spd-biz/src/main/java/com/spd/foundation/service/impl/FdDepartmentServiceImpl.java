package com.spd.foundation.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdDepartmentChangeLog;
import com.spd.foundation.domain.vo.FdDepartmentTreeNode;
import com.spd.foundation.mapper.FdDepartmentChangeLogMapper;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ITenantFoundationAutoGrantService;

/**
 * 科室Service业务层处理
 *
 * @author spd
 * @date 2023-11-26
 */
@Service
public class FdDepartmentServiceImpl implements IFdDepartmentService
{
    private static final Logger log = LoggerFactory.getLogger(FdDepartmentServiceImpl.class);

    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;

    @Autowired
    private FdDepartmentChangeLogMapper fdDepartmentChangeLogMapper;

    @Autowired
    private ISbCustomerService sbCustomerService;

    @Autowired
    private ITenantFoundationAutoGrantService tenantFoundationAutoGrantService;

    @Autowired
    protected Validator validator;

    /**
     * 查询科室
     *
     * @param id 科室主键
     * @return 科室
     */
    @Override
    public FdDepartment selectFdDepartmentById(String id)
    {
        return fdDepartmentMapper.selectFdDepartmentById(id);
    }

    /**
     * 查询科室列表
     *
     * @param fdDepartment 科室
     * @return 科室
     */
    @Override
    public List<FdDepartment> selectFdDepartmentList(FdDepartment fdDepartment)
    {
        if (fdDepartment != null && StringUtils.isEmpty(fdDepartment.getTenantId())) {
            String tid = SecurityUtils.resolveEffectiveTenantId(null);
            if (StringUtils.isNotEmpty(tid)) {
                fdDepartment.setTenantId(tid);
            }
        }
        return fdDepartmentMapper.selectFdDepartmentList(fdDepartment);
    }

    @Override
    public List<FdDepartmentTreeNode> buildDepartmentTreeWithCustomerRoot(List<FdDepartment> flatList)
    {
        String customerId = SecurityUtils.resolveEffectiveTenantId(null);
        String rootLabel = "全部科室";
        if (StringUtils.isNotEmpty(customerId))
        {
            SbCustomer c = sbCustomerService.selectSbCustomerById(customerId);
            if (c != null && StringUtils.isNotEmpty(c.getCustomerName()))
            {
                rootLabel = c.getCustomerName();
            }
        }
        List<FdDepartment> sorted = flatList != null ? new ArrayList<>(flatList) : new ArrayList<>();
        sorted.sort(Comparator.comparing(FdDepartment::getName, Comparator.nullsLast(String::compareTo)));
        Set<Long> idSet = sorted.stream().map(FdDepartment::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, FdDepartmentTreeNode> map = new HashMap<>();
        for (FdDepartment d : sorted)
        {
            if (d.getId() == null)
            {
                continue;
            }
            FdDepartmentTreeNode n = new FdDepartmentTreeNode();
            n.setNodeKey("d-" + d.getId());
            n.setDeptId(d.getId());
            n.setLabel(StringUtils.isNotEmpty(d.getName()) ? d.getName() : nz(d.getCode()));
            n.setChildren(new ArrayList<>());
            map.put(d.getId(), n);
        }
        List<FdDepartmentTreeNode> roots = new ArrayList<>();
        for (FdDepartment d : sorted)
        {
            if (d.getId() == null)
            {
                continue;
            }
            FdDepartmentTreeNode n = map.get(d.getId());
            Long pid = d.getParentId();
            if (pid != null && idSet.contains(pid) && map.containsKey(pid))
            {
                map.get(pid).getChildren().add(n);
            }
            else
            {
                roots.add(n);
            }
        }
        sortDepartmentTreeNodes(roots);
        FdDepartmentTreeNode root = new FdDepartmentTreeNode();
        root.setNodeKey("root");
        root.setDeptId(null);
        root.setLabel(rootLabel);
        root.setChildren(roots);
        return Collections.singletonList(root);
    }

    private static void sortDepartmentTreeNodes(List<FdDepartmentTreeNode> nodes)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return;
        }
        nodes.sort(Comparator.comparing(FdDepartmentTreeNode::getLabel, Comparator.nullsLast(String::compareTo)));
        for (FdDepartmentTreeNode n : nodes)
        {
            sortDepartmentTreeNodes(n.getChildren());
        }
    }

    /**
     * 新增科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    @Override
    public int insertFdDepartment(FdDepartment fdDepartment)
    {
        fdDepartment.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdDepartment.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            fdDepartment.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (fdDepartment.getDelFlag() == null) {
            fdDepartment.setDelFlag(0);
        }
        if (StringUtils.isEmpty(fdDepartment.getTenantId())) {
            String tid = SecurityUtils.resolveEffectiveTenantId(null);
            if (StringUtils.isNotEmpty(tid)) {
                fdDepartment.setTenantId(tid);
            }
        }
        validateParentAssignment(null, fdDepartment.getParentId(), fdDepartment.getTenantId());
        fdDepartment.setHisId(normalizeExternalId(fdDepartment.getHisId()));
        if (importRequiresMandatoryHisDeptId()) {
            if (isExternalIdBlank(fdDepartment.getHisId())) {
                throw new ServiceException("衡水市第三人民医院新增科室时必须填写HIS科室ID（第三方系统科室ID）");
            }
        } else if (isExternalIdBlank(fdDepartment.getHisId())) {
            fdDepartment.setHisId(null);
        }
        int n = fdDepartmentMapper.insertFdDepartment(fdDepartment);
        if (n > 0 && StringUtils.isNotEmpty(fdDepartment.getTenantId())) {
            Long deptId = fdDepartment.getId();
            if (deptId == null && StringUtils.isNotEmpty(fdDepartment.getCode())) {
                FdDepartment re = fdDepartmentMapper.selectFdDepartmentByCodeAndTenantId(fdDepartment.getCode(), fdDepartment.getTenantId());
                if (re != null) {
                    deptId = re.getId();
                }
            }
            if (deptId != null) {
                tenantFoundationAutoGrantService.grantDepartmentToTenantAdmins(fdDepartment.getTenantId(), deptId);
            }
        }
        return n;
    }

    /**
     * 修改科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    @Override
    public int updateFdDepartment(FdDepartment fdDepartment)
    {
        String custId = SecurityUtils.resolveEffectiveTenantId(null);
        if (fdDepartment.getId() == null) {
            throw new ServiceException("科室主键不能为空");
        }
        FdDepartment before = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(fdDepartment.getId()));
        if (before == null) {
            throw new ServiceException("科室不存在");
        }
        if (StringUtils.isNotEmpty(custId) && !custId.equals(before.getTenantId())) {
            throw new ServiceException("只能修改本客户下的科室");
        }
        // 第三方/HIS 科室 ID 仅展示，禁止通过维护接口修改（仅导入新增时可写入）
        if (before != null) {
            fdDepartment.setHisId(before.getHisId());
        }
        validateParentAssignment(fdDepartment.getId(), fdDepartment.getParentId(), before.getTenantId());
        fdDepartment.setUpdateTime(DateUtils.getNowDate());
        int n = fdDepartmentMapper.updateFdDepartment(fdDepartment);
        if (n > 0 && before != null) {
            FdDepartment after = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(fdDepartment.getId()));
            String logOp = StringUtils.isNotEmpty(fdDepartment.getUpdateBy()) ? fdDepartment.getUpdateBy() : SecurityUtils.getUserIdStr();
            recordDepartmentFieldChanges(before, after, logOp);
        }
        return n;
    }

    /**
     * 删除科室信息
     *
     * @param id 科室主键
     * @return 结果
     */
    @Override
    public int deleteFdDepartmentById(String id)
    {
        FdDepartment fdDepartment = fdDepartmentMapper.selectFdDepartmentById(id);
        if (fdDepartment == null) {
            throw new ServiceException(String.format("科室：%s，不存在!", id));
        }
        String custId = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(custId) && !custId.equals(fdDepartment.getTenantId())) {
            throw new ServiceException("只能删除本客户下的科室");
        }
        if (fdDepartment.getId() != null && fdDepartmentMapper.countChildrenByParentId(fdDepartment.getId()) > 0) {
            throw new ServiceException("存在下级科室，请先调整下级科室的上级或删除子科室后再删除");
        }
        fdDepartment.setDelFlag(1);
        fdDepartment.setUpdateTime(DateUtils.getNowDate());
        fdDepartment.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdDepartmentMapper.updateFdDepartment(fdDepartment);
    }

    @Override
    public List<FdDepartment> selectdepartmenAll() {
        String custId = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(custId)) {
            return fdDepartmentMapper.selectdepartmenAllByTenantId(custId);
        }
        return fdDepartmentMapper.selectdepartmenAll();
    }

    @Override
    public List<Long> selectDepartmenListByUserId(Long userId) {
        return fdDepartmentMapper.selectDepartmenListByUserId(userId);
    }

    @Override
    public List<FdDepartment> selectUserDepartmenAll(Long userId) {
        List<FdDepartment> list = fdDepartmentMapper.selectUserDepartmenAll(userId);
        String custId = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(custId) && list != null) {
            list = list.stream().filter(d -> custId.equals(d.getTenantId())).collect(Collectors.toList());
        }
        return list != null ? list : Collections.emptyList();
    }

//    /**
//     * 批量删除科室
//     *
//     * @param ids 需要删除的科室主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdDepartmentByIds(String[] ids)
//    {
//        return fdDepartmentMapper.deleteFdDepartmentByIds(ids);
//    }
//
//    /**
//     * 删除科室信息
//     *
//     * @param id 科室主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdDepartmentById(String id)
//    {
//        return fdDepartmentMapper.deleteFdDepartmentById(id);
//    }

    /**
     * 批量更新科室名称简码（referred_name）
     */
    @Override
    public void updateReferred(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String custId = SecurityUtils.resolveEffectiveTenantId(null);
        String op = SecurityUtils.getUserIdStr();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            FdDepartment before = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(id));
            if (before == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(custId) && !custId.equals(before.getTenantId())) {
                continue;
            }
            String name = before.getName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            FdDepartment dept = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(id));
            dept.setReferredName(PinyinUtils.getPinyinInitials(name));
            dept.setUpdateBy(op);
            dept.setUpdateTime(DateUtils.getNowDate());
            fdDepartmentMapper.updateFdDepartment(dept);
            FdDepartment after = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(id));
            recordDepartmentFieldChanges(before, after, op);
        }
    }

    @Override
    public List<FdDepartmentChangeLog> selectDepartmentChangeLog(Long departmentId) {
        if (departmentId == null) {
            return Collections.emptyList();
        }
        List<FdDepartmentChangeLog> list = fdDepartmentChangeLogMapper.selectByDepartmentId(departmentId);
        return list != null ? list : Collections.emptyList();
    }

    private static String nz(String v) {
        return v == null ? "" : v;
    }

    private void recordDepartmentFieldChanges(FdDepartment before, FdDepartment after, String operator) {
        if (before == null || after == null || after.getId() == null) {
            return;
        }
        Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        Long deptId = after.getId();
        pushDeptChange(deptId, op, now, "code", "科室编码", nz(before.getCode()), nz(after.getCode()));
        pushDeptChange(deptId, op, now, "name", "科室名称", nz(before.getName()), nz(after.getName()));
        pushDeptChange(deptId, op, now, "referred_name", "简码", nz(before.getReferredName()), nz(after.getReferredName()));
        pushDeptChange(deptId, op, now, "remark", "备注", nz(before.getDeptRemark()), nz(after.getDeptRemark()));
        pushDeptChange(deptId, op, now, "parent_id", "上级科室ID",
            before.getParentId() == null ? "" : String.valueOf(before.getParentId()),
            after.getParentId() == null ? "" : String.valueOf(after.getParentId()));
    }

    private void validateParentAssignment(Long deptId, Long parentId, String tenantId)
    {
        if (parentId == null)
        {
            return;
        }
        if (deptId != null && deptId.equals(parentId))
        {
            throw new ServiceException("上级科室不能为本科室");
        }
        FdDepartment parent = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(parentId));
        if (parent == null || (parent.getDelFlag() != null && parent.getDelFlag() == 1))
        {
            throw new ServiceException("上级科室不存在或已删除");
        }
        if (!tenantIdEquals(tenantId, parent.getTenantId()))
        {
            throw new ServiceException("上级科室必须属于同一客户/租户");
        }
        Long cur = parentId;
        for (int i = 0; i < 2000 && cur != null; i++)
        {
            if (deptId != null && cur.equals(deptId))
            {
                throw new ServiceException("不能将上级设为本科室或其下级科室");
            }
            FdDepartment x = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(cur));
            if (x == null)
            {
                break;
            }
            cur = x.getParentId();
        }
    }

    private static boolean tenantIdEquals(String a, String b)
    {
        String na = StringUtils.isEmpty(a) ? "" : a;
        String nb = StringUtils.isEmpty(b) ? "" : b;
        return na.equals(nb);
    }

    private void pushDeptChange(Long departmentId, String operator, Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue) {
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        FdDepartmentChangeLog rec = new FdDepartmentChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setDepartmentId(departmentId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdDepartmentChangeLogMapper.insert(rec);
    }

    @Override
    public Map<String, Object> validateFdDepartmentImport(List<FdDepartment> list, Boolean isUpdateSupport) {
        clearDepartmentImportValidationColumn(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requiresHisDeptId", importRequiresMandatoryHisDeptId());
        ImportRowErrorCollector collector = collectImportValidationErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        if (list != null) {
            result.put("totalRows", list.size());
        } else {
            result.put("totalRows", 0);
        }
        if (valid && list != null && !list.isEmpty()) {
            String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
            int insertCount = 0;
            int updateCount = 0;
            for (FdDepartment row : list) {
                if (row == null) {
                    continue;
                }
                normalizeImportRow(row);
                if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName())) {
                    continue;
                }
                if (StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName())) {
                    continue;
                }
                FdDepartment existing = fdDepartmentMapper.selectFdDepartmentByCodeAndTenantId(row.getCode(), tenantId);
                if (existing == null) {
                    insertCount++;
                } else if (Boolean.TRUE.equals(isUpdateSupport)) {
                    updateCount++;
                }
            }
            result.put("insertCount", insertCount);
            result.put("updateCount", updateCount);
        } else {
            result.put("insertCount", 0);
            result.put("updateCount", 0);
        }
        fillDepartmentValidationTexts(list, collector, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdDepartment.class, list));
        return result;
    }

    @Override
    public String importFdDepartment(List<FdDepartment> list, Boolean isUpdateSupport, String operName, boolean confirmed) {
        if (!confirmed) {
            throw new ServiceException("请先完成校验并在确认后再导入");
        }
        if (list == null || list.isEmpty()) {
            throw new ServiceException("导入科室数据不能为空！");
        }
        clearDepartmentImportValidationColumn(list);
        ImportRowErrorCollector collector = collectImportValidationErrors(list, isUpdateSupport);
        List<String> errors = collector.getAllErrors();
        if (!errors.isEmpty()) {
            throw new ServiceException("数据已变更或校验未通过，请重新校验后再导入。详情：" + String.join("；", errors));
        }
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        int successNum = 0;
        StringBuilder successMsg = new StringBuilder();
        for (FdDepartment row : list) {
            if (row == null || StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName())) {
                continue;
            }
            normalizeImportRow(row);
            try {
                BeanValidators.validateWithException(validator, row);
            } catch (Exception e) {
                throw new ServiceException("导入校验异常：" + e.getMessage());
            }
            row.setReferredName(PinyinUtils.getPinyinInitials(row.getName()));
            FdDepartment existing = fdDepartmentMapper.selectFdDepartmentByCodeAndTenantId(row.getCode(), tenantId);
            if (existing == null) {
                row.setCreateBy(operName);
                row.setDelFlag(0);
                if (StringUtils.isNotEmpty(tenantId)) {
                    row.setTenantId(tenantId);
                }
                insertFdDepartment(row);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、科室 ").append(row.getName()).append(" 导入成功");
            } else if (Boolean.TRUE.equals(isUpdateSupport)) {
                applyImportUpdate(existing, row, operName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、科室 ").append(row.getName()).append(" 更新成功");
            }
        }
        successMsg.insert(0, "增量导入完成（仅新增或按选项更新已有编码，不会删除未出现在文件中的科室）。共处理 " + successNum + " 条，明细如下：");
        markDepartmentImportSuccessTexts(list);
        return successMsg.toString();
    }

    /**
     * 衡水市第三人民医院（{@link TenantEnum#HS_003}）等对接 HIS 的租户：导入时每行必须填写 HIS 科室 ID（库字段 his_id）
     */
    private static boolean importRequiresMandatoryHisDeptId() {
        return TenantEnum.HS_003 == TenantEnum.fromCustomerId(SecurityUtils.resolveEffectiveTenantId(null));
    }

    /**
     * 规范化导入行（与校验阶段逻辑一致）
     */
    private void normalizeImportRow(FdDepartment row) {
        if (row.getCode() != null) {
            row.setCode(row.getCode().trim());
        }
        if (row.getName() != null) {
            row.setName(row.getName().trim());
        }
        if (row.getReferredName() != null) {
            row.setReferredName(row.getReferredName().trim());
        }
        if (row.getDeptRemark() != null) {
            row.setDeptRemark(row.getDeptRemark().trim());
        }
        row.setHisId(normalizeExternalId(row.getHisId()));
    }

    private static String normalizeExternalId(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return "";
        }
        if (s.matches("\\d+\\.0+")) {
            return s.substring(0, s.indexOf('.'));
        }
        return s;
    }

    private static boolean isExternalIdBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private ImportRowErrorCollector collectImportValidationErrors(List<FdDepartment> list, Boolean isUpdateSupport) {
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty()) {
            c.addGlobal("导入科室数据不能为空");
            return c;
        }
        String tenantId = SecurityUtils.resolveEffectiveTenantId(null);
        Map<String, Integer> codeFirstRow = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            FdDepartment row = list.get(i);
            int excelRow = i + 2;
            if (row == null) {
                c.addRow(excelRow, "数据为空");
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName())) {
                continue;
            }
            if (StringUtils.isEmpty(row.getCode()) || StringUtils.isEmpty(row.getName())) {
                c.addRow(excelRow, "科室编码与名称均不能为空");
                continue;
            }
            String code = row.getCode();
            if (codeFirstRow.containsKey(code)) {
                c.addRow(excelRow, "科室编码「" + code + "」与第" + codeFirstRow.get(code) + "行重复");
            } else {
                codeFirstRow.put(code, excelRow);
            }
            FdDepartment existing = fdDepartmentMapper.selectFdDepartmentByCodeAndTenantId(code, tenantId);
            if (existing == null) {
                if (importRequiresMandatoryHisDeptId() && isExternalIdBlank(row.getHisId())) {
                    c.addRow(excelRow, "HIS科室ID（第三方系统科室ID）不能为空（衡水市第三人民医院新增科室时必填）");
                }
            } else {
                if (!Boolean.TRUE.equals(isUpdateSupport)) {
                    c.addRow(excelRow, "科室编码「" + code + "」在租户下已存在，未勾选「更新已存在」则无法导入");
                    continue;
                }
            }
            try {
                BeanValidators.validateWithException(validator, row);
            } catch (Exception e) {
                c.addRow(excelRow, e.getMessage());
            }
        }
        return c;
    }

    private void clearDepartmentImportValidationColumn(List<FdDepartment> list) {
        if (list == null) {
            return;
        }
        for (FdDepartment r : list) {
            if (r != null) {
                r.setValidationResult(null);
            }
        }
    }

    private void fillDepartmentValidationTexts(List<FdDepartment> list, ImportRowErrorCollector collector, boolean fileValid) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            int excelRow = i + 2;
            FdDepartment row = list.get(i);
            if (row == null) {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName())) {
                row.setValidationResult("空行（已跳过）");
                continue;
            }
            List<String> msgs = collector.getRowMessages(excelRow);
            if (!msgs.isEmpty()) {
                row.setValidationResult(String.join("；", msgs));
            } else if (fileValid) {
                row.setValidationResult("校验通过");
            } else {
                row.setValidationResult("本行未单独报错；文件因其他数据未通过校验");
            }
        }
    }

    private void markDepartmentImportSuccessTexts(List<FdDepartment> list) {
        if (list == null) {
            return;
        }
        for (FdDepartment row : list) {
            if (row == null) {
                continue;
            }
            normalizeImportRow(row);
            if (StringUtils.isEmpty(row.getCode()) && StringUtils.isEmpty(row.getName())) {
                row.setValidationResult("空行（已跳过）");
            } else {
                row.setValidationResult("导入成功");
            }
        }
    }

    /**
     * 导入更新已存在科室：仅更新科室名称，简码由名称生成；不修改第三方/HIS 科室 ID 与备注
     */
    private void applyImportUpdate(FdDepartment existing, FdDepartment row, String operName) {
        existing.setName(row.getName());
        existing.setReferredName(PinyinUtils.getPinyinInitials(row.getName()));
        existing.setUpdateBy(operName);
        updateFdDepartment(existing);
    }
}
