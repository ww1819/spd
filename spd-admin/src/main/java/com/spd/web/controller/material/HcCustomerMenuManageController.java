package com.spd.web.controller.material;

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
import com.spd.system.domain.hc.HcCustomerMenuPeriodLog;
import com.spd.system.domain.hc.HcCustomerMenuStatusLog;
import com.spd.system.domain.vo.HcCustomerMenuManageVo;
import com.spd.system.service.IHcCustomerMenuManageService;

/**
 * 耗材系统-客户菜单功能管理：对客户（租户）在耗材侧已具备功能做启用/停用，仅平台用户可访问
 */
@RestController
@RequestMapping("/material/system/customerMenuManage")
@PreAuthorize("@ss.isPlatformUser()")
public class HcCustomerMenuManageController extends BaseController {

  @Autowired
  private IHcCustomerMenuManageService manageService;

  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:list')")
  @GetMapping("/list")
  public AjaxResult list(@RequestParam String tenantId) {
    if (StringUtils.isEmpty(tenantId)) {
      return error("租户ID不能为空");
    }
    List<HcCustomerMenuManageVo> list = manageService.listMenusByTenantId(tenantId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:edit')")
  @PutMapping("/changeStatus")
  public AjaxResult changeStatus(@RequestBody ChangeStatusBody body) {
    if (body == null || StringUtils.isEmpty(body.getTenantId()) || body.getMenuId() == null || StringUtils.isEmpty(body.getStatus())) {
      return error("参数不完整");
    }
    if (StringUtils.isEmpty(body.getReason())) {
      return error("0".equals(body.getStatus()) ? "请输入启用原因" : "请输入停用原因");
    }
    return toAjax(manageService.changeStatus(body.getTenantId(), body.getMenuId(), body.getStatus(), body.getReason()));
  }

  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:query')")
  @GetMapping("/statusLog")
  public AjaxResult statusLog(@RequestParam String tenantId, @RequestParam Long menuId) {
    List<HcCustomerMenuStatusLog> list = manageService.getStatusLogList(tenantId, menuId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:query')")
  @GetMapping("/periodLog")
  public AjaxResult periodLog(@RequestParam String tenantId, @RequestParam Long menuId) {
    List<HcCustomerMenuPeriodLog> list = manageService.getPeriodLogList(tenantId, menuId);
    return success(list);
  }

  public static class ChangeStatusBody {
    private String tenantId;
    private Long menuId;
    private String status;
    private String reason;
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
  }
}
