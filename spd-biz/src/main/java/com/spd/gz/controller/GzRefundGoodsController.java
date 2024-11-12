package com.spd.gz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.service.IGzRefundGoodsService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值退货Controller
 *
 * @author spd
 * @date 2024-06-11
 */
@RestController
@RequestMapping("/gz/goods")
public class GzRefundGoodsController extends BaseController
{
    @Autowired
    private IGzRefundGoodsService gzRefundGoodsService;

    /**
     * 查询高值退货列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzRefundGoods gzRefundGoods)
    {
        startPage();
        List<GzRefundGoods> list = gzRefundGoodsService.selectGzRefundGoodsList(gzRefundGoods);
        return getDataTable(list);
    }

    /**
     * 导出高值退货列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:export')")
    @Log(title = "高值退货", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzRefundGoods gzRefundGoods)
    {
        List<GzRefundGoods> list = gzRefundGoodsService.selectGzRefundGoodsList(gzRefundGoods);
        ExcelUtil<GzRefundGoods> util = new ExcelUtil<GzRefundGoods>(GzRefundGoods.class);
        util.exportExcel(response, list, "高值退货数据");
    }

    /**
     * 获取高值退货详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzRefundGoodsService.selectGzRefundGoodsById(id));
    }

    /**
     * 新增高值退货
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:add')")
    @Log(title = "高值退货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzRefundGoods gzRefundGoods)
    {
        return toAjax(gzRefundGoodsService.insertGzRefundGoods(gzRefundGoods));
    }

    /**
     * 修改高值退货
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:edit')")
    @Log(title = "高值退货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzRefundGoods gzRefundGoods)
    {
        return toAjax(gzRefundGoodsService.updateGzRefundGoods(gzRefundGoods));
    }

    /**
     * 删除高值退货
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:remove')")
    @Log(title = "高值退货", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(gzRefundGoodsService.deleteGzRefundGoodsById(ids));
    }

    /**
     * 审核高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:goodsApply:audit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditGoods")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzRefundGoodsService.auditGoods(json.getString("id"));
        return toAjax(result);
    }
}
