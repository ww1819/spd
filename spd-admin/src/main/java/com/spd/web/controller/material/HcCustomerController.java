package com.spd.web.controller.material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.core.page.TableDataInfo;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.hc.HcCustomerMenu;
import com.spd.system.domain.hc.HcCustomerPeriodLog;
import com.spd.system.domain.hc.HcCustomerStatusLog;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.HcCustomerPeriodLogMapper;
import com.spd.system.mapper.HcCustomerStatusLogMapper;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ITenantDataPurgeService;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.exception.ServiceException;
import com.spd.system.service.ISysMenuService;

/**
 * 耗材系统-客户启停记录与时间段、耗材客户菜单权限分配（与设备共用客户列表，耗材侧单独记录）
 */
@RestController
@RequestMapping("/material/system/customer")
@PreAuthorize("@ss.isPlatformUser()")
public class HcCustomerController extends BaseController {

  @Autowired
  private HcCustomerStatusLogMapper hcCustomerStatusLogMapper;
  @Autowired
  private HcCustomerPeriodLogMapper hcCustomerPeriodLogMapper;
  @Autowired
  private ISysMenuService sysMenuService;
  @Autowired
  private HcCustomerMenuMapper hcCustomerMenuMapper;
  @Autowired
  private ISbCustomerService sbCustomerService;
  @Autowired
  private ITenantDataPurgeService tenantDataPurgeService;

  /** 耗材侧客户列表（供客户菜单功能管理下拉等使用，与设备共用 sb_customer） */
  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:list')")
  @GetMapping("/list")
  public TableDataInfo listCustomers(SbCustomer query) {
    startPage();
    List<SbCustomer> list = sbCustomerService.selectSbCustomerList(query);
    return getDataTable(list);
  }

