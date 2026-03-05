package com.spd.web.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerMenuService;
import com.spd.system.service.ISbCustomerService;

/**
 * 设备系统客户（SaaS租户）管理
 * 仅对无客户ID的平台用户开放，租户用户不可访问且不显示该菜单。
 * 路由前缀：/equipment/system/customer
 */
@RestController
@RequestMapping("/equipment/system/customer")
@org.springframework.security.access.prepost.PreAuthorize("@ss.isPlatformUser()")
public class SbCustomerController extends BaseController {

  @Autowired
  private ISbCustomerService sbCustomerService;

  @Autowired
  private ISbCustomerMenuService sbCustomerMenuService;

  @PreAuthorize("@ss.hasPermi('sb:system:customer:list')")
  @GetMapping("/list")
  public TableDataInfo list(SbCustomer customer) {
    startPage();
    List<SbCustomer> list = sbCustomerService.selectSbCustomerList(customer);
    return getDataTable(list);
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:query')")
  @GetMapping("/{customerId}")
  public AjaxResult getInfo(@PathVariable String customerId) {
    return success(sbCustomerService.selectSbCustomerById(customerId));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:add')")
  @Log(title = "设备客户", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@Validated @RequestBody SbCustomer customer) {
    if (UserConstants.NOT_UNIQUE == (sbCustomerService.checkSbCustomerCodeUnique(customer))) {
      return error("新增客户'" + customer.getCustomerName() + "'失败，客户编码已存在");
    }
    customer.setCreateBy(SecurityUtils.getUsername());
    return toAjax(sbCustomerService.insertSbCustomer(customer));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "设备客户", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@Validated @RequestBody SbCustomer customer) {
    if (UserConstants.NOT_UNIQUE == (sbCustomerService.checkSbCustomerCodeUnique(customer))) {
      return error("修改客户'" + customer.getCustomerName() + "'失败，客户编码已存在");
    }
    customer.setUpdateBy(SecurityUtils.getUsername());
    return toAjax(sbCustomerService.updateSbCustomer(customer));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:remove')")
  @Log(title = "设备客户", businessType = BusinessType.DELETE)
  @DeleteMapping("/{customerId}")
  public AjaxResult remove(@PathVariable String customerId) {
    return toAjax(sbCustomerService.deleteSbCustomerById(customerId));
  }

  /**
   * 客户启停用（必须填写原因）
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "客户启停用", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public AjaxResult changeStatus(@RequestBody SbCustomer customer) {
    if (customer == null || com.spd.common.utils.StringUtils.isEmpty(customer.getCustomerId())
        || com.spd.common.utils.StringUtils.isEmpty(customer.getStatus())) {
      return error("参数不完整");
    }
    if (com.spd.common.utils.StringUtils.isEmpty(customer.getStatusChangeReason())) {
      return error("启停用原因不能为空");
    }
    return toAjax(sbCustomerService.changeStatus(
        customer.getCustomerId(), customer.getStatus(), customer.getStatusChangeReason()));
  }

  /**
   * 获取客户已分配的设备菜单ID列表
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:query')")
  @GetMapping("/menuIds/{customerId}")
  public AjaxResult getCustomerMenuIds(@PathVariable String customerId) {
    List<String> menuIds = sbCustomerMenuService.selectMenuIdsByCustomerId(customerId);
    return success(menuIds);
  }

  /**
   * 保存客户设备菜单权限
   * 支持两种方式：1）body 传 { menuIds: [] }；2）query 传 menuIds（数组或单字符串逗号分隔）
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "客户菜单权限", businessType = BusinessType.UPDATE)
  @PutMapping("/menu")
  public AjaxResult saveCustomerMenus(
      @RequestParam String customerId,
      @RequestBody(required = false) java.util.Map<String, Object> body,
      @RequestParam(required = false) String[] menuIds) {
    String[] resolved = resolveMenuIds(body, menuIds);
    return toAjax(sbCustomerMenuService.saveCustomerMenus(customerId, resolved));
  }

  private String[] resolveMenuIds(java.util.Map<String, Object> body, String[] menuIdsParam) {
    if (body != null) {
      Object menuIdsObj = body.get("menuIds");
      if (menuIdsObj instanceof java.util.List) {
        java.util.List<?> list = (java.util.List<?>) menuIdsObj;
        return list.stream().map(String::valueOf).toArray(String[]::new);
      }
      if (menuIdsObj != null && menuIdsObj.toString().trim().length() > 0) {
        return menuIdsObj.toString().split("\\s*,\\s*");
      }
    }
    if (menuIdsParam != null && menuIdsParam.length > 0) {
      if (menuIdsParam.length == 1 && menuIdsParam[0] != null && menuIdsParam[0].contains(",")) {
        return menuIdsParam[0].split("\\s*,\\s*");
      }
      return menuIdsParam;
    }
    return new String[0];
  }

  /** 客户启停用记录列表 */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:query')")
  @GetMapping("/statusLog/{customerId}")
  public AjaxResult getStatusLogs(@PathVariable String customerId) {
    return success(sbCustomerService.selectStatusLogList(customerId));
  }

  /** 客户实际使用/停用时间段记录列表 */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:query')")
  @GetMapping("/periodLog/{customerId}")
  public AjaxResult getPeriodLogs(@PathVariable String customerId) {
    return success(sbCustomerService.selectPeriodLogList(customerId));
  }
}
