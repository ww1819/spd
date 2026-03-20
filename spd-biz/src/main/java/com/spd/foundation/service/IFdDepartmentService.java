package com.spd.foundation.service;

import java.util.List;
import java.util.Map;

import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdDepartmentChangeLog;
import com.spd.foundation.domain.vo.FdDepartmentTreeNode;

/**
 * 科室Service接口
 *
 * @author spd
 * @date 2023-11-26
 */
public interface IFdDepartmentService
{
    /**
     * 查询科室
     *
     * @param id 科室主键
     * @return 科室
     */
    public FdDepartment selectFdDepartmentById(String id);

    /**
     * 查询科室列表
     *
     * @param fdDepartment 科室
     * @return 科室集合
     */
    public List<FdDepartment> selectFdDepartmentList(FdDepartment fdDepartment);

    /**
     * 构建科室维护左侧树：单根节点为客户显示名称，子节点为当前用户可见科室树
     *
     * @param flatList 已按租户与权限过滤的扁平科室列表
     */
    List<FdDepartmentTreeNode> buildDepartmentTreeWithCustomerRoot(List<FdDepartment> flatList);

    /**
     * 新增科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    public int insertFdDepartment(FdDepartment fdDepartment);

    /**
     * 修改科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    public int updateFdDepartment(FdDepartment fdDepartment);

//    /**
//     * 批量删除科室
//     *
//     * @param ids 需要删除的科室主键集合
//     * @return 结果
//     */
//    public int deleteFdDepartmentByIds(String[] ids);

    /**
     * 删除科室信息
     *
     * @param id 科室主键
     * @return 结果
     */
    public int deleteFdDepartmentById(String id);

    /**
     * 查询所有科室列表
     * @return
     */
    List<FdDepartment> selectdepartmenAll();

    /**
     * 根据用户ID获取科室ID列表
     *
     * @param userId 用户ID
     * @return 选中科室ID列表
     */
    public List<Long> selectDepartmenListByUserId(Long userId);

    /**
     * 根据用户ID获取科室列表
     * @param userId
     * @return
     */
    List<FdDepartment> selectUserDepartmenAll(Long userId);

    /**
     * 批量更新科室名称简码
     *
     * @param ids 科室ID列表
     */
    void updateReferred(java.util.List<Long> ids);

    /**
     * 校验科室导入数据（不落库；全部通过时 valid=true）
     *
     * @param list            解析后的行（方法内会规范化 trim 等）
     * @param isUpdateSupport 是否允许按科室编码更新已存在记录
     * @return valid、errors、insertCount、updateCount、totalRows 等
     */
    Map<String, Object> validateFdDepartmentImport(List<FdDepartment> list, Boolean isUpdateSupport);

    /**
     * 导入科室（Excel），须先校验且用户确认（confirm=true）后再调用
     *
     * @param list            解析后的行
     * @param isUpdateSupport 是否按「科室编码」更新已存在记录
     * @param operName        操作人
     * @param confirmed       必须为 true，否则拒绝导入
     * @return 结果说明
     */
    String importFdDepartment(List<FdDepartment> list, Boolean isUpdateSupport, String operName, boolean confirmed);

    /**
     * 科室字段变更记录（按时间倒序）
     */
    List<FdDepartmentChangeLog> selectDepartmentChangeLog(Long departmentId);
}
