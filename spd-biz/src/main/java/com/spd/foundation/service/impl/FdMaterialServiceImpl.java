package com.spd.foundation.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.spd.common.annotation.DataSource;
import com.spd.common.enums.DataSourceType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterialChangeLog;
import com.spd.foundation.domain.FdMaterialImport;
import com.spd.foundation.domain.FdMaterialStatusLog;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.vo.MaterialTimelineVo;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.mapper.FdMaterialChangeLogMapper;
import com.spd.foundation.mapper.FdMaterialImportMapper;
import com.spd.foundation.mapper.FdMaterialStatusLogMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.mapper.FdLocationMapper;
import com.spd.warehouse.mapper.StkIoBillMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.service.IFdMaterialService;

import javax.validation.Validator;

/**
 * 耗材产品Service业务层处理
 *
 * @author spd
 * @date 2023-12-23
 */
@Service
public class FdMaterialServiceImpl implements IFdMaterialService
{
    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    protected Validator validator;

    @Autowired
    private FdMaterialImportMapper fdMaterialImportMapper;

    @Autowired
    private FdSupplierMapper fdSupplierMapper;

    @Autowired
    private FdFactoryMapper fdFactoryMapper;

    @Autowired
    private FdWarehouseCategoryMapper fdWarehouseCategoryMapper;

    @Autowired
    private FdFinanceCategoryMapper fdFinanceCategoryMapper;

    @Autowired
    private FdUnitMapper fdUnitMapper;

    @Autowired
    private FdLocationMapper fdLocationMapper;

    @Autowired
    private FdMaterialStatusLogMapper fdMaterialStatusLogMapper;

    @Autowired
    private FdMaterialChangeLogMapper fdMaterialChangeLogMapper;

    private static final Logger log = LoggerFactory.getLogger(FdMaterialServiceImpl.class);

    /** 产品档案字段中文名（用于变更记录） */
    private static final Map<String, String> MATERIAL_FIELD_LABELS = new LinkedHashMap<>();
    static {
        MATERIAL_FIELD_LABELS.put("code", "耗材编码");
        MATERIAL_FIELD_LABELS.put("name", "耗材名称");
        MATERIAL_FIELD_LABELS.put("referredName", "名称简码");
        MATERIAL_FIELD_LABELS.put("supplierId", "供应商");
        MATERIAL_FIELD_LABELS.put("speci", "规格");
        MATERIAL_FIELD_LABELS.put("model", "型号");
        MATERIAL_FIELD_LABELS.put("price", "价格");
        MATERIAL_FIELD_LABELS.put("useName", "通用名称");
        MATERIAL_FIELD_LABELS.put("factoryId", "生产厂家");
        MATERIAL_FIELD_LABELS.put("storeroomId", "库房分类");
        MATERIAL_FIELD_LABELS.put("financeCategoryId", "财务分类");
        MATERIAL_FIELD_LABELS.put("unitId", "单位");
        MATERIAL_FIELD_LABELS.put("registerName", "注册证名称");
        MATERIAL_FIELD_LABELS.put("registerNo", "注册证件号");
        MATERIAL_FIELD_LABELS.put("medicalName", "医保名称");
        MATERIAL_FIELD_LABELS.put("medicalNo", "医保编码");
        MATERIAL_FIELD_LABELS.put("periodDate", "有效期");
        MATERIAL_FIELD_LABELS.put("successfulType", "招标类别");
        MATERIAL_FIELD_LABELS.put("successfulNo", "中标号");
        MATERIAL_FIELD_LABELS.put("successfulPrice", "中标价格");
        MATERIAL_FIELD_LABELS.put("salePrice", "销售价");
        MATERIAL_FIELD_LABELS.put("packageSpeci", "包装规格");
        MATERIAL_FIELD_LABELS.put("producer", "产地");
        MATERIAL_FIELD_LABELS.put("materialLevel", "耗材级别");
        MATERIAL_FIELD_LABELS.put("registerLevel", "注册证级别");
        MATERIAL_FIELD_LABELS.put("riskLevel", "风险级别");
        MATERIAL_FIELD_LABELS.put("firstaidLevel", "急救类型");
        MATERIAL_FIELD_LABELS.put("doctorLevel", "医用级别");
        MATERIAL_FIELD_LABELS.put("brand", "品牌");
        MATERIAL_FIELD_LABELS.put("useto", "用途");
        MATERIAL_FIELD_LABELS.put("quality", "材质");
        MATERIAL_FIELD_LABELS.put("function", "功能");
        MATERIAL_FIELD_LABELS.put("isWay", "储存方式");
        MATERIAL_FIELD_LABELS.put("udiNo", "UDI码");
        MATERIAL_FIELD_LABELS.put("permitNo", "许可证编号");
        MATERIAL_FIELD_LABELS.put("countryNo", "国家编码");
        MATERIAL_FIELD_LABELS.put("countryName", "国家医保名称");
        MATERIAL_FIELD_LABELS.put("description", "商品说明");
        MATERIAL_FIELD_LABELS.put("isUse", "使用状态");
        MATERIAL_FIELD_LABELS.put("isProcure", "带量采购");
        MATERIAL_FIELD_LABELS.put("isMonitor", "重点监测");
        MATERIAL_FIELD_LABELS.put("isGz", "是否高值");
        MATERIAL_FIELD_LABELS.put("isFollow", "是否跟台");
        MATERIAL_FIELD_LABELS.put("locationId", "货位");
        MATERIAL_FIELD_LABELS.put("hisId", "第三方系统产品档案ID");
        MATERIAL_FIELD_LABELS.put("selectionReason", "入选原因");
    }

