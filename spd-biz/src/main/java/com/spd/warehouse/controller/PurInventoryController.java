package com.spd.warehouse.controller;

import com.github.pagehelper.PageInfo;
import com.spd.common.constant.HttpStatus;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.TableDataInfo;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.warehouse.vo.PurInventoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 进销存明细
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/purInventory")
public class PurInventoryController extends BaseController
{
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询进销存明细列表
     */
    @GetMapping("/listPurInventory")
    public TableDataInfo listPurInventory(StkIoBill stkIoBill)
    {
        startPage();
        List<Map<String, Object>> mapPage = stkIoBillService.selectListPurInventory(stkIoBill);
        List<Map<String, Object>> mapList = stkIoBillService.selectListPurInventory(stkIoBill);
        List<PurInventoryVo> purInventoryVoList = new ArrayList<PurInventoryVo>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try{
            for(Map<String, Object> map : mapList){
                PurInventoryVo inventoryVo = new PurInventoryVo();
                inventoryVo.setId((Long) map.get("id"));
                inventoryVo.setMaterialCode(map.get("materialCode").toString());
                inventoryVo.setMaterialName(map.get("materialName").toString());
                inventoryVo.setMaterialModel(map.get("materialModel").toString());
                inventoryVo.setMaterialQty((BigDecimal) map.get("materialQty"));
                inventoryVo.setMaterialSpeci(map.get("materialSpeci").toString());
                inventoryVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
                inventoryVo.setUnitName(map.get("unitName").toString());
                inventoryVo.setUnitPrice((BigDecimal) map.get("price"));
                inventoryVo.setWarehouseName(map.get("warehouseName").toString());
                inventoryVo.setFactoryName(map.get("factoryName").toString());
                inventoryVo.setSupplierName(map.get("supplierName").toString());
                if(map.get("departmentName") != null){
                    inventoryVo.setDepartmentName(map.get("departmentName").toString());
                }
                if(map.get("batchNo") != null){
                    inventoryVo.setBatchNo(map.get("batchNo").toString());
                }
                if(map.get("batchNumber") != null){
                    inventoryVo.setBatchNumber(map.get("batchNumber").toString());
                }
                inventoryVo.setBillNo(map.get("billNo").toString());
                inventoryVo.setBillType((Integer) map.get("billType"));
                if(map.get("billDate") != null){
                    Date billDate = formatter.parse(map.get("billDate").toString());
                    inventoryVo.setBillDate(billDate);
                }
                if(map.get("beginTime") != null){
                    Date beginTime = formatter.parse(map.get("beginTime").toString());
                    inventoryVo.setBeginDate(beginTime);
                }
                if(map.get("andTime") != null){
                    Date andTime = formatter.parse(map.get("andTime").toString());
                    inventoryVo.setEndDate(andTime);
                }
                inventoryVo.setFinanceCategoryName(map.get("financeCategoryName").toString());
                purInventoryVoList.add(inventoryVo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(mapPage);
        rspData.setTotal(new PageInfo(purInventoryVoList).getTotal());
//        return getDataTable(purInventoryVoList);
        return rspData;
    }

}
