package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.warehouse.domain.HcDocBillRef;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.HcDocBillRefMapper;
import com.spd.warehouse.service.IHcDocBillRefService;

@Service
public class HcDocBillRefServiceImpl implements IHcDocBillRefService {

    @Autowired
    private HcDocBillRefMapper hcDocBillRefMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRefsAfterStkBillInsert(StkIoBill requestBill, StkIoBill reloadedWithEntries) {
        if (requestBill == null || reloadedWithEntries == null) {
            return;
        }
        List<HcDocBillRef> refs = requestBill.getDocRefList();
        if (refs == null || refs.isEmpty()) {
            return;
        }
        List<StkIoBillEntry> entries = reloadedWithEntries.getStkIoBillEntryList();
        if (entries == null || entries.isEmpty()) {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(reloadedWithEntries.getTenantId())
            ? reloadedWithEntries.getTenantId() : SecurityUtils.getCustomerId();
        String tgtBillId = reloadedWithEntries.getId() != null ? String.valueOf(reloadedWithEntries.getId()) : null;
        String tgtBillNo = reloadedWithEntries.getBillNo();
        Integer tgtKind = reloadedWithEntries.getBillType();
        String tgtBillKind = tgtKind != null ? String.valueOf(tgtKind) : null;

        String operator = SecurityUtils.getUserIdStr();
        hcDocBillRefMapper.softDeleteByTgtBillId(tenantId, tgtBillId, operator);

        String createBy = operator;
        int n = Math.min(refs.size(), entries.size());
        for (int i = 0; i < n; i++) {
            HcDocBillRef r = refs.get(i);
            if (r == null || StringUtils.isEmpty(r.getRefType())) {
                continue;
            }
            StkIoBillEntry en = entries.get(i);
            if (en == null || en.getId() == null) {
                continue;
            }
            HcDocBillRef row = new HcDocBillRef();
            row.setId(UUID7.generateUUID7());
            row.setTenantId(tenantId);
            row.setBizDomain(StringUtils.isNotEmpty(r.getBizDomain()) ? r.getBizDomain() : "STK_IO_BILL");
            row.setRefType(r.getRefType());
            row.setSrcBillKind(r.getSrcBillKind());
            row.setSrcBillId(r.getSrcBillId());
            row.setSrcBillNo(r.getSrcBillNo());
            row.setSrcEntryId(r.getSrcEntryId());
            row.setSrcEntryLineNo(r.getSrcEntryLineNo());
            row.setTgtBillKind(tgtBillKind);
            row.setTgtBillId(tgtBillId);
            row.setTgtBillNo(tgtBillNo);
            row.setTgtEntryId(String.valueOf(en.getId()));
            row.setLineNo(r.getLineNo() != null ? r.getLineNo() : Integer.valueOf(i + 1));
            BigDecimal refQty = r.getRefQty() != null ? r.getRefQty() : en.getQty();
            row.setRefQty(refQty);
            BigDecimal refAmt = r.getRefAmt() != null ? r.getRefAmt() : en.getAmt();
            row.setRefAmt(refAmt);
            row.setLockWarehouseId(r.getLockWarehouseId());
            row.setLockSupplierId(r.getLockSupplierId());
            row.setLockDepartmentId(r.getLockDepartmentId());
            row.setRemark(r.getRemark());
            row.setDelFlag(0);
            row.setCreateBy(createBy);
            hcDocBillRefMapper.insertHcDocBillRef(row);
        }
    }
}
