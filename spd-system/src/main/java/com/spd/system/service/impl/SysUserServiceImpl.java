package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Validator;

import com.spd.system.domain.*;
import com.spd.system.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.spd.common.annotation.DataScope;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysRole;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.bean.BeanValidators;
import com.spd.system.service.ISbUserPermissionService;
import com.spd.system.service.ISysConfigService;
import com.spd.system.service.ISysMenuService;
import com.spd.system.service.ISysUserService;

/**
 * 用户 业务层处理
 *
 * @author spd
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysUserWarehouseMapper userWarehouseMapper;

    @Autowired
    private SysUserDepartmentMapper userDepartmentMapper;

    @Autowired
    private SysUserMenuMapper userMenuMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISbUserPermissionService sbUserPermissionService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private com.spd.system.service.ISbWorkGroupService sbWorkGroupService;

    @Autowired
    protected Validator validator;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user)
    {
        String tid = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(tid))
        {
            if (user == null)
            {
                user = new SysUser();
            }
            user.setCustomerId(tid);
        }
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        String tid = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(tid))
        {
            if (user == null)
            {
                user = new SysUser();
            }
            user.setCustomerId(tid);
        }
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        String tid = SecurityUtils.resolveEffectiveTenantId(null);
        if (StringUtils.isNotEmpty(tid))
        {
            if (user == null)
            {
                user = new SysUser();
            }
            user.setCustomerId(tid);
        }
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserByUserNameAndCustomerId(String userName, String customerId)
    {
        return userMapper.selectUserByUserNameAndCustomerId(userName, customerId);
    }

    @Override
    public SysUser selectUserByUserNameNoCustomer(String userName)
    {
        return userMapper.selectUserByUserNameNoCustomer(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    @Override
    public String selectCustomerIdByUserId(Long userId)
    {
        if (userId == null)
        {
            return null;
        }
        return userMapper.selectCustomerIdByUserId(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user);
        if (StringUtils.isNotNull(info) && !userId.equals(info.getUserId()))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限（不做部门数据范围校验，仅校验：管理员放行；租户用户仅可访问同客户用户；平台用户仅校验用户存在）
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (SysUser.isAdmin(SecurityUtils.getUserId()))
        {
            return;
        }
        // 新增用户时无 userId，直接放行
        if (userId == null)
        {
            return;
        }
        SysUser target = userMapper.selectUserById(userId);
        if (target == null)
        {
            throw new ServiceException("没有权限访问用户数据！");
        }
        com.spd.common.core.domain.model.LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            throw new ServiceException("没有权限访问用户数据！");
        }
        String myCustomerId = loginUser.getUser().getCustomerId() != null ? loginUser.getUser().getCustomerId().trim() : "";
        String targetCustomerId = target.getCustomerId() != null ? target.getCustomerId().trim() : "";
        // 租户用户只能访问同一客户下的用户；平台用户（无 customerId）可访问平台用户
        if (StringUtils.isNotEmpty(myCustomerId))
        {
            if (!StringUtils.isNotEmpty(targetCustomerId) || !myCustomerId.equals(targetCustomerId))
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        // 新增角色 - 已禁用，不再自动创建角色
        // insertRole(user);
        // 新增用户仓库关联
        insertUserWarehouse(user);
        // 新增用户科室关联
        insertUserDepartment(user);
        // 租户用户：同步写入 sb_user_permission_dept、sb_user_permission_warehouse
        if (StringUtils.isNotEmpty(user.getCustomerId())) {
            sbUserPermissionService.saveUserDepts(user.getUserId(), user.getCustomerId(), user.getDepartmentIds());
            sbUserPermissionService.saveUserWarehouses(user.getUserId(), user.getCustomerId(), user.getWarehouseIds());
            // 设备系统工作组写入 sb_work_group_user（非 sys_user_post）
            if (user.getWorkGroupIds() != null && user.getWorkGroupIds().length > 0) {
                sbWorkGroupService.setUserWorkGroups(user.getUserId(), user.getCustomerId(), user.getWorkGroupIds());
            }
        }
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        log.info("更新用户信息 - userId: {}, menuIds: {}", userId, user.getMenuIds() != null ? java.util.Arrays.toString(user.getMenuIds()) : "null");
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 删除用户与仓库关联
        userWarehouseMapper.deleteUserWarehouseByUserId(userId);
        // 删除用户与科室关联
        userDepartmentMapper.deleteUserDepartmentByUserId(userId);
        // 删除用户与菜单关联：平台用户全量替换；租户用户仅在本次提交包含耗材数字菜单或清空时清理 sys_user_menu，避免设备端只改 UUID 菜单时误删耗材菜单
        if (StringUtils.isEmpty(user.getCustomerId())) {
            userMenuMapper.deleteUserMenuByUserId(userId);
        } else if (user.getMenuIds() != null
            && (user.getMenuIds().length == 0 || containsMaterialMenuId(user.getMenuIds()))) {
            userMenuMapper.deleteUserMenuByUserId(userId);
        }
        log.info("已处理用户菜单关联清理 - userId: {}", userId);

        // 新增用户与岗位管理
        insertUserPost(user);
        // 新增用户仓库关联
        insertUserWarehouse(user);
        // 新增用户科室关联
        insertUserDepartment(user);
        // 新增用户菜单关联
        insertUserMenu(user);
        // 租户用户：同步写入 sb_user_permission_dept、sb_user_permission_warehouse
        if (StringUtils.isNotEmpty(user.getCustomerId())) {
            sbUserPermissionService.saveUserDepts(user.getUserId(), user.getCustomerId(), user.getDepartmentIds());
            sbUserPermissionService.saveUserWarehouses(user.getUserId(), user.getCustomerId(), user.getWarehouseIds());
            // 设备系统工作组写入 sb_work_group_user（非 sys_user_post）
            sbWorkGroupService.setUserWorkGroups(user.getUserId(), user.getCustomerId(),
                user.getWorkGroupIds() != null ? user.getWorkGroupIds() : new String[0]);
        }
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds)
    {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar)
    {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password)
    {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增角色
     * @param user
     */
    public void insertRole(SysUser user){
        SysRole role = new SysRole();
        role.setRoleName(user.getUserName());
        role.setStatus("0");
        role.setRoleSort(2);
        role.setCreateBy(SecurityUtils.getUserIdStr());
        role.setUpdateTime(new Date());
        roleMapper.insertRole(role);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts))
        {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts)
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                String tenantForPost = user.getCustomerId();
                if (StringUtils.isEmpty(tenantForPost))
                {
                    tenantForPost = SecurityUtils.resolveEffectiveTenantId(null);
                }
                up.setTenantId(tenantForPost);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 新增用户仓库信息
     * @param user
     */
    public void insertUserWarehouse(SysUser user){
        Long[] warehouseIds = user.getWarehouseIds();
        if (StringUtils.isNotEmpty(warehouseIds))
        {
            // 新增用户与岗位管理
            List<SysUserWarehouse> list = new ArrayList<SysUserWarehouse>(warehouseIds.length);
            for (Long warehouseId : warehouseIds)
            {
                SysUserWarehouse uw = new SysUserWarehouse();
                uw.setUserId(user.getUserId());
                uw.setWarehouseId(warehouseId);
                list.add(uw);
            }
            userWarehouseMapper.batchUserWarehouse(list);
        }
    }

    /**
     * 新增用户科室信息
     * @param user
     */
    public void insertUserDepartment(SysUser user){
        Long[] departmentIds = user.getDepartmentIds();
        if (StringUtils.isNotEmpty(departmentIds))
        {
            // 新增用户与岗位管理
            List<SysUserDepartment> list = new ArrayList<SysUserDepartment>(departmentIds.length);
            for (Long departmentId : departmentIds)
            {
                SysUserDepartment ud = new SysUserDepartment();
                ud.setUserId(user.getUserId());
                ud.setDepartmentId(departmentId);
                list.add(ud);
            }
            userDepartmentMapper.batchUserDepartment(list);
        }
    }

    /**
     * 新增用户菜单信息：平台用户写入 sys_user_menu；租户用户数字 ID 写入 sys_user_menu（耗材），非数字写入 sb_user_permission_menu（设备），互不覆盖。
     */
    public void insertUserMenu(SysUser user) {
        String[] menuIds = user.getMenuIds();
        log.info("保存用户菜单权限 - userId: {}, menuIds长度: {}", user.getUserId(), menuIds != null ? menuIds.length : 0);
        if (menuIds == null) {
            return;
        }
        Long uid = user.getUserId();
        if (StringUtils.isNotEmpty(user.getCustomerId())) {
            String tenantIdForHc = StringUtils.trimToNull(user.getCustomerId());
            List<String> sbIds = new ArrayList<>();
            List<SysUserMenu> materialRows = new ArrayList<>();
            for (String raw : menuIds) {
                if (StringUtils.isEmpty(raw)) {
                    continue;
                }
                String t = raw.trim();
                try {
                    long mid = Long.parseLong(t);
                    if (mid > 0) {
                        SysUserMenu um = new SysUserMenu();
                        um.setUserId(uid);
                        um.setMenuId(mid);
                        um.setTenantId(tenantIdForHc);
                        materialRows.add(um);
                    }
                } catch (NumberFormatException e) {
                    sbIds.add(t);
                }
            }
            if (!sbIds.isEmpty()) {
                sbUserPermissionService.saveUserMenus(uid, user.getCustomerId(), sbIds.toArray(new String[0]));
            } else if (menuIds.length == 0) {
                sbUserPermissionService.saveUserMenus(uid, user.getCustomerId(), new String[0]);
            }
            if (!materialRows.isEmpty()) {
                List<Long> mids = new ArrayList<>();
                for (SysUserMenu um : materialRows) {
                    mids.add(um.getMenuId());
                }
                List<Long> expanded = menuService.expandMenuIdsWithAncestorsForTenant(mids);
                List<SysUserMenu> toSave = new ArrayList<>();
                for (Long mid : expanded) {
                    if (mid == null || mid <= 0) {
                        continue;
                    }
                    SysUserMenu um = new SysUserMenu();
                    um.setUserId(uid);
                    um.setMenuId(mid);
                    um.setTenantId(tenantIdForHc);
                    toSave.add(um);
                }
                if (!toSave.isEmpty()) {
                    userMenuMapper.batchUserMenu(toSave);
                }
            }
            return;
        }
        try {
            List<SysUserMenu> list = new ArrayList<>(menuIds.length);
            for (String menuIdStr : menuIds) {
                if (StringUtils.isEmpty(menuIdStr)) continue;
                try {
                    long menuId = Long.parseLong(menuIdStr.trim());
                    if (menuId > 0) {
                        SysUserMenu um = new SysUserMenu();
                        um.setUserId(user.getUserId());
                        um.setMenuId(menuId);
                        // 平台用户：无租户
                        um.setTenantId(null);
                        list.add(um);
                    }
                } catch (NumberFormatException e) {
                    log.debug("跳过非数字 menuId - userId: {}, menuId: {}", user.getUserId(), menuIdStr);
                }
            }
            if (!list.isEmpty()) {
                userMenuMapper.batchUserMenu(list);
                log.info("保存用户菜单权限成功 - userId: {}, 保存数量: {}", user.getUserId(), list.size());
            }
        } catch (Exception e) {
            log.error("保存用户菜单权限异常 - userId: {}, 错误: {}", user.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    private static boolean containsMaterialMenuId(String[] menuIds) {
        if (menuIds == null) {
            return false;
        }
        for (String raw : menuIds) {
            if (StringUtils.isEmpty(raw)) {
                continue;
            }
            try {
                long mid = Long.parseLong(raw.trim());
                if (mid > 0) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList)
        {
            try
            {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u))
                {
                    BeanValidators.validateWithException(validator, user);
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    String __name = StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName() : user.getUserName();
                    if (StringUtils.isNotEmpty(__name)) {
                        user.setReferredName(PinyinUtils.getPinyinInitials(__name));
                    }
                    userMapper.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(u);
                    checkUserDataScope(u.getUserId());
                    user.setUserId(u.getUserId());
                    user.setUpdateBy(operName);
                    String __name2 = StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName() : user.getUserName();
                    if (StringUtils.isNotEmpty(__name2)) {
                        user.setReferredName(PinyinUtils.getPinyinInitials(__name2));
                    }
                    userMapper.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 通过用户ID查询菜单ID列表
     *
     * @param userId 用户ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> selectMenuListByUserId(Long userId)
    {
        List<Long> menuIds = userMenuMapper.selectMenuListByUserId(userId);
        log.info("获取用户菜单权限 - userId: {}, menuIds: {}", userId, menuIds);
        return menuIds != null ? menuIds : new ArrayList<>();
    }

    /**
     * 批量更新用户名称简码（根据用户名称/昵称生成拼音首字母）
     */
    @Override
    public void updateReferred(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            SysUser user = userMapper.selectUserById(userId);
            if (user == null) {
                continue;
            }
            String name = user.getNickName();
            if (StringUtils.isEmpty(name)) {
                name = user.getUserName();
            }
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            user.setReferredName(PinyinUtils.getPinyinInitials(name));
            userMapper.updateUser(user);
        }
    }
}
