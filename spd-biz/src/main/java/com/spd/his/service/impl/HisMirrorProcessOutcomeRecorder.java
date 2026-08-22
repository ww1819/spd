package com.spd.his.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.his.constant.HisMirrorProcessConstants;
import com.spd.his.domain.HisMirrorProcessLog;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorProcessLogMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.support.HisAutoWriteOffOperatorSupport;

/**
 * 消耗处理结果落库（处理情况 + 操作日志；与主业务事务分离时避免回滚丢失）。
 */
@Service
public class HisMirrorProcessOutcomeRecorder
{
    private static final String KIND_IN = "INPATIENT";

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;
    @Autowired
    private HisMirrorProcessLogMapper hisMirrorProcessLogMapper;
    @Autowired
    private ISbTenantSettingService sbTenantSettingService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void recordFailure(String tenantId, String visitKind, String mirrorRowId, String operation,
        String processType, String processParty, String failMessage)
    {
        if (StringUtils.isAnyEmpty(tenantId, visitKind, mirrorRowId))
        {
            return;
        }
        List<String> ids = Collections.singletonList(mirrorRowId);
        Date procTime = DateUtils.getNowDate();
        String procBy = HisAutoWriteOffOperatorSupport.resolveProcessBy(tenantId, processParty, sbTenantSettingService);
        String situation = HisMirrorProcessConstants.truncateSituation(failMessage);
        String party = HisMirrorProcessConstants.resolveParty(processParty);
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessOutcome(tenantId, ids, situation, party, procTime, procBy);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessOutcome(tenantId, ids, situation, party, procTime, procBy);
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessOutcome(tenantId, ids, situation, party, procTime, procBy);
        insertLog(tenantId, visitKind, mirrorRowId, operation, HisMirrorProcessConstants.OUTCOME_FAIL, processType,
            party, situation, procBy, procTime);
    }

    /**
     * 成功操作日志（主事务内调用；与镜像行状态更新同事务提交）。
     */
    public void recordSuccessLog(String tenantId, String visitKind, String mirrorRowId, String operation,
        String processType, String processParty, String situation)
    {
        if (StringUtils.isAnyEmpty(tenantId, visitKind, mirrorRowId))
        {
            return;
        }
        Date procTime = DateUtils.getNowDate();
        String procBy = HisAutoWriteOffOperatorSupport.resolveProcessBy(tenantId, processParty, sbTenantSettingService);
        String party = HisMirrorProcessConstants.resolveParty(processParty);
        String msg = HisMirrorProcessConstants.truncateSituation(
            StringUtils.defaultIfBlank(situation, HisMirrorProcessConstants.RESULT_SUCCESS));
        insertLog(tenantId, visitKind, mirrorRowId, operation, HisMirrorProcessConstants.OUTCOME_SUCCESS, processType,
            party, msg, procBy, procTime);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void recordFailureLogOnly(String tenantId, String visitKind, String mirrorRowId, String operation,
        String processType, String processParty, String failMessage)
    {
        if (StringUtils.isAnyEmpty(tenantId, visitKind, mirrorRowId))
        {
            return;
        }
        Date procTime = DateUtils.getNowDate();
        String procBy = HisAutoWriteOffOperatorSupport.resolveProcessBy(tenantId, processParty, sbTenantSettingService);
        String party = HisMirrorProcessConstants.resolveParty(processParty);
        String situation = HisMirrorProcessConstants.truncateSituation(failMessage);
        insertLog(tenantId, visitKind, mirrorRowId, operation, HisMirrorProcessConstants.OUTCOME_FAIL, processType,
            party, situation, procBy, procTime);
    }

    private void insertLog(String tenantId, String visitKind, String mirrorRowId, String operation, String outcome,
        String processType, String processParty, String situation, String processBy, Date processTime)
    {
        HisMirrorProcessLog row = new HisMirrorProcessLog();
        row.setId(UUID7.generateUUID7());
        row.setTenantId(tenantId);
        row.setVisitKind(visitKind);
        row.setMirrorRowId(mirrorRowId);
        row.setOperation(StringUtils.defaultString(operation));
        row.setOutcome(outcome);
        row.setProcessType(processType);
        row.setSituation(situation);
        row.setProcessParty(processParty);
        row.setProcessBy(processBy);
        row.setProcessTime(processTime != null ? processTime : DateUtils.getNowDate());
        row.setCreateTime(DateUtils.getNowDate());
        hisMirrorProcessLogMapper.insert(row);
    }
}
