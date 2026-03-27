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
import com.spd.common.utils.StringUtils;
import com.spd.common.enums.TenantEnum;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerMenuService;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ITenantDataPurgeService;

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
  @Autowired
  private ITenantDataPurgeService tenantDataPurgeService;

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

  /**
   * 代码内租户列表（TenantEnum），新增客户时从此列表选择以关联租户表与枚举
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:list')")
  @GetMapping("/tenantEnumList")
  public AjaxResult tenantEnumList() {
    return success(TenantEnum.toVoList());
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:add')")
  @Log(title = "设备客户", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@Validated @RequestBody SbCustomer customer) {
    if (StringUtils.isEmpty(customer.getTenantKey())) {
      return error("请从代码内租户列表选择租户类型（tenantKey）");
    }
    if (UserConstants.NOT_UNIQUE == (sbCustomerService.checkSbCustomerCodeUnique(customer))) {
      return error("新增客户'" + customer.getCustomerName() + "'失败，客户编码已存在");
    }
    customer.setCreateBy(SecurityUtils.getUserIdStr());
    return toAjax(sbCustomerService.insertSbCustomer(customer));
  }

  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "设备客户", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@Validated @RequestBody SbCustomer customer) {
    if (UserConstants.NOT_UNIQUE == (sbCustomerService.checkSbCustomerCodeUnique(customer))) {
      return error("修改客户'" + customer.getCustomerName() + "'失败，客户编码已存在");
    }
    customer.setUpdateBy(SecurityUtils.getUserIdStr());
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

  /**
   * 设备功能重置：若 super 组和 super_01 不存在则创建；将默认对客户开放的权限开放给客户、super 组、super_01 用户。
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "设备功能重置", businessType = BusinessType.UPDATE)
  @PutMapping("/resetEquipment/{customerId}")
  public AjaxResult resetEquipment(@PathVariable String customerId) {
    sbCustomerService.resetEquipmentFunctions(customerId);
    return success();
  }

  /**
   * 耗材功能重置：若 super 组（岗位）和 super_01 不存在则创建；重置耗材客户菜单权限、super 岗位菜单权限、super_01 菜单权限为系统设置下非平台管理功能。
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "耗材功能重置", businessType = BusinessType.UPDATE)
  @PutMapping("/resetMaterial/{customerId}")
  public AjaxResult resetMaterial(@PathVariable String customerId) {
    sbCustomerService.resetMaterialFunctions(customerId);
    return success();
  }

  /**
   * 按客户物理删除设备侧数据（customer_id）；不删除 sb_customer 行；删除该客户下 sys_user。
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:purgeEq')")
  @Log(title = "清理设备租户数据", businessType = BusinessType.DELETE)
  @PostMapping("/{customerId}/purgeEquipmentData")
  public AjaxResult purgeEquipmentData(@PathVariable String customerId,
      @RequestBody(required = false) java.util.Map<String, String> body) {
    String c = body != null ? body.get("confirm") : null;
    if (!"PURGE_EQ".equals(c)) {
      return error("请在请求体中传入 {\"confirm\":\"PURGE_EQ\"} 以确认清理设备数据");
    }
    int n = tenantDataPurgeService.purgeEquipmentDataForCustomer(customerId);
    return success("已清理设备数据，影响行数约 " + n);
  }

  /**
   * 与耗材客户管理并列：在设备客户列表行内可触发清理该租户耗材数据（逻辑同
   * {@code POST /material/system/customer/{id}/purgeConsumablesData}）。
   */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:purgeHc')")
  @Log(title = "清理耗材租户数据", businessType = BusinessType.DELETE)
  @PostMapping("/{customerId}/purgeConsumablesData")
  public AjaxResult purgeConsumablesDataFromEquipmentUi(@PathVariable String customerId,
      @RequestBody(required = false) java.util.Map<String, String> body) {
    String c = body != null ? body.get("confirm") : null;
    if (!"PURGE_HC".equals(c)) {
      return error("请在请求体中传入 {\"confirm\":\"PURGE_HC\"} 以确认清理耗材数据");
    }
    int n = tenantDataPurgeService.purgeConsumablesDataForTenant(customerId);
    return success("已清理耗材数据，影响行数约 " + n);
  }
}
