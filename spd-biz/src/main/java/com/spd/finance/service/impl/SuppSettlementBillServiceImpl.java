package com.spd.finance.service.impl;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.finance.domain.SuppSettlementBill;
import com.spd.finance.domain.SuppSettlementInvoice;
import com.spd.finance.mapper.SuppSettlementBillMapper;
import com.spd.finance.mapper.SuppSettlementInvoiceMapper;
import com.spd.finance.service.ISuppSettlementBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供应商结算单Service实现
 */
@Service
public class SuppSettlementBillServiceImpl implements ISuppSettlementBillService {

    @Autowired
    private SuppSettlementBillMapper suppSettlementBillMapper;
    @Autowired
    private SuppSettlementInvoiceMapper suppSettlementInvoiceMapper;

    @Override
    public List<SuppSettlementBill> list(SuppSettlementBill query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return suppSettlementBillMapper.selectList(query);
    }

    @Override
    public SuppSettlementBill getById(String id) {
        SuppSettlementBill bill = suppSettlementBillMapper.selectById(id);
        if (bill == null) return null;
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            return null;
        }
        bill.setEntryList(suppSettlementBillMapper.selectEntriesByParenId(id));
        return bill;
    }

    @Override
    public List<SuppSettlementInvoice> listInvoices(String suppSettlementId) {
        if (StringUtils.isEmpty(suppSettlementId)) return java.util.Collections.emptyList();
        return suppSettlementInvoiceMapper.selectBySuppSettlementId(suppSettlementId);
    }

    @Override
    public int addInvoice(String suppSettlementId, String invoiceId) {
        if (StringUtils.isEmpty(suppSettlementId) || StringUtils.isEmpty(invoiceId)) return 0;
        SuppSettlementBill bill = suppSettlementBillMapper.selectById(suppSettlementId);
        if (bill == null) throw new ServiceException("供应商结算单不存在");
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (bill.getAuditStatus() != null && bill.getAuditStatus() == 1) {
            throw new ServiceException("结算单已审核，不得新增或删除发票关联");
        }
        SuppSettlementInvoice link = new SuppSettlementInvoice();
        link.setId(UUID7.generateUUID7());
        link.setTenantId(bill.getTenantId());
        link.setSuppSettlementId(suppSettlementId);
        link.setInvoiceId(invoiceId);
        return suppSettlementInvoiceMapper.insert(link);
    }

    @Override
    public int removeInvoice(String suppSettlementId, String invoiceId) {
        if (StringUtils.isEmpty(suppSettlementId) || StringUtils.isEmpty(invoiceId)) return 0;
        SuppSettlementBill bill = suppSettlementBillMapper.selectById(suppSettlementId);
        if (bill == null) throw new ServiceException("供应商结算单不存在");
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (bill.getAuditStatus() != null && bill.getAuditStatus() == 1) {
            throw new ServiceException("结算单已审核，不得新增或删除发票关联");
        }
        return suppSettlementInvoiceMapper.logicalDeleteBySuppSettlementAndInvoice(suppSettlementId, invoiceId, SecurityUtils.getUserIdStr());
    }
}
