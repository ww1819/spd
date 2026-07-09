package com.spd.foundation.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdMaterialListRequest;
import com.spd.foundation.dto.MaterialImportAddDto;
import com.spd.foundation.dto.MaterialImportUpdateDto;
import com.spd.foundation.dto.MaterialSyncHisChargeItemDto;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.file.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.stream.Collectors;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.ZqTcmMasterDataGuard;
import com.spd.common.utils.PinyinUtils;
import com.spd.his.config.HisTenantDbHandle;
import com.spd.his.config.HisTenantJdbcAccess;
import com.spd.his.domain.HisChargeItemMirror;
import com.spd.his.mapper.HisChargeItemMirrorMapper;
import com.spd.foundation.mapper.FdMaterialMapper;

/**
 * 耗材产品Controller
 *
 * @author spd
 * @date 2023-12-23
 */
@RestController
@RequestMapping("/foundation/material")
public class FdMaterialController extends BaseController
{
    private static final String HS_THIRD_TENANT_ID = "hengsui-third-001";

    @Autowired
    private IFdMaterialService fdMaterialService;

    @Autowired
    private HisTenantJdbcAccess hisTenantJdbcAccess;

    @Autowired
    private HisChargeItemMirrorMapper hisChargeItemMirrorMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /** 服务器interface接口URL */
    @Value("${spd.interface.url:http://localhost:8081}")
    private String interfaceUrl;

    /**
     * 查询耗材产品列表（GET）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdMaterial fdMaterial)
    {
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return getDataTable(list);
    }

    /**
     * 查询耗材产品列表（POST，请求体传参，避免 excludeMaterialIds 等过长导致 400/414）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @PostMapping("/list")
    public TableDataInfo listPost(@RequestBody FdMaterialListRequest request)
    {
        Integer pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        FdMaterial query = request.getQuery() != null ? request.getQuery() : new FdMaterial();
        PageHelper.startPage(pageNum, pageSize);
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
        return getDataTable(list);
    }

    /**
     * 查询所有耗材产品列表
     */
    @GetMapping("/listAll")
    public List<FdMaterial> listAll(FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return list;
    }

