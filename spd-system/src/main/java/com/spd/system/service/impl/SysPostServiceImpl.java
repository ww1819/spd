package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.constant.UserConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysPostMenu;
import com.spd.system.domain.SysPostDepartment;
import com.spd.system.domain.SysPostWarehouse;
import com.spd.system.domain.SysUserDepartment;
import com.spd.system.domain.SysUserMenu;
import com.spd.system.domain.SysUserWarehouse;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.SysMenuMapper;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserPostMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.mapper.SysUserMenuMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.mapper.SysPostMenuMapper;
import com.spd.system.mapper.SysPostDepartmentMapper;
import com.spd.system.mapper.SysPostWarehouseMapper;
import com.spd.system.service.ISysPostService;

/**
 * 岗位信息 服务层处理
 * 
 * @author spd
 */
@Service
public class SysPostServiceImpl implements ISysPostService
{
    private static final Map<Long, ISysPostService.SyncStatus> SYNC_STATUS_MAP = new ConcurrentHashMap<>();

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysUserMenuMapper userMenuMapper;

    @Autowired
    private SysUserDepartmentMapper userDepartmentMapper;

    @Autowired
    private SysUserWarehouseMapper userWarehouseMapper;

    @Autowired
    private SysPostMenuMapper postMenuMapper;

    @Autowired
    private SysPostDepartmentMapper postDepartmentMapper;

    @Autowired
    private SysPostWarehouseMapper postWarehouseMapper;

