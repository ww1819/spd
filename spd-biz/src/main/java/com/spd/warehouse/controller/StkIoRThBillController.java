package com.spd.warehouse.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.utils.StringUtils;
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

    /**
     * 查询入退货列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:inWarehouseQuery:list')")
    @GetMapping("/RTHList")
    public List<StkRTHVo> RTHList(StkIoBill stkIoBill)
    {
        List<StkRTHVo> stkRTHVoList = new ArrayList<StkRTHVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectRTHStkIoBillList(stkIoBill);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            for(Map<String, Object> map : mapList){
                StkRTHVo stkRTHVo = new StkRTHVo();
                stkRTHVo.setId((Long) map.get("id"));
                stkRTHVo.setMaterialCode(StringUtils.nvl(map.get("materialCode"),"").toString());
                stkRTHVo.setMaterialName(StringUtils.nvl(map.get("materialName"),"").toString());
                stkRTHVo.setMaterialModel(StringUtils.nvl(map.get("materialModel"),"").toString());
                stkRTHVo.setMaterialQty((BigDecimal) map.get("materialQty"));
                stkRTHVo.setMaterialSpeci(map.get("materialSpeci").toString());
                stkRTHVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
                stkRTHVo.setUnitName(map.get("unitName").toString());
                stkRTHVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
                stkRTHVo.setWarehouseName(map.get("warehouseName").toString());
                stkRTHVo.setFactoryName(map.get("factoryName").toString());
                stkRTHVo.setSupplierName(map.get("supplierName").toString());
                stkRTHVo.setBatchNo(map.get("batchNo").toString());
                stkRTHVo.setBatchNumber(map.get("batchNumber").toString());
                stkRTHVo.setBillNo(map.get("billNo").toString());
                stkRTHVo.setBillType((Integer) map.get("billType"));
                if(map.get("billDate") != null){
                    Date billDate = formatter.parse(map.get("billDate").toString());
                    stkRTHVo.setBillDate(billDate);
                }
                if(map.get("beginTime") != null){
                    Date beginTime = formatter.parse(map.get("beginTime").toString());
                    stkRTHVo.setBeginDate(beginTime);
                }
                if(map.get("andTime") != null){
                    Date andTime = formatter.parse(map.get("andTime").toString());
                    stkRTHVo.setEndDate(andTime);
                }
                stkRTHVo.setFinanceCategoryName(map.get("financeCategoryName").toString());
                stkRTHVoList.add(stkRTHVo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return stkRTHVoList;
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
            stkRTHVo.setId((Long) map.get("id"));
            stkRTHVo.setMaterialCode(map.get("materialCode").toString());
            stkRTHVo.setMaterialName(map.get("materialName").toString());
            stkRTHVo.setMaterialModel(map.get("materialModel").toString());
            stkRTHVo.setMaterialQty((BigDecimal) map.get("materialQty"));
            stkRTHVo.setMaterialSpeci(map.get("materialSpeci").toString());
            stkRTHVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
            stkRTHVo.setUnitName(map.get("unitName").toString());
            stkRTHVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
            stkRTHVo.setWarehouseName(map.get("warehouseName").toString());
            stkRTHVo.setSupplierName(map.get("supplierName").toString());
            stkRTHVo.setFactoryName(map.get("factoryName").toString());
            stkRTHVo.setBillType((Integer) map.get("billType"));
            stkRTHVoList.add(stkRTHVo);
        }
        return stkRTHVoList;
    }

    /**
     * 查询出退库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:outWarehouseQuery:list')")
    @GetMapping("/CTKList")
    public List<StkCTKVo> CTKList(StkIoBill stkIoBill)
    {
        List<StkCTKVo> stkRTHVoList = new ArrayList<StkCTKVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectCTKStkIoBillList(stkIoBill);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            for(Map<String, Object> map : mapList){
                StkCTKVo stkCTKVo = new StkCTKVo();
                stkCTKVo.setId((Long) map.get("id"));
                stkCTKVo.setMaterialCode(map.get("materialCode").toString());
                stkCTKVo.setMaterialName(map.get("materialName").toString());
                stkCTKVo.setMaterialModel(map.get("materialModel").toString());
                stkCTKVo.setMaterialQty((BigDecimal) map.get("materialQty"));
                stkCTKVo.setMaterialSpeci(map.get("materialSpeci").toString());
                stkCTKVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
                stkCTKVo.setUnitName(map.get("unitName").toString());
                stkCTKVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
                stkCTKVo.setWarehouseName(map.get("warehouseName").toString());
                stkCTKVo.setFactoryName(map.get("factoryName").toString());
                stkCTKVo.setDepartmentName(map.get("departmentName").toString());
                stkCTKVo.setBatchNo(map.get("batchNo").toString());
                stkCTKVo.setBatchNumber(map.get("batchNumber").toString());
                stkCTKVo.setBillNo(map.get("billNo").toString());
                stkCTKVo.setBillType((Integer) map.get("billType"));
                if(map.get("billDate") != null){
                    Date billDate = formatter.parse(map.get("billDate").toString());
                    stkCTKVo.setBillDate(billDate);
                }
                if(map.get("beginTime") != null){
                    Date beginTime = formatter.parse(map.get("beginTime").toString());
                    stkCTKVo.setBeginDate(beginTime);
                }
                if(map.get("andTime") != null){
                    Date andTime = formatter.parse(map.get("andTime").toString());
                    stkCTKVo.setEndDate(andTime);
                }
                stkCTKVo.setFinanceCategoryName(map.get("financeCategoryName").toString());
                stkRTHVoList.add(stkCTKVo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return stkRTHVoList;
    }


    /**
     * 查询出退库汇总列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:outWarehouseQuery:list')")
    @GetMapping("/CTKListSummary")
    public List<StkCTKVo> CTKListSummary(StkIoBill stkIoBill){
        List<StkCTKVo> stkRTHVoList = new ArrayList<StkCTKVo>();
        List<Map<String, Object>> mapList = stkIoBillService.selectCTKStkIoBillListSummary(stkIoBill);
        for(Map<String, Object> map : mapList){
            StkCTKVo stkCTKVo = new StkCTKVo();
            stkCTKVo.setId((Long) map.get("id"));
            stkCTKVo.setMaterialCode(map.get("materialCode").toString());
            stkCTKVo.setMaterialName(map.get("materialName").toString());
            stkCTKVo.setMaterialModel(map.get("materialModel").toString());
            stkCTKVo.setMaterialQty((BigDecimal) map.get("materialQty"));
            stkCTKVo.setMaterialSpeci(map.get("materialSpeci").toString());
            stkCTKVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
            stkCTKVo.setUnitName(map.get("unitName").toString());
            stkCTKVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
            stkCTKVo.setWarehouseName(map.get("warehouseName").toString());
            stkCTKVo.setDepartmentName(map.get("departmentName").toString());
            stkCTKVo.setFactoryName(map.get("factoryName").toString());
            stkCTKVo.setBillType((Integer) map.get("billType"));
            stkRTHVoList.add(stkCTKVo);
        }
        return stkRTHVoList;
    }

}
