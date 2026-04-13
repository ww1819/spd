package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SysUserPost;

/**
 * 用户与岗位关联表 数据层
 * 
 * @author spd
 */
public interface SysUserPostMapper
{
    /**
     * 通过用户ID删除用户和岗位关联
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserPostByUserId(Long userId);

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    public int countUserPostById(Long postId);

    /**
     * 批量删除用户和岗位关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserPost(Long[] ids);

    /**
     * 批量新增用户岗位信息
     * 
     * @param userPostList 用户角色列表
     * @return 结果
     */
    public int batchUserPost(List<SysUserPost> userPostList);

    /**
     * 回填用户-岗位关联的租户 ID（仅当 tenant_id 为空）
     */
    public int updateUserPostTenantIdIfBlank(@Param("userId") Long userId, @Param("postId") Long postId,
        @Param("tenantId") String tenantId);

    /**
     * 将该客户下所有关联指定 super 岗位的用户行的 tenant_id 回填
     */
    public int updateTenantIdIfBlankForCustomerSuperPost(@Param("tenantId") String tenantId, @Param("postId") Long postId);

    /**
     * 耗材工作组：查询关联该岗位的用户主键列表
     */
    List<Long> selectUserIdsByPostId(@Param("postId") Long postId);

    /**
     * 批量查询用户与耗材岗位（工作组）关联，用于用户列表补充 postIds
     */
    List<SysUserPost> selectUserPostsByUserIds(@Param("userIds") List<Long> userIds);
}
