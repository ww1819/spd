package com.spd.equipment.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.common.utils.uuid.UUID7;
import com.spd.common.utils.StringUtils;
import com.spd.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.equipment.mapper.EquipmentInfoMapper;
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备信息管理Service业务层处理
 * 
 * @author spd
 * @date 2024-01-01
 */
@Service
public class EquipmentInfoServiceImpl implements IEquipmentInfoService 
{
    private static final Logger log = LoggerFactory.getLogger(EquipmentInfoServiceImpl.class);
    
    @Autowired
    private EquipmentInfoMapper equipmentInfoMapper;

    /**
     * 查询设备信息管理
     * 
     * @param id 设备信息管理主键
     * @return 设备信息管理
     */
    @Override
    public EquipmentInfo selectEquipmentInfoById(String id)
    {
        EquipmentInfo equipmentInfo = equipmentInfoMapper.selectEquipmentInfoById(id);
        if (equipmentInfo != null && equipmentInfo.getAttachedMaterials() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> attachedMaterialsList = objectMapper.readValue(equipmentInfo.getAttachedMaterials(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                equipmentInfo.setAttachedMaterialsList(attachedMaterialsList);
            } catch (Exception e) {
                // 处理异常，设置为空列表
                equipmentInfo.setAttachedMaterialsList(new ArrayList<>());
            }
        }
        return equipmentInfo;
    }

    /**
     * 查询设备信息管理列表
     * 
     * @param equipmentInfo 设备信息管理
     * @return 设备信息管理
     */
    @Override
    public List<EquipmentInfo> selectEquipmentInfoList(EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoMapper.selectEquipmentInfoList(equipmentInfo);
        // 处理每个对象的附属资料列表
        for (EquipmentInfo info : list) {
            if (info.getAttachedMaterials() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<String> attachedMaterialsList = objectMapper.readValue(info.getAttachedMaterials(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    info.setAttachedMaterialsList(attachedMaterialsList);
                } catch (Exception e) {
                    // 处理异常，设置为空列表
                    info.setAttachedMaterialsList(new ArrayList<>());
                }
            }
        }
        return list;
    }

    /**
     * 新增设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    @Override
    public int insertEquipmentInfo(EquipmentInfo equipmentInfo)
    {
                        // 生成UUID作为主键
                equipmentInfo.setId(UUID7.generateUUID7Simple());
                
                // 设置默认删除标志
                equipmentInfo.setDelFlag("0");
                
                // 处理附属资料列表转JSON
                if (equipmentInfo.getAttachedMaterialsList() != null && !equipmentInfo.getAttachedMaterialsList().isEmpty()) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        equipmentInfo.setAttachedMaterials(objectMapper.writeValueAsString(equipmentInfo.getAttachedMaterialsList()));
                    } catch (JsonProcessingException e) {
                        // 处理异常
                    }
                }
                
                equipmentInfo.setCreateTime(DateUtils.getNowDate());
        return equipmentInfoMapper.insertEquipmentInfo(equipmentInfo);
    }

    /**
     * 修改设备信息管理
     * 
     * @param equipmentInfo 设备信息管理
     * @return 结果
     */
    @Override
    public int updateEquipmentInfo(EquipmentInfo equipmentInfo)
    {
        // 处理附属资料列表转JSON
        if (equipmentInfo.getAttachedMaterialsList() != null && !equipmentInfo.getAttachedMaterialsList().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                equipmentInfo.setAttachedMaterials(objectMapper.writeValueAsString(equipmentInfo.getAttachedMaterialsList()));
            } catch (JsonProcessingException e) {
                // 处理异常
            }
        }
        
        equipmentInfo.setUpdateTime(DateUtils.getNowDate());
        return equipmentInfoMapper.updateEquipmentInfo(equipmentInfo);
    }

    /**
     * 批量删除设备信息管理
     * 
     * @param ids 需要删除的设备信息管理主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentInfoByIds(String[] ids)
    {
        return equipmentInfoMapper.deleteEquipmentInfoByIds(ids);
    }

    /**
     * 删除设备信息管理信息
     * 
     * @param id 设备信息管理主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentInfoById(String id)
    {
        return equipmentInfoMapper.deleteEquipmentInfoById(id);
    }

    /**
     * 根据资产编号查询设备信息
     * 
     * @param assetCode 资产编号
     * @return 设备信息管理
     */
    @Override
    public EquipmentInfo selectEquipmentInfoByAssetCode(String assetCode)
    {
        return equipmentInfoMapper.selectEquipmentInfoByAssetCode(assetCode);
    }

    /**
     * 查询设备信息统计
     * 
     * @param equipmentInfo 设备信息管理
     * @return 统计结果
     */
    @Override
    public List<EquipmentInfo> selectEquipmentInfoStatistics(EquipmentInfo equipmentInfo)
    {
        return equipmentInfoMapper.selectEquipmentInfoStatistics(equipmentInfo);
    }

    /**
     * 导入资产数据
     * 
     * @param equipmentInfoList 资产数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importEquipmentInfo(List<EquipmentInfo> equipmentInfoList, Boolean isUpdateSupport, String operName)
    {
        if (equipmentInfoList == null || equipmentInfoList.size() == 0)
        {
            throw new ServiceException("导入资产数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (EquipmentInfo equipmentInfo : equipmentInfoList)
        {
            try
            {
                // 检查equipmentInfo是否为null
                if (equipmentInfo == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、数据行为空，跳过导入");
                    continue;
                }
                
                // 检查是否为真正的空行（所有关键字段都为空）
                String assetCode = equipmentInfo.getAssetCode();
                String assetName = equipmentInfo.getAssetName();
                String specification = equipmentInfo.getSpecification();
                String model = equipmentInfo.getModel();
                
                // 如果资产编号和资产名称都为空，且没有其他关键数据，则认为是空行
                boolean isEmptyRow = StringUtils.isEmpty(assetCode) && 
                                    StringUtils.isEmpty(assetName) &&
                                    StringUtils.isEmpty(specification) &&
                                    StringUtils.isEmpty(model);
                
                if (isEmptyRow)
                {
                    failureNum++;
                    // 输出更详细的调试信息
                    log.warn("第{}行数据为空 - 资产编号:{}, 资产名称:{}, 规格:{}, 型号:{}", 
                            failureNum, assetCode, assetName, specification, model);
                    failureMsg.append("<br/>" + failureNum + "、数据行为空（资产编号、资产名称、规格、型号都为空），跳过导入");
                    continue;
                }
                
                // 记录成功解析的数据（用于调试）
                log.debug("解析第{}条数据 - 资产编号:{}, 资产名称:{}, 规格:{}, 型号:{}", 
                        (successNum + failureNum + 1), assetCode, assetName, specification, model);
                
                // 检查operName是否为null
                if (operName == null)
                {
                    operName = "system";
                }
                
                // 如果资产编号为空，自动生成8位数字编码
                if (StringUtils.isEmpty(assetCode))
                {
                    assetCode = generateAssetCode();
                    if (StringUtils.isEmpty(assetCode))
                    {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、资产编号生成失败，跳过导入");
                        continue;
                    }
                    equipmentInfo.setAssetCode(assetCode);
                }
                
                // 设置必填字段的默认值
                if (StringUtils.isEmpty(equipmentInfo.getDelFlag()))
                {
                    equipmentInfo.setDelFlag("0");
                }
                if (equipmentInfo.getCreateTime() == null)
                {
                    equipmentInfo.setCreateTime(DateUtils.getNowDate());
                }
                if (StringUtils.isEmpty(equipmentInfo.getCreateBy()))
                {
                    equipmentInfo.setCreateBy(operName);
                }
                
                // 验证是否存在这个资产编号
                EquipmentInfo existInfo = equipmentInfoMapper.selectEquipmentInfoByAssetCode(assetCode);
                if (existInfo == null)
                {
                    // 不存在，新增
                    String id = UUID7.generateUUID7Simple();
                    if (StringUtils.isEmpty(id))
                    {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、资产编号 " + assetCode + " ID生成失败");
                        continue;
                    }
                    equipmentInfo.setId(id);
                    equipmentInfoMapper.insertEquipmentInfo(equipmentInfo);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、资产编号 " + assetCode + " 导入成功");
                }
                else if (isUpdateSupport != null && isUpdateSupport)
                {
                    // 存在且支持更新
                    String existId = existInfo.getId();
                    if (StringUtils.isEmpty(existId))
                    {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、资产编号 " + assetCode + " 已存在但ID为空，无法更新");
                        continue;
                    }
                    equipmentInfo.setId(existId);
                    equipmentInfo.setUpdateTime(DateUtils.getNowDate());
                    equipmentInfo.setUpdateBy(operName);
                    equipmentInfoMapper.updateEquipmentInfo(equipmentInfo);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、资产编号 " + assetCode + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、资产编号 " + assetCode + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String assetCode = "未知";
                if (equipmentInfo != null)
                {
                    assetCode = equipmentInfo.getAssetCode() != null ? equipmentInfo.getAssetCode() : "未生成";
                }
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty())
                {
                    errorMsg = e.getClass().getSimpleName();
                    if (e.getCause() != null)
                    {
                        String causeMsg = e.getCause().getMessage();
                        if (causeMsg != null && !causeMsg.isEmpty())
                        {
                            errorMsg += ": " + causeMsg;
                        }
                    }
                }
                String msg = "<br/>" + failureNum + "、资产编号 " + assetCode + " 导入失败：" + errorMsg;
                failureMsg.append(msg);
                // 记录异常日志
                log.error("导入资产失败，资产编号：{}，错误信息：{}", assetCode, errorMsg, e);
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
     * 生成8位数字资产编码
     * 查询所有资产编码，找出最大的8位数字编码，然后+1，并确保唯一性
     * 
     * @return 8位数字编码，不会返回null
     */
    private String generateAssetCode()
    {
        try
        {
            // 直接使用Mapper查询所有资产信息（不使用Service层，避免分页问题）
            List<EquipmentInfo> allEquipmentList = null;
            try
            {
                if (equipmentInfoMapper != null)
                {
                    allEquipmentList = equipmentInfoMapper.selectEquipmentInfoList(new EquipmentInfo());
                }
            }
            catch (Exception e)
            {
                log.warn("查询资产列表失败，使用默认编码", e);
            }
            
            // 提取所有8位数字编码
            int maxCode = 10000000 - 1; // 从10000000开始
            if (allEquipmentList != null && !allEquipmentList.isEmpty())
            {
                for (EquipmentInfo info : allEquipmentList)
                {
                    if (info != null)
                    {
                        String code = info.getAssetCode();
                        if (code != null && code.matches("^\\d{8}$"))
                        {
                            try
                            {
                                int codeInt = Integer.parseInt(code);
                                if (codeInt >= 10000000 && codeInt <= 99999999 && codeInt > maxCode)
                                {
                                    maxCode = codeInt;
                                }
                            }
                            catch (NumberFormatException e)
                            {
                                // 忽略非数字编码
                            }
                        }
                    }
                }
            }
            
            // 生成下一个编码，并确保唯一性
            int nextCode = maxCode + 1;
            int maxAttempts = 100; // 最多尝试100次
            int attempts = 0;
            
            while (attempts < maxAttempts)
            {
                if (nextCode > 99999999)
                {
                    // 如果超过最大值，从10000000重新开始
                    nextCode = 10000000;
                }
                
                String codeStr = String.format("%08d", nextCode);
                
                // 检查编码是否已存在
                try
                {
                    if (equipmentInfoMapper != null)
                    {
                        EquipmentInfo existInfo = equipmentInfoMapper.selectEquipmentInfoByAssetCode(codeStr);
                        if (existInfo == null)
                        {
                            // 编码不存在，可以使用
                            return codeStr;
                        }
                    }
                    else
                    {
                        // Mapper为null，直接返回生成的编码
                        return codeStr;
                    }
                }
                catch (Exception e)
                {
                    // 如果查询出错，直接返回生成的编码
                    log.warn("检查编码唯一性失败，使用生成的编码：{}", codeStr, e);
                    return codeStr;
                }
                
                // 编码已存在，尝试下一个
                nextCode++;
                attempts++;
            }
            
            // 如果100次尝试都失败，返回默认编码（这种情况应该很少发生）
            return String.format("%08d", nextCode);
        }
        catch (Exception e)
        {
            // 如果查询失败，返回默认编码
            log.error("生成资产编码失败，使用默认编码", e);
            return "10000000";
        }
    }
} 