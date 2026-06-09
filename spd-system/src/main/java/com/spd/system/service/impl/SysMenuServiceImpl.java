package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.constant.Constants;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.TreeSelect;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.core.domain.entity.SysRole;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysPostMenu;
import com.spd.system.domain.SysUserMenu;
import com.spd.system.domain.dto.MenuBatchGrantBody;
import com.spd.system.domain.hc.HcCustomerMenu;
import com.spd.system.domain.vo.MenuBatchGrantExistingVo;
import com.spd.system.domain.vo.MetaVo;
import com.spd.system.domain.vo.RouterVo;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.SysMenuMapper;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysPostMenuMapper;
import com.spd.system.mapper.SysRoleMapper;
import com.spd.system.mapper.SysRoleMenuMapper;
import com.spd.system.mapper.SysUserMapper;
import com.spd.system.mapper.SysUserMenuMapper;
import com.spd.system.service.ISysMenuService;

/**
 * 菜单 业务层处理
 * 
 * @author spd
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private HcCustomerMenuMapper hcCustomerMenuMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysPostMenuMapper postMenuMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserMenuMapper userMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId)
    {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId)
    {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId))
        {
            menuList = menuMapper.selectMenuList(menu);
        }
        else
        {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId, Boolean forTenant)
    {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId, forTenant);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId)
    {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     * 
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId, Boolean forTenant)
    {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId))
        {
            menus = menuMapper.selectMenuTreeAll();
        }
        else
        {
            menus = menuMapper.selectMenuTreeByUserId(userId, forTenant);
            // 租户：sys_user_menu 仅含叶子时，若祖先目录在「扩展写库」时被 is_platform 误判中断，getRouters 无法从 parent_id=0 组树，侧栏不显示子菜单
            if (menus != null && !menus.isEmpty() && Boolean.TRUE.equals(forTenant))
            {
                menus = appendMissingAncestorMenusForHcRouter(menus);
                normalizeVisibleForTenantHcSidebar(menus);
            }
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 租户侧栏：路由 hidden 来自 menu.visible；若目录(M)被标为「隐藏」，其下子路由整枝不渲染（表现为有权限但无菜单）。
     * 耗材科室下页面(C) component 以 department/ 开头时同理。仅修正本次返回的内存对象。
     */
    private void normalizeVisibleForTenantHcSidebar(List<SysMenu> menus)
    {
        if (menus == null)
        {
            return;
        }
        for (SysMenu m : menus)
        {
            if (m == null)
            {
                continue;
            }
            if (!"1".equals(String.valueOf(m.getVisible()).trim()))
            {
                continue;
            }
            if (UserConstants.TYPE_DIR.equals(m.getMenuType()))
            {
                m.setVisible("0");
                continue;
            }
            if (UserConstants.TYPE_MENU.equals(m.getMenuType())
                    && StringUtils.isNotEmpty(m.getComponent())
                    && m.getComponent().startsWith("department/"))
            {
                m.setVisible("0");
            }
        }
    }

    /**
     * 耗材租户侧栏：沿 parent_id 向上只读补充缺失的祖先菜单行（仅用于组树，不改变 sys_user_menu 落库）。
     * 若某级目录(M)被误标 is_platform=1，expandMenuIdsWithAncestorsForTenant 会中断导致无祖先 ID，此处仍补充 M 目录以便挂载子菜单。
     */
    private List<SysMenu> appendMissingAncestorMenusForHcRouter(List<SysMenu> menus)
    {
        Map<Long, SysMenu> byId = new LinkedHashMap<>();
        for (SysMenu m : menus)
        {
            if (m != null && m.getMenuId() != null)
            {
                byId.put(m.getMenuId(), m);
            }
        }
        boolean added = true;
        while (added)
        {
            added = false;
            for (Long id : new ArrayList<>(byId.keySet()))
            {
                SysMenu node = byId.get(id);
                if (node == null)
                {
                    continue;
                }
                Long pid = node.getParentId();
                if (pid == null || pid <= 0 || byId.containsKey(pid))
                {
                    continue;
                }
                SysMenu parent = menuMapper.selectMenuById(pid);
                if (parent == null || !"0".equals(String.valueOf(parent.getStatus()).trim()))
                {
                    continue;
                }
                if ("1".equals(String.valueOf(parent.getIsPlatform()).trim())
                        && !UserConstants.TYPE_DIR.equals(parent.getMenuType()))
                {
                    continue;
                }
                byId.put(pid, parent);
                added = true;
            }
        }
        return new ArrayList<>(byId.values());
    }

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    /**
     * 构建前端路由所需要的菜单
     * 
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus)
    {
        return buildMenus(menus, null);
    }

    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus, java.util.Set<Long> pausedMenuIds)
    {
        boolean markPaused = pausedMenuIds != null && !pausedMenuIds.isEmpty();
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus)
        {
            boolean paused = markPaused && pausedMenuIds.contains(menu.getMenuId());
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            MetaVo meta = new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath());
            if (markPaused) {
                meta.setPaused(paused);
            }
            router.setMeta(meta);
            List<SysMenu> cMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
            {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus, markPaused ? pausedMenuIds : null));
            }
            else if (isMenuFrame(menu))
            {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                MetaVo childMeta = new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath());
                if (markPaused) {
                    childMeta.setPaused(paused);
                }
                children.setMeta(childMeta);
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            else if (menu.getParentId().intValue() == 0 && isInnerLink(menu))
            {
                MetaVo innerMeta = new MetaVo(menu.getMenuName(), menu.getIcon());
                if (markPaused) {
                    innerMeta.setPaused(paused);
                }
                router.setMeta(innerMeta);
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                MetaVo innerChildMeta = new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath());
                if (markPaused) {
                    innerChildMeta.setPaused(paused);
                }
                children.setMeta(innerChildMeta);
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();)
        {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId()))
            {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus)
    {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     * 
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId)
    {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId)
    {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu)
    {
        sanitizeHcMenuFlags(menu);
        return menuMapper.insertMenu(menu);
    }

    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu)
    {
        sanitizeHcMenuFlags(menu);
        return menuMapper.updateMenu(menu);
    }

    /**
     * 耗材菜单扩展字段：仅允许 0/1；平台独占菜单不可「默认对客户开放」（与功能重置 SQL 一致）。
     */
    private void sanitizeHcMenuFlags(SysMenu menu)
    {
        if (menu == null)
        {
            return;
        }
        String ip = StringUtils.trimToEmpty(menu.getIsPlatform());
        if (!"1".equals(ip))
        {
            ip = "0";
        }
        menu.setIsPlatform(ip);
        String open = StringUtils.trimToEmpty(menu.getDefaultOpenToCustomer());
        if (!"1".equals(open))
        {
            open = "0";
        }
        if ("1".equals(ip))
        {
            open = "0";
        }
        menu.setDefaultOpenToCustomer(open);
    }

    /**
     * 删除菜单管理信息
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId)
    {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu)
    {
        // 修改时排除自身；同父级下多条同名时 limit 1 可能取到本行以外的记录，必须在 SQL 中排除 menu_id
        Long excludeId = (menu.getMenuId() != null && menu.getMenuId() > 0L) ? menu.getMenuId() : null;
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId(), excludeId,
            menu.getMenuType());
        return StringUtils.isNull(info) ? UserConstants.UNIQUE : UserConstants.NOT_UNIQUE;
    }

    /**
     * 根据用户ID查询设备前端（sb_menu）菜单树
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectSbMenuTreeByUserId(Long userId)
    {
        List<SysMenu> menus;
        if (SecurityUtils.isAdmin(userId))
        {
            menus = menuMapper.selectSbMenuTreeAll();
        }
        else
        {
            menus = menuMapper.selectSbMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    @Override
    public List<TreeSelect> selectMenuTreeForHcCustomerAssign()
    {
        List<SysMenu> menus = menuMapper.selectMenuTreeForHcCustomerAssign();
        return buildMenuTreeSelect(menus);
    }

    @Override
    public List<TreeSelect> selectMenuTreeForPostAssign(String tenantId)
    {
        if (StringUtils.isEmpty(tenantId)) {
            return new ArrayList<>();
        }
        List<SysMenu> customerMenus = menuMapper.selectMenuTreeForPostAssign(tenantId);
        Set<Long> customerGrantedIds = new HashSet<>();
        if (customerMenus != null)
        {
            for (SysMenu m : customerMenus)
            {
                if (m != null && m.getMenuId() != null)
                {
                    customerGrantedIds.add(m.getMenuId());
                }
            }
        }
        List<SysMenu> menus = customerMenus != null ? new ArrayList<>(customerMenus) : new ArrayList<>();
        // 仅向上补齐目录节点用于建树，不向下展开租户未在 hc_customer_menu 登记的子功能，避免授权树出现客户未开通的菜单
        menus = appendAncestorMenusForHcAssignTree(menus);
        List<TreeSelect> tree = buildMenuTreeSelect(menus);
        return pruneAssignTreeForCustomerScope(tree, customerGrantedIds);
    }

    @Override
    public boolean isMenuUnderCustomerHcScope(String tenantId, Long menuId)
    {
        if (StringUtils.isEmpty(tenantId) || menuId == null || menuId <= 0)
        {
            return false;
        }
        Long cur = menuId;
        int guard = 0;
        while (cur != null && cur > 0 && guard++ < 200)
        {
            if (hcCustomerMenuMapper.countByTenantIdAndMenuId(tenantId, cur) > 0)
            {
                return true;
            }
            SysMenu m = menuMapper.selectMenuById(cur);
            if (m == null || m.getParentId() == null)
            {
                break;
            }
            cur = m.getParentId();
        }
        return false;
    }

    @Override
    public List<Long> filterMenuIdsUnderCustomerHcScope(String tenantId, List<Long> menuIds)
    {
        if (menuIds == null || menuIds.isEmpty())
        {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(tenantId))
        {
            return new ArrayList<>(menuIds);
        }
        Set<Long> customerGranted = new HashSet<>(hcCustomerMenuMapper.selectMenuIdsByTenantId(tenantId));
        if (customerGranted.isEmpty())
        {
            return new ArrayList<>();
        }
        Map<Long, SysMenu> menuMap = loadActiveMenuMap();
        List<Long> out = new ArrayList<>();
        for (Long id : menuIds)
        {
            if (id == null || id <= 0)
            {
                continue;
            }
            if (!isMenuIdUnderCustomerGrantedScope(id, customerGranted, menuMap))
            {
                continue;
            }
            SysMenu menu = menuMap.get(id);
            if (menu != null && "1".equals(StringUtils.trimToEmpty(menu.getIsPlatform())))
            {
                continue;
            }
            out.add(id);
        }
        return out;
    }

    /** 一次加载启用菜单的 id/parent/is_platform，供授权过滤在内存中走祖先链 */
    private Map<Long, SysMenu> loadActiveMenuMap()
    {
        SysMenu query = new SysMenu();
        query.setStatus("0");
        List<SysMenu> list = menuMapper.selectMenuList(query);
        Map<Long, SysMenu> map = new HashMap<>();
        if (list != null)
        {
            for (SysMenu m : list)
            {
                if (m != null && m.getMenuId() != null)
                {
                    map.put(m.getMenuId(), m);
                }
            }
        }
        return map;
    }

    private boolean isMenuIdUnderCustomerGrantedScope(Long menuId, Set<Long> customerGranted, Map<Long, SysMenu> menuMap)
    {
        Long cur = menuId;
        int guard = 0;
        while (cur != null && cur > 0 && guard++ < 200)
        {
            if (customerGranted.contains(cur))
            {
                return true;
            }
            SysMenu m = menuMap.get(cur);
            if (m == null || m.getParentId() == null)
            {
                break;
            }
            cur = m.getParentId();
        }
        return false;
    }

    /**
     * 用户/工作组菜单授权树：客户未开通且无下级的节点不展示；有下级则仅作目录展示且不可勾选。
     */
    private List<TreeSelect> pruneAssignTreeForCustomerScope(List<TreeSelect> nodes, Set<Long> customerGrantedIds)
    {
        if (nodes == null || nodes.isEmpty())
        {
            return new ArrayList<>();
        }
        List<TreeSelect> result = new ArrayList<>();
        for (TreeSelect node : nodes)
        {
            TreeSelect pruned = pruneAssignTreeNodeForCustomerScope(node, customerGrantedIds);
            if (pruned != null)
            {
                result.add(pruned);
            }
        }
        return result;
    }

    private TreeSelect pruneAssignTreeNodeForCustomerScope(TreeSelect node, Set<Long> customerGrantedIds)
    {
        if (node == null || node.getId() == null)
        {
            return null;
        }
        List<TreeSelect> prunedChildren = new ArrayList<>();
        if (node.getChildren() != null)
        {
            for (TreeSelect child : node.getChildren())
            {
                TreeSelect pc = pruneAssignTreeNodeForCustomerScope(child, customerGrantedIds);
                if (pc != null)
                {
                    prunedChildren.add(pc);
                }
            }
        }
        boolean granted = customerGrantedIds != null && customerGrantedIds.contains(node.getId());
        if (granted)
        {
            node.setChildren(prunedChildren.isEmpty() ? null : prunedChildren);
            node.setCheckable(Boolean.TRUE);
            return node;
        }
        if (prunedChildren.isEmpty())
        {
            return null;
        }
        node.setChildren(prunedChildren);
        node.setCheckable(Boolean.FALSE);
        return node;
    }

    @Override
    public List<Long> expandMenuIdsWithAncestorsForTenant(List<Long> menuIds)
    {
        if (menuIds == null || menuIds.isEmpty())
        {
            return new ArrayList<>();
        }
        Map<Long, SysMenu> menuMap = loadActiveMenuMap();
        Set<Long> have = new LinkedHashSet<>();
        for (Long id : menuIds)
        {
            if (id == null || id <= 0)
            {
                continue;
            }
            Long cur = id;
            while (cur != null && cur > 0)
            {
                if (!have.add(cur))
                {
                    break;
                }
                SysMenu m = menuMap.get(cur);
                if (m == null)
                {
                    break;
                }
                Long pid = m.getParentId();
                if (pid == null || pid <= 0)
                {
                    break;
                }
                SysMenu parent = menuMap.get(pid);
                // 目录(M)被误标 is_platform=1 时仍须纳入祖先链，否则租户 sys_user_menu 无父级 ID，getRouters 无法组树
                if (parent != null && "1".equals(String.valueOf(parent.getIsPlatform()).trim())
                        && !UserConstants.TYPE_DIR.equals(parent.getMenuType()))
                {
                    break;
                }
                cur = pid;
            }
        }
        return new ArrayList<>(have);
    }

    @Override
    public List<Long> expandMenuIdsWithDescendants(List<Long> menuIds)
    {
        if (menuIds == null || menuIds.isEmpty())
        {
            return new ArrayList<>();
        }
        Set<Long> have = new LinkedHashSet<>();
        for (Long id : menuIds)
        {
            if (id != null && id > 0)
            {
                have.add(id);
            }
        }
        List<Long> frontier = new ArrayList<>(have);
        while (!frontier.isEmpty())
        {
            List<SysMenu> children = menuMapper.selectMenuListChildrenOfParents(frontier);
            frontier = new ArrayList<>();
            if (children == null)
            {
                break;
            }
            for (SysMenu ch : children)
            {
                if (ch == null || ch.getMenuId() == null)
                {
                    continue;
                }
                if (have.add(ch.getMenuId()))
                {
                    frontier.add(ch.getMenuId());
                }
            }
        }
        return new ArrayList<>(have);
    }

    /**
     * 向下补齐：任意 hc_customer_menu 中存在的节点，其下所有非平台 M/C/F 子孙均纳入授权树
     */
    private List<SysMenu> appendDescendantMenusForHcAssignTree(List<SysMenu> menus)
    {
        if (menus == null || menus.isEmpty())
        {
            return menus;
        }
        Map<Long, SysMenu> byId = new LinkedHashMap<>();
        for (SysMenu m : menus)
        {
            if (m != null && m.getMenuId() != null)
            {
                byId.put(m.getMenuId(), m);
            }
        }
        List<Long> frontier = new ArrayList<>(byId.keySet());
        while (!frontier.isEmpty())
        {
            List<SysMenu> children = menuMapper.selectMenuListChildrenOfParents(frontier);
            frontier = new ArrayList<>();
            if (children == null)
            {
                break;
            }
            for (SysMenu ch : children)
            {
                if (ch == null || ch.getMenuId() == null)
                {
                    continue;
                }
                if (!byId.containsKey(ch.getMenuId()))
                {
                    byId.put(ch.getMenuId(), ch);
                    frontier.add(ch.getMenuId());
                }
            }
        }
        List<SysMenu> merged = new ArrayList<>(byId.values());
        merged.sort(Comparator
            .comparing((SysMenu x) -> x.getParentId() != null ? x.getParentId() : 0L)
            .thenComparing(x -> x.getOrderNum() != null ? x.getOrderNum() : 0));
        return merged;
    }

    /**
     * 客户可分配菜单可能未包含目录(M)节点，仅含子菜单/按钮时无法建树；向上补齐 sys_menu 中的父级（至根），便于「全功能展示、按需勾选」。
     */
    private List<SysMenu> appendAncestorMenusForHcAssignTree(List<SysMenu> menus)
    {
        if (menus == null || menus.isEmpty())
        {
            return menus;
        }
        Map<Long, SysMenu> byId = new LinkedHashMap<>();
        for (SysMenu m : menus)
        {
            if (m != null && m.getMenuId() != null)
            {
                byId.put(m.getMenuId(), m);
            }
        }
        while (true)
        {
            Set<Long> missingPids = new HashSet<>();
            for (SysMenu m : byId.values())
            {
                if (m == null)
                {
                    continue;
                }
                Long pid = m.getParentId();
                if (pid != null && pid > 0 && !byId.containsKey(pid))
                {
                    missingPids.add(pid);
                }
            }
            if (missingPids.isEmpty())
            {
                break;
            }
            List<SysMenu> parents = menuMapper.selectMenuByIds(new ArrayList<>(missingPids));
            if (parents == null || parents.isEmpty())
            {
                break;
            }
            for (SysMenu parent : parents)
            {
                // 不向上挂平台管理目录，避免在租户授权树中露出平台节点
                if (parent != null && parent.getMenuId() != null
                        && (parent.getIsPlatform() == null || !"1".equals(String.valueOf(parent.getIsPlatform()).trim())))
                {
                    byId.put(parent.getMenuId(), parent);
                }
            }
        }
        List<SysMenu> merged = new ArrayList<>(byId.values());
        merged.sort(Comparator
            .comparing((SysMenu x) -> x.getParentId() != null ? x.getParentId() : 0L)
            .thenComparing(x -> x.getOrderNum() != null ? x.getOrderNum() : 0));
        return merged;
    }

    @Override
    public List<SysMenu> selectMenuTreeForDefaultOpenBatch()
    {
        List<SysMenu> menus = menuMapper.selectMenuTreeAll();
        if (menus == null)
        {
            return new ArrayList<>();
        }
        return getChildPerms(menus, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSetDefaultOpenToCustomer(List<Long> menuIds)
    {
        String updateBy = SecurityUtils.getUserIdStr();
        menuMapper.resetAllDefaultOpenToCustomer(updateBy);
        if (menuIds != null && !menuIds.isEmpty())
        {
            menuMapper.batchSetDefaultOpenToCustomer(menuIds, updateBy);
        }
    }

    @Override
    public void markMenusDefaultOpenToCustomer(List<Long> menuIds)
    {
        if (menuIds == null || menuIds.isEmpty())
        {
            return;
        }
        menuMapper.batchSetDefaultOpenToCustomer(menuIds, SecurityUtils.getUserIdStr());
    }

    /**
     * 获取路由名称
     * 
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu)
    {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu))
        {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     * 
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu))
        {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
        {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu))
        {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     * 
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu)
    {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu))
        {
            component = menu.getComponent();
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu))
        {
            component = UserConstants.INNER_LINK;
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu))
        {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu)
    {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu)
    {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu)
    {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     * 
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu t = (SysMenu) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId)
            {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     * 
     * @param list 分类表
     * @param t 子节点
     */
    private void recursionFn(List<SysMenu> list, SysMenu t)
    {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext())
        {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t)
    {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 内链域名特殊字符替换
     * 
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path)
    {
        return StringUtils.replaceEach(path, new String[] { Constants.HTTP, Constants.HTTPS, Constants.WWW, "." },
                new String[] { "", "", "", "/" });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGrantMenusToTenants(MenuBatchGrantBody body)
    {
        if (body == null)
        {
            throw new ServiceException("请求参数不能为空");
        }
        List<Long> rawMenuIds = body.getMenuIds();
        if (rawMenuIds == null || rawMenuIds.isEmpty())
        {
            throw new ServiceException("请至少勾选一个菜单");
        }
        List<String> customerIds = body.getCustomerIds();
        if (customerIds == null || customerIds.isEmpty())
        {
            throw new ServiceException("请至少选择一个租户");
        }
        boolean grantTenant = Boolean.TRUE.equals(body.getGrantTenant());
        boolean grantAllPosts = Boolean.TRUE.equals(body.getGrantAllPosts());
        boolean grantAllUsers = Boolean.TRUE.equals(body.getGrantAllUsers());
        if (!grantTenant && !grantAllPosts && !grantAllUsers)
        {
            throw new ServiceException("请至少选择一种赋权范围");
        }
        List<Long> menuIds = normalizeBatchGrantMenuIds(rawMenuIds);
        for (Long menuId : menuIds)
        {
            SysMenu menu = menuMapper.selectMenuById(menuId);
            if (menu != null && "1".equals(menu.getIsPlatform()))
            {
                throw new ServiceException("不能将平台管理菜单分配给租户，请取消勾选平台菜单");
            }
        }
        boolean needTenantMenu = grantTenant || grantAllPosts || grantAllUsers;
        String operator = SecurityUtils.getUserIdStr();
        for (String tenantId : customerIds)
        {
            if (StringUtils.isEmpty(tenantId))
            {
                continue;
            }
            String tid = tenantId.trim();
            if (needTenantMenu)
            {
                mergeHcCustomerMenus(tid, menuIds, operator);
            }
            if (grantAllPosts)
            {
                mergeAllPostMenus(tid, menuIds);
            }
            if (grantAllUsers)
            {
                mergeAllUserMenus(tid, menuIds);
            }
        }
        if (!menuIds.isEmpty())
        {
            markMenusDefaultOpenToCustomer(menuIds);
        }
    }

    @Override
    public MenuBatchGrantExistingVo resolveBatchGrantExistingTenantMenuIds(List<String> customerIds)
    {
        MenuBatchGrantExistingVo vo = new MenuBatchGrantExistingVo();
        if (customerIds == null || customerIds.isEmpty())
        {
            return vo;
        }
        Set<Long> intersection = null;
        Set<Long> union = new LinkedHashSet<>();
        for (String tenantId : customerIds)
        {
            if (StringUtils.isEmpty(tenantId))
            {
                continue;
            }
            List<Long> ids = hcCustomerMenuMapper.selectMenuIdsByTenantId(tenantId.trim());
            Set<Long> set = new LinkedHashSet<>();
            if (ids != null)
            {
                for (Long id : ids)
                {
                    if (id != null && id > 0)
                    {
                        set.add(id);
                    }
                }
            }
            union.addAll(set);
            if (intersection == null)
            {
                intersection = new LinkedHashSet<>(set);
            }
            else
            {
                intersection.retainAll(set);
            }
        }
        if (intersection == null)
        {
            intersection = new LinkedHashSet<>();
        }
        Set<Long> partial = new LinkedHashSet<>(union);
        partial.removeAll(intersection);
        vo.setMenuIdsAll(new ArrayList<>(intersection));
        vo.setMenuIdsPartial(new ArrayList<>(partial));
        return vo;
    }

    private void mergeHcCustomerMenus(String tenantId, List<Long> menuIds, String operator)
    {
        List<HcCustomerMenu> toInsert = new ArrayList<>();
        for (Long menuId : menuIds)
        {
            if (menuId == null || menuId <= 0)
            {
                continue;
            }
            if (hcCustomerMenuMapper.countByTenantIdAndMenuId(tenantId, menuId) > 0)
            {
                continue;
            }
            HcCustomerMenu e = new HcCustomerMenu();
            e.setTenantId(tenantId);
            e.setMenuId(menuId);
            e.setStatus("0");
            e.setIsEnabled("1");
            e.setCreateBy(operator);
            toInsert.add(e);
        }
        if (!toInsert.isEmpty())
        {
            hcCustomerMenuMapper.batchInsert(toInsert);
        }
    }

    private void mergeAllPostMenus(String tenantId, List<Long> menuIds)
    {
        SysPost query = new SysPost();
        query.setTenantId(tenantId);
        List<SysPost> posts = postMapper.selectPostList(query);
        if (posts == null || posts.isEmpty())
        {
            return;
        }
        for (SysPost post : posts)
        {
            if (post == null || post.getPostId() == null)
            {
                continue;
            }
            List<Long> existing = postMenuMapper.selectMenuListByPostId(post.getPostId());
            Set<Long> have = new HashSet<>();
            if (existing != null)
            {
                have.addAll(existing);
            }
            List<SysPostMenu> toInsert = new ArrayList<>();
            for (Long menuId : menuIds)
            {
                if (menuId == null || menuId <= 0 || have.contains(menuId))
                {
                    continue;
                }
                SysPostMenu pm = new SysPostMenu();
                pm.setPostId(post.getPostId());
                pm.setMenuId(menuId);
                pm.setTenantId(tenantId);
                toInsert.add(pm);
            }
            if (!toInsert.isEmpty())
            {
                postMenuMapper.batchPostMenu(toInsert);
            }
        }
    }

    private void mergeAllUserMenus(String tenantId, List<Long> menuIds)
    {
        SysUser query = new SysUser();
        query.setCustomerId(tenantId);
        List<SysUser> users = userMapper.selectUserList(query);
        if (users == null || users.isEmpty())
        {
            return;
        }
        for (SysUser user : users)
        {
            if (user == null || user.getUserId() == null)
            {
                continue;
            }
            List<Long> existing = userMenuMapper.selectMenuListByUserId(user.getUserId());
            Set<Long> have = new HashSet<>();
            if (existing != null)
            {
                have.addAll(existing);
            }
            List<SysUserMenu> toInsert = new ArrayList<>();
            for (Long menuId : menuIds)
            {
                if (menuId == null || menuId <= 0 || have.contains(menuId))
                {
                    continue;
                }
                SysUserMenu um = new SysUserMenu();
                um.setUserId(user.getUserId());
                um.setMenuId(menuId);
                um.setTenantId(tenantId);
                toInsert.add(um);
            }
            if (!toInsert.isEmpty())
            {
                userMenuMapper.batchUserMenu(toInsert);
            }
        }
    }

    private List<Long> normalizeBatchGrantMenuIds(List<Long> rawMenuIds)
    {
        List<Long> result = new ArrayList<>();
        Set<Long> seen = new LinkedHashSet<>();
        for (Long id : rawMenuIds)
        {
            if (id != null && id > 0 && seen.add(id))
            {
                result.add(id);
            }
        }
        return result;
    }
}
