package com.spd.web.controller.equipment;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.biz.domain.EquipmentPurchaseApplication;
import com.spd.biz.service.IEquipmentPurchaseApplicationService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.SecurityUtils;
import java.util.Date;

/**
 * 采购审批Controller
 * 
 * @author spd
 * @date 2024-01-15
 */
@RestController
@RequestMapping("/equipment/purchaseApproval")
public class PurchaseApprovalController extends BaseController
{
    @Autowired
    private IEquipmentPurchaseApplicationService equipmentPurchaseApplicationService;

    /**
     * 查询采购审批列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:purchaseApproval:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        startPage();
        List<EquipmentPurchaseApplication> list = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationList(equipmentPurchaseApplication);
        return getDataTable(list);
    }

    /**
     * 导出采购审批列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:purchaseApproval:export')")
    @Log(title = "采购审批", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        List<EquipmentPurchaseApplication> list = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationList(equipmentPurchaseApplication);
        ExcelUtil<EquipmentPurchaseApplication> util = new ExcelUtil<EquipmentPurchaseApplication>(EquipmentPurchaseApplication.class);
        util.exportExcel(response, list, "采购审批数据");
    }

    /**
     * 获取采购审批详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:purchaseApproval:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationById(id));
    }

    /**
     * 审批采购申请
     */
    @PreAuthorize("@ss.hasPermi('equipment:purchaseApproval:approve')")
    @Log(title = "采购审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        EquipmentPurchaseApplication application = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationById(equipmentPurchaseApplication.getId());
        if (application == null) {
            return error("采购申请不存在");
        }
        if (!"0".equals(application.getStatus())) {
            return error("该申请不是待审核状态，无法审批");
        }
        
        // 设置审批信息
        application.setStatus("1"); // 审核通过
        application.setReviewer(SecurityUtils.getUsername());
        application.setReviewDate(new Date());
        application.setReviewOpinion(equipmentPurchaseApplication.getReviewOpinion());
        
        return toAjax(equipmentPurchaseApplicationService.updateEquipmentPurchaseApplication(application));
    }

    /**
     * 拒绝采购申请
     */
    @PreAuthorize("@ss.hasPermi('equipment:purchaseApproval:reject')")
    @Log(title = "采购审批", businessType = BusinessType.UPDATE)
    @PostMapping("/reject")
    public AjaxResult reject(@RequestBody EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        EquipmentPurchaseApplication application = equipmentPurchaseApplicationService.selectEquipmentPurchaseApplicationById(equipmentPurchaseApplication.getId());
        if (application == null) {
            return error("采购申请不存在");
        }
        if (!"0".equals(application.getStatus())) {
            return error("该申请不是待审核状态，无法拒绝");
        }
        
        // 设置拒绝信息
        application.setStatus("2"); // 审核拒绝
        application.setReviewer(SecurityUtils.getUsername());
        application.setReviewDate(new Date());
        application.setReviewOpinion(equipmentPurchaseApplication.getReviewOpinion());
        
        return toAjax(equipmentPurchaseApplicationService.updateEquipmentPurchaseApplication(application));
    }
}

