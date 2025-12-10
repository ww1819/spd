package com.spd.gz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.service.IGzOrderService;
import com.spd.gz.service.IGzShipmentService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.http.HttpHelper;

/**
 * 高值入库Controller（兼容入库和出库）
 *
 * @author spd
 * @date 2024-06-11
 */
@RestController
@RequestMapping("/gz/order")
public class GzOrderController extends BaseController
{
    @Autowired
    private IGzOrderService gzOrderService;

    @Autowired
    private IGzShipmentService gzShipmentService;

    /**
     * 查询高值入库列表（兼容入库和出库）
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzOrder gzOrder)
    {
        // 如果 orderType 是 102（出库），查询出库表
        if (gzOrder != null && gzOrder.getOrderType() != null && gzOrder.getOrderType() == 102) {
            startPage();
            GzShipment gzShipment = convertToShipment(gzOrder);
            List<GzShipment> list = gzShipmentService.selectGzShipmentList(gzShipment);
            return getDataTable(list);
        } else {
            // 默认查询入库表
            startPage();
            List<GzOrder> list = gzOrderService.selectGzOrderList(gzOrder);
            return getDataTable(list);
        }
    }

    /**
     * 导出高值入库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:export')")
    @Log(title = "高值入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzOrder gzOrder)
    {
        List<GzOrder> list = gzOrderService.selectGzOrderList(gzOrder);
        ExcelUtil<GzOrder> util = new ExcelUtil<GzOrder>(GzOrder.class);
        util.exportExcel(response, list, "高值入库数据");
    }

    /**
     * 获取高值入库详细信息（兼容入库和出库）
     * 注意：此方法需要根据 orderType 判断，但这里只有 id，无法判断
     * 需要前端传递 orderType 参数，或者先查询入库表，如果不存在再查询出库表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id, Integer orderType)
    {
        // 如果 orderType 是 102（出库），查询出库表
        if (orderType != null && orderType == 102) {
            return success(gzShipmentService.selectGzShipmentById(id));
        } else {
            // 默认查询入库表
            GzOrder gzOrder = gzOrderService.selectGzOrderById(id);
            if (gzOrder == null && orderType == null) {
                // 如果入库表查不到，且没有指定 orderType，尝试查询出库表
                GzShipment gzShipment = gzShipmentService.selectGzShipmentById(id);
                if (gzShipment != null) {
                    return success(gzShipment);
                }
            }
            return success(gzOrder);
        }
    }

    /**
     * 新增高值入库（兼容入库和出库）
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:add')")
    @Log(title = "高值入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzOrder gzOrder, HttpServletRequest request)
    {
        // 如果 orderType 是 102（出库），保存到出库表
        if (gzOrder != null && gzOrder.getOrderType() != null && gzOrder.getOrderType() == 102) {
            // 从原始请求中读取 JSON，确保能获取到 inHospitalCode
            String originalJson = null;
            try {
                originalJson = HttpHelper.getBodyString(request);
                System.out.println("原始请求 JSON: " + originalJson);
            } catch (Exception e) {
                System.out.println("读取原始请求失败: " + e.getMessage());
            }
            GzShipment gzShipment = convertToShipment(gzOrder, originalJson);
            // 调试：打印转换后的数据
            if (gzShipment.getGzShipmentEntryList() != null) {
                for (com.spd.gz.domain.GzShipmentEntry entry : gzShipment.getGzShipmentEntryList()) {
                    System.out.println("出库明细 - materialId: " + entry.getMaterialId() + ", qty: " + entry.getQty() + ", inHospitalCode: " + entry.getInHospitalCode());
                }
            }
            return toAjax(gzShipmentService.insertGzShipment(gzShipment));
        } else {
            // 默认保存到入库表
            return toAjax(gzOrderService.insertGzOrder(gzOrder));
        }
    }

    /**
     * 修改高值入库（兼容入库和出库）
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:edit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzOrder gzOrder, HttpServletRequest request)
    {
        // 如果 orderType 是 102（出库），更新出库表
        if (gzOrder != null && gzOrder.getOrderType() != null && gzOrder.getOrderType() == 102) {
            // 从原始请求中读取 JSON，确保能获取到 inHospitalCode
            String originalJson = null;
            try {
                originalJson = HttpHelper.getBodyString(request);
                System.out.println("原始请求 JSON (edit): " + originalJson);
            } catch (Exception e) {
                System.out.println("读取原始请求失败 (edit): " + e.getMessage());
            }
            GzShipment gzShipment = convertToShipment(gzOrder, originalJson);
            // 调试：打印转换后的数据
            if (gzShipment.getGzShipmentEntryList() != null) {
                for (com.spd.gz.domain.GzShipmentEntry entry : gzShipment.getGzShipmentEntryList()) {
                    System.out.println("更新出库明细 - materialId: " + entry.getMaterialId() + ", qty: " + entry.getQty() + ", inHospitalCode: " + entry.getInHospitalCode());
                }
            }
            return toAjax(gzShipmentService.updateGzShipment(gzShipment));
        } else {
            // 默认更新入库表
            return toAjax(gzOrderService.updateGzOrder(gzOrder));
        }
    }

    /**
     * 删除高值入库（兼容入库和出库）
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:remove')")
    @Log(title = "高值入库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids, Integer orderType)
    {
        // 如果指定了 orderType 是 102（出库），删除出库表
        if (orderType != null && orderType == 102) {
            return toAjax(gzShipmentService.deleteGzShipmentById(ids));
        } else {
            // 默认删除入库表
            // 先尝试删除入库表，如果不存在，再尝试删除出库表
            GzOrder gzOrder = gzOrderService.selectGzOrderById(ids);
            if (gzOrder != null) {
                return toAjax(gzOrderService.deleteGzOrderById(ids));
            } else {
                // 如果入库表查不到，尝试删除出库表
                GzShipment gzShipment = gzShipmentService.selectGzShipmentById(ids);
                if (gzShipment != null) {
                    return toAjax(gzShipmentService.deleteGzShipmentById(ids));
                } else {
                    return error("未找到要删除的记录");
                }
            }
        }
    }

    /**
     * 审核高值入库（兼容入库和出库）
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:audit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditOrder")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        String id = json.getString("id");
        Integer orderType = json.getInteger("orderType");
        
        // 如果 orderType 是 102（出库），审核出库表
        if (orderType != null && orderType == 102) {
            int result = gzShipmentService.auditGzShipment(id);
            return toAjax(result);
        } else {
            // 默认审核入库表
            int result = gzOrderService.auditGzOrder(id);
            return toAjax(result);
        }
    }

    /**
     * 将 GzOrder 转换为 GzShipment（用于查询条件）
     */
    private GzShipment convertToShipment(GzOrder gzOrder) {
        return convertToShipment(gzOrder, null);
    }
    
