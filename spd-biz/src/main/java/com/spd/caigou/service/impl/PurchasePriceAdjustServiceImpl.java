package com.spd.caigou.service.impl;

import com.spd.caigou.domain.PurchasePriceAdjust;
import com.spd.caigou.domain.PurchasePriceAdjustEntry;
import com.spd.caigou.mapper.PurchasePriceAdjustMapper;
import com.spd.caigou.service.IPurchasePriceAdjustService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 采购调价单 Service 实现
 * 单号：TJ- + yyyyMMdd + 5位流水（对齐入库 RK 规则，前缀带横杠）
 */
@Service
public class PurchasePriceAdjustServiceImpl implements IPurchasePriceAdjustService {

    private static final String BILL_PREFIX = "TJ-";
    private static final String ADJUST_TYPE_ARCHIVE = "archive";
    private static final int BILL_NO_RETRY = 5;

    @Autowired
    private PurchasePriceAdjustMapper purchasePriceAdjustMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Override
    public PurchasePriceAdjust selectPurchasePriceAdjustById(Long id) {
        PurchasePriceAdjust bill = purchasePriceAdjustMapper.selectPurchasePriceAdjustById(id);
        if (bill == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(bill.getTenantId());
        bill.setEntryList(purchasePriceAdjustMapper.selectEntryListByParentId(id));
        return bill;
    }

    @Override
    public List<PurchasePriceAdjust> selectPurchasePriceAdjustList(PurchasePriceAdjust query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId())
            && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return purchasePriceAdjustMapper.selectPurchasePriceAdjustList(query);
    }

    @Override
    public List<PurchasePriceAdjustEntry> selectReportEntryList(PurchasePriceAdjust query) {
        return purchasePriceAdjustMapper.selectReportEntryList(query);
    }

