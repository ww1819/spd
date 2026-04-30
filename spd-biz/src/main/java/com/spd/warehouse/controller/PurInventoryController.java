package com.spd.warehouse.controller;

import com.github.pagehelper.PageInfo;
import com.spd.common.constant.HttpStatus;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.core.page.TableDataInfo;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        long total = new PageInfo<>(mapPage).getTotal();

        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        if (mapPage != null) {
            for (Map<String, Object> row : mapPage) {
                Object qtyObj = row.get("materialQty");
                if (qtyObj instanceof BigDecimal) {
                    subTotalQty = subTotalQty.add((BigDecimal) qtyObj);
                } else if (qtyObj != null) {
                    try {
                        subTotalQty = subTotalQty.add(new BigDecimal(qtyObj.toString()));
                    } catch (Exception ignored) { }
                }
                Object amtObj = row.get("materialAmt");
                if (amtObj instanceof BigDecimal) {
                    subTotalAmt = subTotalAmt.add((BigDecimal) amtObj);
                } else if (amtObj != null) {
                    try {
                        subTotalAmt = subTotalAmt.add(new BigDecimal(amtObj.toString()));
                    } catch (Exception ignored) { }
                }
            }
        }

        TotalInfo totalInfo = stkIoBillService.selectListPurInventoryTotal(stkIoBill);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(mapPage);
        rspData.setTotal(total);
        rspData.setTotalInfo(totalInfo);
        return rspData;
    }

}
