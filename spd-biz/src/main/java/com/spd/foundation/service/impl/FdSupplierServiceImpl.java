package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.domain.FdSupplier;
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

    /**
     * 查询供应商
     *
     * @param id 供应商主键
     * @return 供应商
     */
    @Override
    public FdSupplier selectFdSupplierById(Long id)
    {
        return fdSupplierMapper.selectFdSupplierById(id);
    }

    /**
     * 查询供应商列表
     *
     * @param fdSupplier 供应商
     * @return 供应商
     */
    @Override
    public List<FdSupplier> selectFdSupplierList(FdSupplier fdSupplier)
    {
        return fdSupplierMapper.selectFdSupplierList(fdSupplier);
    }

    /**
     * 新增供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    @Override
    public int insertFdSupplier(FdSupplier fdSupplier)
    {
        fdSupplier.setCreateTime(DateUtils.getNowDate());
        return fdSupplierMapper.insertFdSupplier(fdSupplier);
    }

    /**
     * 修改供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    @Override
    public int updateFdSupplier(FdSupplier fdSupplier)
    {
        fdSupplier.setUpdateTime(DateUtils.getNowDate());
        return fdSupplierMapper.updateFdSupplier(fdSupplier);
    }

//    /**
//     * 批量删除供应商
//     *
//     * @param ids 需要删除的供应商主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdSupplierByIds(Long[] ids)
//    {
//        return fdSupplierMapper.deleteFdSupplierByIds(ids);
//    }

    /**
     * 删除供应商信息
     *
     * @param id 供应商主键
     * @return 结果
     */
    @Override
    public int deleteFdSupplierById(Long id)
    {
        checkSupplierIsExist(id);
        FdSupplier fdSupplier = fdSupplierMapper.selectFdSupplierById(id);
        if(fdSupplier == null){
            throw new ServiceException(String.format("供应商：%s，不存在!", id));
        }
        fdSupplier.setDelFlag(1);
        fdSupplier.setUpdateTime(DateUtils.getNowDate());
        fdSupplier.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdSupplierMapper.updateFdSupplier(fdSupplier);
    }


    /**
     * 校验供应商是否已存在出入库业务
     * @param id
     */
    private void checkSupplierIsExist(Long id){

        int count = fdSupplierMapper.selectSupplierIsExist(id);
        if(count > 0){
            throw new ServiceException(String.format("已存在出入库业务的供应商不能进行删除!"));
        }
    }

    /**
     * 批量更新供应商名称简码（referredCode）
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
            FdSupplier supplier = fdSupplierMapper.selectFdSupplierById(id);
            if (supplier == null) {
                continue;
            }
            String name = supplier.getName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            // 简单规则：公司名称首字符（英文转大写，其他原样）+ 后面1~3个字符
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
            supplier.setReferredCode(shortCode.toString());
            fdSupplierMapper.updateFdSupplier(supplier);
        }
    }
}
