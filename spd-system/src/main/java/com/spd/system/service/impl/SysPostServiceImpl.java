package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.constant.UserConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysPostMenu;
import com.spd.system.domain.SysPostDepartment;
import com.spd.system.domain.SysPostWarehouse;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserPostMapper;
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
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysPostMenuMapper postMenuMapper;

    @Autowired
    private SysPostDepartmentMapper postDepartmentMapper;

    @Autowired
    private SysPostWarehouseMapper postWarehouseMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
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
     * 新增工作组菜单权限
     */
    public void insertPostMenu(SysPost post)
    {
        Long postId = post.getPostId();
        Long[] menuIds = post.getMenuIds();
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
}
