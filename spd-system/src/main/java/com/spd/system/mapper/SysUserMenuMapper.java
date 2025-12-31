package com.spd.system.mapper;

import com.spd.system.domain.SysUserMenu;

import java.util.List;

/**
 * 用户与菜单关联表 数据层
 *
 * @author spd
 */
public interface SysUserMenuMapper
{
    /**
     * 通过用户ID删除用户和菜单关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserMenuByUserId(Long userId);

    /**
     * 通过菜单ID查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int countUserMenuById(Long menuId);

    /**
     * 批量删除用户和菜单关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserMenu(Long[] ids);

    /**
     * 批量新增用户菜单信息
     *
     * @param userMenuList
     * @return 结果
     */
    public int batchUserMenu(List<SysUserMenu> userMenuList);

    /**
     * 通过用户ID查询菜单ID列表
     *
     * @param userId 用户ID
     * @return 菜单ID列表
     */
    public List<Long> selectMenuListByUserId(Long userId);
}

