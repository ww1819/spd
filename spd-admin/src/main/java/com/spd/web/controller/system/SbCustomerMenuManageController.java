package com.spd.web.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbCustomerMenuPeriodLog;
import com.spd.system.domain.SbCustomerMenuStatusLog;
import com.spd.system.domain.vo.SbCustomerMenuManageVo;
import com.spd.system.service.ISbCustomerMenuManageService;

/**
 * 客户菜单功能管理：对客户已具备的功能做启用/停用，仅平台用户可访问，租户不显示该菜单
 */
@RestController
@RequestMapping("/equipment/system/customerMenuManage")
@PreAuthorize("@ss.isPlatformUser()")
public class SbCustomerMenuManageController extends BaseController {

  @Autowired
  private ISbCustomerMenuManageService manageService;

  @PreAuthorize("@ss.hasPermi('sb:system:customerMenuManage:list')")
  @GetMapping("/list")
  public AjaxResult list(@RequestParam String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      return error("客户ID不能为空");
    }
    List<SbCustomerMenuManageVo> list = manageService.listMenusByCustomerId(customerId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customerMenuManage:edit')")
  @PutMapping("/changeStatus")
  public AjaxResult changeStatus(@RequestBody ChangeStatusBody body) {
    if (body == null || StringUtils.isEmpty(body.getCustomerId()) || StringUtils.isEmpty(body.getMenuId()) || StringUtils.isEmpty(body.getStatus())) {
      return error("参数不完整");
    }
    if (StringUtils.isEmpty(body.getReason())) {
      return error("0".equals(body.getStatus()) ? "请输入启用原因" : "请输入停用原因");
    }
    return toAjax(manageService.changeStatus(body.getCustomerId(), body.getMenuId(), body.getStatus(), body.getReason()));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customerMenuManage:query')")
  @GetMapping("/statusLog")
  public AjaxResult statusLog(@RequestParam String customerId, @RequestParam String menuId) {
    List<SbCustomerMenuStatusLog> list = manageService.getStatusLogList(customerId, menuId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customerMenuManage:query')")
  @GetMapping("/periodLog")
  public AjaxResult periodLog(@RequestParam String customerId, @RequestParam String menuId) {
    List<SbCustomerMenuPeriodLog> list = manageService.getPeriodLogList(customerId, menuId);
    return success(list);
  }

  public static class ChangeStatusBody {
    private String customerId;
    private String menuId;
    private String status;
    private String reason;
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
  }
}
