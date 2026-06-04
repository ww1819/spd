package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.foundation.mapper.FoundationArchiveDeleteGuardMapper;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.system.service.ITenantFoundationAutoGrantService;

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

    @Autowired
    private FoundationArchiveDeleteGuardMapper foundationArchiveDeleteGuardMapper;

    @Autowired
    private ITenantFoundationAutoGrantService tenantFoundationAutoGrantService;

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
        if (fdWarehouse != null && StringUtils.isEmpty(fdWarehouse.getTenantId())) {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid)) {
                fdWarehouse.setTenantId(tid);
            }
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
        if (fdWarehouse.getName() != null) {
            fdWarehouse.setName(fdWarehouse.getName().trim());
        }
        fdWarehouse.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdWarehouse.getTenantId()))
        {
            String tid = SecurityUtils.requiredScopedTenantIdForSql();
            if (StringUtils.isNotEmpty(tid))
            {
                fdWarehouse.setTenantId(tid);
            }
        }
        if (StringUtils.isEmpty(fdWarehouse.getName())) {
            throw new ServiceException("仓库名称不能为空");
        }
        if (fdWarehouseMapper.countWarehouseByTenantAndName(fdWarehouse.getTenantId(), fdWarehouse.getName(), null) > 0) {
            throw new ServiceException("仓库名称「" + fdWarehouse.getName() + "」已存在，不能重复");
        }
        int n = fdWarehouseMapper.insertFdWarehouse(fdWarehouse);
        if (n > 0 && StringUtils.isNotEmpty(fdWarehouse.getTenantId()) && fdWarehouse.getId() != null) {
            tenantFoundationAutoGrantService.grantWarehouseToTenantAdmins(fdWarehouse.getTenantId(), fdWarehouse.getId());
        }
        return n;
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
        if (fdWarehouse.getName() != null) {
            fdWarehouse.setName(fdWarehouse.getName().trim());
        }
        if (StringUtils.isNotEmpty(fdWarehouse.getName())) {
            String tenantId = StringUtils.isNotEmpty(fdWarehouse.getTenantId()) ? fdWarehouse.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
            if (fdWarehouseMapper.countWarehouseByTenantAndName(tenantId, fdWarehouse.getName(), fdWarehouse.getId()) > 0) {
                throw new ServiceException("仓库名称「" + fdWarehouse.getName() + "」已存在，不能重复");
            }
        }
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
        Long warehouseId = Long.valueOf(id);
        checkWarehouseBusinessUsage(warehouseId);
        FdWarehouse fdWarehouse = fdWarehouseMapper.selectFdWarehouseById(id);
        if(fdWarehouse == null){
            throw new ServiceException(String.format("仓库：%s，不存在!", id));
        }
        return fdWarehouseMapper.deleteFdWarehouseById(id, SecurityUtils.getUserIdStr());
    }

    private void checkWarehouseBusinessUsage(Long warehouseId)
    {
        if (foundationArchiveDeleteGuardMapper.countWarehouseBusinessUsage(warehouseId) > 0)
        {
            throw new ServiceException("该仓库已发生业务数据（申领、申购、出入库/退库、批量消耗、高值单据、库存等），不允许删除");
        }
    }

    @Override
    public List<FdWarehouse> selectwarehouseAll() {
        String tid = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(tid)) {
            FdWarehouse q = new FdWarehouse();
            q.setTenantId(tid);
            return fdWarehouseMapper.selectFdWarehouseList(q);
        }
        return fdWarehouseMapper.selectwarehouseAll();
    }

    @Override
    public List<FdWarehouse> selectUserWarehouseAll(Long userId) {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
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
