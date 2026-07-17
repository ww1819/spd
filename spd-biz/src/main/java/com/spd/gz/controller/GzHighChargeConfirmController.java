package com.spd.gz.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.utils.StringUtils;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.gz.domain.dto.GzHighChargeConfirmBody;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;
import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;
import com.spd.gz.service.IGzHighChargeConfirmService;
import com.spd.gz.service.IGzHighValueWriteOffService;

/**
 * 高值核销确认：已扫码核销明细列表、临床消耗确认（不建结算单，待库房即入即出审核）。
 */
@RestController
@RequestMapping("/gz/highChargeConfirm")
public class GzHighChargeConfirmController extends BaseController
{
    @Autowired
    private IGzHighChargeConfirmService gzHighChargeConfirmService;
    @Autowired
    private IGzHighValueWriteOffService gzHighValueWriteOffService;

    @PreAuthorize("@ss.hasPermi('gz:highChargeConfirm:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzHighChargeConfirmQuery query,
        @RequestParam(value = "sortField", required = false) String sortField,
        @RequestParam(value = "sortOrder", required = false) String sortOrder)
    {
        if (StringUtils.isNotBlank(sortField))
        {
            query.setSortField(sortField.trim());
            query.setSortOrder(sortOrder);
        }
        clearPage();
        try
        {
            List<GzHighChargeConfirmRowVo> list = gzHighChargeConfirmService.selectConfirmList(query);
            return getDataTable(list);
        }
        finally
        {
            clearPage();
        }
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeConfirm:confirm')")
    @Log(title = "高值核销确认", businessType = BusinessType.OTHER)
    @PostMapping("/confirm")
    public AjaxResult confirm(@RequestBody GzHighChargeConfirmBody body)
    {
        GzHighChargeConfirmResultVo vo = gzHighChargeConfirmService.confirm(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeConfirm:list')")
    @GetMapping("/confirmDetail/{confirmId}")
    public AjaxResult confirmDetail(@PathVariable("confirmId") String confirmId)
    {
        return success(gzHighChargeConfirmService.getConfirmDetail(confirmId));
    }

    /** 临床段档 A 冲销（未确认）；已确认须到即入即出页由库房冲销（HV-Q-006） */
    @PreAuthorize("@ss.hasPermi('gz:highChargeConfirm:writeOff')")
    @Log(title = "高值核销冲销", businessType = BusinessType.OTHER)
    @PostMapping("/writeOff")
    public AjaxResult writeOff(@RequestBody GzHighValueWriteOffBody body)
    {
        if (body == null)
        {
            body = new GzHighValueWriteOffBody();
        }
        body.setSource("CONFIRM");
        GzHighValueWriteOffResultVo vo = gzHighValueWriteOffService.writeOff(body);
        return success(vo);
    }
}