    /**
     * 科室/临床模块专用产品档案低敏列表（仅必要字段，避免返回完整产品档案）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listDeptSafe")
    public List<Map<String, Object>> listDeptSafe(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "speci", required = false) String speci,
        @RequestParam(value = "factoryKeyword", required = false) String factoryKeyword,
        @RequestParam(value = "udiNo", required = false) String udiNo,
        @RequestParam(value = "isGz", required = false) String isGz,
        @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled)
    {
        FdMaterial query = new FdMaterial();
        String searchKey = StringUtils.isNotEmpty(keyword) ? keyword : name;
        if (StringUtils.isNotEmpty(searchKey))
        {
            String normalized = com.spd.common.utils.MaterialSearchKeywordUtils.normalizeAndEscapeLike(searchKey.trim());
            if (StringUtils.isNotEmpty(normalized))
            {
                query.setName(normalized);
            }
        }
        if (StringUtils.isNotEmpty(speci))
        {
            String normalizedSpeci = com.spd.common.utils.MaterialSearchKeywordUtils.normalizeAndEscapeLike(speci.trim());
            if (StringUtils.isNotEmpty(normalizedSpeci))
            {
                query.setSpeci(normalizedSpeci);
            }
        }
        if (StringUtils.isNotEmpty(factoryKeyword))
        {
            String normalizedFactory = com.spd.common.utils.MaterialSearchKeywordUtils.normalizeAndEscapeLike(factoryKeyword.trim());
            if (StringUtils.isNotEmpty(normalizedFactory))
            {
                query.setFactoryKeyword(normalizedFactory);
            }
        }
        if (StringUtils.isNotEmpty(udiNo))
        {
            String normalizedUdi = com.spd.common.utils.MaterialSearchKeywordUtils.normalizeAndEscapeLike(udiNo.trim());
            if (StringUtils.isNotEmpty(normalizedUdi))
            {
                query.setUdiNo(normalizedUdi);
            }
        }
        if (StringUtils.isNotEmpty(isGz))
        {
            query.setIsGz(isGz);
        }
        if (Boolean.TRUE.equals(onlyEnabled))
        {
            query.setIsUse("1");
        }
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
        // 业务选耗材：再次排除已删除、已停用（与定数申购等入口一致，避免仅 SQL 时遗漏）
        list = list.stream()
            .filter(m -> m != null)
            .filter(m -> !(m.getDelFlag() != null && m.getDelFlag() == 1))
            .filter(m -> Boolean.TRUE.equals(onlyEnabled) ? isMaterialEnabled(m) : isMaterialNotDisabled(m))
            .collect(Collectors.toList());
        List<Map<String, Object>> safeList = new ArrayList<>();
        for (FdMaterial material : list)
        {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", material.getId());
            item.put("name", material.getName());
            item.put("code", material.getCode());
            item.put("supplierId", material.getSupplierId());
            item.put("referredName", material.getReferredName());
            item.put("speci", material.getSpeci());
            item.put("model", material.getModel());
            item.put("brand", material.getBrand());
            item.put("isGz", material.getIsGz());
            safeList.add(item);
        }
        return safeList;
    }

    /**
     * HIS 收费项目列表（仅衡水三院租户，来源：v_charge_item）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query') or @ss.hasPermi('foundation:chargeItem:query')")
    @GetMapping("/hisChargeItem/list")
    public TableDataInfo listHisChargeItem(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "speci", required = false) String speci,
        @RequestParam(value = "chargeItemId", required = false) String chargeItemId,
        @RequestParam(value = "itemCode", required = false) String itemCode,
        @RequestParam(value = "referredCode", required = false) String referredCode,
        @RequestParam(value = "valueLevel", required = false) String valueLevel,
        @RequestParam(value = "pageNum", required = false) Integer pageNum,
        @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return getDataTable(new ArrayList<>());
        }

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        PageHelper.startPage(pageNum, pageSize);
        List<HisChargeItemMirror> mirrorRows = hisChargeItemMirrorMapper.selectList(tenantId, name, speci, chargeItemId, itemCode, referredCode, valueLevel);
        long total = new PageInfo<>(mirrorRows).getTotal();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (HisChargeItemMirror r : mirrorRows)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("chargeItemId", r.getChargeItemId());
            item.put("chargeCode", r.getItemCode());
            item.put("chargeName", r.getItemName());
            item.put("chargeSpeci", r.getSpecModel());
            item.put("chargeModel", r.getSpecModel());
            item.put("chargePrice", r.getPrice());
            item.put("referredCode", r.getReferredCode());
            item.put("valueLevel", r.getValueLevel());
            rows.add(item);
        }
        TableDataInfo data = getDataTable(rows);
        data.setTotal(total);
        return data;
    }

    /**
     * 导出 HIS 收费项目镜像（含高低值属性）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:export') or @ss.hasPermi('foundation:chargeItem:export')")
    @Log(title = "导出HIS收费项目", businessType = BusinessType.EXPORT)
    @PostMapping("/hisChargeItem/export")
    public void exportHisChargeItem(
        HttpServletResponse response,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "speci", required = false) String speci,
        @RequestParam(value = "chargeItemId", required = false) String chargeItemId,
        @RequestParam(value = "itemCode", required = false) String itemCode,
        @RequestParam(value = "referredCode", required = false) String referredCode,
        @RequestParam(value = "valueLevel", required = false) String valueLevel)
    {
        String tenantId = SecurityUtils.getCustomerId();
        List<HisChargeItemMirror> list = hisChargeItemMirrorMapper.selectList(tenantId, name, speci, chargeItemId, itemCode, referredCode, valueLevel);
        ExcelUtil<HisChargeItemMirror> util = new ExcelUtil<>(HisChargeItemMirror.class);
        util.exportExcel(response, list, "收费项目维护数据");
    }

    /**
     * 维护收费项目高低值属性（1高值 2低值）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit') or @ss.hasPermi('foundation:chargeItem:edit')")
    @Log(title = "维护收费项目高低值属性", businessType = BusinessType.UPDATE)
    @PutMapping("/hisChargeItem/valueLevel")
    public AjaxResult updateHisChargeItemValueLevel(@RequestBody Map<String, Object> body)
    {
        if (body == null || body.get("chargeItemId") == null || body.get("valueLevel") == null)
        {
            return error("chargeItemId 和 valueLevel 不能为空");
        }
        String chargeItemId = String.valueOf(body.get("chargeItemId")).trim();
        String valueLevel = String.valueOf(body.get("valueLevel")).trim();
        if (StringUtils.isEmpty(chargeItemId))
        {
            return error("chargeItemId 不能为空");
        }
        if (!"1".equals(valueLevel) && !"2".equals(valueLevel))
        {
            return error("valueLevel 仅支持 1(高值) 或 2(低值)");
        }
        String tenantId = SecurityUtils.getCustomerId();
        int rows = hisChargeItemMirrorMapper.updateValueLevel(tenantId, chargeItemId, valueLevel);
        if (rows <= 0)
        {
            return error("收费项目不存在或不可维护");
        }
        return success("保存成功");
    }

    /**
     * 批量维护收费项目高低值属性（1高值 2低值）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit') or @ss.hasPermi('foundation:chargeItem:edit')")
    @Log(title = "批量维护收费项目高低值属性", businessType = BusinessType.UPDATE)
    @PutMapping("/hisChargeItem/valueLevel/batch")
    public AjaxResult batchUpdateHisChargeItemValueLevel(@RequestBody Map<String, Object> body)
    {
        if (body == null || body.get("chargeItemIds") == null || body.get("valueLevel") == null)
        {
            return error("chargeItemIds 和 valueLevel 不能为空");
        }
        String valueLevel = String.valueOf(body.get("valueLevel")).trim();
        if (!"1".equals(valueLevel) && !"2".equals(valueLevel))
        {
            return error("valueLevel 仅支持 1(高值) 或 2(低值)");
        }
        Object idsObj = body.get("chargeItemIds");
        if (!(idsObj instanceof List))
        {
            return error("chargeItemIds 格式不正确");
        }
        List<String> chargeItemIds = new ArrayList<>();
        for (Object id : (List<?>) idsObj)
        {
            if (id == null)
            {
                continue;
            }
            String cid = String.valueOf(id).trim();
            if (StringUtils.isNotEmpty(cid))
            {
                chargeItemIds.add(cid);
            }
        }
        if (chargeItemIds.isEmpty())
        {
            return error("请至少选择一个收费项目");
        }
        String tenantId = SecurityUtils.getCustomerId();
        // 去重、排序（降低锁冲突概率）
        chargeItemIds = chargeItemIds.stream().distinct().sorted().collect(Collectors.toList());

        // 分批更新：避免一次性 IN 过大导致 SQL 超时/连接中断
        final int chunkSize = 500;
        int updated = 0;
        for (int i = 0; i < chargeItemIds.size(); i += chunkSize)
        {
            int end = Math.min(i + chunkSize, chargeItemIds.size());
            List<String> chunk = chargeItemIds.subList(i, end);
            updated += hisChargeItemMirrorMapper.updateValueLevelBatch(tenantId, chunk, valueLevel);
        }

        if (updated <= 0)
        {
            return error("所选收费项目不存在或不可维护");
        }
        return AjaxResult.success("批量保存成功，共更新 " + updated + " 条");
    }

    /**
     * 将产品档案 is_gz 同步到已对照 HIS 收费项目的 value_level（1高值 2低值）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit') or @ss.hasPermi('foundation:chargeItem:edit')")
    @Log(title = "同步产品档案高低值到收费项目", businessType = BusinessType.UPDATE)
    @PutMapping("/syncHisChargeItemValueLevel")
    public AjaxResult syncHisChargeItemValueLevelFromMaterial(@RequestBody MaterialSyncHisChargeItemDto dto)
    {
        if (dto == null)
        {
            return error("请求参数不能为空");
        }
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }

        List<Long> materialIds = resolveSyncMaterialTargetIds(dto);
        if (materialIds.isEmpty())
        {
            return error("当前没有可同步的产品档案");
        }

        final int loadChunk = 500;
        List<FdMaterial> materials = new ArrayList<>();
        for (int i = 0; i < materialIds.size(); i += loadChunk)
        {
            int end = Math.min(i + loadChunk, materialIds.size());
            List<FdMaterial> chunk = fdMaterialMapper.selectFdMaterialByIds(materialIds.subList(i, end));
            if (chunk != null && !chunk.isEmpty())
            {
                materials.addAll(chunk);
            }
        }
        if (materials.isEmpty())
        {
            return error("未找到有效的产品档案");
        }

        // chargeItemId -> valueLevel（同一收费项多条档案时以后者为准）
        Map<String, String> chargeItemToLevel = new LinkedHashMap<>();
        int skippedNoBind = 0;
        int skippedInvalidGz = 0;
        for (FdMaterial m : materials)
        {
            if (m == null)
            {
                continue;
            }
            String chargeItemId = StringUtils.trimToEmpty(m.getHisChargeItemId());
            if (StringUtils.isEmpty(chargeItemId))
            {
                skippedNoBind++;
                continue;
            }
            String valueLevel = StringUtils.trimToEmpty(m.getIsGz());
            if (!"1".equals(valueLevel) && !"2".equals(valueLevel))
            {
                skippedInvalidGz++;
                continue;
            }
            chargeItemToLevel.put(chargeItemId, valueLevel);
        }
        if (chargeItemToLevel.isEmpty())
        {
            return error("所选产品均未对照收费项目，或高低值标志无效");
        }

        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : chargeItemToLevel.entrySet())
        {
            grouped.computeIfAbsent(e.getValue(), k -> new ArrayList<>()).add(e.getKey());
        }

        final int chunkSize = 500;
        int updated = 0;
        for (Map.Entry<String, List<String>> entry : grouped.entrySet())
        {
            List<String> ids = entry.getValue().stream().distinct().sorted().collect(Collectors.toList());
            for (int i = 0; i < ids.size(); i += chunkSize)
            {
                int end = Math.min(i + chunkSize, ids.size());
                updated += hisChargeItemMirrorMapper.updateValueLevelBatch(tenantId, ids.subList(i, end), entry.getKey());
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("requestedMaterialCount", materialIds.size());
        data.put("syncChargeItemCount", chargeItemToLevel.size());
        data.put("updatedCount", updated);
        data.put("skippedNoBind", skippedNoBind);
        data.put("skippedInvalidGz", skippedInvalidGz);
        return AjaxResult.success("同步完成，共更新收费项目 " + updated + " 条", data);
    }

    private List<Long> resolveSyncMaterialTargetIds(MaterialSyncHisChargeItemDto dto)
    {
        if (Boolean.TRUE.equals(dto.getSyncAll()))
        {
            FdMaterial query = dto.getQueryCriteria();
            if (query == null)
            {
                return new ArrayList<>();
            }
            if (query.getIncludeDisabledInList() == null)
            {
                query.setIncludeDisabledInList(true);
            }
            List<Long> ids = fdMaterialMapper.selectFdMaterialIdList(query);
            return ids != null ? ids : new ArrayList<>();
        }
        List<Long> materialIds = new ArrayList<>();
        if (dto.getMaterialIds() != null)
        {
            for (Long id : dto.getMaterialIds())
            {
                if (id != null)
                {
                    materialIds.add(id);
                }
            }
        }
        return materialIds;
    }

    /**
     * 抓取 HIS 收费项目到本地镜像表 his_charge_item_mirror
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query') or @ss.hasPermi('foundation:chargeItem:fetch')")
    @Log(title = "抓取HIS收费项目", businessType = BusinessType.OTHER)
    @PostMapping("/hisChargeItem/fetch")
    public AjaxResult fetchHisChargeItem()
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }
        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        String sql = "select charge_item_id, item_code, item_name, item_type, consumable_type, spec_model, "
            + "unit, price, manufacturer, register_no, is_active, create_time, update_time "
            + "from v_charge_item";
        List<HisChargeItemMirror> hisRows = hisDb.getJdbcTemplate().query(sql, (rs, i) -> {
            HisChargeItemMirror row = new HisChargeItemMirror();
            row.setTenantId(tenantId);
            row.setChargeItemId(rs.getString("charge_item_id"));
            row.setItemCode(rs.getString("item_code"));
            row.setItemName(rs.getString("item_name"));
            row.setItemType(rs.getString("item_type"));
            row.setConsumableType(rs.getString("consumable_type"));
            row.setSpecModel(rs.getString("spec_model"));
            row.setUnit(rs.getString("unit"));
            row.setPrice(rs.getBigDecimal("price"));
            row.setManufacturer(rs.getString("manufacturer"));
            row.setRegisterNo(rs.getString("register_no"));
            row.setIsActive(rs.getString("is_active"));
            row.setReferredCode(PinyinUtils.getPinyinInitials(rs.getString("item_name")));
            row.setHisCreateTime(rs.getString("create_time"));
            row.setHisUpdateTime(rs.getString("update_time"));
            return row;
        });
        // 先将本租户本地镜像全部标记为删除，再将本次抓取到的数据回写为有效（deleted_flag=0）
        int markedDeleted = hisChargeItemMirrorMapper.markAllDeletedByTenant(tenantId);
        if (hisRows.isEmpty())
        {
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("fetchedRows", 0);
            emptyData.put("affectedRows", 0);
            emptyData.put("markedDeletedRows", markedDeleted);
            emptyData.put("refreshTime", new java.util.Date());
            return AjaxResult.success("抓取完成，HIS视图无数据", emptyData);
        }
        int affect = 0;
        final int batchSize = 500;
        for (int i = 0; i < hisRows.size(); i += batchSize)
        {
            int end = Math.min(i + batchSize, hisRows.size());
            affect += hisChargeItemMirrorMapper.insertOrUpdateBatch(hisRows.subList(i, end));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("fetchedRows", hisRows.size());
        data.put("affectedRows", affect);
        data.put("markedDeletedRows", markedDeleted);
        data.put("refreshTime", new java.util.Date());
        return AjaxResult.success("抓取完成", data);
    }

    /**
     * 绑定耗材与 HIS 收费项目（fd_material.his_charge_item_id = v_charge_item.charge_item_id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @PostMapping("/bindHisChargeItem")
    public AjaxResult bindHisChargeItem(@RequestBody Map<String, Object> body)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }
        if (body == null || body.get("materialId") == null || body.get("chargeItemId") == null)
        {
            return error("materialId 和 chargeItemId 不能为空");
        }

        Long materialId = Long.valueOf(String.valueOf(body.get("materialId")));
        String chargeItemId = String.valueOf(body.get("chargeItemId")).trim();
        if (StringUtils.isEmpty(chargeItemId))
        {
            return error("chargeItemId 不能为空");
        }

        FdMaterial db = fdMaterialService.selectFdMaterialById(materialId);
        if (db == null)
        {
            return error("耗材不存在");
        }
        db.setHisChargeItemId(chargeItemId);
        db.setUpdateBy(getUserIdStr());
        return toAjax(fdMaterialService.updateFdMaterial(db));
    }

    /**
     * 解绑耗材与 HIS 收费项目（清空 fd_material.his_charge_item_id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @PostMapping("/unbindHisChargeItem")
    public AjaxResult unbindHisChargeItem(@RequestBody Map<String, Object> body)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }
        if (body == null || body.get("materialId") == null)
        {
            return error("materialId 不能为空");
        }
        Long materialId = Long.valueOf(String.valueOf(body.get("materialId")));
        FdMaterial db = fdMaterialService.selectFdMaterialById(materialId);
        if (db == null)
        {
            return error("耗材不存在");
        }
        return toAjax(fdMaterialService.clearMaterialHisChargeItem(materialId));
    }

    /**
     * 查询所有耗材产品列表（支持分页，避免一次返回过多数据）
     *
     * 说明：
     * - 前端可通过 pageNum、pageSize 传参控制分页；
     * - 返回结果结构与 /list 一致：{ rows: [...], total: n }。
     * - 不改动原有 /listAll 接口，避免影响已有依赖。
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @GetMapping("/listAllPage")
    public TableDataInfo listAllPage(FdMaterial fdMaterial)
    {
        // 读取前端传入的 pageNum / pageSize，并开启分页
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        // 封装为带 total 的分页对象返回
        return getDataTable(list);
    }

    /**
     * 采购计划「添加产品明细」专用：低敏分页列表。
     * <p>
     * 过滤条件与 /list 一致（如 storeroomId、factoryId、name、supplierId、isGz、includeMaterialIds 等），
     * 由服务端 PageHelper 分页；响应中去除审计字段、租户、HIS/医保/UDI 等敏感信息。
     * </p>
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:list') or @ss.hasPermi('foundation:material:list')")
    @GetMapping("/listPurchasePlanPick")
    public TableDataInfo listPurchasePlanPickGet(FdMaterial fdMaterial)
    {
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        for (FdMaterial m : list)
        {
            stripMaterialForPurchasePlanPick(m);
        }
        return getDataTable(list);
    }

    /**
     * 采购计划「添加产品明细」专用：低敏分页列表（POST，避免 includeMaterialIds 过长）
     */
    @PreAuthorize("@ss.hasPermi('caigou:jihua:list') or @ss.hasPermi('foundation:material:list')")
    @PostMapping("/listPurchasePlanPick")
    public TableDataInfo listPurchasePlanPickPost(@RequestBody FdMaterialListRequest request)
    {
        Integer pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        FdMaterial query = request.getQuery() != null ? request.getQuery() : new FdMaterial();
        PageHelper.startPage(pageNum, pageSize);
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
        for (FdMaterial m : list)
        {
            stripMaterialForPurchasePlanPick(m);
        }
        return getDataTable(list);
    }

