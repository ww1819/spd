package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.service.IFdUnitService;
import java.text.DecimalFormat;

/**
 * 单位明细Service业务层处理
 *
 * @author spd
 * @date 2024-04-07
 */
@Service
public class FdUnitServiceImpl implements IFdUnitService
{
    @Autowired
    private FdUnitMapper fdUnitMapper;

    /**
     * 查询单位明细
     *
     * @param unitId 单位明细主键
     * @return 单位明细
     */
    @Override
    public FdUnit selectFdUnitByUnitId(Long unitId)
    {
        return fdUnitMapper.selectFdUnitByUnitId(unitId);
    }

    /**
     * 查询单位明细列表
     *
     * @param fdUnit 单位明细
     * @return 单位明细
     */
    @Override
    public List<FdUnit> selectFdUnitList(FdUnit fdUnit)
    {
        return fdUnitMapper.selectFdUnitList(fdUnit);
    }

    /**
     * 新增单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    @Override
    public int insertFdUnit(FdUnit fdUnit)
    {
        // 如果单位编码为空，自动生成D开头的编码
        if (StringUtils.isEmpty(fdUnit.getUnitCode()))
        {
            fdUnit.setUnitCode(generateUnitCode());
        }
        fdUnit.setCreateTime(DateUtils.getNowDate());
        return fdUnitMapper.insertFdUnit(fdUnit);
    }

    /**
     * 自动生成单位编码（D开头+7位数字）
     *
     * @return 单位编码
     */
    private String generateUnitCode()
    {
        String maxCode = fdUnitMapper.selectMaxUnitCode();
        int nextNum = 1000024; // 默认起始编号（对应D0100024）
        
        if (StringUtils.isNotEmpty(maxCode) && maxCode.startsWith("D") && maxCode.length() > 1)
        {
            try
            {
                // 提取D后面的数字部分（去掉前导0后转换为整数）
                String numStr = maxCode.substring(1);
                int maxNum = Integer.parseInt(numStr);
                nextNum = maxNum + 1;
            }
            catch (NumberFormatException e)
            {
                // 如果解析失败，使用默认值
                nextNum = 1000024;
            }
        }
        
        // 格式化为7位数字，前面补0
        DecimalFormat df = new DecimalFormat("0000000");
        return "D" + df.format(nextNum);
    }

    /**
     * 修改单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    @Override
    public int updateFdUnit(FdUnit fdUnit)
    {
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }

    /**
     * 批量删除单位明细
     *
     * @param unitIds 需要删除的单位明细主键
     * @return 结果
     */
    @Override
    public int deleteFdUnitByUnitIds(Long unitIds)
    {
        FdUnit fdUnit = fdUnitMapper.selectFdUnitByUnitId(unitIds);
        if(fdUnit == null){
            throw new ServiceException(String.format("单位明细：%s，不存在!", unitIds));
        }
        fdUnit.setDelFlag(1);
        fdUnit.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        fdUnit.setUpdateTime(new Date());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }

//    /**
//     * 删除单位明细信息
//     *
//     * @param unitId 单位明细主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdUnitByUnitId(Long unitId)
//    {
//        return fdUnitMapper.deleteFdUnitByUnitId(unitId);
//    }
}
