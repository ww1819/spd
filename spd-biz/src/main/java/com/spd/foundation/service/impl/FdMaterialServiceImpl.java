package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.annotation.DataSource;
import com.spd.common.enums.DataSourceType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
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
