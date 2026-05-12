package com.spd.equipment.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.EquipmentAccessory;
import com.spd.equipment.domain.EquipmentAccessoryIo;
import com.spd.equipment.domain.EquipmentAccessoryIoEntry;
import com.spd.equipment.domain.EquipmentAccessoryStock;
import com.spd.equipment.domain.dto.EquipmentAccessoryIoSubmitBody;
import com.spd.equipment.mapper.EquipmentAccessoryIoEntryMapper;
import com.spd.equipment.mapper.EquipmentAccessoryIoMapper;
import com.spd.equipment.mapper.EquipmentAccessoryMapper;
import com.spd.equipment.mapper.EquipmentAccessoryStockMapper;
import com.spd.equipment.service.IEquipmentAccessoryService;

@Service
public class EquipmentAccessoryServiceImpl implements IEquipmentAccessoryService {

    @Autowired
    private EquipmentAccessoryMapper accessoryMapper;
    @Autowired
    private EquipmentAccessoryStockMapper stockMapper;
    @Autowired
    private EquipmentAccessoryIoMapper ioMapper;
    @Autowired
    private EquipmentAccessoryIoEntryMapper ioEntryMapper;

    @Override
    public List<EquipmentAccessory> selectAccessoryList(EquipmentAccessory q) {
        if (q != null && StringUtils.isEmpty(q.getTenantId())) {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return accessoryMapper.selectList(q);
    }

    @Override
    public EquipmentAccessory selectAccessoryById(String id) {
        return accessoryMapper.selectById(id);
    }

    @Override
    public int insertAccessory(EquipmentAccessory row) {
        if (StringUtils.isEmpty(row.getTenantId())) {
            row.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (accessoryMapper.countActiveByCode(row.getAccessoryCode(), null) > 0) {
            throw new ServiceException("配件编码已存在");
        }
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        return accessoryMapper.insert(row);
    }

    @Override
    public int updateAccessory(EquipmentAccessory row) {
        if (StringUtils.isNotEmpty(row.getAccessoryCode())
                && accessoryMapper.countActiveByCode(row.getAccessoryCode(), row.getId()) > 0) {
            throw new ServiceException("配件编码已存在");
        }
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return accessoryMapper.update(row);
    }

    @Override
    public int deleteAccessoryById(String id) {
        EquipmentAccessory row = new EquipmentAccessory();
        row.setId(id);
        row.setDelFlag(1);
        row.setDeleteBy(SecurityUtils.getUserIdStr());
        row.setDeleteTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDeleteBy());
        row.setUpdateTime(row.getDeleteTime());
        return accessoryMapper.update(row);
    }

    @Override
    public List<EquipmentAccessoryStock> selectStockList(EquipmentAccessoryStock q) {
        if (q != null && StringUtils.isEmpty(q.getTenantId())) {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return stockMapper.selectList(q);
    }

    @Override
    public List<EquipmentAccessoryIo> selectIoList(EquipmentAccessoryIo q) {
        if (q != null && StringUtils.isEmpty(q.getTenantId())) {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return ioMapper.selectList(q);
    }

    @Override
    public EquipmentAccessoryIo selectIoById(String id) {
        return ioMapper.selectById(id);
    }

    @Override
    public List<EquipmentAccessoryIoEntry> selectIoEntries(String ioId) {
        return ioEntryMapper.selectByIoId(ioId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitIo(EquipmentAccessoryIoSubmitBody body) {
        if (body == null || StringUtils.isEmpty(body.getIoType())) {
            throw new ServiceException("出入库类型不能为空");
        }
        String ioType = body.getIoType().trim().toUpperCase();
        if (!"IN".equals(ioType) && !"OUT".equals(ioType)) {
            throw new ServiceException("出入库类型仅支持 IN 或 OUT");
        }
        if (body.getEntries() == null || body.getEntries().isEmpty()) {
            throw new ServiceException("明细不能为空");
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        Date now = DateUtils.getNowDate();
        String uid = SecurityUtils.getUserIdStr();

        String ioId = UUID7.generateUUID7();
        String ioNo = "ACC-" + UUID7.generateUUID7();

        EquipmentAccessoryIo header = new EquipmentAccessoryIo();
        header.setId(ioId);
        header.setTenantId(tenantId);
        header.setIoNo(ioNo);
        header.setIoType(ioType);
        header.setBizDate(body.getBizDate() != null ? body.getBizDate()
                : DateUtils.dateTime(DateUtils.YYYY_MM_DD, DateUtils.getDate()));
        header.setEquipmentId(body.getEquipmentId());
        header.setRemark(body.getRemark());
        header.setDelFlag(0);
        header.setCreateTime(now);
        header.setCreateBy(uid);
        header.setUpdateTime(now);
        header.setUpdateBy(uid);
        ioMapper.insert(header);

        int line = 1;
        for (EquipmentAccessoryIoEntry e : body.getEntries()) {
            if (e == null || StringUtils.isEmpty(e.getAccessoryId())) {
                throw new ServiceException("明细配件不能为空");
            }
            String wh = e.getWarehouseCode() != null ? e.getWarehouseCode().trim() : "";
            if (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("明细数量必须大于 0");
            }
            EquipmentAccessory acc = accessoryMapper.selectById(e.getAccessoryId());
            if (acc == null) {
                throw new ServiceException("配件不存在或已删除：" + e.getAccessoryId());
            }

            String entryId = UUID7.generateUUID7();
            e.setId(entryId);
            e.setTenantId(tenantId);
            e.setIoId(ioId);
            e.setLineNo(line++);
            e.setWarehouseCode(wh);
            e.setDelFlag(0);
            e.setCreateTime(now);
            e.setCreateBy(uid);
            e.setUpdateTime(now);
            e.setUpdateBy(uid);
            ioEntryMapper.insert(e);

            if ("IN".equals(ioType)) {
                applyInbound(tenantId, e.getAccessoryId(), wh, e.getQty(), uid, now);
            } else {
                int n = stockMapper.deductQty(e.getAccessoryId(), wh, e.getQty(), uid, now);
                if (n == 0) {
                    throw new ServiceException("库存不足：" + e.getAccessoryId() + " / " + wh);
                }
            }
        }
        return 1;
    }

    private void applyInbound(String tenantId, String accessoryId, String warehouseCode, BigDecimal qty,
            String uid, Date now) {
        EquipmentAccessoryStock row = stockMapper.selectActive(accessoryId, warehouseCode);
        if (row == null) {
            EquipmentAccessoryStock ins = new EquipmentAccessoryStock();
            ins.setId(UUID7.generateUUID7());
            ins.setTenantId(tenantId);
            ins.setAccessoryId(accessoryId);
            ins.setWarehouseCode(warehouseCode);
            ins.setQuantity(qty);
            ins.setDelFlag(0);
            ins.setCreateTime(now);
            ins.setCreateBy(uid);
            ins.setUpdateTime(now);
            ins.setUpdateBy(uid);
            stockMapper.insert(ins);
        } else {
            stockMapper.addQty(row.getId(), qty, uid, now);
        }
    }
}
