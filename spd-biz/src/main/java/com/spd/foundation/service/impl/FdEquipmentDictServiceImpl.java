package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.annotation.DataSource;
import com.spd.common.enums.DataSourceType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdEquipmentDictMapper;
import com.spd.foundation.domain.FdEquipmentDict;
import com.spd.foundation.service.IFdEquipmentDictService;

import javax.validation.Validator;

/**
 * 设备字典Service业务层处理
 *
 * @author spd
 * @date 2024-12-16
 */
@Service
public class FdEquipmentDictServiceImpl implements IFdEquipmentDictService
{
    @Autowired
    private FdEquipmentDictMapper fdEquipmentDictMapper;

    @Autowired
    protected Validator validator;

    private static final Logger log = LoggerFactory.getLogger(FdEquipmentDictServiceImpl.class);

    /**
     * 查询设备字典
     *
     * @param id 设备字典主键
     * @return 设备字典
     */
    @Override
    public FdEquipmentDict selectFdEquipmentDictById(Long id)
    {
        return fdEquipmentDictMapper.selectFdEquipmentDictById(id);
    }

    /**
     * 查询设备字典列表
     *
     * @param fdEquipmentDict 设备字典
     * @return 设备字典
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public List<FdEquipmentDict> selectFdEquipmentDictList(FdEquipmentDict fdEquipmentDict)
    {
        return fdEquipmentDictMapper.selectFdEquipmentDictList(fdEquipmentDict);
    }

    /**
     * 新增设备字典
     *
     * @param fdEquipmentDict 设备字典
     * @return 结果
     */
    @Override
    public int insertFdEquipmentDict(FdEquipmentDict fdEquipmentDict)
    {
        fdEquipmentDict.setCreateTime(DateUtils.getNowDate());
        return fdEquipmentDictMapper.insertFdEquipmentDict(fdEquipmentDict);
    }

    /**
     * 修改设备字典
     *
     * @param fdEquipmentDict 设备字典
     * @return 结果
     */
    @Override
    public int updateFdEquipmentDict(FdEquipmentDict fdEquipmentDict)
    {
        fdEquipmentDict.setUpdateTime(DateUtils.getNowDate());
        return fdEquipmentDictMapper.updateFdEquipmentDict(fdEquipmentDict);
    }

    /**
     * 批量删除设备字典
     *
     * @param id 需要删除的设备字典主键
     * @return 结果
     */
    @Override
    public int deleteFdEquipmentDictByIds(Long id)
    {
        FdEquipmentDict fdEquipmentDict = fdEquipmentDictMapper.selectFdEquipmentDictById(id);
        if(fdEquipmentDict == null){
            throw new ServiceException(String.format("设备：%s，不存在!", id));
        }
        fdEquipmentDict.setUpdateTime(DateUtils.getNowDate());
        fdEquipmentDict.setDelFlag(1);//1:已删除
        fdEquipmentDict.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdEquipmentDictMapper.updateFdEquipmentDict(fdEquipmentDict);
    }

    @Override
    public String importFdEquipmentDict(List<FdEquipmentDict> fdEquipmentDictList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(fdEquipmentDictList) || fdEquipmentDictList.size() == 0)
        {
            throw new ServiceException("导入设备数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (FdEquipmentDict fdEquipmentDict : fdEquipmentDictList)
        {
            try
            {
                // 验证是否存在这个设备
                FdEquipmentDict u = fdEquipmentDictMapper.selectFdEquipmentDictByCode(fdEquipmentDict.getCode());
                if (StringUtils.isNull(u))
                {
                    BeanValidators.validateWithException(validator, fdEquipmentDict);
                    fdEquipmentDict.setCreateBy(operName);
                    fdEquipmentDictMapper.insertFdEquipmentDict(fdEquipmentDict);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、设备名称 " + fdEquipmentDict.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, fdEquipmentDict);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、设备名称 " + fdEquipmentDict.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备名称 " + fdEquipmentDict.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、设备名称 " + fdEquipmentDict.getName() + " 导入失败：";
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
}