    /**
     * 查询耗材产品
     *
     * @param id 耗材产品主键
     * @return 耗材产品
     */
    @Override
    public FdMaterial selectFdMaterialById(Long id)
    {
        return fdMaterialMapper.selectFdMaterialById(id);
    }

    /**
     * 查询耗材产品列表
     *
     * @param fdMaterial 耗材产品
     * @return 耗材产品
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public List<FdMaterial> selectFdMaterialList(FdMaterial fdMaterial)
    {
        return fdMaterialMapper.selectFdMaterialList(fdMaterial);
    }

    /**
     * 新增耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    @Override
    public int insertFdMaterial(FdMaterial fdMaterial)
    {
        fdMaterial.setCreateTime(DateUtils.getNowDate());
        return fdMaterialMapper.insertFdMaterial(fdMaterial);
    }

    /**
     * 修改耗材产品（含启用/停用原因写入状态记录、字段变更写入变更记录）
     *
     * @param fdMaterial 耗材产品（可带 statusChangeReason 表示启用/停用原因）
     * @return 结果
     */
    @Override
    public int updateFdMaterial(FdMaterial fdMaterial)
    {
        Date now = DateUtils.getNowDate();
        fdMaterial.setUpdateTime(now);
        String operator = SecurityUtils.getUsername();
        if (operator == null) {
            operator = fdMaterial.getUpdateBy() != null ? fdMaterial.getUpdateBy() : "";
        }
        FdMaterial oldMaterial = fdMaterialMapper.selectFdMaterialById(fdMaterial.getId());
        if (oldMaterial != null) {
            // 使用状态变更：记录启用/停用流水
            if (fdMaterial.getIsUse() != null && !fdMaterial.getIsUse().equals(oldMaterial.getIsUse())
                    && StringUtils.isNotEmpty(fdMaterial.getStatusChangeReason())) {
                FdMaterialStatusLog statusLog = new FdMaterialStatusLog();
                statusLog.setId(UUID7.generateUUID7());
                statusLog.setMaterialId(fdMaterial.getId());
                statusLog.setAction("1".equals(fdMaterial.getIsUse()) ? "enable" : "disable");
                statusLog.setActionTime(now);
                statusLog.setOperator(operator);
                statusLog.setReason(fdMaterial.getStatusChangeReason());
                fdMaterialStatusLogMapper.insert(statusLog);
            }
            // 字段变更记录
            saveChangeLogs(fdMaterial.getId(), oldMaterial, fdMaterial, now, operator);
        }
        return fdMaterialMapper.updateFdMaterial(fdMaterial);
    }

