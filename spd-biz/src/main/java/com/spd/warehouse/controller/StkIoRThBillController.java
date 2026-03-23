package com.spd.warehouse.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.PageDomain;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.core.page.TableSupport;
import com.spd.common.utils.StringUtils;
import com.github.pagehelper.PageInfo;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.warehouse.vo.StkCTKVo;
import com.spd.warehouse.vo.StkRTHVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 入库Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/warehouse/rthWarehouse")
public class StkIoRThBillController extends BaseController
{
    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;
    @Autowired
    private IFdMaterialService fdMaterialService;

    /**
     * 查询入退货列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:inWarehouseQuery:list')")
    @GetMapping("/RTHList")
    public TableDataInfo RTHList(StkIoBill stkIoBill)
    {
        // 启动分页
        startPage();
        List<StkRTHVo> stkRTHVoList = new ArrayList<StkRTHVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectRTHStkIoBillList(stkIoBill);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Integer loopCount = 0;
        try {
            for(Map<String, Object> map : mapList){

                try {  // 每条数据单独捕获异常

                    StkRTHVo stkRTHVo = new StkRTHVo();
                    Long materialId = (Long) map.get("materialId");
                    if (materialId != null){
                        FdMaterial fdMaterial = fdMaterialService.selectFdMaterialById(materialId);
                        stkRTHVo.setMaterial(fdMaterial);
                    }
                    stkRTHVo.setId((Long) map.get("id"));
                    stkRTHVo.setMaterialCode(StringUtils.nvl(map.get("materialCode"),"").toString());
                    stkRTHVo.setMaterialName(StringUtils.nvl(map.get("materialName"),"").toString());
                    stkRTHVo.setMaterialModel(StringUtils.nvl(map.get("materialModel"),"").toString());
                    stkRTHVo.setMaterialQty((BigDecimal) map.get("materialQty"));
                    stkRTHVo.setMaterialSpeci(StringUtils.nvl(map.get("materialSpeci"),"").toString());
                    stkRTHVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
                    stkRTHVo.setUnitName(StringUtils.nvl(map.get("unitName"),"").toString());
                    stkRTHVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
                    stkRTHVo.setWarehouseName(StringUtils.nvl(map.get("warehouseName"),"").toString());
                    stkRTHVo.setFactoryName(StringUtils.nvl(map.get("factoryName"),"").toString());
                    stkRTHVo.setSupplierName(StringUtils.nvl(map.get("supplierName"),"").toString());
                    stkRTHVo.setBatchNo(StringUtils.nvl(map.get("batchNo"),"").toString());
                    if (map.get("batchNumber") != null){
                        stkRTHVo.setBatchNumber(map.get("batchNumber").toString());
                    }
                    stkRTHVo.setBillNo(StringUtils.nvl(map.get("billNo"),"").toString());
                    stkRTHVo.setBillType((Integer) map.get("billType"));
                    if(map.get("billDate") != null){
                        Date billDate = formatter.parse(map.get("billDate").toString());
                        stkRTHVo.setBillDate(billDate);
                    }
                    if(map.get("beginTime") != null){
                        Date beginTime = formatter.parse(map.get("beginTime").toString());
                        stkRTHVo.setBeginDate(beginTime);
                    }
                    if(map.get("endTime") != null){
                        Date endTime = formatter.parse(map.get("endTime").toString());
                        stkRTHVo.setEndDate(endTime);
                    }
                    stkRTHVo.setFinanceCategoryName(StringUtils.nvl(map.get("financeCategoryName"),"").toString());
                    stkRTHVoList.add(stkRTHVo);
                    loopCount++;
                } catch (Exception e) {
                    logger.error("处理数据失败，map内容：{}", map, e);  // 记录异常和出错数据
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        logger.info("loopCount:" + loopCount);
        
        // 根据分页参数截取数据
        List<StkRTHVo> pageList = new ArrayList<>();
        try {
            PageDomain pageDomain = TableSupport.buildPageRequest();
            int pageNum = pageDomain.getPageNum();
            int pageSize = pageDomain.getPageSize();
            int total = stkRTHVoList.size();
            
            // 计算分页截取的开始和结束索引
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            if (startIndex < total) {
                pageList = stkRTHVoList.subList(startIndex, endIndex);
            }
        } catch (Exception e) {
            logger.error("分页处理失败", e);
            // 发生异常时返回原始列表
            pageList = stkRTHVoList;
        }
        
        return getDataTable(pageList);
    }

    /**
     * 查询入退货汇总列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:inWarehouseQuery:list')")
    @GetMapping("/listRTHSummary")
    public List<StkRTHVo> listRTHSummary(StkIoBill stkIoBill)
    {
        List<StkRTHVo> stkRTHVoList = new ArrayList<StkRTHVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectRTHStkIoBillSummaryList(stkIoBill);

        for(Map<String, Object> map : mapList){
            StkRTHVo stkRTHVo = new StkRTHVo();
            Long materialId = (Long) map.get("materialId");
            if (materialId != null){
                FdMaterial fdMaterial = fdMaterialService.selectFdMaterialById(materialId);
                stkRTHVo.setMaterial(fdMaterial);
            }
            stkRTHVo.setId((Long) map.get("id"));
            stkRTHVo.setMaterialCode(map.get("materialCode") != null ? map.get("materialCode").toString() : null);
            stkRTHVo.setMaterialName(map.get("materialName") != null ? map.get("materialName").toString() : null);
            stkRTHVo.setMaterialModel(map.get("materialModel") != null ? map.get("materialModel").toString() : null);
            stkRTHVo.setMaterialQty((BigDecimal) map.get("materialQty"));
            stkRTHVo.setMaterialSpeci(map.get("materialSpeci") != null ? map.get("materialSpeci").toString() : null);
            stkRTHVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
            stkRTHVo.setUnitName(map.get("unitName") != null ? map.get("unitName").toString() : null);
            stkRTHVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
            stkRTHVo.setWarehouseName(map.get("warehouseName") != null ? map.get("warehouseName").toString() : null);
            stkRTHVo.setSupplierName(map.get("supplierName") != null ? map.get("supplierName").toString() : null);
            stkRTHVo.setFactoryName(map.get("factoryName") != null ? map.get("factoryName").toString() : null);
            stkRTHVo.setBillType((Integer) map.get("billType"));
            stkRTHVoList.add(stkRTHVo);
        }
        return stkRTHVoList;
    }

    /** 根据明细/汇总查询结果中的产品档案字段组装 FdMaterial，供前端 material.registerNo、material.packageSpeci 等使用，避免循环查库 */
    private FdMaterial buildMaterialFromMap(Map<String, Object> map) {
        if (map == null) return null;
        FdMaterial m = new FdMaterial();
        if (map.get("materialId") != null) m.setId(((Number) map.get("materialId")).longValue());
        if (map.get("materialRegisterNo") != null) m.setRegisterNo(map.get("materialRegisterNo").toString());
        if (map.get("materialPackageSpeci") != null) m.setPackageSpeci(map.get("materialPackageSpeci").toString());
        if (map.get("materialIsWay") != null) m.setIsWay(map.get("materialIsWay").toString());
        if (map.get("materialWarehouseCategoryName") != null) {
            FdWarehouseCategory wc = new FdWarehouseCategory();
            wc.setWarehouseCategoryName(map.get("materialWarehouseCategoryName").toString());
            m.setFdWarehouseCategory(wc);
        }
        if (map.get("financeCategoryName") != null) {
            FdFinanceCategory fc = new FdFinanceCategory();
            fc.setFinanceCategoryName(map.get("financeCategoryName").toString());
            m.setFdFinanceCategory(fc);
        }
        return m;
    }