    /** 产品档案启用（is_use=1） */
    private static boolean isMaterialEnabled(FdMaterial m)
    {
        if (m == null || m.getIsUse() == null)
        {
            return false;
        }
        return "1".equals(m.getIsUse().toString().trim());
    }

    /** 产品档案未停用（is_use 为空或非 2） */
    private static boolean isMaterialNotDisabled(FdMaterial m)
    {
        if (m == null)
        {
            return false;
        }
        Object u = m.getIsUse();
        if (u == null)
        {
            return true;
        }
        String s = u.toString().trim();
        return s.isEmpty() || !"2".equals(s);
    }

    /** 采购计划选耗材：去掉不必要且偏敏感的产品档案字段 */
    private void stripMaterialForPurchasePlanPick(FdMaterial m)
    {
        if (m == null)
        {
            return;
        }
        m.setTenantId(null);
        m.setCreateBy(null);
        m.setCreateTime(null);
        m.setUpdateBy(null);
        m.setUpdateTime(null);
        m.setDeleteBy(null);
        m.setDeleteTime(null);
        m.setAuditBy(null);
        m.setAuditDate(null);
        m.setHisId(null);
        m.setHisChargeItemId(null);
        m.setMedicalName(null);
        m.setMedicalNo(null);
        m.setCountryNo(null);
        m.setCountryName(null);
        m.setUdiNo(null);
        m.setPermitNo(null);
        m.setDescription(null);
        m.setSuccessfulType(null);
        m.setSuccessfulNo(null);
        m.setSuccessfulPrice(null);
        m.setSalePrice(null);
        m.setPackageSpeci(null);
        m.setMaterialLevel(null);
        m.setRegisterLevel(null);
        m.setRiskLevel(null);
        m.setFirstaidLevel(null);
        m.setDoctorLevel(null);
        m.setQuality(null);
        m.setFunction(null);
        m.setUseto(null);
        m.setSelectionReason(null);
        m.setProducer(null);
        m.setRegisterName(null);
    }

