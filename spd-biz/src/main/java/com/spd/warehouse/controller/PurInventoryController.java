package com.spd.warehouse.controller;

import com.github.pagehelper.PageInfo;
import com.spd.common.constant.HttpStatus;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.TableDataInfo;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.warehouse.vo.PurInventoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    /**
     * 查询进销存明细列表
     */
    @GetMapping("/listPurInventory")
    public TableDataInfo listPurInventory(@RequestParam(required = false) String billTypeStr, StkIoBill stkIoBill)
    {
        // 处理 billTypeStr 参数：如果是逗号分隔的字符串，转换为 List 存储到 params 中
        if (billTypeStr != null && !billTypeStr.isEmpty()) {
            if (stkIoBill.getParams() == null) {
                stkIoBill.setParams(new java.util.HashMap<>());
            }
            // 将逗号分隔的字符串转换为 List<Integer>
            List<Integer> billTypeList = new ArrayList<>();
            String[] types = billTypeStr.split(",");
            for (String type : types) {
                try {
                    billTypeList.add(Integer.parseInt(type.trim()));
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
            }
            if (!billTypeList.isEmpty()) {
                stkIoBill.getParams().put("billTypeList", billTypeList);
                // 如果只有一个值，也设置到 billType
                if (billTypeList.size() == 1) {
                    stkIoBill.setBillType(billTypeList.get(0));
                } else {
                    stkIoBill.setBillType(null);
                }
            }
        }
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
                if(map.get("endTime") != null){
                    Date endTime = formatter.parse(map.get("endTime").toString());
                    inventoryVo.setEndDate(endTime);
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
        rspData.setTotal(new PageInfo<PurInventoryVo>(purInventoryVoList).getTotal());
//        return getDataTable(purInventoryVoList);
        return rspData;
    }

}
