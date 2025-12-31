package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdCategory68;

/**
 * 68分类Service接口
 *
 * @author spd
 * @date 2024-04-12
 */
public interface IFdCategory68Service
{
    /**
     * 查询68分类
     *
     * @param category68Id 68分类主键
     * @return 68分类
     */
    public FdCategory68 selectFdCategory68ByCategory68Id(Long category68Id);

    /**
     * 查询68分类列表
     *
     * @param fdCategory68 68分类
     * @return 68分类集合
     */
    public List<FdCategory68> selectFdCategory68List(FdCategory68 fdCategory68);

    /**
     * 查询68分类树形列表
     *
     * @return 68分类集合
     */
    public List<FdCategory68> selectFdCategory68Tree();

    /**
     * 新增68分类
     *
     * @param fdCategory68 68分类
     * @return 结果
     */
    public int insertFdCategory68(FdCategory68 fdCategory68);

    /**
     * 修改68分类
     *
     * @param fdCategory68 68分类
     * @return 结果
     */
    public int updateFdCategory68(FdCategory68 fdCategory68);

    /**
     * 批量删除68分类
     *
     * @param category68Ids 需要删除的68分类主键集合
     * @return 结果
     */
    public int deleteFdCategory68ByCategory68Ids(Long category68Ids);
}

