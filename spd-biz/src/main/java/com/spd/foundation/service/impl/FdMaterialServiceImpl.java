package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.spd.common.annotation.DataSource;
import com.spd.common.enums.DataSourceType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterialImport;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.mapper.FdMaterialImportMapper;
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

    private static final Logger log = LoggerFactory.getLogger(FdMaterialServiceImpl.class);

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
     * 修改耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    @Override
    public int updateFdMaterial(FdMaterial fdMaterial)
    {
        fdMaterial.setUpdateTime(DateUtils.getNowDate());
        return fdMaterialMapper.updateFdMaterial(fdMaterial);
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
            // 简单名称简码策略：取名称首字母（英文转大写，其他字符原样），再配合原名称前几个字符
            char first = name.charAt(0);
            StringBuilder shortCode = new StringBuilder();
            if (Character.isLetter(first)) {
                shortCode.append(Character.toUpperCase(first));
            } else {
                shortCode.append(first);
            }
            if (name.length() > 1) {
                shortCode.append(name.substring(1, Math.min(4, name.length())));
            }
            material.setReferredName(shortCode.toString());
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
