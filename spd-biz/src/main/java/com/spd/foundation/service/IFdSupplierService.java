package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdSupplier;

/**
 * 供应商Service接口
 *
 * @author spd
 * @date 2023-12-05
 */
public interface IFdSupplierService
{
    /**
     * 查询供应商
     *
     * @param id 供应商主键
     * @return 供应商
     */
    public FdSupplier selectFdSupplierById(Long id);

    /**
     * 查询供应商列表
     *
     * @param fdSupplier 供应商
     * @return 供应商集合
     */
    public List<FdSupplier> selectFdSupplierList(FdSupplier fdSupplier);

    /**
     * 新增供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    public int insertFdSupplier(FdSupplier fdSupplier);

    /**
     * 修改供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    public int updateFdSupplier(FdSupplier fdSupplier);

//    /**
//     * 批量删除供应商
//     *
//     * @param ids 需要删除的供应商主键集合
//     * @return 结果
//     */
//    public int deleteFdSupplierByIds(Long[] ids);

    /**
     * 删除供应商信息
     *
     * @param id 供应商主键
     * @return 结果
     */
    public int deleteFdSupplierById(Long id);
}