    @Autowired
    private HcCustomerMenuMapper hcCustomerMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            if (post == null)
            {
                post = new SysPost();
            }
            post.setTenantId(SecurityUtils.getCustomerId());
        }
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        if (StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            SysPost q = new SysPost();
            q.setTenantId(SecurityUtils.getCustomerId());
            return postMapper.selectPostList(q);
        }
        return postMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException("该工作组内有用户不能删除");
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        if (StringUtils.isEmpty(post.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            post.setTenantId(SecurityUtils.getCustomerId());
        }
        return postMapper.insertPost(post);
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updatePost(SysPost post)
    {
        int result = postMapper.updatePost(post);
        // 保存权限信息
        if (result > 0)
        {
            // 保存菜单权限
            insertPostMenu(post);
            // 保存科室权限
            insertPostDepartment(post);
            // 保存仓库权限
            insertPostWarehouse(post);
        }
        return result;
    }

    /**
     * 新增工作组菜单权限（仅允许客户菜单权限表 hc_customer_menu 内该客户已有的菜单）
     */
    public void insertPostMenu(SysPost post)
    {
        Long postId = post.getPostId();
        Long[] menuIds = post.getMenuIds();
        String tenantId = post.getTenantId();
        if (StringUtils.isEmpty(tenantId))
        {
            tenantId = SecurityUtils.getCustomerId();
        }
        if (StringUtils.isEmpty(tenantId) && postId != null)
        {
            SysPost db = postMapper.selectPostById(postId);
            if (db != null)
            {
                tenantId = db.getTenantId();
            }
        }
        if (StringUtils.isNotNull(menuIds) && menuIds.length > 0 && StringUtils.isNotEmpty(tenantId))
        {
            for (Long menuId : menuIds)
            {
                if (menuId == null || menuId <= 0) continue;
                if (!isMenuUnderCustomerHcScope(tenantId, menuId))
                {
                    throw new ServiceException("菜单权限必须在客户菜单权限范围内，请从客户已分配菜单中选择");
                }
                SysMenu menu = sysMenuMapper.selectMenuById(menuId);
                if (menu != null && "1".equals(menu.getIsPlatform()))
                {
                    throw new ServiceException("不能将平台管理菜单分配给工作组，请从可分配菜单中选择");
                }
            }
        }
        if (StringUtils.isNotNull(menuIds))
        {
            // 删除原有权限
            postMenuMapper.deletePostMenuByPostId(postId);
            // 新增权限
            if (menuIds.length > 0)
            {
                List<SysPostMenu> list = new ArrayList<SysPostMenu>(menuIds.length);
                for (Long menuId : menuIds)
                {
                    if (menuId != null && menuId > 0)
                    {
                        SysPostMenu pm = new SysPostMenu();
                        pm.setPostId(postId);
                        pm.setMenuId(menuId);
                        if (StringUtils.isNotEmpty(tenantId)) {
                            pm.setTenantId(tenantId);
                        }
                        list.add(pm);
                    }
                }
                if (list.size() > 0)
                {
                    postMenuMapper.batchPostMenu(list);
                }
            }
        }
    }

    /**
     * 新增工作组科室权限
     */
    public void insertPostDepartment(SysPost post)
    {
        Long postId = post.getPostId();
        Long[] departmentIds = post.getDepartmentIds();
        if (StringUtils.isNotNull(departmentIds))
        {
            // 删除原有权限
            postDepartmentMapper.deletePostDepartmentByPostId(postId);
            // 新增权限
            if (departmentIds.length > 0)
            {
                List<SysPostDepartment> list = new ArrayList<SysPostDepartment>(departmentIds.length);
                for (Long departmentId : departmentIds)
                {
                    if (departmentId != null && departmentId > 0)
                    {
                        SysPostDepartment pd = new SysPostDepartment();
                        pd.setPostId(postId);
                        pd.setDepartmentId(departmentId);
                        if (StringUtils.isNotEmpty(post.getTenantId())) {
                            pd.setTenantId(post.getTenantId());
                        }
                        list.add(pd);
                    }
                }
                if (list.size() > 0)
                {
                    postDepartmentMapper.batchPostDepartment(list);
                }
            }
        }
    }

    /**
     * 新增工作组仓库权限
     */
    public void insertPostWarehouse(SysPost post)
    {
        Long postId = post.getPostId();
        Long[] warehouseIds = post.getWarehouseIds();
        if (StringUtils.isNotNull(warehouseIds))
        {
            // 删除原有权限
            postWarehouseMapper.deletePostWarehouseByPostId(postId);
            // 新增权限
            if (warehouseIds.length > 0)
            {
                List<SysPostWarehouse> list = new ArrayList<SysPostWarehouse>(warehouseIds.length);
                for (Long warehouseId : warehouseIds)
                {
                    if (warehouseId != null && warehouseId > 0)
                    {
                        SysPostWarehouse pw = new SysPostWarehouse();
                        pw.setPostId(postId);
                        pw.setWarehouseId(warehouseId);
                        if (StringUtils.isNotEmpty(post.getTenantId())) {
                            pw.setTenantId(post.getTenantId());
                        }
                        list.add(pw);
                    }
                }
                if (list.size() > 0)
                {
                    postWarehouseMapper.batchPostWarehouse(list);
                }
            }
        }
    }

    /**
     * 通过工作组ID查询菜单ID列表
     *
     * @param postId 工作组ID
     * @return 菜单ID列表
     */
    public List<Long> selectMenuListByPostId(Long postId)
    {
        return postMenuMapper.selectMenuListByPostId(postId);
    }

    /**
     * 通过工作组ID查询科室ID列表
     *
     * @param postId 工作组ID
     * @return 科室ID列表
     */
    public List<Long> selectDepartmentListByPostId(Long postId)
    {
        return postDepartmentMapper.selectDepartmentListByPostId(postId);
    }

    /**
     * 通过工作组ID查询仓库ID列表
     *
     * @param postId 工作组ID
     * @return 仓库ID列表
     */
    public List<Long> selectWarehouseListByPostId(Long postId)
    {
        return postWarehouseMapper.selectWarehouseListByPostId(postId);
    }

    /**
     * 菜单是否在客户耗材权限范围内：自身在 hc_customer_menu，或任一祖先在 hc_customer_menu（客户只勾父目录「科室收货」时子页「收货确认」未单独落表也可分配工作组）
     */
    private boolean isMenuUnderCustomerHcScope(String tenantId, Long menuId)
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
            SysMenu m = sysMenuMapper.selectMenuById(cur);
            if (m == null || m.getParentId() == null)
            {
                break;
            }
            cur = m.getParentId();
        }
        return false;
    }

    @Override
    public List<Long> selectUserIdsByPostId(Long postId)
    {
        if (postId == null)
        {
            return new ArrayList<>();
        }
        List<Long> ids = userPostMapper.selectUserIdsByPostId(postId);
        return ids != null ? ids : new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncMenuToPostUsers(Long postId, String syncMode)
    {
        if (postId == null)
        {
            return 0;
        }
        List<Long> postMenuIds = postMenuMapper.selectMenuListByPostId(postId);
        if (postMenuIds == null || postMenuIds.isEmpty())
        {
            return 0;
        }
        SysPost post = postMapper.selectPostById(postId);
        String tenantId = post != null ? post.getTenantId() : SecurityUtils.getCustomerId();
        List<Long> userIds = selectUserIdsByPostId(postId);
        if (userIds == null || userIds.isEmpty())
        {
            return 0;
        }
        int affected = 0;
        boolean copyMode = "copy".equalsIgnoreCase(syncMode);
        for (Long userId : userIds)
        {
            if (copyMode)
            {
                userMenuMapper.deleteUserMenuByUserId(userId, tenantId);
            }
            List<Long> userMenuIds = userMenuMapper.selectMenuListByUserId(userId);
            Set<Long> exists = new HashSet<>(userMenuIds == null ? new ArrayList<>() : userMenuIds);
            List<SysUserMenu> toInsert = new ArrayList<>();
            for (Long menuId : postMenuIds)
            {
                if (menuId != null && menuId > 0 && !exists.contains(menuId))
                {
                    SysUserMenu userMenu = new SysUserMenu();
                    userMenu.setUserId(userId);
                    userMenu.setMenuId(menuId);
                    userMenu.setTenantId(tenantId);
                    toInsert.add(userMenu);
                    exists.add(menuId);
                }
            }
            if (!toInsert.isEmpty())
            {
                affected += userMenuMapper.batchUserMenu(toInsert);
            }
        }
        return affected;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncDepartmentToPostUsers(Long postId, String syncMode)
    {
        if (postId == null)
        {
            return 0;
        }
        List<Long> postDepartmentIds = postDepartmentMapper.selectDepartmentListByPostId(postId);
        if (postDepartmentIds == null || postDepartmentIds.isEmpty())
        {
            return 0;
        }
        List<Long> userIds = selectUserIdsByPostId(postId);
        if (userIds == null || userIds.isEmpty())
        {
            return 0;
        }
        int affected = 0;
        boolean copyMode = "copy".equalsIgnoreCase(syncMode);
        for (Long userId : userIds)
        {
            if (copyMode)
            {
                userDepartmentMapper.deleteUserDepartmentByUserId(userId);
            }
            List<Long> userDepartmentIds = userDepartmentMapper.selectDepartmentIdsByUserId(userId);
            Set<Long> exists = new HashSet<>(userDepartmentIds == null ? new ArrayList<>() : userDepartmentIds);
            List<SysUserDepartment> toInsert = new ArrayList<>();
            for (Long departmentId : postDepartmentIds)
            {
                if (departmentId != null && departmentId > 0 && !exists.contains(departmentId))
                {
                    SysUserDepartment userDepartment = new SysUserDepartment();
                    userDepartment.setUserId(userId);
                    userDepartment.setDepartmentId(departmentId);
                    userDepartment.setStatus(0);
                    toInsert.add(userDepartment);
                    exists.add(departmentId);
                }
            }
            if (!toInsert.isEmpty())
            {
                affected += userDepartmentMapper.batchUserDepartment(toInsert);
            }
        }
        return affected;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncWarehouseToPostUsers(Long postId, String syncMode)
    {
        if (postId == null)
        {
            return 0;
        }
        List<Long> postWarehouseIds = postWarehouseMapper.selectWarehouseListByPostId(postId);
        if (postWarehouseIds == null || postWarehouseIds.isEmpty())
        {
            return 0;
        }
        List<Long> userIds = selectUserIdsByPostId(postId);
        if (userIds == null || userIds.isEmpty())
        {
            return 0;
        }
        int affected = 0;
        boolean copyMode = "copy".equalsIgnoreCase(syncMode);
        for (Long userId : userIds)
        {
            if (copyMode)
            {
                userWarehouseMapper.deleteUserWarehouseByUserId(userId);
            }
            List<Long> userWarehouseIds = userWarehouseMapper.selectWarehouseIdsByUserId(userId);
            Set<Long> exists = new HashSet<>(userWarehouseIds == null ? new ArrayList<>() : userWarehouseIds);
            List<SysUserWarehouse> toInsert = new ArrayList<>();
            for (Long warehouseId : postWarehouseIds)
            {
                if (warehouseId != null && warehouseId > 0 && !exists.contains(warehouseId))
                {
                    SysUserWarehouse userWarehouse = new SysUserWarehouse();
                    userWarehouse.setUserId(userId);
                    userWarehouse.setWarehouseId(warehouseId);
                    userWarehouse.setStatus(0);
                    toInsert.add(userWarehouse);
                    exists.add(warehouseId);
                }
            }
            if (!toInsert.isEmpty())
            {
                affected += userWarehouseMapper.batchUserWarehouse(toInsert);
            }
        }
        return affected;
    }

    @Override
    public ISysPostService.SyncStatus getMenuSyncStatus(Long postId)
    {
        ISysPostService.SyncStatus status = SYNC_STATUS_MAP.get(postId);
        if (status == null)
        {
            status = new ISysPostService.SyncStatus();
            status.setPostId(postId);
            status.setStatus("IDLE");
            status.setMessage("未查询到同步任务");
            status.setAffected(0);
            status.setUpdateTime(System.currentTimeMillis());
        }
        return status;
    }

    public static Map<Long, ISysPostService.SyncStatus> getSyncStatusMap()
    {
        return SYNC_STATUS_MAP;
    }
}
