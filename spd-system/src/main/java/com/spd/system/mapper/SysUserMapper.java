package com.spd.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import com.spd.common.core.domain.entity.SysUser;

/**
 * 用户表 数据层
 * 
 * @author spd
 */
public interface SysUserMapper
{
    /**
     * 根据条件分页查询用户列表
     * 
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询已配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户名与客户ID查询用户（租户登录）
     * 使用注解绑定，避免仅依赖 XML 时部分运行环境未加载 mapper 片段导致 Invalid bound statement
     */
    @ResultMap("SysUserResult")
    @Select("select u.user_id, u.dept_id, u.customer_id, u.his_id, u.user_name, u.nick_name, u.email, u.avatar, u.phonenumber, u.password, u.sex, u.status, u.del_flag, u.login_ip, u.login_date, u.create_by, u.create_time, u.remark, "
            + "d.dept_id, d.parent_id, d.ancestors, d.dept_name, d.order_num, d.leader, d.status as dept_status, "
            + "r.role_id, r.role_name, r.role_key, r.role_sort, r.data_scope, r.status as role_status "
            + "from sys_user u "
            + "left join sys_dept d on u.dept_id = d.dept_id "
            + "left join sys_user_role ur on u.user_id = ur.user_id "
            + "left join sys_role r on r.role_id = ur.role_id "
            + "where u.user_name = #{userName} and u.del_flag = '0' and (u.customer_id = #{customerId} or (#{customerId} is null and u.customer_id is null))")
    SysUser selectUserByUserNameAndCustomerId(@Param("userName") String userName, @Param("customerId") String customerId);

    /**
     * 通过用户名查询用户（仅限无客户归属的平台用户）
     */
    public SysUser selectUserByUserNameNoCustomer(String userName);

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 仅查询 customer_id（按主键），用于修正 Redis 中 LoginUser 与库不一致。
     */
    String selectCustomerIdByUserId(Long userId);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户头像
     * 
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 校验用户名+客户ID是否唯一（同一客户下用户名唯一，平台用户 customerId 为空）
     *
     * @param user 用户信息（含 userName、customerId、userId 用于排除自身）
     * @return 已存在的用户，无则 null
     */
    public SysUser checkUserNameUnique(SysUser user);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public SysUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public SysUser checkEmailUnique(String email);
}
