package com.spd.web.controller.system;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.constant.HttpStatus;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.system.domain.SysPrintDocRows;
import com.spd.system.service.ISysPrintDocRowsService;

/**
 * 单据类型每页打印行数
 *
 * @author spd
 */
@RestController
@RequestMapping("/system/printDocRows")
public class SysPrintDocRowsController extends BaseController
{
    /** 允许小票据（如每页 1～2 行）；仅做合理上限防误操作 */
    private static final int ROWS_MIN = 1;

    private static final int ROWS_MAX = 30;

    private static final Set<String> ALLOWED_KINDS = new HashSet<>(Arrays.asList(
        "INBOUND", "OUTBOUND", "REFUND_DEPOT", "REFUND_GOODS"));

    @Autowired
    private ISysPrintDocRowsService sysPrintDocRowsService;

    /**
     * 按单据类型查询（无记录时返回默认 rowsPerPage=6，不入库）。
     */
    @GetMapping("/{docKind}")
    public AjaxResult getByDocKind(@PathVariable("docKind") String docKind)
    {
        String kind = normalizeDocKind(docKind);
        if (kind == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "无效的单据类型");
        }
        SysPrintDocRows row = sysPrintDocRowsService.selectByDocKind(kind);
        if (row == null)
        {
            SysPrintDocRows def = new SysPrintDocRows();
            def.setDocKind(kind);
            def.setRowsPerPage(6);
            return success(def);
        }
        return success(row);
    }

    /**
     * 更新或插入每页行数（需登录；与 getDefault 类似，供业务打印页保存行数）
     */
    @PutMapping
    public AjaxResult upsert(@Valid @RequestBody SysPrintDocRows body)
    {
        String kind = normalizeDocKind(body.getDocKind());
        if (kind == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "无效的单据类型");
        }
        Integer n = body.getRowsPerPage();
        if (n == null || n < ROWS_MIN || n > ROWS_MAX)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "每页行数必须在 " + ROWS_MIN + "-" + ROWS_MAX + " 之间");
        }
        body.setDocKind(kind);
        body.setRowsPerPage(n);
        sysPrintDocRowsService.upsert(body);
        SysPrintDocRows saved = sysPrintDocRowsService.selectByDocKind(kind);
        return success(saved != null ? saved : body);
    }

    private static String normalizeDocKind(String docKind)
    {
        if (StringUtils.isBlank(docKind))
        {
            return null;
        }
        String k = docKind.trim().toUpperCase();
        if (!ALLOWED_KINDS.contains(k))
        {
            return null;
        }
        return k;
    }
}
