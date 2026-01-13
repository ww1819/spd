package com.spd.gz.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import java.math.BigDecimal;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.mapper.GzTraceabilityMapper;
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.domain.GzDepInventory;

/**
 * 高值追溯单Service业务层处理
 *
 * @author spd
 * @date 2025-01-01
 */
@Service
public class GzTraceabilityServiceImpl implements IGzTraceabilityService
{
    @Autowired
    private GzTraceabilityMapper gzTraceabilityMapper;
    
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    /**
     * 查询高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 高值追溯单
     */
    @Override
    public GzTraceability selectGzTraceabilityById(Long id)
    {
        GzTraceability traceability = gzTraceabilityMapper.selectGzTraceabilityById(id);
        // 明细列表已通过Mapper的collection自动加载
        return traceability;
    }

    /**
     * 查询高值追溯单列表
     *
     * @param gzTraceability 高值追溯单
     * @return 高值追溯单
     */
    @Override
    public List<GzTraceability> selectGzTraceabilityList(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectGzTraceabilityList(gzTraceability);
    }

    /**
     * 新增高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzTraceability(GzTraceability gzTraceability)
    {
        // 生成追溯单号
        if (StringUtils.isEmpty(gzTraceability.getTraceNo())) {
            gzTraceability.setTraceNo(generateTraceNo());
        }
        
        gzTraceability.setOrderStatus(1); // 默认未审核状态
        gzTraceability.setDelFlag("0");
        gzTraceability.setCreateTime(DateUtils.getNowDate());
        gzTraceability.setCreateBy(SecurityUtils.getUsername());
        
        int rows = gzTraceabilityMapper.insertGzTraceability(gzTraceability);
        insertGzTraceabilityEntry(gzTraceability);
        // 扣减科室库存
        deductDepartmentInventory(gzTraceability);
        return rows;
    }

    /**
     * 修改高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzTraceability(GzTraceability gzTraceability)
    {
        // 先恢复旧的明细占用的库存
        restoreDepartmentInventory(gzTraceability.getId());
        
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUsername());
        gzTraceabilityMapper.deleteGzTraceabilityEntryByParentId(gzTraceability.getId());
        insertGzTraceabilityEntry(gzTraceability);
        
        // 扣减新的明细占用的库存
        deductDepartmentInventory(gzTraceability);
        
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /**
     * 批量删除高值追溯单
     *
     * @param ids 需要删除的高值追溯单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGzTraceabilityByIds(Long[] ids)
    {
        // 恢复库存
        for (Long id : ids) {
            restoreDepartmentInventory(id);
        }
        return gzTraceabilityMapper.deleteGzTraceabilityByIds(ids);
    }

    /**
     * 删除高值追溯单信息
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGzTraceabilityById(Long id)
    {
        // 恢复库存
        restoreDepartmentInventory(id);
        return gzTraceabilityMapper.deleteGzTraceabilityById(id);
    }

    /**
     * 审核高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    public int auditGzTraceability(Long id)
    {
        GzTraceability gzTraceability = new GzTraceability();
        gzTraceability.setId(id);
        gzTraceability.setOrderStatus(2); // 已审核
        gzTraceability.setAuditDate(DateUtils.getNowDate());
        gzTraceability.setAuditBy(SecurityUtils.getUsername());
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUsername());
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /**
     * 反审核高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    public int unauditGzTraceability(Long id)
    {
        GzTraceability gzTraceability = new GzTraceability();
        gzTraceability.setId(id);
        gzTraceability.setOrderStatus(1); // 未审核
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUsername());
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /**
     * 新增追溯单明细信息
     *
     * @param gzTraceability 追溯单对象
     */
    public void insertGzTraceabilityEntry(GzTraceability gzTraceability)
    {
        List<GzTraceabilityEntry> traceabilityEntryList = gzTraceability.getTraceabilityEntryList();
        Long id = gzTraceability.getId();
        if (StringUtils.isNotNull(traceabilityEntryList))
        {
            List<GzTraceabilityEntry> list = new ArrayList<GzTraceabilityEntry>();
            for (GzTraceabilityEntry entry : traceabilityEntryList)
            {
                entry.setParentId(id);
                entry.setDelFlag("0");
                entry.setCreateTime(DateUtils.getNowDate());
                entry.setCreateBy(SecurityUtils.getUsername());
                list.add(entry);
            }
            if (list.size() > 0)
            {
                gzTraceabilityMapper.batchGzTraceabilityEntry(list);
            }
        }
    }