    /**
     * 导出耗材产品列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:export')")
    @Log(title = "耗材产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        util.exportExcel(response, list, "耗材产品数据");
    }

    /**
     * 根据主条码(udi_no)或耗材编码查询产品档案（用于入库单扫码带出产品）
     */
    @GetMapping("/getByMainBarcode")
    public AjaxResult getByMainBarcode(@RequestParam String mainBarcode)
    {
        return success(fdMaterialService.getByMainBarcode(mainBarcode));
    }

    /**
     * 获取耗材产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdMaterialService.selectFdMaterialById(id));
    }

    /**
     * 新增耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:add')")
    @Log(title = "耗材产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdMaterial fdMaterial)
    {
        ZqTcmMasterDataGuard.assertManualCreateAllowed();
        return toAjax(fdMaterialService.insertFdMaterial(fdMaterial));
    }

    /**
     * 修改耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdMaterial fdMaterial)
    {
        return toAjax(fdMaterialService.updateFdMaterial(fdMaterial));
    }

    /**
     * 删除耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:remove')")
    @Log(title = "耗材产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdMaterialService.deleteFdMaterialByIds(ids));
    }

    @Log(title = "耗材产品导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        List<FdMaterial> fdmaterialList = util.importExcel(file.getInputStream());
        String operName = getUserIdStr();
        String message = fdMaterialService.importFdMaterial(fdmaterialList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        // 设置文件名响应头
        FileUtils.setAttachmentResponseHeader(response, "耗材产品档案基础字典导入.xlsx");
        util.importTemplateExcel(response, "耗材数据");
    }

    /** 耗材档案新增导入模板（不含 SPD 主键；含数据校验结果列供回填） */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddTemplate")
    public void importAddTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        FileUtils.setAttachmentResponseHeader(response, "耗材档案新增导入模板.xlsx");
        util.importTemplateExcel(response, "耗材档案新增导入");
    }

    /** 耗材档案更新导入模板 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        FileUtils.setAttachmentResponseHeader(response, "耗材档案更新导入模板.xlsx");
        util.importTemplateExcel(response, "耗材档案更新导入");
    }

    /** 耗材档案新增导入：仅校验 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        ZqTcmMasterDataGuard.assertManualCreateAllowed();
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        List<MaterialImportAddDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdMaterialService.validateMaterialImportAdd(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    /** 耗材档案更新导入：仅校验 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        List<MaterialImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdMaterialService.validateMaterialImportUpdate(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "耗材档案新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddData")
    public AjaxResult importAddData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ZqTcmMasterDataGuard.assertManualCreateAllowed();
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        List<MaterialImportAddDto> list = util.importExcel(file.getInputStream());
        String operName = getUserIdStr();
        String message = fdMaterialService.importMaterialImportAdd(list, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
    }

    @Log(title = "耗材档案更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        List<MaterialImportUpdateDto> list = util.importExcel(file.getInputStream());
        String operName = getUserIdStr();
        String message = fdMaterialService.importMaterialImportUpdate(list, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
    }

    /**
     * 批量更新耗材产品名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:updateReferred')")
    @Log(title = "耗材产品", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody Map<String, List<Long>> body)
    {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdMaterialService.updateReferred(ids);
        return success("更新简码成功");
    }

    /**
     * 批量修改产品档案（库房/财务/材料类别及标志位；仅请求体中非空字段会写入）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品批量修改", businessType = BusinessType.UPDATE)
    @PostMapping("/batchUpdate")
    public AjaxResult batchUpdate(@RequestBody com.spd.foundation.dto.MaterialBatchUpdateDto dto) {
        int n = fdMaterialService.batchUpdateMaterials(dto);
        return success("批量修改成功，共更新 " + n + " 条");
    }

    /**
     * 产品档案停用（记录停用时间、停用人、停用原因）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品停用", businessType = BusinessType.UPDATE)
    @PutMapping("/disable")
    public AjaxResult disable(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? Long.valueOf(params.get("id").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        if (id == null) {
            return error("产品档案ID不能为空");
        }
        if (reason == null || reason.trim().isEmpty()) {
            return error("请填写停用原因");
        }
        fdMaterialService.disableMaterial(id, reason.trim());
        return success();
    }

    /**
     * 产品档案启用（记录启用时间、启用人、启用原因）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品启用", businessType = BusinessType.UPDATE)
    @PutMapping("/enable")
    public AjaxResult enable(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? Long.valueOf(params.get("id").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        if (id == null) {
            return error("产品档案ID不能为空");
        }
        if (reason == null || reason.trim().isEmpty()) {
            return error("请填写启用原因");
        }
        fdMaterialService.enableMaterial(id, reason.trim());
        return success();
    }

    /**
     * 查询产品档案启用停用记录列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/statusLog")
    public AjaxResult listStatusLog(@PathVariable("id") Long id) {
        return success(fdMaterialService.listStatusLogByMaterialId(id));
    }

    /**
     * 查询产品档案变更记录列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/changeLog")
    public AjaxResult listChangeLog(@PathVariable("id") Long id) {
        return success(fdMaterialService.listChangeLogByMaterialId(id));
    }

    /**
     * 查询产品档案时间轴（合并启用停用与变更记录，按时间倒序）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/timeline")
    public AjaxResult getTimeline(@PathVariable("id") Long id) {
        return success(fdMaterialService.getMaterialTimeline(id));
    }

    /**
     * 查询产品档案入库记录-供应商树
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/inboundSuppliers")
    public AjaxResult listInboundSuppliers(@PathVariable("id") Long id) {
        return success(fdMaterialService.listInboundSuppliers(id));
    }

    /**
     * 查询产品档案入库记录
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/inboundRecords")
    public AjaxResult listInboundRecords(@PathVariable("id") Long id,
                                         @RequestParam(value = "supplierId", required = false) Long supplierId,
                                         @RequestParam(value = "warehouseId", required = false) Long warehouseId,
                                         @RequestParam(value = "auditBeginTime", required = false) String auditBeginTime,
                                         @RequestParam(value = "auditEndTime", required = false) String auditEndTime,
                                         @RequestParam(value = "orderMode", required = false) String orderMode) {
        return success(fdMaterialService.listInboundRecords(id, supplierId, warehouseId, auditBeginTime, auditEndTime, orderMode));
    }

    /**
     * 推送档案
     * 调用服务器interface接口推送供应商档案
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:push')")
    @Log(title = "推送档案", businessType = BusinessType.OTHER)
    @PostMapping("/pushArchive")
    public AjaxResult pushArchive(@RequestBody Map<String, Object> params)
    {
        try
        {
            Long supplierId = null;
            if (params.get("supplierId") instanceof Number)
            {
                supplierId = ((Number) params.get("supplierId")).longValue();
            }
            else if (params.get("supplierId") != null)
            {
                supplierId = Long.parseLong(params.get("supplierId").toString());
            }

            if (supplierId == null)
            {
                return error("供应商ID不能为空");
            }

            // 调用服务器interface接口
            String url = interfaceUrl;
            if (!url.endsWith("/"))
            {
                url += "/";
            }
            url += "api/spd/pushSupplier";
            
            Map<String, Object> requestData = new java.util.HashMap<>();
            requestData.put("supplierId", supplierId);
            
            String jsonData = com.alibaba.fastjson2.JSON.toJSONString(requestData);
            String result = com.spd.common.utils.http.HttpUtils.sendPost(url, jsonData, "application/json;charset=UTF-8");
            
            // 解析返回结果
            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(result);
            Integer code = jsonResult.getInteger("code");
            String msg = jsonResult.getString("msg");
            
            if (code != null && code == 200)
            {
                return success(jsonResult.get("data"));
            }
            else
            {
                return error(msg != null ? msg : "推送失败");
            }
        }
        catch (Exception e)
        {
            return error("推送档案失败: " + e.getMessage());
        }
    }


}
