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
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.his.constant.HisMirrorProcessConstants;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.support.HisAutoWriteOffOperatorSupport;

/**
 * 消耗处理失败结果单独落库（与主业务事务分离，避免回滚时丢失处理情况）。
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
    private ISbTenantSettingService sbTenantSettingService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void recordFailure(String tenantId, String visitKind, String mirrorRowId, String processParty, String failMessage)
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
    }
}
