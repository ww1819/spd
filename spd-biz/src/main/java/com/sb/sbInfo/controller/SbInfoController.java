package com.sb.sbInfo.controller;

import com.sb.sbInfo.service.SbInfoService;
import com.spd.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sb/sbInfo")
public class SbInfoController extends BaseController {

    @Autowired
    private SbInfoService sbInfoService;



}
