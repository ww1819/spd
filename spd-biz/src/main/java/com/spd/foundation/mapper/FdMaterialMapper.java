package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 耗材产品Mapper接口
 *
 * @author spd
 * @date 2023-12-23
 */
@Mapper
@Repository
public interface FdMaterialMapper
{
    /**
     * 查询耗材产品
     *
     * @param code 耗材产品编码
     * @return 耗材产品
     */

    public FdMaterial selectFdMaterialByCode(String code);
    /**
     * 查询耗材产品
     *
     * @param id 耗材产品主键
     * @return 耗材产品
     */
    public FdMaterial selectFdMaterialById(Long id);

    /**
     * 查询耗材产品列表
     *
     * @param fdMaterial 耗材产品
     * @return 耗材产品集合
     */
    public List<FdMaterial> selectFdMaterialList(FdMaterial fdMaterial);

    /**
     * 新增耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    public int insertFdMaterial(FdMaterial fdMaterial);

    /**
     * 修改耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    public int updateFdMaterial(FdMaterial fdMaterial);

    /**
     * 删除耗材产品
     *
     * @param id 耗材产品主键
     * @return 结果
     */
    public int deleteFdMaterialById(Long id);

    /**
     * 批量删除耗材产品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFdMaterialByIds(Long[] ids);
}
