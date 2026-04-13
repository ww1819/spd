package com.spd.warehouse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.warehouse.mapper.HcDocBillRefMapper;

/**
 * 单据引用关联查询（按生成后单据主表 ID）
 */
@RestController
@RequestMapping("/warehouse/docBillRef")
public class HcDocBillRefController extends BaseController {

    @Autowired
    private HcDocBillRefMapper hcDocBillRefMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listByTgtBill")
    public AjaxResult listByTgtBill(@RequestParam String tgtBillId) {
        return success(hcDocBillRefMapper.selectByTgtBillId(tgtBillId));
    }
}
