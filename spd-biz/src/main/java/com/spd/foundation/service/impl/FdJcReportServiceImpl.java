package com.spd.foundation.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.constant.JcConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdJcPeriod;
import com.spd.foundation.domain.FdJcReport;
import com.spd.foundation.domain.FdJcType;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdJcReportMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.service.IFdJcPeriodService;
import com.spd.foundation.service.IFdJcReportService;
import com.spd.foundation.service.IFdJcSettingService;
import com.spd.foundation.service.IFdJcTypeService;

@Service
public class FdJcReportServiceImpl implements IFdJcReportService
{
    @Autowired
    private FdJcReportMapper fdJcReportMapper;

    @Autowired
    private IFdJcSettingService fdJcSettingService;

    @Autowired
    private IFdJcPeriodService fdJcPeriodService;

    @Autowired
    private IFdJcTypeService fdJcTypeService;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Override
    public FdJcReport selectFdJcReportById(Long id)
    {
        FdJcReport row = fdJcReportMapper.selectFdJcReportById(id);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    @Override
    public List<FdJcReport> selectFdJcReportList(FdJcReport query)
    {
        if (query == null)
        {
            query = new FdJcReport();
        }
        // 列表默认只看当前模式；若显式传 reportMode（如查历史）则尊重入参
        if (StringUtils.isEmpty(query.getReportMode()))
        {
            query.setReportMode(fdJcSettingService.getReportMode());
        }
        return fdJcReportMapper.selectFdJcReportList(query);
    }

    @Override
    public int saveFdJcReport(FdJcReport row)
    {
        String mode = fdJcSettingService.getReportMode();
        normalizeAndValidate(row, mode);

        FdJcReport existing = findExisting(row, mode);
        if (existing != null)
        {
            existing.setReportQty(row.getReportQty());
            existing.setRemark(row.getRemark());
            existing.setUpdateBy(SecurityUtils.getUserIdStr());
            existing.setUpdateTime(DateUtils.getNowDate());
            return fdJcReportMapper.updateFdJcReport(existing);
        }

        row.setReportMode(mode);
        row.setDelFlag(0);
        row.setCreateBy(SecurityUtils.getUserIdStr());
        row.setCreateTime(DateUtils.getNowDate());
        // PRODUCT：类型从产品带出仅作展示，报量行不落 jc_type_id，避免与 TYPE 唯一语义混淆
        if (JcConstants.MODE_PRODUCT.equals(mode))
        {
            row.setJcTypeId(null);
        }
        else
        {
            row.setMaterialId(null);
        }
        return fdJcReportMapper.insertFdJcReport(row);
    }

    @Override
    public int updateFdJcReport(FdJcReport row)
    {
        if (row.getId() == null)
        {
            throw new ServiceException("主键不能为空");
        }
        FdJcReport existing = selectFdJcReportById(row.getId());
        if (existing == null)
        {
            throw new ServiceException("报量记录不存在");
        }
        String currentMode = fdJcSettingService.getReportMode();
        if (!currentMode.equals(existing.getReportMode()))
        {
            throw new ServiceException("当前为" + modeLabel(currentMode) + "模式，不能修改另一模式的历史报量；请先切换模式或仅查看");
        }
        if (row.getReportQty() == null || row.getReportQty().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("报量数不能为空且不能为负数");
        }
        existing.setReportQty(row.getReportQty());
        existing.setRemark(row.getRemark());
        existing.setUpdateBy(SecurityUtils.getUserIdStr());
        existing.setUpdateTime(DateUtils.getNowDate());
        return fdJcReportMapper.updateFdJcReport(existing);
    }

    @Override
    public int deleteFdJcReportById(Long id)
    {
        FdJcReport existing = selectFdJcReportById(id);
        if (existing == null)
        {
            throw new ServiceException("报量记录不存在");
        }
        String currentMode = fdJcSettingService.getReportMode();
        if (!currentMode.equals(existing.getReportMode()))
        {
            throw new ServiceException("当前为" + modeLabel(currentMode) + "模式，不能删除另一模式的历史报量");
        }
        return fdJcReportMapper.deleteFdJcReportById(id, SecurityUtils.getUserIdStr());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSave(List<FdJcReport> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            throw new ServiceException("报量数据不能为空");
        }
        int n = 0;
        for (FdJcReport row : rows)
        {
            n += saveFdJcReport(row);
        }
        return n;
    }

    private FdJcReport findExisting(FdJcReport row, String mode)
    {
        if (JcConstants.MODE_PRODUCT.equals(mode))
        {
            return fdJcReportMapper.selectUniqueProduct(row.getPeriodId(), row.getMaterialId(), mode);
        }
        return fdJcReportMapper.selectUniqueType(row.getPeriodId(), row.getJcTypeId(), mode);
    }

    private void normalizeAndValidate(FdJcReport row, String mode)
    {
        if (row == null || row.getPeriodId() == null)
        {
            throw new ServiceException("请选择集采周期");
        }
        FdJcPeriod period = fdJcPeriodService.selectFdJcPeriodById(row.getPeriodId());
        if (period == null)
        {
            throw new ServiceException("集采周期不存在");
        }
        if (!"1".equals(period.getIsUse()))
        {
            throw new ServiceException("集采周期已停用，不能录入报量");
        }
        if (row.getReportQty() == null || row.getReportQty().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("报量数不能为空且不能为负数");
        }

        if (JcConstants.MODE_PRODUCT.equals(mode))
        {
            if (row.getMaterialId() == null)
            {
                throw new ServiceException("按产品报量时必须选择产品");
            }
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(row.getMaterialId());
            if (material == null)
            {
                throw new ServiceException("产品不存在");
            }
            SecurityUtils.ensureTenantAccess(material.getTenantId());
        }
        else if (JcConstants.MODE_TYPE.equals(mode))
        {
            if (row.getJcTypeId() == null)
            {
                throw new ServiceException("按类型报量时必须选择集采类型");
            }
            FdJcType type = fdJcTypeService.selectFdJcTypeById(row.getJcTypeId());
            if (type == null)
            {
                throw new ServiceException("集采类型不存在");
            }
            if (!"1".equals(type.getIsUse()))
            {
                throw new ServiceException("集采类型已停用，不能录入报量");
            }
        }
        else
        {
            throw new ServiceException("未知报量模式：" + mode);
        }
    }

    private static String modeLabel(String mode)
    {
        if (JcConstants.MODE_PRODUCT.equals(mode))
        {
            return "按产品";
        }
        if (JcConstants.MODE_TYPE.equals(mode))
        {
            return "按类型";
        }
        return mode;
    }
}
