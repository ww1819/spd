package com.spd.system.service;

import java.util.List;
import java.util.Set;

import com.spd.common.core.domain.TreeSelect;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.system.domain.vo.RouterVo;

/**
 * 菜单 业务层
 * 
 * @author spd
 */
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @param forTenant 是否租户视角（true 时排除平台管理菜单），可为 null
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(Long userId, Boolean forTenant);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @param forTenant 是否租户视角（true 时排除平台管理菜单），可为 null
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId, Boolean forTenant);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端路由；pausedMenuIds 非空时为对应菜单设置 meta.paused（耗材租户下该菜单已被暂停）
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus, java.util.Set<Long> pausedMenuIds);

    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     * 
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean checkMenuNameUnique(SysMenu menu);

  /**
   * 根据用户ID查询设备前端（sb_menu）菜单树信息
   *
   * @param userId 用户ID
   * @return 菜单列表
   */
  public List<SysMenu> selectSbMenuTreeByUserId(Long userId);

  /**
   * 耗材客户权限分配用菜单树（排除客户管理、客户菜单功能管理及其子节点）
   *
   * @return 下拉树结构列表
   */
  public List<TreeSelect> selectMenuTreeForHcCustomerAssign();

  /**
   * 耗材工作组（岗位）分配菜单用：仅展示客户菜单权限表 hc_customer_menu 内该客户已有的菜单
   *
   * @param tenantId 租户ID（客户ID），为空则返回空列表
   * @return 下拉树结构列表
   */
  List<TreeSelect> selectMenuTreeForPostAssign(String tenantId);

  /**
   * 将菜单 ID 扩展为包含其下全部非平台子孙（M/C/F），用于客户保存 hc_customer_menu 与业务含义「开通父即含子」一致
   */
  List<Long> expandMenuIdsWithDescendants(List<Long> menuIds);

  /**
   * 租户用户保存 sys_user_menu 时：将勾选的菜单扩展为包含所有非平台父级目录，否则 getRouters 仅从根组树时缺少父节点会导致侧栏不显示子菜单（如「收货确认」）。
   */
  List<Long> expandMenuIdsWithAncestorsForTenant(List<Long> menuIds);

  /**
   * 耗材菜单树（含 default_open_to_customer），用于批量设置默认对客户开放
   */
  List<SysMenu> selectMenuTreeForDefaultOpenBatch();

  /**
   * 批量设置耗材菜单「默认对客户开放」：先全部置否，再对 menuIds 置是（平台管理菜单始终为否）
   */
  void batchSetDefaultOpenToCustomer(List<Long> menuIds);
}