  /** 耗材侧客户详情（含 hcStatus、hcPlannedDisableTime） */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @GetMapping("/{customerId}")
  public AjaxResult getInfo(@PathVariable String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      return error("客户ID不能为空");
    }
    SbCustomer customer = sbCustomerService.selectSbCustomerById(customerId);
    return customer != null ? success(customer) : error("客户不存在");
  }

  /** 耗材侧客户更新（仅允许更新客户名称、备注、耗材状态、耗材计划停用时间，不修改设备侧 status/planned_disable_time） */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @PutMapping
  public AjaxResult update(@RequestBody SbCustomer body) {
    if (body == null || StringUtils.isEmpty(body.getCustomerId())) {
      return error("客户ID不能为空");
    }
    SbCustomer existing = sbCustomerService.selectSbCustomerById(body.getCustomerId());
    if (existing == null) {
      return error("客户不存在");
    }
    existing.setCustomerName(body.getCustomerName());
    existing.setRemark(body.getRemark());
    existing.setHcStatus(body.getHcStatus());
    existing.setHcPlannedDisableTime(body.getHcPlannedDisableTime());
    existing.setUpdateBy(SecurityUtils.getUserIdStr());
    return toAjax(sbCustomerService.updateSbCustomer(existing));
  }

  /** 耗材侧客户启停用（更新 hc_status，写 hc_customer_status_log、hc_customer_period_log） */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @PutMapping("/changeHcStatus")
  public AjaxResult changeHcStatus(@RequestBody ChangeHcStatusBody body) {
    if (body == null || StringUtils.isEmpty(body.getCustomerId()) || StringUtils.isEmpty(body.getStatus())) {
      return error("客户ID和状态不能为空");
    }
    if (StringUtils.isEmpty(body.getStatusChangeReason()) || body.getStatusChangeReason().trim().isEmpty()) {
      return error("请输入启停用原因");
    }
    return toAjax(sbCustomerService.changeHcStatus(body.getCustomerId(), body.getStatus(), body.getStatusChangeReason()));
  }

  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @GetMapping("/statusLog")
  public AjaxResult getStatusLogs(@RequestParam String tenantId) {
    if (StringUtils.isEmpty(tenantId)) {
      return error("租户ID不能为空");
    }
    List<HcCustomerStatusLog> list = hcCustomerStatusLogMapper.selectByTenantId(tenantId);
    return success(list);
  }

  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @GetMapping("/periodLog")
  public AjaxResult getPeriodLogs(@RequestParam String tenantId) {
    if (StringUtils.isEmpty(tenantId)) {
      return error("租户ID不能为空");
    }
    List<HcCustomerPeriodLog> list = hcCustomerPeriodLogMapper.selectByTenantId(tenantId);
    return success(list);
  }

  /** 耗材客户权限分配：菜单树（排除客户管理、客户菜单功能管理） */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @GetMapping("/treeselectMenu")
  public AjaxResult treeselectMenu() {
    return success(sysMenuService.selectMenuTreeForHcCustomerAssign());
  }

  /** 耗材客户权限：已分配菜单ID列表 */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @GetMapping("/menuIds/{customerId}")
  public AjaxResult getMenuIds(@PathVariable("customerId") String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      return error("客户ID不能为空");
    }
    List<Long> menuIds = hcCustomerMenuMapper.selectMenuIdsByTenantId(customerId);
    return success(menuIds != null ? menuIds.stream().map(String::valueOf).collect(Collectors.toList()) : new ArrayList<>());
  }

  /** 耗材客户权限：保存（覆盖） */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:query')")
  @PutMapping("/menu")
  public AjaxResult saveMenus(@RequestParam String customerId, @RequestBody SaveMenusBody body) {
    if (StringUtils.isEmpty(customerId)) {
      return error("客户ID不能为空");
    }
    List<Long> menuIds = new ArrayList<>();
    if (body != null && body.getMenuIds() != null) {
      for (Object id : body.getMenuIds()) {
        if (id instanceof Number) {
          menuIds.add(((Number) id).longValue());
        } else if (id != null && !StringUtils.isEmpty(id.toString())) {
          try {
            menuIds.add(Long.parseLong(id.toString()));
          } catch (NumberFormatException ignored) { }
        }
      }
    }
    for (Long menuId : menuIds) {
      SysMenu menu = sysMenuService.selectMenuById(menuId);
      if (menu != null && "1".equals(menu.getIsPlatform())) {
        throw new ServiceException("不能将平台管理菜单分配给客户，请从可分配菜单中选择");
      }
    }
    String username = SecurityUtils.getUserIdStr();
    hcCustomerMenuMapper.deleteByTenantId(customerId);
    if (!menuIds.isEmpty()) {
      List<HcCustomerMenu> list = new ArrayList<>();
      for (Long menuId : menuIds) {
        HcCustomerMenu e = new HcCustomerMenu();
        e.setTenantId(customerId);
        e.setMenuId(menuId);
        e.setStatus("0");
        e.setIsEnabled("1");
        e.setCreateBy(username);
        list.add(e);
      }
      hcCustomerMenuMapper.batchInsert(list);
    }
    return success();
  }

  public static class SaveMenusBody {
    private List<Object> menuIds;
    public List<Object> getMenuIds() { return menuIds; }
    public void setMenuIds(List<Object> menuIds) { this.menuIds = menuIds; }
  }

  /**
   * 设备功能重置：若 super 组和 super_01 不存在则创建；将默认对客户开放的权限开放给客户、super 组、super_01 用户。
   */
  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:edit')")
  @PutMapping("/resetEquipment/{customerId}")
  public AjaxResult resetEquipment(@PathVariable String customerId) {
    sbCustomerService.resetEquipmentFunctions(customerId);
    return success();
  }

  /**
   * 耗材功能重置：若 super 组（岗位）和 super_01 不存在则创建；重置耗材客户菜单权限、super 岗位菜单权限、super_01 菜单权限为系统设置下非平台管理功能。
   */
  @PreAuthorize("@ss.hasPermi('hc:system:customerMenuManage:edit')")
  @PutMapping("/resetMaterial/{customerId}")
  public AjaxResult resetMaterial(@PathVariable String customerId) {
    sbCustomerService.resetMaterialFunctions(customerId);
    return success();
  }

  /**
   * 平台级：清空全部租户与业务数据，保留菜单/字典/参数/角色菜单定义等；仅保留 user_name=admin。
   * 请求体 confirmToken 须为 {@link ITenantDataPurgeService#FULL_RESET_CONFIRM_TOKEN} 的常量值。
   */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:initDb')")
  @PostMapping("/initFullDatabase")
  public AjaxResult initFullDatabase(@RequestBody FullDbInitBody body) {
    if (body == null || StringUtils.isEmpty(body.getConfirmToken())) {
      return error("缺少确认口令");
    }
    tenantDataPurgeService.purgeAllDataKeepPlatform(body.getConfirmToken());
    return success("已执行全库初始化（租户数据已清空，请重新登录验证）");
  }

  /**
   * 按租户物理删除耗材侧数据（含该租户 sys_user）；不删除 sb_customer 行。
   */
  @PreAuthorize("@ss.hasPermi('hc:system:customer:purgeHc')")
  @PostMapping("/{customerId}/purgeConsumablesData")
  public AjaxResult purgeConsumablesData(@PathVariable String customerId, @RequestBody(required = false) PurgeConfirmBody body) {
    if (body == null || !"PURGE_HC".equals(body.getConfirm())) {
      return error("请在请求体中传入 {\"confirm\":\"PURGE_HC\"} 以确认清理耗材数据");
    }
    int n = tenantDataPurgeService.purgeConsumablesDataForTenant(customerId);
    return success("已清理耗材数据，影响行数约 " + n);
  }

  public static class FullDbInitBody {
    private String confirmToken;
    public String getConfirmToken() { return confirmToken; }
    public void setConfirmToken(String confirmToken) { this.confirmToken = confirmToken; }
  }

  public static class PurgeConfirmBody {
    private String confirm;
    public String getConfirm() { return confirm; }
    public void setConfirm(String confirm) { this.confirm = confirm; }
  }

  public static class ChangeHcStatusBody {
    private String customerId;
    private String status;
    private String statusChangeReason;
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusChangeReason() { return statusChangeReason; }
    public void setStatusChangeReason(String statusChangeReason) { this.statusChangeReason = statusChangeReason; }
  }
}
