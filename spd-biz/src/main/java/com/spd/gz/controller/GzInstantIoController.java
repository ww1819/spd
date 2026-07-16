package com.spd.gz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;
import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;
import com.spd.gz.domain.dto.GzInstantIoAuditBody;
import com.spd.gz.domain.dto.GzInstantIoExportRow;
import com.spd.gz.domain.dto.GzInstantIoReverseBody;
import com.spd.gz.service.IGzHighValueWriteOffService;
import com.spd.gz.service.IGzInstantIoService;

/**
 * 库房高值即入即出：审核生成 G-RK/G-CK；人工反向生成退货301+退库401。
 */
@RestController
@RequestMapping("/gz/instantIo")
public class GzInstantIoController extends BaseController
{
    @Autowired
    private IGzInstantIoService gzInstantIoService;
    @Autowired
    private IGzHighValueWriteOffService gzHighValueWriteOffService;

    @PreAuthorize("@ss.hasPermi('gz:instantIo:list')")
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
            List<GzHighChargeConfirmRowVo> list = gzInstantIoService.selectList(query);
            return getDataTable(list);
        }
        finally
        {
            clearPage();
        }
    }

    @PreAuthorize("@ss.hasPermi('gz:instantIo:list')")
    @Log(title = "高值即入即出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzHighChargeConfirmQuery query,
        @RequestParam(value = "linkIds", required = false) String linkIds)
    {
        List<String> ids = null;
        if (StringUtils.isNotBlank(linkIds))
        {
            ids = Arrays.stream(linkIds.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        }
        List<GzHighChargeConfirmRowVo> list = gzInstantIoService.selectListForExport(query, ids);
        List<GzInstantIoExportRow> rows = new ArrayList<>();
        if (list != null)
        {
            for (GzHighChargeConfirmRowVo r : list)
            {
                rows.add(GzInstantIoExportRow.from(r));
            }
        }
        ExcelUtil<GzInstantIoExportRow> util = new ExcelUtil<>(GzInstantIoExportRow.class);
        util.exportExcel(response, rows, "高值即入即出");
    }

    @PreAuthorize("@ss.hasPermi('gz:instantIo:audit')")
    @Log(title = "高值即入即出审核", businessType = BusinessType.OTHER)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody GzInstantIoAuditBody body)
    {
        GzHighChargeConfirmResultVo vo = gzInstantIoService.audit(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('gz:instantIo:reverse')")
    @Log(title = "高值即入即出反向单据", businessType = BusinessType.OTHER)
    @PostMapping("/reverse")
    public AjaxResult reverse(@RequestBody GzInstantIoReverseBody body)
    {
        GzHighChargeConfirmResultVo vo = gzInstantIoService.reverse(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('gz:instantIo:writeOff')")
    @Log(title = "高值冲销", businessType = BusinessType.OTHER)
    @PostMapping("/writeOff")
    public AjaxResult writeOff(@RequestBody GzHighValueWriteOffBody body)
    {
        if (body == null)
        {
            body = new GzHighValueWriteOffBody();
        }
        body.setSource("INSTANT_IO");
        GzHighValueWriteOffResultVo vo = gzHighValueWriteOffService.writeOff(body);
        return success(vo);
    }
}
