package com.spd.equipment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.DictUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetCategory;
import com.spd.equipment.domain.SbCustomerAssetLedger;
import com.spd.equipment.mapper.SbCustomerAssetLedgerMapper;
import com.spd.equipment.service.ISbCustomerAssetLedgerService;
import com.spd.equipment.service.ISbAssetCategoryService;
import com.spd.equipment.service.ISbEquipmentManufacturerService;
import com.spd.equipment.service.ISbEquipmentSupplierService;
import com.spd.equipment.domain.SbEquipmentManufacturer;
import com.spd.equipment.domain.SbEquipmentSupplier;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.mapper.SbCustomerCategory68Mapper;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.system.service.ISbUserPermissionService;
import com.spd.system.service.ISbWorkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SbCustomerAssetLedgerServiceImpl implements ISbCustomerAssetLedgerService {

    private static final String USE_STATUS_DEFAULT = "in_use";
    private static final String REPAIR_STATUS_DEFAULT = "no_fault";
    private static final String LABEL_PRINT_DEFAULT = "N";

    @Autowired
    private SbCustomerAssetLedgerMapper mapper;
    @Autowired
    private SbCustomerCategory68Mapper category68Mapper;
    @Autowired
    private IFdDepartmentService fdDepartmentService;
    @Autowired
    private ISbWorkGroupService sbWorkGroupService;
    @Autowired
    private ISbUserPermissionService sbUserPermissionService;
    @Autowired
    private ISbEquipmentManufacturerService manufacturerService;
    @Autowired
    private ISbEquipmentSupplierService supplierService;
    @Autowired
    private ISbAssetCategoryService assetCategoryService;

    @Override
    public List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        List<SbCustomerAssetLedger> list = mapper.selectList(q);
        fillDictLabels(list);
        return list;
    }

    @Override
    public SbCustomerAssetLedger selectById(String id) {
        SbCustomerAssetLedger row = mapper.selectById(id);
        if (row != null) fillDictLabel(row);
        return row;
    }

    @Override
    public int insert(SbCustomerAssetLedger row) {
        if (StringUtils.isEmpty(row.getCustomerId())) row.setCustomerId(SecurityUtils.getCustomerId());
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        if (StringUtils.isEmpty(row.getEquipmentSerialNo())) {
            Integer maxN = mapper.selectMaxSerialNoNumeric(row.getCustomerId());
            row.setEquipmentSerialNo(String.valueOf((maxN == null ? 0 : maxN) + 1));
        }
        if (StringUtils.isNotEmpty(row.getCategory68Id())) {
            SbCustomerCategory68 cat = category68Mapper.selectById(row.getCategory68Id());
            if (cat != null) {
                String code = cat.getCategory68Code();
                if (StringUtils.isEmpty(row.getCategory68Code())) row.setCategory68Code(code);
                Integer maxSeq = mapper.selectMaxArchiveNoSeq(row.getCustomerId(), row.getCategory68Id());
                int next = (maxSeq == null ? 0 : maxSeq) + 1;
                row.setCategory68ArchiveNo(code + "-" + String.format("%04d", next));
            }
        }
        if (StringUtils.isEmpty(row.getUseStatus())) row.setUseStatus(USE_STATUS_DEFAULT);
        if (StringUtils.isEmpty(row.getRepairStatus())) row.setRepairStatus(REPAIR_STATUS_DEFAULT);
        if (StringUtils.isEmpty(row.getLabelPrintStatus())) row.setLabelPrintStatus(LABEL_PRINT_DEFAULT);
        fillDictLabel(row);
        return mapper.insert(row);
    }

    @Override
    public int update(SbCustomerAssetLedger row) {
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        SbCustomerAssetLedger row = new SbCustomerAssetLedger();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        return mapper.update(row);
    }

    @Override
    public String importAssetLedger(List<SbCustomerAssetLedger> list) {
        if (list == null || list.isEmpty()) {
            throw new ServiceException("导入数据不能为空");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(customerId)) {
            throw new ServiceException("未获取到当前客户，无法导入");
        }
        List<FdDepartment> allowedDepts = getAllowedDeptsForCurrentUser();

        // 第一遍：整份文件校验。所属科室或流水号任一不规范则整个文件不允许导入
        StringBuilder validateErrors = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            SbCustomerAssetLedger row = list.get(i);
            int rowNum = i + 2;
            String serialNo = row.getEquipmentSerialNo() != null ? row.getEquipmentSerialNo().trim() : "";
            if (StringUtils.isNotEmpty(serialNo)) {
                SbCustomerAssetLedger existing = mapper.selectByCustomerIdAndEquipmentSerialNo(customerId, serialNo);
                if (existing == null) {
                    validateErrors.append("<br/>第").append(rowNum).append("行：设备流水号【").append(serialNo).append("】在系统中不存在");
                }
            }
            String deptName = row.getDeptName() != null ? row.getDeptName().trim() : "";
            if (StringUtils.isEmpty(deptName)) {
                validateErrors.append("<br/>第").append(rowNum).append("行：所属科室不能为空");
            } else {
                boolean deptMatched = false;
                for (FdDepartment d : allowedDepts) {
                    if (d.getName() != null && d.getName().trim().equals(deptName)) {
                        deptMatched = true;
                        break;
                    }
                }
                if (!deptMatched) {
                    validateErrors.append("<br/>第").append(rowNum).append("行：所属科室【").append(deptName).append("】不在客户科室列表中");
                }
            }
            if (StringUtils.isEmpty(row.getName()) || row.getName().trim().isEmpty()) {
                validateErrors.append("<br/>第").append(rowNum).append("行：名称为空");
            }
        }
        if (validateErrors.length() > 0) {
            throw new ServiceException("校验未通过，整个文件不允许导入或更新：" + validateErrors.toString());
        }

        // 第二遍：逐行解析并写入
        int successNum = 0;
        for (int i = 0; i < list.size(); i++) {
            SbCustomerAssetLedger row = list.get(i);
            int rowNum = i + 2;
            try {
                String serialNo = row.getEquipmentSerialNo() != null ? row.getEquipmentSerialNo().trim() : "";
                boolean isUpdate = StringUtils.isNotEmpty(serialNo);

                if (isUpdate) {
                    SbCustomerAssetLedger existing = mapper.selectByCustomerIdAndEquipmentSerialNo(customerId, serialNo);
                    row.setId(existing.getId());
                    row.setCustomerId(customerId);
                    row.setEquipmentSerialNo(existing.getEquipmentSerialNo());
                }

                String deptName = row.getDeptName().trim();
                for (FdDepartment d : allowedDepts) {
                    if (d.getName() != null && d.getName().trim().equals(deptName)) {
                        row.setDeptId(String.valueOf(d.getId()));
                        row.setDeptName(d.getName());
                        break;
                    }
                }
                if (StringUtils.isNotEmpty(row.getManufacturerName())) {
                    SbEquipmentManufacturer m = manufacturerService.getOrCreateByName(row.getManufacturerName().trim());
                    if (m != null) {
                        row.setManufacturerId(m.getId());
                        row.setManufacturerName(m.getName());
                    }
                }
                if (StringUtils.isNotEmpty(row.getSupplierName())) {
                    SbEquipmentSupplier s = supplierService.getOrCreateByName(row.getSupplierName().trim());
                    if (s != null) {
                        row.setSupplierId(s.getId());
                        row.setSupplierName(s.getName());
                    }
                }
                if (StringUtils.isNotEmpty(row.getAssetCategoryName()) && row.getAssetCategoryName().trim().length() > 0) {
                    SbAssetCategory q = new SbAssetCategory();
                    q.setCustomerId(customerId);
                    q.setCategoryName(row.getAssetCategoryName().trim());
                    List<SbAssetCategory> cats = assetCategoryService.selectList(q);
                    if (cats != null) {
                        for (SbAssetCategory c : cats) {
                            if (row.getAssetCategoryName().trim().equals(c.getCategoryName())) {
                                row.setAssetCategoryId(c.getId());
                                row.setAssetCategoryName(c.getCategoryName());
                                break;
                            }
                        }
                    }
                }
                row.setCustomerId(customerId);
                if (isUpdate) {
                    if (StringUtils.isNotEmpty(row.getName())) {
                        row.setNamePinyin(PinyinUtils.getPinyinInitials(row.getName()));
                    }
                    fillDictLabel(row);
                    mapper.update(row);
                } else {
                    insert(row);
                }
                successNum++;
            } catch (Exception e) {
                throw new ServiceException("第" + rowNum + "行执行失败，已终止导入：" + e.getMessage());
            }
        }
        return "成功导入 " + successNum + " 条";
    }

    private List<FdDepartment> getAllowedDeptsForCurrentUser() {
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(customerId)) return new ArrayList<>();
        List<FdDepartment> list = fdDepartmentService.selectdepartmenAll();
        if (list == null) list = new ArrayList<>();
        if (!sbWorkGroupService.isUserInSuperGroup(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = sbUserPermissionService.selectDeptIdsByUserId(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty()) list = new ArrayList<>();
            else list = list.stream().filter(d -> d.getId() != null && allowedIds.contains(d.getId())).collect(Collectors.toList());
        }
        return list;
    }

    private void fillDictLabels(List<SbCustomerAssetLedger> list) {
        if (list == null) return;
        for (SbCustomerAssetLedger r : list) fillDictLabel(r);
    }

    private void fillDictLabel(SbCustomerAssetLedger r) {
        if (r == null) return;
        if (StringUtils.isNotEmpty(r.getUseStatus())) {
            r.setUseStatusName(DictUtils.getDictLabel("eq_use_status", r.getUseStatus()));
        }
        if (StringUtils.isNotEmpty(r.getRepairStatus())) {
            r.setRepairStatusName(DictUtils.getDictLabel("eq_repair_status", r.getRepairStatus()));
        }
    }
}
