package com.spd.finance.service.impl;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.finance.domain.FinInvoice;
import com.spd.finance.mapper.FinInvoiceMapper;
import com.spd.finance.service.IFinInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票管理Service实现
 *
 * @author spd
 */
@Service
public class FinInvoiceServiceImpl implements IFinInvoiceService {

    @Autowired
    private FinInvoiceMapper finInvoiceMapper;

    @Override
    public List<FinInvoice> list(FinInvoice query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return finInvoiceMapper.selectList(query);
    }

    @Override
    public FinInvoice getById(String id) {
        FinInvoice row = finInvoiceMapper.selectById(id);
        if (row == null) return null;
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(row.getTenantId()) && !customerId.equals(row.getTenantId())) {
            return null;
        }
        return row;
    }

    @Override
    public int add(FinInvoice row) {
        if (row == null) return 0;
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        if (StringUtils.isEmpty(row.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            row.setTenantId(SecurityUtils.getCustomerId());
        }
        if (row.getAuditStatus() == null) {
            row.setAuditStatus(0);
        }
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUsername());
        }
        return finInvoiceMapper.insert(row);
    }

    @Override
    public int update(FinInvoice row) {
        if (row == null || StringUtils.isEmpty(row.getId())) return 0;
        FinInvoice existing = finInvoiceMapper.selectById(row.getId());
        if (existing == null) {
            throw new ServiceException("发票不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(existing.getTenantId()) && !customerId.equals(existing.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (existing.getAuditStatus() != null && existing.getAuditStatus() == 1) {
            throw new ServiceException("已审核的发票不可变更发票信息");
        }
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(SecurityUtils.getUsername());
        }
        return finInvoiceMapper.update(row);
    }

    @Override
    public int remove(String id) {
        if (StringUtils.isEmpty(id)) return 0;
        FinInvoice inv = finInvoiceMapper.selectById(id);
        if (inv == null) return 0;
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()) && StringUtils.isNotEmpty(inv.getTenantId()) && !SecurityUtils.getCustomerId().equals(inv.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        return finInvoiceMapper.deleteById(id, SecurityUtils.getUsername());
    }

    @Override
    public int audit(String id) {
        if (StringUtils.isEmpty(id)) return 0;
        FinInvoice invoice = finInvoiceMapper.selectById(id);
        if (invoice == null) {
            throw new ServiceException("发票不存在");
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()) && StringUtils.isNotEmpty(invoice.getTenantId()) && !SecurityUtils.getCustomerId().equals(invoice.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (invoice.getAuditStatus() != null && invoice.getAuditStatus() == 1) {
            throw new ServiceException("该发票已审核");
        }
        return finInvoiceMapper.updateAuditStatus(id, SecurityUtils.getUsername());
    }
}
