package com.spd.system.mapper;

import com.spd.system.domain.SysPostMenu;

import java.util.List;

import org.apache.ibatis.annotations.Param;

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

    /**
     * 客户收回菜单权限时：删除该租户下所有工作组岗位对这些菜单的关联
     */
    int deleteByTenantIdAndMenuIds(@Param("tenantId") String tenantId, @Param("menuIds") List<Long> menuIds);

    /**
     * 客户收回/重置耗材菜单后：删除该租户岗位在 sys_post_menu 中已不在 hc_customer_menu 内的行
     */
    int deletePostMenusNotInHcCustomerMenus(@Param("tenantId") String tenantId);
}
