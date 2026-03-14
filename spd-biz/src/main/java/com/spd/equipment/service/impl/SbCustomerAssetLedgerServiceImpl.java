package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.DictUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbCustomerAssetLedger;
import com.spd.equipment.mapper.SbCustomerAssetLedgerMapper;
import com.spd.equipment.service.ISbCustomerAssetLedgerService;
import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.mapper.SbCustomerCategory68Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SbCustomerAssetLedgerServiceImpl implements ISbCustomerAssetLedgerService {

    private static final String USE_STATUS_DEFAULT = "in_use";
    private static final String REPAIR_STATUS_DEFAULT = "no_fault";
    private static final String LABEL_PRINT_DEFAULT = "N";

    @Autowired
    private SbCustomerAssetLedgerMapper mapper;
    @Autowired
    private SbCustomerCategory68Mapper category68Mapper;

    @Override
    public List<SbCustomerAssetLedger> selectList(SbCustomerAssetLedger q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        List<SbCustomerAssetLedger> list = mapper.selectList(q);
        fillDictLabels(list);
        return list;
    }

    @Override
    public SbCustomerAssetLedger selectById(String id) {
        SbCustomerAssetLedger row = mapper.selectById(id);
        if (row != null) fillDictLabel(row);
        return row;
    }

    @Override
    public int insert(SbCustomerAssetLedger row) {
        if (StringUtils.isEmpty(row.getCustomerId())) row.setCustomerId(SecurityUtils.getCustomerId());
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        if (StringUtils.isEmpty(row.getEquipmentSerialNo())) {
            Integer maxN = mapper.selectMaxSerialNoNumeric(row.getCustomerId());
            row.setEquipmentSerialNo(String.valueOf((maxN == null ? 0 : maxN) + 1));
        }
        if (StringUtils.isNotEmpty(row.getCategory68Id())) {
            SbCustomerCategory68 cat = category68Mapper.selectById(row.getCategory68Id());
            if (cat != null) {
                String code = cat.getCategory68Code();
                if (StringUtils.isEmpty(row.getCategory68Code())) row.setCategory68Code(code);
                Integer maxSeq = mapper.selectMaxArchiveNoSeq(row.getCustomerId(), row.getCategory68Id());
                int next = (maxSeq == null ? 0 : maxSeq) + 1;
                row.setCategory68ArchiveNo(code + "-" + String.format("%04d", next));
            }
        }
        if (StringUtils.isEmpty(row.getUseStatus())) row.setUseStatus(USE_STATUS_DEFAULT);
        if (StringUtils.isEmpty(row.getRepairStatus())) row.setRepairStatus(REPAIR_STATUS_DEFAULT);
        if (StringUtils.isEmpty(row.getLabelPrintStatus())) row.setLabelPrintStatus(LABEL_PRINT_DEFAULT);
        fillDictLabel(row);
        return mapper.insert(row);
    }

    @Override
    public int update(SbCustomerAssetLedger row) {
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        SbCustomerAssetLedger row = new SbCustomerAssetLedger();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        return mapper.update(row);
    }

    private void fillDictLabels(List<SbCustomerAssetLedger> list) {
        if (list == null) return;
        for (SbCustomerAssetLedger r : list) fillDictLabel(r);
    }

    private void fillDictLabel(SbCustomerAssetLedger r) {
        if (r == null) return;
        if (StringUtils.isNotEmpty(r.getUseStatus())) {
            r.setUseStatusName(DictUtils.getDictLabel("eq_use_status", r.getUseStatus()));
        }
        if (StringUtils.isNotEmpty(r.getRepairStatus())) {
            r.setRepairStatusName(DictUtils.getDictLabel("eq_repair_status", r.getRepairStatus()));
        }
    }
}
