package com.spd.foundation.controller;

import java.util.List;

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
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.domain.SbCustomerCategory68Log;
import com.spd.foundation.service.ISbCustomerCategory68Service;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.core.page.TableDataInfo;

/**
 * 客户68分类维护 Controller（客户对自己68分类的增删改查、同步、操作记录）
 *
 * @author spd
 */
@RestController
@RequestMapping("/foundation/customerCategory68")
public class SbCustomerCategory68Controller extends BaseController {

    @Autowired
    private ISbCustomerCategory68Service sbCustomerCategory68Service;

    private String requireCustomerId() {
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(customerId)) {
            throw new IllegalArgumentException("未获取到客户ID，请确认登录上下文");
        }
        return customerId;
    }

    /**
     * 查询当前客户的68分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbCustomerCategory68 query) {
        query.setCustomerId(requireCustomerId());
        query.setDelFlag(0);
        startPage();
        List<SbCustomerCategory68> list = sbCustomerCategory68Service.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询当前客户的68分类树
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:list')")
    @GetMapping("/treeselect")
    public AjaxResult treeselect() {
        String customerId = requireCustomerId();
        List<SbCustomerCategory68> list = sbCustomerCategory68Service.selectTree(customerId);
        return success(list);
    }

    /**
     * 获取客户68分类详情
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return success(sbCustomerCategory68Service.selectById(id));
    }

    /**
     * 新增客户68分类（与标准一一对应场景下一般通过同步产生，此处供特殊新增）
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:add')")
    @Log(title = "医疗器械68分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbCustomerCategory68 row) {
        row.setCustomerId(requireCustomerId());
        return toAjax(sbCustomerCategory68Service.insert(row));
    }

    /**
     * 修改客户68分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:edit')")
    @Log(title = "医疗器械68分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbCustomerCategory68 row) {
        return toAjax(sbCustomerCategory68Service.update(row));
    }

    /**
     * 删除客户68分类（逻辑删除）
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:remove')")
    @Log(title = "医疗器械68分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        return toAjax(sbCustomerCategory68Service.deleteById(id));
    }

    /**
     * 同步：以 fd_category68 为蓝本，更新已有、新增没有
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:sync')")
    @Log(title = "医疗器械68分类同步", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public AjaxResult sync() {
        String customerId = requireCustomerId();
        sbCustomerCategory68Service.syncFromStandard(customerId);
        return success();
    }

    /**
     * 查询当前客户的68分类操作记录
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:log')")
    @GetMapping("/log")
    public AjaxResult log() {
        String customerId = requireCustomerId();
        List<SbCustomerCategory68Log> list = sbCustomerCategory68Service.selectLogByCustomerId(customerId);
        return success(list);
    }

    /**
     * 按某条客户68分类ID查询其操作记录
     */
    @PreAuthorize("@ss.hasPermi('foundation:customerCategory68:log')")
    @GetMapping("/log/target/{targetId}")
    public AjaxResult logByTarget(@PathVariable String targetId) {
        List<SbCustomerCategory68Log> list = sbCustomerCategory68Service.selectLogByTargetId(targetId);
        return success(list);
    }
}
