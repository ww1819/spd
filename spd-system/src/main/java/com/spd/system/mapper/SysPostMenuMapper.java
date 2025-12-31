package com.spd.system.mapper;

import com.spd.system.domain.SysPostMenu;

import java.util.List;

/**
 * 工作组与菜单关联表 数据层
 *
 * @author spd
 */
public interface SysPostMenuMapper
{
    /**
     * 通过工作组ID删除工作组和菜单关联
     *
     * @param postId 工作组ID
     * @return 结果
     */
    public int deletePostMenuByPostId(Long postId);

    /**
     * 批量新增工作组菜单信息
     *
     * @param postMenuList
     * @return 结果
     */
    public int batchPostMenu(List<SysPostMenu> postMenuList);

    /**
     * 通过工作组ID查询菜单ID列表
     *
     * @param postId 工作组ID
     * @return 菜单ID列表
     */
    public List<Long> selectMenuListByPostId(Long postId);
}

