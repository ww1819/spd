package com.spd.monitoring.service.impl;

import com.spd.monitoring.domain.FixedNumberSaveRequest;
import com.spd.monitoring.domain.WhFixedNumber;
import com.spd.monitoring.mapper.DeptFixedNumberMapper;
import com.spd.monitoring.mapper.WhFixedNumberMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FixedNumberServiceImplTest {

    @InjectMocks
    private FixedNumberServiceImpl fixedNumberService;

    @Mock
    private WhFixedNumberMapper whFixedNumberMapper;

    @Mock
    private DeptFixedNumberMapper deptFixedNumberMapper;

    private FixedNumberSaveRequest request;

    @Before
    public void setUp() {
        request = new FixedNumberSaveRequest();
        request.setFixedNumberType("1");
        request.setWarehouseId(1001L);

        FixedNumberSaveRequest.Detail detail = new FixedNumberSaveRequest.Detail();
        detail.setMaterialId(2002L);
        detail.setUpperLimit(50);
        detail.setLowerLimit(10);
        detail.setExpiryReminder(30);
        detail.setMonitoring("1");
        detail.setLocation("A-01");
        detail.setLocationId(3003L);
        detail.setRemark("re-add after deleted");
        request.setDetailList(Collections.singletonList(detail));
    }

    @Test
    public void saveFixedNumber_shouldRecoverDeletedWarehouseFixedNumber_whenReAdding() {
        WhFixedNumber deletedRecord = new WhFixedNumber();
        deletedRecord.setId("deleted-id");
        deletedRecord.setWarehouseId(1001L);
        deletedRecord.setMaterialId(2002L);
        deletedRecord.setDelFlag(1);

        when(whFixedNumberMapper.selectByWarehouseAndMaterial(1001L, 2002L)).thenReturn(deletedRecord);

        fixedNumberService.saveFixedNumber(request, "tester");

        verify(whFixedNumberMapper, never()).insertWhFixedNumber(any(WhFixedNumber.class));
        verify(whFixedNumberMapper).updateWhFixedNumber(any(WhFixedNumber.class));
        verify(whFixedNumberMapper).selectByWarehouseAndMaterial(eq(1001L), eq(2002L));
    }
}
