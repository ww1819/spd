package com.spd.web.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Anonymous;
import com.spd.common.core.domain.AjaxResult;

/**
 * SSO 单点登录接口
 *
 * @author spd
 */
@RestController
@RequestMapping("/sso")
public class SSOController {

    /**
     * OSS 登录测试接口（匿名访问，跳过认证）
     *
     * @param token  令牌
     * @param clientId 客户端ID
     * @return 结果
     */
    @Anonymous
    @GetMapping("/osslogin")
    public AjaxResult osslogin(
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "clientId", required = false) String clientId) {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("token", token);
        ajax.put("clientId", clientId);
        ajax.put("message", "osslogin 测试接口调用成功");
        return ajax;
    }
}
