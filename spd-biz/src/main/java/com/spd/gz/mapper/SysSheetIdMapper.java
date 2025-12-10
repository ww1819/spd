package com.spd.gz.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

/**
 * 系统单据种子Mapper接口
 *
 * @author spd
 * @date 2024-12-08
 */
public interface SysSheetIdMapper
{
    /**
     * 查询序列号
     *
     * @param businessType 业务类型
     * @param sheetType 单据类型
     * @return 序列号
     */
    @Select("SELECT sheet_id FROM sys_sheet_id WHERE business_type = #{businessType} AND sheet_type = #{sheetType}")
    Long selectSheetId(@Param("businessType") String businessType, @Param("sheetType") String sheetType);

    /**
     * 更新序列号
     *
     * @param businessType 业务类型
     * @param sheetType 单据类型
     * @param sheetId 序列号
     * @return 结果
     */
    @Update("UPDATE sys_sheet_id SET sheet_id = #{sheetId} WHERE business_type = #{businessType} AND sheet_type = #{sheetType}")
    int updateSheetId(@Param("businessType") String businessType, @Param("sheetType") String sheetType, @Param("sheetId") Long sheetId);

    /**
     * 插入序列号记录
     *
     * @param businessType 业务类型
     * @param sheetType 单据类型
     * @param sheetId 序列号
     * @return 结果
     */
    @Insert("INSERT INTO sys_sheet_id(business_type, sheet_type, stock_id, dept_id, sheet_id) VALUES (#{businessType}, #{sheetType}, NULL, NULL, #{sheetId})")
    int insertSheetId(@Param("businessType") String businessType, @Param("sheetType") String sheetType, @Param("sheetId") Long sheetId);

    /**
     * 检查记录是否存在
     *
     * @param businessType 业务类型
     * @param sheetType 单据类型
     * @return 记录数
     */
    @Select("SELECT COUNT(*) FROM sys_sheet_id WHERE business_type = #{businessType} AND sheet_type = #{sheetType}")
    int countSheetId(@Param("businessType") String businessType, @Param("sheetType") String sheetType);
}

