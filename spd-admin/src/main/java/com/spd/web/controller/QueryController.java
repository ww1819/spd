package com.spd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 查询接口
 * 
 * @author spd
 */
@Api("查询接口")
@RestController
@RequestMapping("/query")
public class QueryController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    /**
     * 查询用户信息
     * 
     * @param userId 用户ID
     * @return 用户名
     */
    @ApiOperation("查询用户信息")
    @PostMapping("/user")
    public AjaxResult queryUser(@ApiParam("用户ID") @RequestBody Long userId)
    {
        SysUser user = userService.selectUserById(userId);
        if (user == null)
        {
            return error("用户不存在");
        }
        return success(user.getUserName());
    }
} 