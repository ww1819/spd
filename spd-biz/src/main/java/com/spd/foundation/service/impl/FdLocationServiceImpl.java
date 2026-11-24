package com.spd.foundation.service.impl;




import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.mapper.FdLocationChangeLogMapper;
import com.spd.foundation.domain.FdLocationChangeLog;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdLocationMapper;
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.service.IFdLocationService;

/**
 * 货位Service业务层处理
 *
 * @author spd
 * @date 2024-12-13
 */
@Service
public class FdLocationServiceImpl implements IFdLocationService
{
    @Autowired
    private FdLocationMapper fdLocationMapper;

    @Autowired
    private FdLocationChangeLogMapper fdLocationChangeLogMapper;

    @Override
    public FdLocation selectFdLocationByLocationId(Long locationId)
    {
        FdLocation loc = fdLocationMapper.selectFdLocationByLocationId(locationId);
        if (loc != null)
        {
            SecurityUtils.ensureTenantAccess(loc.getTenantId());
        }
        return loc;
    }

    @Override
    public List<FdLocation> selectFdLocationList(FdLocation fdLocation)
    {
        if (fdLocation != null && StringUtils.isEmpty(fdLocation.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdLocation.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdLocationMapper.selectFdLocationList(fdLocation);
    }

    @Override
    public List<FdLocation> selectFdLocationTree()
    {
        FdLocation q = new FdLocation();
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdLocationMapper.selectFdLocationTree(q);
    }

    @Override
    public int insertFdLocation(FdLocation fdLocation)
    {
        if (StringUtils.isEmpty(fdLocation.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdLocation.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(fdLocation.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdLocation.setCreateBy(SecurityUtils.getUserIdStr());
            fdLocation.setCreateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        fdLocation.setCreateTime(DateUtils.getNowDate());
        if (fdLocation.getParentId() == null)
        {
            fdLocation.setParentId(0L);
        }
        if (fdLocation.getDelFlag() == null)
        {
            fdLocation.setDelFlag(0);
        }
        return fdLocationMapper.insertFdLocation(fdLocation);
    }

    @Override
    public int updateFdLocation(FdLocation fdLocation)
    {
        if (fdLocation.getLocationId() == null)
        {
            throw new ServiceException("货位主键不能为空");
        }
        FdLocation before = fdLocationMapper.selectFdLocationByLocationId(fdLocation.getLocationId());
        if (before == null)
        {
            throw new ServiceException("货位不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdLocation.setTenantId(before.getTenantId());
        fdLocation.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdLocation.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdLocation.setUpdateBy(SecurityUtils.getUserIdStr());
            fdLocation.setUpdateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        }
        FdLocation beforeSnap = fdLocationMapper.selectFdLocationByLocationId(fdLocation.getLocationId());
        int n = fdLocationMapper.updateFdLocation(fdLocation);
        FdLocation afterSnap = fdLocationMapper.selectFdLocationByLocationId(fdLocation.getLocationId());
        recordLocationFieldChanges(beforeSnap, afterSnap, SecurityUtils.getUserIdStr());
        return n;
    }

    @Override
    public int deleteFdLocationByLocationIds(Long locationIds)
    {
        FdLocation fdLocation = fdLocationMapper.selectFdLocationByLocationId(locationIds);
        if (fdLocation == null)
        {
            throw new ServiceException(String.format("货位：%s，不存在!", locationIds));
        }
        SecurityUtils.ensureTenantAccess(fdLocation.getTenantId());
        fdLocation.setDelFlag(1);
        fdLocation.setDeleteBy(SecurityUtils.getUserIdStr());
            fdLocation.setDeleteByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        fdLocation.setDeleteTime(DateUtils.getNowDate());
        fdLocation.setUpdateTime(DateUtils.getNowDate());
        fdLocation.setUpdateBy(SecurityUtils.getUserIdStr());
            fdLocation.setUpdateByName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        return fdLocationMapper.updateFdLocation(fdLocation);
    }


    public java.util.List<FdLocationChangeLog> selectLocationChangeLog(Long locationId)
    {
        if (locationId == null) { return java.util.Collections.emptyList(); }
        return fdLocationChangeLogMapper.selectByEntityId(locationId);
    }

    private void recordLocationFieldChanges(FdLocation before, FdLocation after, String operator)
    {
        if (before == null || after == null || after.getLocationId() == null) { return; }
        java.util.Date now = DateUtils.getNowDate();
        String op = StringUtils.isNotEmpty(operator) ? operator : "";
        pushChange(after.getLocationId(), op, now, "parent_id", "父货位ID", nzObj(before.getParentId()), nzObj(after.getParentId()));
        pushChange(after.getLocationId(), op, now, "location_code", "货位编码", nzObj(before.getLocationCode()), nzObj(after.getLocationCode()));
        pushChange(after.getLocationId(), op, now, "location_name", "货位名称", nzObj(before.getLocationName()), nzObj(after.getLocationName()));
        pushChange(after.getLocationId(), op, now, "warehouse_id", "仓库ID", nzObj(before.getWarehouseId()), nzObj(after.getWarehouseId()));
        pushChange(after.getLocationId(), op, now, "zone_type", "五区类型", nzObj(before.getZoneType()), nzObj(after.getZoneType()));
        pushChange(after.getLocationId(), op, now, "shelf_code", "货架编码", nzObj(before.getShelfCode()), nzObj(after.getShelfCode()));
        pushChange(after.getLocationId(), op, now, "layer_no", "层号", nzObj(before.getLayerNo()), nzObj(after.getLayerNo()));
        pushChange(after.getLocationId(), op, now, "slot_no", "格口号", nzObj(before.getSlotNo()), nzObj(after.getSlotNo()));
        pushChange(after.getLocationId(), op, now, "pos_x", "平面X", nzObj(before.getPosX()), nzObj(after.getPosX()));
        pushChange(after.getLocationId(), op, now, "pos_y", "平面Y", nzObj(before.getPosY()), nzObj(after.getPosY()));
        pushChange(after.getLocationId(), op, now, "pos_z", "高度Z", nzObj(before.getPosZ()), nzObj(after.getPosZ()));
        pushChange(after.getLocationId(), op, now, "capacity", "容量", nzObj(before.getCapacity()), nzObj(after.getCapacity()));
        pushChange(after.getLocationId(), op, now, "remark", "备注", nzObj(before.getRemark()), nzObj(after.getRemark()));
    }

    private void pushChange(Long entityId, String operator, java.util.Date changeTime, String fieldName, String fieldLabel, String oldValue, String newValue)
    {
        if (java.util.Objects.equals(oldValue, newValue)) { return; }
        FdLocationChangeLog rec = new FdLocationChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setLocationId(entityId);
        rec.setChangeTime(changeTime);
        rec.setOperator(operator);
        rec.setOperatorName(com.spd.common.utils.SecurityUtils.getUsernameSafe());
        rec.setFieldName(fieldName);
        rec.setFieldLabel(fieldLabel);
        rec.setOldValue(oldValue);
        rec.setNewValue(newValue);
        fdLocationChangeLogMapper.insert(rec);
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