    /**
     * 生成追溯单号
     * 格式：GZ-01 + 当前日期（yyyyMMd格式，如2026011） + -01，然后自动叠加
     * 例如：GZ-012026011-01, GZ-012026011-02, GZ-012026011-03...
     *
     * @return 追溯单号
     */
    private String generateTraceNo() {
        String prefix = "GZ-01";
        // 获取当前日期，格式为yyyyMMd（去掉最后一位）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateFull = sdf.format(new Date());
        String date = dateFull.substring(0, 7); // 取前7位，如2026011
        
        // 查询当天最大的单号
        String maxTraceNo = gzTraceabilityMapper.selectMaxTraceNo(prefix, date);
        
        int sequence = 1;
        if (StringUtils.isNotEmpty(maxTraceNo)) {
            // 提取序号部分，格式：GZ-01 + 日期 + -序号
            String expectedPrefix = prefix + date + "-";
            if (maxTraceNo.startsWith(expectedPrefix)) {
                String seqStr = maxTraceNo.substring(expectedPrefix.length());
                try {
                    sequence = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }
        
        // 格式化序号为两位数字
        String seqStr = String.format("%02d", sequence);
        return prefix + date + "-" + seqStr;
    }
    
    /**
     * 扣减科室库存
     * 
     * @param gzTraceability 追溯单对象
     */
    private void deductDepartmentInventory(GzTraceability gzTraceability) {
        if (gzTraceability.getTraceabilityEntryList() == null || gzTraceability.getTraceabilityEntryList().isEmpty()) {
            return;
        }
        
        Long execDeptId = gzTraceability.getExecDeptId();
        if (execDeptId == null) {
            return;
        }
        
        for (GzTraceabilityEntry entry : gzTraceability.getTraceabilityEntryList()) {
            if (entry.getInventoryId() != null) {
                // 根据inventoryId扣减库存
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal newQty = inventory.getQty().subtract(entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE);
                    if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                        newQty = BigDecimal.ZERO;
                    }
                    inventory.setQty(newQty);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            } else if (entry.getInHospitalCode() != null && !entry.getInHospitalCode().trim().isEmpty()) {
                // 如果没有inventoryId，根据院内码和执行科室查找库存（不过滤数量为0的记录）
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(entry.getInHospitalCode(), execDeptId);
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal newQty = inventory.getQty().subtract(entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE);
                    if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                        newQty = BigDecimal.ZERO;
                    }
                    inventory.setQty(newQty);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            }
        }
    }
    
    /**
     * 查询追溯单明细列表（用于使用追溯明细表）
     *
     * @param gzTraceability 查询条件
     * @return 追溯单明细集合
     */
    @Override
    public List<GzTraceabilityEntry> selectTraceabilityEntryList(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilityEntryList(gzTraceability);
    }

    /**
     * 恢复科室库存（修改追溯单时，先恢复旧的明细占用的库存）
     *
     * @param traceabilityId 追溯单ID
     */
    private void restoreDepartmentInventory(Long traceabilityId) {
        // 查询旧的明细数据
        GzTraceability oldTraceability = gzTraceabilityMapper.selectGzTraceabilityById(traceabilityId);
        if (oldTraceability == null || oldTraceability.getTraceabilityEntryList() == null || oldTraceability.getTraceabilityEntryList().isEmpty()) {
            return;
        }
        
        Long execDeptId = oldTraceability.getExecDeptId();
        if (execDeptId == null) {
            return;
        }
        
        for (GzTraceabilityEntry entry : oldTraceability.getTraceabilityEntryList()) {
            if (entry.getInventoryId() != null) {
                // 根据inventoryId恢复库存
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal newQty = inventory.getQty().add(entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE);
                    inventory.setQty(newQty);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            } else if (entry.getInHospitalCode() != null && !entry.getInHospitalCode().trim().isEmpty()) {
                // 如果没有inventoryId，根据院内码和执行科室查找库存（不过滤数量为0的记录）
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(entry.getInHospitalCode(), execDeptId);
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal newQty = inventory.getQty().add(entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE);
                    inventory.setQty(newQty);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            }
        }
    }
}