    @Override
    public List<PurchasePriceAdjustEntry> selectReportSummaryList(PurchasePriceAdjust query) {
        return purchasePriceAdjustMapper.selectReportSummaryList(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PurchasePriceAdjust insertPurchasePriceAdjust(PurchasePriceAdjust bill) {
        validateForSave(bill);
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        String tenantId = StringUtils.isNotEmpty(bill.getTenantId())
            ? bill.getTenantId()
            : SecurityUtils.requiredScopedTenantIdForSql();

        bill.setTenantId(tenantId);
        bill.setDelFlag("0");
        // 保存为未提交；提交后为未审核(1)，审核后为已审核(2)
        bill.setBillStatus("0");
        bill.setCreateBy(uid);
        bill.setCreateTime(now);
        if (bill.getBillDate() == null) {
            bill.setBillDate(now);
        }

        int rows = 0;
        DuplicateKeyException lastDup = null;
        for (int i = 0; i < BILL_NO_RETRY; i++) {
            bill.setBillNo(nextBillNo());
            try {
                rows = purchasePriceAdjustMapper.insertPurchasePriceAdjust(bill);
                lastDup = null;
                break;
            } catch (DuplicateKeyException ex) {
                lastDup = ex;
            }
        }
        if (lastDup != null || rows <= 0 || bill.getId() == null) {
            throw new ServiceException("调价单号生成冲突，请重试保存");
        }

        insertEntries(bill, uid, now, tenantId);
        return bill;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePurchasePriceAdjust(PurchasePriceAdjust bill) {
        if (bill == null || bill.getId() == null) {
            throw new ServiceException("调价单不存在");
        }
        PurchasePriceAdjust existing = purchasePriceAdjustMapper.selectPurchasePriceAdjustById(bill.getId());
        if (existing == null) {
            throw new ServiceException("调价单不存在");
        }
        SecurityUtils.ensureTenantAccess(existing.getTenantId());
        if ("2".equals(existing.getBillStatus())) {
            throw new ServiceException("已审核的调价单不可修改");
        }
        validateForSave(bill);

        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        bill.setUpdateBy(uid);
        bill.setUpdateTime(now);
        // 单号生成后不可改
        bill.setBillNo(existing.getBillNo());

        int rows = purchasePriceAdjustMapper.updatePurchasePriceAdjust(bill);
        purchasePriceAdjustMapper.deleteEntryByParentId(bill.getId(), uid);
        bill.setBillNo(existing.getBillNo());
        insertEntries(bill, uid, now, existing.getTenantId());
        return rows;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int submitPurchasePriceAdjustByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要提交的调价单");
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        int count = 0;
        for (Long id : ids) {
            PurchasePriceAdjust existing = purchasePriceAdjustMapper.selectPurchasePriceAdjustById(id);
            if (existing == null) {
                throw new ServiceException("调价单不存在");
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if (!"0".equals(String.valueOf(existing.getBillStatus()))) {
                throw new ServiceException("只能提交未提交状态的单据：" + existing.getBillNo());
            }
            PurchasePriceAdjust upd = new PurchasePriceAdjust();
            upd.setId(id);
            upd.setBillStatus("1");
            upd.setUpdateBy(uid);
            upd.setUpdateTime(now);
            count += purchasePriceAdjustMapper.updateBillStatus(upd);
        }
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int auditPurchasePriceAdjustByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要审核的调价单");
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        int count = 0;
        for (Long id : ids) {
            PurchasePriceAdjust existing = purchasePriceAdjustMapper.selectPurchasePriceAdjustById(id);
            if (existing == null) {
                throw new ServiceException("调价单不存在");
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if (!"1".equals(String.valueOf(existing.getBillStatus()))) {
                throw new ServiceException("只能审核未审核状态的单据：" + existing.getBillNo());
            }
            // 档案调价：审核后回写耗材产品档案价格
            if (ADJUST_TYPE_ARCHIVE.equals(existing.getAdjustType())) {
                applyArchivePriceToMaterials(id, existing.getBillNo(), existing.getTenantId(), uid, now);
            }
            PurchasePriceAdjust upd = new PurchasePriceAdjust();
            upd.setId(id);
            upd.setBillStatus("2");
            upd.setAuditBy(uid);
            upd.setAuditDate(now);
            upd.setUpdateBy(uid);
            upd.setUpdateTime(now);
            count += purchasePriceAdjustMapper.updateBillStatus(upd);
        }
        return count;
    }

    /**
     * 档案调价审核：按明细现价更新 fd_material.price（耗材产品档案「价格」）
     */
    private void applyArchivePriceToMaterials(Long billId, String billNo, String tenantId, String uid, Date now) {
        List<PurchasePriceAdjustEntry> entries = purchasePriceAdjustMapper.selectEntryListByParentId(billId);
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("调价单无明细，无法审核回写档案价格：" + billNo);
        }
        for (PurchasePriceAdjustEntry entry : entries) {
            if (entry == null || entry.getMaterialId() == null) {
                throw new ServiceException("调价明细缺少耗材，无法审核回写档案价格：" + billNo);
            }
            if (entry.getNewPrice() == null) {
                String code = StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : String.valueOf(entry.getMaterialId());
                throw new ServiceException("耗材「" + code + "」现价为空，无法审核回写档案价格：" + billNo);
            }
            if (entry.getNewPrice().compareTo(BigDecimal.ZERO) < 0) {
                String code = StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : String.valueOf(entry.getMaterialId());
                throw new ServiceException("耗材「" + code + "」现价不能小于0：" + billNo);
            }
            FdMaterial material = StringUtils.isNotEmpty(tenantId)
                ? fdMaterialMapper.selectFdMaterialByIdAndTenant(entry.getMaterialId(), tenantId)
                : fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (material == null) {
                String code = StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : String.valueOf(entry.getMaterialId());
                throw new ServiceException("耗材档案不存在，无法回写价格：" + code + "（" + billNo + "）");
            }
            SecurityUtils.ensureTenantAccess(material.getTenantId());
            FdMaterial patch = new FdMaterial();
            patch.setId(material.getId());
            patch.setTenantId(material.getTenantId());
            patch.setPrice(entry.getNewPrice());
            patch.setUpdateBy(uid);
            patch.setUpdateTime(now);
            int rows = fdMaterialMapper.updateFdMaterial(patch);
            if (rows <= 0) {
                String code = StringUtils.isNotEmpty(entry.getMaterialCode()) ? entry.getMaterialCode() : material.getCode();
                throw new ServiceException("回写耗材档案价格失败：" + code + "（" + billNo + "）");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deletePurchasePriceAdjustByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要删除的调价单");
        }
        String uid = SecurityUtils.getUserIdStr();
        int count = 0;
        for (Long id : ids) {
            PurchasePriceAdjust existing = purchasePriceAdjustMapper.selectPurchasePriceAdjustById(id);
            if (existing == null) {
                continue;
            }
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            if ("2".equals(existing.getBillStatus())) {
                throw new ServiceException("已审核的调价单不可删除：" + existing.getBillNo());
            }
            purchasePriceAdjustMapper.deleteEntryByParentId(id, uid);
            count += purchasePriceAdjustMapper.deletePurchasePriceAdjustById(id, uid);
        }
        return count;
    }

    private String nextBillNo() {
        String date = FillRuleUtil.getDateNum();
        String maxNum = purchasePriceAdjustMapper.selectMaxBillNo(date);
        return FillRuleUtil.getNumber(BILL_PREFIX, maxNum, date);
    }

    private void validateForSave(PurchasePriceAdjust bill) {
        if (bill == null) {
            throw new ServiceException("调价单数据不能为空");
        }
        if (StringUtils.isEmpty(bill.getAdjustType())) {
            throw new ServiceException("请选择调价类型");
        }
        Set<String> types = new HashSet<>();
        types.add("archive");
        types.add("warehouse");
        types.add("department");
        if (!types.contains(bill.getAdjustType())) {
            throw new ServiceException("调价类型无效");
        }
        if (bill.getSupplierId() == null) {
            throw new ServiceException("请选择供应商");
        }
        List<PurchasePriceAdjustEntry> entries = bill.getEntryList();
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("请添加调价明细");
        }
        for (PurchasePriceAdjustEntry e : entries) {
            if (e == null || e.getMaterialId() == null) {
                throw new ServiceException("明细耗材不能为空");
            }
            if (e.getNewPrice() == null) {
                throw new ServiceException("请填写现价");
            }
            if (e.getNewPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("现价不能小于0");
            }
        }
    }

    private void insertEntries(PurchasePriceAdjust bill, String uid, Date now, String tenantId) {
        List<PurchasePriceAdjustEntry> src = bill.getEntryList();
        List<PurchasePriceAdjustEntry> list = new ArrayList<>();
        int line = 1;
        for (PurchasePriceAdjustEntry e : src) {
            if (e == null || e.getMaterialId() == null) {
                continue;
            }
            e.setId(null);
            e.setParentId(bill.getId());
            e.setBillNo(bill.getBillNo());
            e.setLineNo(line++);
            e.setDelFlag("0");
            e.setTenantId(tenantId);
            e.setCreateBy(uid);
            e.setCreateTime(now);
            list.add(e);
        }
        if (list.isEmpty()) {
            throw new ServiceException("请添加调价明细");
        }
        purchasePriceAdjustMapper.batchInsertEntry(list);
    }
}
