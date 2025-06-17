package com.sb.sbInfo.controller;

import com.sb.sbInfo.service.SbInfoService;
import com.spd.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sb/sbInfo")
public class SbInfoController extends BaseController {

    @Autowired
    private SbInfoService sbInfoService;

    /**
     * 获取设备标签信息
     *
     * @param code 设备编码
     * @return 设备标签信息
     */
    @PreAuthorize("@ss.hasPermi('sb:sbInfo:getSbLabelInfo')")
    @RequestMapping("/getSbLabelInfo")
    public String getSbLabelInfo(String code) {
        if (code == null || code.isEmpty()) {
            return "设备编码不能为空";
        }
        return sbInfoService.getSbLabelInfo(code);
    }
}