    /**
     * 产品档案停用：更新为停用并记录停用时间、停用人、停用原因
     */
    @Override
    public void disableMaterial(Long materialId, String reason) {
        if (materialId == null) {
            throw new ServiceException("产品档案ID不能为空");
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(materialId);
        if (material == null) {
            throw new ServiceException("产品档案不存在");
        }
        material.setIsUse("2");
        material.setUpdateTime(DateUtils.getNowDate());
        material.setUpdateBy(SecurityUtils.getUsername());
        fdMaterialMapper.updateFdMaterial(material);
        FdMaterialStatusLog logRecord = new FdMaterialStatusLog();
        logRecord.setId(UUID7.generateUUID7());
        logRecord.setMaterialId(materialId);
        logRecord.setAction("disable");
        logRecord.setActionTime(DateUtils.getNowDate());
        logRecord.setOperator(SecurityUtils.getUsername());
        logRecord.setReason(reason);
        fdMaterialStatusLogMapper.insert(logRecord);
    }

    /**
     * 产品档案启用：更新为启用并记录启用时间、启用人、启用原因
     */
    @Override
    public void enableMaterial(Long materialId, String reason) {
        if (materialId == null) {
            throw new ServiceException("产品档案ID不能为空");
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(materialId);
        if (material == null) {
            throw new ServiceException("产品档案不存在");
        }
        material.setIsUse("1");
        material.setUpdateTime(DateUtils.getNowDate());
        material.setUpdateBy(SecurityUtils.getUsername());
        fdMaterialMapper.updateFdMaterial(material);
        FdMaterialStatusLog logRecord = new FdMaterialStatusLog();
        logRecord.setId(UUID7.generateUUID7());
        logRecord.setMaterialId(materialId);
        logRecord.setAction("enable");
        logRecord.setActionTime(DateUtils.getNowDate());
        logRecord.setOperator(SecurityUtils.getUsername());
        logRecord.setReason(reason);
        fdMaterialStatusLogMapper.insert(logRecord);
    }

    @Override
    public List<FdMaterialStatusLog> listStatusLogByMaterialId(Long materialId) {
        if (materialId == null) {
            return new ArrayList<>();
        }
        return fdMaterialStatusLogMapper.selectByMaterialId(materialId);
    }

    @Override
    public List<FdMaterialChangeLog> listChangeLogByMaterialId(Long materialId) {
        if (materialId == null) {
            return new ArrayList<>();
        }
        return fdMaterialChangeLogMapper.selectByMaterialId(materialId);
    }

    @Override
    public List<MaterialTimelineVo> getMaterialTimeline(Long materialId) {
        if (materialId == null) {
            return new ArrayList<>();
        }
        List<MaterialTimelineVo> list = new ArrayList<>();
        for (FdMaterialStatusLog s : fdMaterialStatusLogMapper.selectByMaterialId(materialId)) {
            MaterialTimelineVo vo = new MaterialTimelineVo();
            vo.setEventTime(s.getActionTime());
            vo.setType(s.getAction());
            vo.setOperator(s.getOperator());
            vo.setTitle("enable".equals(s.getAction()) ? "启用" : "停用");
            vo.setDescription(StringUtils.isNotEmpty(s.getReason()) ? s.getReason() : "");
            list.add(vo);
        }
        List<FdMaterialChangeLog> changeLogs = fdMaterialChangeLogMapper.selectByMaterialId(materialId);
        Map<String, List<FdMaterialChangeLog>> byTime = changeLogs.stream()
                .collect(Collectors.groupingBy(c -> (c.getChangeTime() != null ? c.getChangeTime().getTime() : 0) + "_" + (c.getOperator() != null ? c.getOperator() : "")));
        for (List<FdMaterialChangeLog> group : byTime.values()) {
            if (group.isEmpty()) continue;
            FdMaterialChangeLog first = group.get(0);
            MaterialTimelineVo vo = new MaterialTimelineVo();
            vo.setEventTime(first.getChangeTime());
            vo.setType("change");
            vo.setOperator(first.getOperator());
            String fields = group.stream()
                    .map(FdMaterialChangeLog::getFieldLabel)
                    .filter(StringUtils::isNotEmpty)
                    .distinct()
                    .collect(Collectors.joining("、"));
            vo.setTitle("字段变更");
            vo.setDescription(fields.isEmpty() ? "若干字段" : fields);
            list.add(vo);
        }
        list.sort(Comparator.comparing(MaterialTimelineVo::getEventTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    /**
     * 比较新旧档案，将变更写入 fd_material_change_log
     */
    private void saveChangeLogs(Long materialId, FdMaterial oldM, FdMaterial newM, Date changeTime, String operator) {
        List<FdMaterialChangeLog> logs = new ArrayList<>();
        for (Map.Entry<String, String> entry : MATERIAL_FIELD_LABELS.entrySet()) {
            String fieldName = entry.getKey();
            String fieldLabel = entry.getValue();
            String oldVal = getFieldValue(oldM, fieldName);
            String newVal = getFieldValue(newM, fieldName);
            if (oldVal == null && newVal == null) {
                continue;
            }
            if (oldVal == null) {
                oldVal = "";
            }
            if (newVal == null) {
                newVal = "";
            }
            if (!oldVal.equals(newVal)) {
                FdMaterialChangeLog changeLog = new FdMaterialChangeLog();
                changeLog.setId(UUID7.generateUUID7());
                changeLog.setMaterialId(materialId);
                changeLog.setChangeTime(changeTime);
                changeLog.setOperator(operator);
                changeLog.setFieldName(fieldName);
                changeLog.setFieldLabel(fieldLabel);
                changeLog.setOldValue(oldVal.length() > 500 ? oldVal.substring(0, 500) + "..." : oldVal);
                changeLog.setNewValue(newVal.length() > 500 ? newVal.substring(0, 500) + "..." : newVal);
                logs.add(changeLog);
            }
        }
        for (FdMaterialChangeLog logEntry : logs) {
            fdMaterialChangeLogMapper.insert(logEntry);
        }
    }

    private String getFieldValue(FdMaterial m, String fieldName) {
        if (m == null) {
            return null;
        }
        switch (fieldName) {
            case "code": return m.getCode();
            case "name": return m.getName();
            case "referredName": return m.getReferredName();
            case "supplierId": return m.getSupplierId() != null ? String.valueOf(m.getSupplierId()) : null;
            case "speci": return m.getSpeci();
            case "model": return m.getModel();
            case "price": return m.getPrice() != null ? m.getPrice().toPlainString() : null;
            case "useName": return m.getUseName();
            case "factoryId": return m.getFactoryId() != null ? String.valueOf(m.getFactoryId()) : null;
            case "storeroomId": return m.getStoreroomId() != null ? String.valueOf(m.getStoreroomId()) : null;
            case "financeCategoryId": return m.getFinanceCategoryId() != null ? String.valueOf(m.getFinanceCategoryId()) : null;
            case "unitId": return m.getUnitId() != null ? String.valueOf(m.getUnitId()) : null;
            case "registerName": return m.getRegisterName();
            case "registerNo": return m.getRegisterNo();
            case "medicalName": return m.getMedicalName();
            case "medicalNo": return m.getMedicalNo();
            case "periodDate": return m.getPeriodDate() != null ? DateUtils.dateTime(m.getPeriodDate()) : null;
            case "successfulType": return m.getSuccessfulType();
            case "successfulNo": return m.getSuccessfulNo();
            case "successfulPrice": return m.getSuccessfulPrice() != null ? m.getSuccessfulPrice().toPlainString() : null;
            case "salePrice": return m.getSalePrice() != null ? m.getSalePrice().toPlainString() : null;
            case "packageSpeci": return m.getPackageSpeci();
            case "producer": return m.getProducer();
            case "materialLevel": return m.getMaterialLevel();
            case "registerLevel": return m.getRegisterLevel();
            case "riskLevel": return m.getRiskLevel();
            case "firstaidLevel": return m.getFirstaidLevel();
            case "doctorLevel": return m.getDoctorLevel();
            case "brand": return m.getBrand();
            case "useto": return m.getUseto();
            case "quality": return m.getQuality();
            case "function": return m.getFunction();
            case "isWay": return m.getIsWay();
            case "udiNo": return m.getUdiNo();
            case "permitNo": return m.getPermitNo();
            case "countryNo": return m.getCountryNo();
            case "countryName": return m.getCountryName();
            case "description": return m.getDescription();
            case "isUse": return m.getIsUse();
            case "isProcure": return m.getIsProcure();
            case "isMonitor": return m.getIsMonitor();
            case "isGz": return m.getIsGz();
            case "isFollow": return m.getIsFollow();
            case "locationId": return m.getLocationId() != null ? String.valueOf(m.getLocationId()) : null;
            case "hisId": return m.getHisId();
            case "selectionReason": return m.getSelectionReason();
            default: return null;
        }
    }

    /**
     * 批量删除耗材产品
     *
     * @param id 需要删除的耗材产品主键
     * @return 结果
     */
    @Override
    public int deleteFdMaterialByIds(Long id)
    {
        checkMaterialIsWarehouse(id);
        FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(id);
        if(fdMaterial == null){
            throw new ServiceException(String.format("耗材：%s，不存在!", id));
        }
        fdMaterial.setUpdateTime(DateUtils.getNowDate());
        fdMaterial.setDelFlag(1);//1:已删除
        fdMaterial.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdMaterialMapper.updateFdMaterial(fdMaterial);
    }

    /**
     * 校验耗材是否已存在出入库业务
     * @param id
     */
    private void checkMaterialIsWarehouse(Long id){

        int count = stkIoBillMapper.selectStkIobillEntryMaterialIsExist(id);
        if(count > 0){
            throw new ServiceException(String.format("已存在出入库业务的耗材不能进行删除!"));
        }
    }

    /**
     * 生成6位数字编码
     * 
     * @return 编码
     */
    private String generateMaterialCode()
    {
        // 查询所有耗材编码，找出最大的6位数字编码
        List<FdMaterial> allMaterials = fdMaterialMapper.selectFdMaterialList(new FdMaterial());
        int maxCode = 99999; // 从100000开始，所以初始值为99999
        
        for (FdMaterial material : allMaterials)
        {
            String code = material.getCode();
            if (StringUtils.isNotEmpty(code) && code.matches("^\\d{6}$"))
            {
                try
                {
                    int codeValue = Integer.parseInt(code);
                    if (codeValue >= 100000 && codeValue <= 999999 && codeValue > maxCode)
                    {
                        maxCode = codeValue;
                    }
                }
                catch (NumberFormatException e)
                {
                    // 忽略非数字编码
                }
            }
        }
        
        int nextCode = maxCode + 1;
        if (nextCode > 999999)
        {
            nextCode = 100000; // 如果超过最大值，从100000重新开始
        }
        
        return String.format("%06d", nextCode);
    }

    @Override
    public String importFdMaterial(List<FdMaterial> fdmaterialList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(fdmaterialList) || fdmaterialList.size() == 0)
        {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (FdMaterial fdmaterial : fdmaterialList)
        {
            try
            {
                // 1. 先写入导入中间表，保留原始数据
                saveToImportTable(fdmaterial, operName);

                // 2. 对供应商 / 厂家 / 库房分类 / 财务分类字段做数据清洗
                cleanReferenceFields(fdmaterial);

                // 如果编码为空或空白，自动生成编码
                if (StringUtils.isEmpty(fdmaterial.getCode()))
                {
                    fdmaterial.setCode(generateMaterialCode());
                }

                // 验证是否存在这个耗材
                FdMaterial u = fdMaterialMapper.selectFdMaterialByCode(fdmaterial.getCode());
                if (StringUtils.isNull(u))
                {
                    BeanValidators.validateWithException(validator, fdmaterial);
                    fdmaterial.setCreateBy(operName);
                    fdMaterialMapper.insertFdMaterial(fdmaterial);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、耗材名称 " + fdmaterial.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, fdmaterial);
//                    checkUserAllowed(u);
//                    checkUserDataScope(u.getUserId());
//                    user.setUserId(u.getUserId());
//                    user.setUpdateBy(operName);
//                    userMapper.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、耗材名称 " + fdmaterial.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、耗材名称 " + fdmaterial.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、耗材名称 " + fdmaterial.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 批量更新耗材产品名称简码（referredName）
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
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(id);
            if (material == null) {
                continue;
            }
            String name = material.getName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            material.setReferredName(PinyinUtils.getPinyinInitials(name));
            fdMaterialMapper.updateFdMaterial(material);
        }
    }

    /**
     * 将导入的原始数据写入中间表（fd_material_import）
     */
    private void saveToImportTable(FdMaterial fdmaterial, String operName) {
        try {
            FdMaterialImport record = new FdMaterialImport();
            record.setId(UUID7.generateUUID7());
            record.setCode(fdmaterial.getCode());
            record.setName(fdmaterial.getName());
            // 这里保存的是“导入时的原始值字符串”，既可以是ID也可以是名称
            record.setSupplierValue(fdmaterial.getSupplierId() != null ? String.valueOf(fdmaterial.getSupplierId()) : null);
            record.setFactoryValue(fdmaterial.getFactoryId() != null ? String.valueOf(fdmaterial.getFactoryId()) : null);
            record.setWarehouseCategoryValue(fdmaterial.getStoreroomId() != null ? String.valueOf(fdmaterial.getStoreroomId()) : null);
            record.setFinanceCategoryValue(fdmaterial.getFinanceCategoryId() != null ? String.valueOf(fdmaterial.getFinanceCategoryId()) : null);
            record.setUnitValue(fdmaterial.getUnitId() != null ? String.valueOf(fdmaterial.getUnitId()) : null);
            record.setLocationValue(fdmaterial.getLocationId() != null ? String.valueOf(fdmaterial.getLocationId()) : null);
            record.setRawData(JSON.toJSONString(fdmaterial));
            record.setImportTime(new Date());
            record.setOperator(operName);
            fdMaterialImportMapper.insertFdMaterialImport(record);
        } catch (Exception e) {
            // 中间表写入失败不影响主流程，只记录日志
            log.error("保存耗材导入中间表失败：{}", fdmaterial.getName(), e);
        }
    }

    /**
     * 对导入数据中的外键（供应商、厂家、库房分类、财务分类、单位、货位）做数据清洗：
     * - 如果是数字，则按ID查询；
     * - 否则按名称精确匹配，找到对应ID后回填到 FdMaterial 对象。
     */
    private void cleanReferenceFields(FdMaterial fdmaterial) {
        // 供应商
        if (fdmaterial.getSupplierId() == null && fdmaterial.getSupplier() != null
                && StringUtils.isNotEmpty(fdmaterial.getSupplier().getName())) {
            Long supplierId = resolveSupplierId(fdmaterial.getSupplier().getName());
            if (supplierId != null) {
                fdmaterial.setSupplierId(supplierId);
            }
        }

        // 厂家
        if (fdmaterial.getFactoryId() == null && fdmaterial.getFdFactory() != null
                && StringUtils.isNotEmpty(fdmaterial.getFdFactory().getFactoryName())) {
            Long factoryId = resolveFactoryId(fdmaterial.getFdFactory().getFactoryName());
            if (factoryId != null) {
                fdmaterial.setFactoryId(factoryId);
            }
        }

        // 库房分类
        if (fdmaterial.getStoreroomId() == null && fdmaterial.getFdWarehouseCategory() != null
                && StringUtils.isNotEmpty(fdmaterial.getFdWarehouseCategory().getWarehouseCategoryName())) {
            Long wid = resolveWarehouseCategoryId(fdmaterial.getFdWarehouseCategory().getWarehouseCategoryName());
            if (wid != null) {
                fdmaterial.setStoreroomId(wid);
            }
        }

        // 财务分类
        if (fdmaterial.getFinanceCategoryId() == null && fdmaterial.getFdFinanceCategory() != null
                && StringUtils.isNotEmpty(fdmaterial.getFdFinanceCategory().getFinanceCategoryName())) {
            Long fid = resolveFinanceCategoryId(fdmaterial.getFdFinanceCategory().getFinanceCategoryName());
            if (fid != null) {
                fdmaterial.setFinanceCategoryId(fid);
            }
        }

        // 单位
        if (fdmaterial.getUnitId() == null && fdmaterial.getFdUnit() != null
                && StringUtils.isNotEmpty(fdmaterial.getFdUnit().getUnitName())) {
            Long uid = resolveUnitId(fdmaterial.getFdUnit().getUnitName());
            if (uid != null) {
                fdmaterial.setUnitId(uid);
            }
        }

        // 货位
        if (fdmaterial.getLocationId() == null && fdmaterial.getFdLocation() != null
                && StringUtils.isNotEmpty(fdmaterial.getFdLocation().getLocationName())) {
            Long lid = resolveLocationId(fdmaterial.getFdLocation().getLocationName());
            if (lid != null) {
                fdmaterial.setLocationId(lid);
            }
        }
    }

    private Long resolveSupplierId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        // 数字优先按ID解析
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdSupplier sup = fdSupplierMapper.selectFdSupplierById(id);
                if (sup != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        // 按名称精确匹配
        FdSupplier query = new FdSupplier();
        query.setName(value);
        List<FdSupplier> list = fdSupplierMapper.selectFdSupplierList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getId();
        }
        return null;
    }

    private Long resolveFactoryId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdFactory fac = fdFactoryMapper.selectFdFactoryByFactoryId(id);
                if (fac != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        FdFactory query = new FdFactory();
        query.setFactoryName(value);
        List<FdFactory> list = fdFactoryMapper.selectFdFactoryList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getFactoryId();
        }
        return null;
    }

    private Long resolveWarehouseCategoryId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdWarehouseCategory wc = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(id);
                if (wc != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        FdWarehouseCategory query = new FdWarehouseCategory();
        query.setWarehouseCategoryName(value);
        List<FdWarehouseCategory> list = fdWarehouseCategoryMapper.selectFdWarehouseCategoryList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getWarehouseCategoryId();
        }
        return null;
    }

    private Long resolveFinanceCategoryId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdFinanceCategory fc = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(id);
                if (fc != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        FdFinanceCategory query = new FdFinanceCategory();
        query.setFinanceCategoryName(value);
        List<FdFinanceCategory> list = fdFinanceCategoryMapper.selectFdFinanceCategoryList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getFinanceCategoryId();
        }
        return null;
    }

    private Long resolveUnitId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdUnit u = fdUnitMapper.selectFdUnitByUnitId(id);
                if (u != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        FdUnit query = new FdUnit();
        query.setUnitName(value);
        List<FdUnit> list = fdUnitMapper.selectFdUnitList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getUnitId();
        }
        return null;
    }

    private Long resolveLocationId(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (value.matches("^\\d+$")) {
            try {
                Long id = Long.parseLong(value);
                FdLocation l = fdLocationMapper.selectFdLocationByLocationId(id);
                if (l != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        FdLocation query = new FdLocation();
        query.setLocationName(value);
        List<FdLocation> list = fdLocationMapper.selectFdLocationList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getLocationId();
        }
        return null;
    }

//    /**
//     * 批量删除耗材产品
//     *
//     * @param ids 需要删除的耗材产品主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdMaterialByIds(Long[] ids)
//    {
//        return fdMaterialMapper.deleteFdMaterialByIds(ids);
//    }

//    /**
//     * 删除耗材产品信息
//     *
//     * @param id 耗材产品主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdMaterialById(Long id)
//    {
//        return fdMaterialMapper.deleteFdMaterialById(id);
//    }
}