    private GzShipment convertToShipment(GzOrder gzOrder, String originalJson) {
        GzShipment gzShipment = new GzShipment();
        if (gzOrder != null) {
            gzShipment.setId(gzOrder.getId());
            // 如果 orderNo 不为空，设置到 shipmentNo（用于查询）
            if (gzOrder.getOrderNo() != null) {
                gzShipment.setShipmentNo(gzOrder.getOrderNo());
            }
            gzShipment.setDepartmentId(gzOrder.getDepartmentId());
            gzShipment.setShipmentDate(gzOrder.getOrderDate());
            gzShipment.setWarehouseId(gzOrder.getWarehouseId());
            gzShipment.setShipmentStatus(gzOrder.getOrderStatus());
            gzShipment.setShipmentType(gzOrder.getOrderType());
            gzShipment.setDelFlag(gzOrder.getDelFlag());
            gzShipment.setAuditDate(gzOrder.getAuditDate());
            gzShipment.setCreateBy(gzOrder.getCreateBy());
            gzShipment.setCreateTime(gzOrder.getCreateTime());
            gzShipment.setUpdateBy(gzOrder.getUpdateBy());
            gzShipment.setUpdateTime(gzOrder.getUpdateTime());
            gzShipment.setRemark(gzOrder.getRemark());
        }

        // 从 JSON 中提取 inHospitalCode（因为 GzOrderEntry 没有这个字段）
        java.util.Map<Integer, String> inHospitalCodeMap = new java.util.HashMap<>();
        try {
            // 优先使用原始 JSON，如果没有则从对象转换
            String jsonStr = originalJson != null ? originalJson : com.alibaba.fastjson2.JSON.toJSONString(gzOrder);
            System.out.println("转换前的 JSON: " + jsonStr);
            JSONObject jsonObject = com.alibaba.fastjson2.JSON.parseObject(jsonStr);
            if (jsonObject != null && jsonObject.containsKey("gzOrderEntryList")) {
                com.alibaba.fastjson2.JSONArray entryList = jsonObject.getJSONArray("gzOrderEntryList");
                if (entryList != null) {
                    System.out.println("明细列表大小: " + entryList.size());
                    for (int i = 0; i < entryList.size(); i++) {
                        JSONObject entryJson = entryList.getJSONObject(i);
                        if (entryJson != null && entryJson.containsKey("inHospitalCode")) {
                            String inHospitalCode = entryJson.getString("inHospitalCode");
                            if (inHospitalCode != null && !inHospitalCode.trim().isEmpty()) {
                                inHospitalCodeMap.put(i, inHospitalCode);
                                System.out.println("提取院内码 - index: " + i + ", inHospitalCode: " + inHospitalCode);
                            } else {
                                System.out.println("院内码为空 - index: " + i);
                            }
                        } else {
                            System.out.println("未找到 inHospitalCode 字段 - index: " + i);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("JSON 解析错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 转换明细列表
        if (gzOrder != null && gzOrder.getGzOrderEntryList() != null) {
            List<com.spd.gz.domain.GzShipmentEntry> shipmentEntryList = new java.util.ArrayList<>();
            int index = 0;
            for (com.spd.gz.domain.GzOrderEntry orderEntry : gzOrder.getGzOrderEntryList()) {
                com.spd.gz.domain.GzShipmentEntry shipmentEntry = new com.spd.gz.domain.GzShipmentEntry();
                shipmentEntry.setId(orderEntry.getId());
                shipmentEntry.setParenId(orderEntry.getParenId());
                shipmentEntry.setMaterialId(orderEntry.getMaterialId());
                shipmentEntry.setQty(orderEntry.getQty());
                shipmentEntry.setPrice(orderEntry.getPrice());
                shipmentEntry.setAmt(orderEntry.getAmt());
                shipmentEntry.setBatchNo(orderEntry.getBatchNo());
                shipmentEntry.setBatchNumber(orderEntry.getBatchNumber());
                shipmentEntry.setBeginTime(orderEntry.getBeginTime());
                shipmentEntry.setEndTime(orderEntry.getEndTime());
                shipmentEntry.setDelFlag(orderEntry.getDelFlag());
                shipmentEntry.setRemark(orderEntry.getRemark());
                // 使用反射获取可能存在的字段（masterBarcode, secondaryBarcode, inHospitalCode）
                try {
                    // 获取 masterBarcode
                    try {
                        java.lang.reflect.Method getMethod = orderEntry.getClass().getMethod("getMasterBarcode");
                        Object value = getMethod.invoke(orderEntry);
                        if (value != null) {
                            shipmentEntry.setMasterBarcode(value.toString());
                        }
                    } catch (NoSuchMethodException e) {
                        // 忽略
                    }
                    // 获取 secondaryBarcode
                    try {
                        java.lang.reflect.Method getMethod = orderEntry.getClass().getMethod("getSecondaryBarcode");
                        Object value = getMethod.invoke(orderEntry);
                        if (value != null) {
                            shipmentEntry.setSecondaryBarcode(value.toString());
                        }
                    } catch (NoSuchMethodException e) {
                        // 忽略
                    }
                    // 获取 inHospitalCode
                    // 由于 GzOrderEntry 没有 inHospitalCode 字段，从之前解析的 Map 中获取
                    if (inHospitalCodeMap.containsKey(index)) {
                        String inHospitalCode = inHospitalCodeMap.get(index);
                        if (inHospitalCode != null && !inHospitalCode.trim().isEmpty()) {
                            shipmentEntry.setInHospitalCode(inHospitalCode);
                            System.out.println("设置院内码 - index: " + index + ", inHospitalCode: " + inHospitalCode);
                        }
                    } else {
                        System.out.println("未找到院内码 - index: " + index + ", inHospitalCodeMap: " + inHospitalCodeMap);
                    }
                } catch (Exception e) {
                    // 如果字段不存在或获取失败，忽略（前端可能没有传递该字段）
                }
                shipmentEntryList.add(shipmentEntry);
                index++;
            }
            gzShipment.setGzShipmentEntryList(shipmentEntryList);
        }
        
        return gzShipment;
    }

    /**
     * 根据院内码查询是否有未出库的出库单
     */
    @PostMapping("/checkInHospitalCode")
    public AjaxResult checkInHospitalCode(@RequestBody JSONObject json)
    {
        String inHospitalCode = json.getString("inHospitalCode");
        List<String> orderNos = gzOrderService.selectOutboundOrderNosByInHospitalCode(inHospitalCode);
        return success(orderNos);
    }
}
