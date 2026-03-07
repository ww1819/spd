package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdWarehouseService;

/**
 * 仓库Service业务层处理
 *
 * @author spd
 * @date 2023-11-26
 */
@Service
public class FdWarehouseServiceImpl implements IFdWarehouseService
{
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;

    /**
     * 查询仓库
     *
     * @param id 仓库主键
     * @return 仓库
     */
    @Override
    public FdWarehouse selectFdWarehouseById(String id)
    {
        return fdWarehouseMapper.selectFdWarehouseById(id);
    }

    /**
     * 查询仓库列表
     *
     * @param fdWarehouse 仓库
     * @return 仓库
     */
    @Override
    public List<FdWarehouse> selectFdWarehouseList(FdWarehouse fdWarehouse)
    {
        if (fdWarehouse != null && StringUtils.isEmpty(fdWarehouse.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdWarehouse.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdWarehouseMapper.selectFdWarehouseList(fdWarehouse);
    }

    @Override
    public List<Long> selectWarehouseListByUserId(Long userId) {
        return fdWarehouseMapper.selectWarehouseListByUserId(userId);
    }

    /**
     * 新增仓库
     *
     * @param fdWarehouse 仓库
     * @return 结果
     */
    @Override
    public int insertFdWarehouse(FdWarehouse fdWarehouse)
    {
        if (StringUtils.isEmpty(fdWarehouse.getSettlementType())) {
            throw new ServiceException("仓库创建时必须选择结算方式（入库结算/出库结算/消耗结算）");
        }
        fdWarehouse.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdWarehouse.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdWarehouse.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdWarehouseMapper.insertFdWarehouse(fdWarehouse);
    }

    /**
     * 修改仓库（结算方式创建后不可修改）
     *
     * @param fdWarehouse 仓库
     * @return 结果
     */
    @Override
    public int updateFdWarehouse(FdWarehouse fdWarehouse)
    {
        FdWarehouse existing = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(fdWarehouse.getId()));
        if (existing != null && StringUtils.isNotEmpty(existing.getSettlementType())) {
            fdWarehouse.setSettlementType(existing.getSettlementType());
        }
        fdWarehouse.setUpdateTime(DateUtils.getNowDate());
        return fdWarehouseMapper.updateFdWarehouse(fdWarehouse);
    }

    /**
     * 删除仓库信息
     *
     * @param id 仓库主键
     * @return 结果
     */
    @Override
    public int deleteFdWarehouseById(String id)
    {
        checkWarehouseIsExist(Long.valueOf(id));
        FdWarehouse fdWarehouse = fdWarehouseMapper.selectFdWarehouseById(id);
        if(fdWarehouse == null){
            throw new ServiceException(String.format("仓库：%s，不存在!", id));
        }
        fdWarehouse.setDelFlag(1);
        fdWarehouse.setUpdateTime(DateUtils.getNowDate());
        fdWarehouse.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdWarehouseMapper.updateFdWarehouse(fdWarehouse);
    }

    /**
     * 校验仓库是否已存在出入库业务
     * @param id
     */
    private void checkWarehouseIsExist(Long id){

        int count = fdWarehouseMapper.selectWarehouseIsExist(id);
        if(count > 0){
            throw new ServiceException(String.format("已存在出入库业务的仓库不能进行删除!"));
        }
    }

    @Override
    public List<FdWarehouse> selectwarehouseAll() {
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            FdWarehouse q = new FdWarehouse();
            q.setTenantId(SecurityUtils.getCustomerId());
            return fdWarehouseMapper.selectFdWarehouseList(q);
        }
        return fdWarehouseMapper.selectwarehouseAll();
    }

    @Override
    public List<FdWarehouse> selectUserWarehouseAll(Long userId) {
        String tenantId = SecurityUtils.getCustomerId();
        return fdWarehouseMapper.selectUserWarehouseAll(userId, tenantId);
    }

//    /**
//     * 批量删除仓库
//     *
//     * @param ids 需要删除的仓库主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdWarehouseByIds(String[] ids)
//    {
//        return fdWarehouseMapper.deleteFdWarehouseByIds(ids);
//    }
//
//    /**
//     * 删除仓库信息
//     *
//     * @param id 仓库主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdWarehouseById(String id)
//    {
//        return fdWarehouseMapper.deleteFdWarehouseById(id);
//    }
}