    /**
     * 查询出退库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:outWarehouseQuery:list')")
    @GetMapping("/CTKList")
    public TableDataInfo CTKList(StkIoBill stkIoBill)
    {
        // 启动分页
        startPage();
        List<StkCTKVo> stkRTHVoList = new ArrayList<StkCTKVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectCTKStkIoBillList(stkIoBill);
        long total = new PageInfo<>(mapList).getTotal();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;

        try {
            for(Map<String, Object> map : mapList){
                try {
                    StkCTKVo stkCTKVo = new StkCTKVo();
                    stkCTKVo.setMaterial(buildMaterialFromMap(map));
                    stkCTKVo.setId((Long) map.get("id"));
                    stkCTKVo.setMaterialCode(StringUtils.nvl(map.get("materialCode"), "").toString());
                    stkCTKVo.setMaterialName(StringUtils.nvl(map.get("materialName"), "").toString());
                    stkCTKVo.setMaterialModel(StringUtils.nvl(map.get("materialModel"), "").toString());
                    BigDecimal materialQty = (BigDecimal) map.get("materialQty");
                    stkCTKVo.setMaterialQty(materialQty);
                    stkCTKVo.setMaterialSpeci(StringUtils.nvl(map.get("materialSpeci"), "").toString());
                    BigDecimal materialAmt = (BigDecimal) map.get("materialAmt");
                    stkCTKVo.setMaterialAmt(materialAmt);
                    stkCTKVo.setUnitName(StringUtils.nvl(map.get("unitName"), "").toString());
                    // 如果单价为空，但有金额和数量，通过金额/数量计算单价
                    BigDecimal unitPrice = (BigDecimal) map.get("unitPrice");
                    if(unitPrice == null) {
                        if(materialAmt != null && materialQty != null && materialQty.compareTo(BigDecimal.ZERO) > 0) {
                            unitPrice = materialAmt.divide(materialQty, 2, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                    stkCTKVo.setUnitPrice(unitPrice);
                    stkCTKVo.setWarehouseName(StringUtils.nvl(map.get("warehouseName"), "").toString());
                    stkCTKVo.setFactoryName(StringUtils.nvl(map.get("factoryName"), "").toString());
                    stkCTKVo.setDepartmentName(StringUtils.nvl(map.get("departmentName"), "").toString());
                    if(map.get("supplierName") != null){
                        stkCTKVo.setSupplierName(map.get("supplierName").toString());
                    }
                    stkCTKVo.setBatchNo(StringUtils.nvl(map.get("batchNo"), "").toString());
                    stkCTKVo.setBatchNumber(StringUtils.nvl(map.get("batchNumber"), "").toString());
                    stkCTKVo.setBillNo(StringUtils.nvl(map.get("billNo"), "").toString());
                    stkCTKVo.setBillType((Integer) map.get("billType"));
                    if(map.get("billDate") != null){
                        Date billDate = formatter.parse(map.get("billDate").toString());
                        stkCTKVo.setBillDate(billDate);
                    }
                    if(map.get("beginTime") != null){
                        Date beginTime = formatter.parse(map.get("beginTime").toString());
                        stkCTKVo.setBeginDate(beginTime);
                    }
                    if(map.get("endTime") != null){
                        Date endTime = formatter.parse(map.get("endTime").toString());
                        stkCTKVo.setEndDate(endTime);
                    }
                    stkCTKVo.setFinanceCategoryName(StringUtils.nvl(map.get("financeCategoryName"), "").toString());
                    // 制单日期和制单人
                    if(map.get("createTime") != null){
                        Date createTime = formatter.parse(map.get("createTime").toString());
                        stkCTKVo.setCreateTime(createTime);
                    }
                    if(map.get("createrNickName") != null){
                        stkCTKVo.setCreaterNickName(map.get("createrNickName").toString());
                    }
                    if(map.get("createrUserName") != null){
                        stkCTKVo.setCreaterUserName(map.get("createrUserName").toString());
                    }
                    // 审核日期和审核人
                    if(map.get("auditDate") != null){
                        Date auditDate = formatter.parse(map.get("auditDate").toString());
                        stkCTKVo.setAuditDate(auditDate);
                    }
                    if(map.get("auditNickName") != null){
                        stkCTKVo.setAuditNickName(map.get("auditNickName").toString());
                    }
                    if(map.get("auditUserName") != null){
                        stkCTKVo.setAuditUserName(map.get("auditUserName").toString());
                    }
                    stkRTHVoList.add(stkCTKVo);
                    
                    // 计算当前页合计
                    if(materialQty != null) {
                        subTotalQty = subTotalQty.add(materialQty);
                    }
                    if(materialAmt != null) {
                        subTotalAmt = subTotalAmt.add(materialAmt);
                    }
                } catch (Exception e) {
                    logger.warn("CTKList 单条数据转换失败，跳过，map: {}", map, e);
                }
            }
        } catch (Exception e) {
            logger.error("CTKList 处理失败", e);
        }
        
        // mapList 已被 PageHelper 分页，stkRTHVoList 即为当前页数据，直接返回
        TotalInfo totalInfo = stkIoBillService.selectCTKStkIoBillListTotal(stkIoBill);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        
        return getDataTable(stkRTHVoList, totalInfo, total);
    }


    /**
     * 查询出退库汇总列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:outWarehouseQuery:list')")
    @GetMapping("/CTKListSummary")
    public TableDataInfo CTKListSummary(StkIoBill stkIoBill){
        // 启动分页
        startPage();
        List<StkCTKVo> stkRTHVoList = new ArrayList<StkCTKVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectCTKStkIoBillListSummary(stkIoBill);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        
        for(Map<String, Object> map : mapList){
            try {
                StkCTKVo stkCTKVo = new StkCTKVo();
                stkCTKVo.setMaterial(buildMaterialFromMap(map));
                stkCTKVo.setId((Long) map.get("id"));
                stkCTKVo.setMaterialCode(StringUtils.nvl(map.get("materialCode"), "").toString());
                stkCTKVo.setMaterialName(StringUtils.nvl(map.get("materialName"), "").toString());
                stkCTKVo.setMaterialModel(StringUtils.nvl(map.get("materialModel"), "").toString());
                BigDecimal materialQty = (BigDecimal) map.get("materialQty");
                stkCTKVo.setMaterialQty(materialQty);
                stkCTKVo.setMaterialSpeci(StringUtils.nvl(map.get("materialSpeci"), "").toString());
                BigDecimal materialAmt = (BigDecimal) map.get("materialAmt");
                stkCTKVo.setMaterialAmt(materialAmt);
                stkCTKVo.setUnitName(StringUtils.nvl(map.get("unitName"), "").toString());
                stkCTKVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
                stkCTKVo.setWarehouseName(StringUtils.nvl(map.get("warehouseName"), "").toString());
                stkCTKVo.setDepartmentName(StringUtils.nvl(map.get("departmentName"), "").toString());
                stkCTKVo.setFactoryName(StringUtils.nvl(map.get("factoryName"), "").toString());
                if(map.get("supplierName") != null){
                    stkCTKVo.setSupplierName(map.get("supplierName").toString());
                }
                stkCTKVo.setBillType((Integer) map.get("billType"));
                if(map.get("createTime") != null){
                    try {
                        Date createTime = formatter.parse(map.get("createTime").toString());
                        stkCTKVo.setCreateTime(createTime);
                    } catch (Exception e) { /* ignore parse error */ }
                }
                if(map.get("createrNickName") != null){
                    stkCTKVo.setCreaterNickName(map.get("createrNickName").toString());
                }
                if(map.get("createrUserName") != null){
                    stkCTKVo.setCreaterUserName(map.get("createrUserName").toString());
                }
                if(map.get("auditDate") != null){
                    try {
                        Date auditDate = formatter.parse(map.get("auditDate").toString());
                        stkCTKVo.setAuditDate(auditDate);
                    } catch (Exception e) { /* ignore parse error */ }
                }
                if(map.get("auditNickName") != null){
                    stkCTKVo.setAuditNickName(map.get("auditNickName").toString());
                }
                if(map.get("auditUserName") != null){
                    stkCTKVo.setAuditUserName(map.get("auditUserName").toString());
                }
                stkRTHVoList.add(stkCTKVo);
                if(materialQty != null) subTotalQty = subTotalQty.add(materialQty);
                if(materialAmt != null) subTotalAmt = subTotalAmt.add(materialAmt);
            } catch (Exception e) {
                logger.warn("CTKListSummary 单条数据转换失败，跳过，map: {}", map, e);
            }
        }
        
        // 保存总数据大小（在分页前）
        Long total = Long.valueOf(stkRTHVoList.size());
        
        // 根据分页参数截取数据
        List<StkCTKVo> pageList = new ArrayList<>();
        try {
            PageDomain pageDomain = TableSupport.buildPageRequest();
            int pageNum = pageDomain.getPageNum();
            int pageSize = pageDomain.getPageSize();
            
            // 计算分页截取的开始和结束索引
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total.intValue());
            
            if (startIndex < total.intValue()) {
                pageList = stkRTHVoList.subList(startIndex, endIndex);
            }
        } catch (Exception e) {
            logger.error("分页处理失败", e);
            pageList = stkRTHVoList;
        }
        
        // 计算总合计（需要查询所有数据）
        TotalInfo totalInfo = stkIoBillService.selectCTKStkIoBillListSummaryTotal(stkIoBill);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        
        return getDataTable(pageList, totalInfo, total);
    }

}
