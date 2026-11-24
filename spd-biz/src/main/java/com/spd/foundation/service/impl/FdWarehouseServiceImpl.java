package com.spd.foundation.service.impl;




import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.mapper.FdWarehouseChangeLogMapper;
import com.spd.foundation.domain.FdWarehouseChangeLog;
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
    private FdWarehouseChangeLogMapper fdWarehouseChangeLogMapper;

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
        FdWarehouse before = existing;
        int n = fdWarehouseMapper.updateFdWarehouse(fdWarehouse);
        FdWarehouse after = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(fdWarehouse.getId()));
        recordWarehouseFieldChanges(before, after, SecurityUtils.getUserIdStr());
        return n;
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
            throw new ServiceException("存在业务无法删除");
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


    public java.util.List<FdWarehouseChangeLog> selectWarehouseChangeLog(Long warehouseId)
    {
        if (warehouseId == null)
        {
            return java.util.Collections.emptyList();
        }
        return fdWarehouseChangeLogMapper.selectByEntityId(warehouseId);
    }

    private void recordWarehouseFieldChanges(FdWarehouse before, FdWarehouse after, String operator)
    {
        if (before == null || after == null || after.getId() == null)
        {
            return;
        }
        java.util.Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        pushChange(after.getId(), op, now, "code", "仓库编码", nzObj(before.getCode()), nzObj(after.getCode()));
        pushChange(after.getId(), op, now, "name", "仓库名称", nzObj(before.getName()), nzObj(after.getName()));
        pushChange(after.getId(), op, now, "warehouse_person", "负责人", nzObj(before.getWarehousePerson()), nzObj(after.getWarehousePerson()));
        pushChange(after.getId(), op, now, "warehouse_phone", "电话", nzObj(before.getWarehousePhone()), nzObj(after.getWarehousePhone()));
        pushChange(after.getId(), op, now, "warehouse_status", "状态", nzObj(before.getWarehouseStatus()), nzObj(after.getWarehouseStatus()));
        pushChange(after.getId(), op, now, "warehouse_type", "仓库类型", nzObj(before.getWarehouseType()), nzObj(after.getWarehouseType()));
        pushChange(after.getId(), op, now, "settlement_type", "结算类型", nzObj(before.getSettlementType()), nzObj(after.getSettlementType()));
        pushChange(after.getId(), op, now, "his_id", "HIS仓库ID", nzObj(before.getHisId()), nzObj(after.getHisId()));
        pushChange(after.getId(), op, now, "is_settlement_warehouse", "结算仓库", nzObj(before.getIsSettlementWarehouse()), nzObj(after.getIsSettlementWarehouse()));
        pushChange(after.getId(), op, now, "lv_audit_gen_inhospital_in", "入库审核生成院内码", nzObj(before.getLvAuditGenInhospitalIn()), nzObj(after.getLvAuditGenInhospitalIn()));
        pushChange(after.getId(), op, now, "lv_audit_gen_inhospital_out", "出库审核生成院内码", nzObj(before.getLvAuditGenInhospitalOut()), nzObj(after.getLvAuditGenInhospitalOut()));
        pushChange(after.getId(), op, now, "require_produce_date", "必填生产日期", nzObj(before.getRequireProduceDate()), nzObj(after.getRequireProduceDate()));
        pushChange(after.getId(), op, now, "require_expiry_date", "必填有效期", nzObj(before.getRequireExpiryDate()), nzObj(after.getRequireExpiryDate()));
        pushChange(after.getId(), op, now, "require_batch_no", "必填批号", nzObj(before.getRequireBatchNo()), nzObj(after.getRequireBatchNo()));
        pushChange(after.getId(), op, now, "inbound_expiry_warn_days", "效期临近天数", nzObj(before.getInboundExpiryWarnDays()), nzObj(after.getInboundExpiryWarnDays()));
        pushChange(after.getId(), op, now, "remark", "备注", nzObj(before.getRemark()), nzObj(after.getRemark()));
    }

    private void pushChange(Long entityId, String operator, java.util.Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue)
    {
        if (java.util.Objects.equals(oldValue, newValue))
        {
            return;
        }
        FdWarehouseChangeLog rec = new FdWarehouseChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setWarehouseId(entityId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdWarehouseChangeLogMapper.insert(rec);
    }

    private static String nzObj(Object v)
    {
        if (v == null) { return ""; }
        if (v instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) v).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(v);
    }

}
