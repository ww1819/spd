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
 * 路由前缀：/equipment/system/customer
 */
@RestController
@RequestMapping("/equipment/system/customer")
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
   */
  @PreAuthorize("@ss.hasPermi('sb:system:customer:edit')")
  @Log(title = "客户菜单权限", businessType = BusinessType.UPDATE)
  @PutMapping("/menu")
  public AjaxResult saveCustomerMenus(String customerId, String[] menuIds) {
    return toAjax(sbCustomerMenuService.saveCustomerMenus(customerId, menuIds));
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
