package com.spd.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.system.domain.SysPost;
import com.spd.system.service.ISysMenuService;
import com.spd.system.service.ISysPostService;
import com.spd.system.service.impl.SysPostServiceImpl;

/**
 * 岗位信息操作处理
 * 
 * @author spd
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(SysPostController.class);
    private static final Map<Long, ISysPostService.SyncStatus> MENU_SYNC_STATUS_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, ISysPostService.SyncStatus> DEPARTMENT_SYNC_STATUS_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, ISysPostService.SyncStatus> WAREHOUSE_SYNC_STATUS_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }
    
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/menuTreeselect")
    public AjaxResult menuTreeselect(String tenantId)
    {
        return success(sysMenuService.selectMenuTreeForPostAssign(tenantId));
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/roleMenuTreeselect/{postId}")
    public AjaxResult roleMenuTreeselect(@PathVariable Long postId)
    {
        SysPost post = postService.selectPostById(postId);
        AjaxResult ajax = AjaxResult.success();
        String tenantId = post != null ? post.getTenantId() : null;
        ajax.put("menus", sysMenuService.selectMenuTreeForPostAssign(tenantId));
        List<Long> menuIds = ((SysPostServiceImpl) postService).selectMenuListByPostId(postId);
        ajax.put("checkedKeys", menuIds != null ? menuIds : new ArrayList<>());
        return ajax;
    }

    /**
     * 根据岗位编号获取详细信息
     */
    /**
     * 耗材工作组关联用户 ID（sys_user_post），供「同步仓库/科室/菜单」按组批量更新用户权限
     */
    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:query') or @ss.hasPermi('system:post:edit')")
    @GetMapping("/{postId}/userIds")
    public AjaxResult userIdsByPost(@PathVariable Long postId)
    {
        return success(postService.selectUserIdsByPostId(postId));
    }

    /**
     * 异步同步工作组菜单到组内用户（仅补充缺失项）
     */
    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:edit')")
    @PostMapping("/sync/menu/{postId}")
    public AjaxResult syncMenuToUsers(@PathVariable Long postId,
        @RequestParam(value = "syncMode", required = false, defaultValue = "supplement") String syncMode)
    {
        ISysPostService.SyncStatus running = new ISysPostService.SyncStatus();
        running.setPostId(postId);
        running.setStatus("RUNNING");
        running.setMessage("正在后台同步菜单权限...");
        running.setAffected(0);
        running.setUpdateTime(System.currentTimeMillis());
        MENU_SYNC_STATUS_MAP.put(postId, running);

        CompletableFuture.runAsync(() -> {
            try
            {
                int affected = postService.syncMenuToPostUsers(postId, syncMode);
                ISysPostService.SyncStatus success = new ISysPostService.SyncStatus();
                success.setPostId(postId);
                success.setStatus("SUCCESS");
                success.setAffected(affected);
                success.setMessage("copy".equalsIgnoreCase(syncMode) ? "菜单复制完成" : "菜单补全完成");
                success.setUpdateTime(System.currentTimeMillis());
                MENU_SYNC_STATUS_MAP.put(postId, success);
            }
            catch (Exception e)
            {
                log.error("工作组菜单同步失败，postId={}", postId, e);
                ISysPostService.SyncStatus failed = new ISysPostService.SyncStatus();
                failed.setPostId(postId);
                failed.setStatus("FAILED");
                failed.setAffected(0);
                failed.setMessage(e.getMessage() != null ? e.getMessage() : "菜单同步失败");
                failed.setUpdateTime(System.currentTimeMillis());
                MENU_SYNC_STATUS_MAP.put(postId, failed);
            }
        });

        return AjaxResult.success("已提交后台同步任务，请稍后查看结果");
    }

    /**
     * 查询工作组菜单同步状态
     */
    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:query')")
    @GetMapping("/sync/menu/status/{postId}")
    public AjaxResult getMenuSyncStatus(@PathVariable Long postId)
    {
        ISysPostService.SyncStatus status = MENU_SYNC_STATUS_MAP.get(postId);
        if (status == null)
        {
            status = new ISysPostService.SyncStatus();
            status.setPostId(postId);
            status.setStatus("IDLE");
            status.setMessage("未查询到同步任务");
            status.setAffected(0);
            status.setUpdateTime(System.currentTimeMillis());
        }
        return AjaxResult.success(status);
    }

    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:edit')")
    @PostMapping("/sync/department/{postId}")
    public AjaxResult syncDepartmentToUsers(@PathVariable Long postId,
        @RequestParam(value = "syncMode", required = false, defaultValue = "supplement") String syncMode)
    {
        ISysPostService.SyncStatus running = new ISysPostService.SyncStatus();
        running.setPostId(postId);
        running.setStatus("RUNNING");
        running.setMessage("正在后台同步科室权限...");
        running.setAffected(0);
        running.setUpdateTime(System.currentTimeMillis());
        DEPARTMENT_SYNC_STATUS_MAP.put(postId, running);

        CompletableFuture.runAsync(() -> {
            try
            {
                int affected = postService.syncDepartmentToPostUsers(postId, syncMode);
                ISysPostService.SyncStatus success = new ISysPostService.SyncStatus();
                success.setPostId(postId);
                success.setStatus("SUCCESS");
                success.setAffected(affected);
                success.setMessage("copy".equalsIgnoreCase(syncMode) ? "科室复制完成" : "科室补全完成");
                success.setUpdateTime(System.currentTimeMillis());
                DEPARTMENT_SYNC_STATUS_MAP.put(postId, success);
            }
            catch (Exception e)
            {
                log.error("工作组科室同步失败，postId={}", postId, e);
                ISysPostService.SyncStatus failed = new ISysPostService.SyncStatus();
                failed.setPostId(postId);
                failed.setStatus("FAILED");
                failed.setAffected(0);
                failed.setMessage(e.getMessage() != null ? e.getMessage() : "科室同步失败");
                failed.setUpdateTime(System.currentTimeMillis());
                DEPARTMENT_SYNC_STATUS_MAP.put(postId, failed);
            }
        });
        return AjaxResult.success("已提交后台同步任务，请稍后查看结果");
    }

    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:query')")
    @GetMapping("/sync/department/status/{postId}")
    public AjaxResult getDepartmentSyncStatus(@PathVariable Long postId)
    {
        ISysPostService.SyncStatus status = DEPARTMENT_SYNC_STATUS_MAP.get(postId);
        if (status == null)
        {
            status = new ISysPostService.SyncStatus();
            status.setPostId(postId);
            status.setStatus("IDLE");
            status.setMessage("未查询到同步任务");
            status.setAffected(0);
            status.setUpdateTime(System.currentTimeMillis());
        }
        return AjaxResult.success(status);
    }

    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:edit')")
    @PostMapping("/sync/warehouse/{postId}")
    public AjaxResult syncWarehouseToUsers(@PathVariable Long postId,
        @RequestParam(value = "syncMode", required = false, defaultValue = "supplement") String syncMode)
    {
        ISysPostService.SyncStatus running = new ISysPostService.SyncStatus();
        running.setPostId(postId);
        running.setStatus("RUNNING");
        running.setMessage("正在后台同步仓库权限...");
        running.setAffected(0);
        running.setUpdateTime(System.currentTimeMillis());
        WAREHOUSE_SYNC_STATUS_MAP.put(postId, running);

        CompletableFuture.runAsync(() -> {
            try
            {
                int affected = postService.syncWarehouseToPostUsers(postId, syncMode);
                ISysPostService.SyncStatus success = new ISysPostService.SyncStatus();
                success.setPostId(postId);
                success.setStatus("SUCCESS");
                success.setAffected(affected);
                success.setMessage("copy".equalsIgnoreCase(syncMode) ? "仓库复制完成" : "仓库补全完成");
                success.setUpdateTime(System.currentTimeMillis());
                WAREHOUSE_SYNC_STATUS_MAP.put(postId, success);
            }
            catch (Exception e)
            {
                log.error("工作组仓库同步失败，postId={}", postId, e);
                ISysPostService.SyncStatus failed = new ISysPostService.SyncStatus();
                failed.setPostId(postId);
                failed.setStatus("FAILED");
                failed.setAffected(0);
                failed.setMessage(e.getMessage() != null ? e.getMessage() : "仓库同步失败");
                failed.setUpdateTime(System.currentTimeMillis());
                WAREHOUSE_SYNC_STATUS_MAP.put(postId, failed);
            }
        });
        return AjaxResult.success("已提交后台同步任务，请稍后查看结果");
    }

    @PreAuthorize("@ss.hasPermi('system:post:sync') or @ss.hasPermi('system:post:query')")
    @GetMapping("/sync/warehouse/status/{postId}")
    public AjaxResult getWarehouseSyncStatus(@PathVariable Long postId)
    {
        ISysPostService.SyncStatus status = WAREHOUSE_SYNC_STATUS_MAP.get(postId);
        if (status == null)
        {
            status = new ISysPostService.SyncStatus();
            status.setPostId(postId);
            status.setStatus("IDLE");
            status.setMessage("未查询到同步任务");
            status.setAffected(0);
            status.setUpdateTime(System.currentTimeMillis());
        }
        return AjaxResult.success(status);
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        SysPost post = postService.selectPostById(postId);
        if (post != null)
        {
            // 获取权限ID列表
            List<Long> menuIds = ((SysPostServiceImpl) postService).selectMenuListByPostId(postId);
            List<Long> departmentIds = ((SysPostServiceImpl) postService).selectDepartmentListByPostId(postId);
            List<Long> warehouseIds = ((SysPostServiceImpl) postService).selectWarehouseListByPostId(postId);
            post.setMenuIds(menuIds != null ? menuIds.toArray(new Long[0]) : new Long[0]);
            post.setDepartmentIds(departmentIds != null ? departmentIds.toArray(new Long[0]) : new Long[0]);
            post.setWarehouseIds(warehouseIds != null ? warehouseIds.toArray(new Long[0]) : new Long[0]);
        }
        return success(post);
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(getUserIdStr());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(getUserIdStr());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return success(posts);
    }

    /**
     * 获取岗位下拉树列表（工作组树形结构）
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return success(posts);
    }
}
