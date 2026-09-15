package com.spd.foundation.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.mapper.SpdScmSupplierBindMapper;
import com.spd.caigou.mapper.SpdScmTenantBindMapper;
import com.spd.common.bridge.SpdBridgeActions;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.SpdScmMaterialApplyLog;
import com.spd.foundation.domain.SpdScmMaterialArchive;
import com.spd.foundation.domain.SpdScmMaterialPushFieldCfg;
import com.spd.foundation.domain.SpdScmMaterialSyncBatch;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.SpdScmMaterialApplyLogMapper;
import com.spd.foundation.mapper.SpdScmMaterialArchiveMapper;
import com.spd.foundation.mapper.SpdScmMaterialPushFieldCfgMapper;
import com.spd.foundation.mapper.SpdScmMaterialSyncBatchMapper;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.foundation.service.ISpdScmMaterialArchiveService;
import com.spd.foundation.service.bridge.SpdScmBridgeClient;

/**
 * 医院产品档案推送 / 拉取镜像 / 应用到 fd_material
 */
@Service
public class SpdScmMaterialArchiveServiceImpl implements ISpdScmMaterialArchiveService
{
    /** MAT-B-003 院内铁锁：永不从平台应用覆盖 */
    private static final Set<String> IRON_LOCK_FIELDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "code", "spdMaterialCode", "spd_material_code",
        "hisId", "his_id", "hisSpecPackingId", "his_spec_packing_id",
        "hisChargeItemId", "his_charge_item_id", "hisChargeItemCode", "his_charge_item_code",
        "hisChargeItemName", "his_charge_item_name",
        "tenantId", "tenant_id", "delFlag", "del_flag"
    )));

    private static final String[][] DEFAULT_FIELD_SEED = {
        {"materialName", "产品名称", "1", "1"},
        {"specification", "规格", "1", "1"},
        {"model", "型号", "1", "1"},
        {"unitName", "单位", "1", "0"},
        {"price", "单价", "1", "1"},
        {"salePrice", "销售价", "1", "1"},
        {"registerNo", "注册证号", "1", "1"},
        {"registerName", "注册证名称", "1", "1"},
        {"manufacturerName", "生产厂家", "1", "0"},
        {"udiCode", "UDI", "1", "1"},
        {"medicalName", "医保名称", "1", "1"},
        {"medicalNo", "医保编码", "1", "1"},
        {"brand", "品牌", "1", "1"},
        {"useto", "用途", "1", "1"},
        {"quality", "材质", "1", "1"},
        {"functionDesc", "功能", "1", "1"},
        {"isWay", "储存/植入方式", "1", "1"},
        {"countryNo", "国家码", "1", "1"},
        {"countryName", "国家码名称", "1", "1"},
        {"description", "描述", "1", "1"},
        {"periodDate", "有效期", "1", "1"},
        {"packageSpeci", "包装规格", "1", "1"},
        {"minPackageQty", "最小包装数", "1", "1"},
        {"pinyinCode", "拼音简码", "1", "1"},
        {"spdMaterialCode", "院内编码", "1", "0"},
        {"isGz", "是否高值", "1", "1"},
        {"isBilling", "是否计费", "1", "1"}
    };

    @Autowired
    private SpdScmTenantBindMapper spdScmTenantBindMapper;

    @Autowired
    private SpdScmSupplierBindMapper spdScmSupplierBindMapper;

    @Autowired
    private SpdScmBridgeClient spdScmBridgeClient;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private IFdMaterialService fdMaterialService;

    @Autowired
    private SpdScmMaterialArchiveMapper archiveMapper;

    @Autowired
    private SpdScmMaterialPushFieldCfgMapper fieldCfgMapper;

    @Autowired
    private SpdScmMaterialSyncBatchMapper syncBatchMapper;

    @Autowired
    private SpdScmMaterialApplyLogMapper applyLogMapper;

    private String tenantId()
    {
        String tid = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tid))
        {
            tid = SecurityUtils.requiredScopedTenantIdForSql();
        }
        return tid;
    }

    private String hospitalCodeOrThrow()
    {
        String code = spdScmTenantBindMapper.selectHospitalCodeByTenantId(tenantId());
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("请先在「云平台编码绑定」中维护当前租户的平台医院编码");
        }
        return code.trim();
    }

    private String currentUser()
    {
        try
        {
            String u = SecurityUtils.getUsername();
            return StringUtils.isNotEmpty(u) ? u : "spd";
        }
        catch (Exception e)
        {
            return "spd";
        }
    }

    @Override
    public List<Map<String, Object>> listLocalMaterialsForPush(String keyword)
    {
        return archiveMapper.selectPushCandidates(tenantId(), StringUtils.trim(keyword));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object pushSelected(List<Long> materialIds)
    {
        if (materialIds == null || materialIds.isEmpty())
        {
            throw new ServiceException("请选择要推送的产品");
        }
        String hospitalCode = hospitalCodeOrThrow();
        String tid = tenantId();
        ensureDefaultFieldCfg();
        List<String> whitelist = loadPushEnabledFields(tid);
        if (whitelist.isEmpty())
        {
            throw new ServiceException("推送字段白名单为空，请先在字段配置中启用推送字段");
        }

        List<Map<String, Object>> items = new ArrayList<>();
        List<String> skipReasons = new ArrayList<>();
        for (Long mid : materialIds)
        {
            if (mid == null)
            {
                continue;
            }
            FdMaterial m = fdMaterialMapper.selectFdMaterialById(mid);
            if (m == null)
            {
                skipReasons.add(mid + ":产品不存在");
                continue;
            }
            SecurityUtils.ensureTenantAccess(m.getTenantId());
            if (m.getSupplierId() == null)
            {
                skipReasons.add(mid + ":未维护供应商");
                continue;
            }
            SpdScmSupplierBind bind = spdScmSupplierBindMapper.selectByTenantAndSupplier(tid,
                String.valueOf(m.getSupplierId()));
            if (bind == null || StringUtils.isEmpty(bind.getScmSupplierCode()))
            {
                skipReasons.add(mid + ":供应商未绑定平台编码");
                continue;
            }
            Map<String, Object> item = buildPushItem(m, bind.getScmSupplierCode().trim(), whitelist);
            items.add(item);
        }
        if (items.isEmpty())
        {
            throw new ServiceException("无可推送明细：" + String.join("; ", skipReasons));
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("hospitalCode", hospitalCode);
        payload.put("pushBy", currentUser());
        payload.put("fieldWhitelist", whitelist);
        payload.put("items", items);
        Object data = spdScmBridgeClient.invoke(SpdBridgeActions.MATERIAL_ARCHIVE_PUSH, hospitalCode, tid, payload);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("bridgeResult", data);
        out.put("pushedCount", items.size());
        out.put("skipped", skipReasons);
        out.put("fieldWhitelist", whitelist);
        return out;
    }

    private Map<String, Object> buildPushItem(FdMaterial m, String scmSupplierCode, List<String> whitelist)
    {
        Map<String, Object> full = new LinkedHashMap<>();
        full.put("spdMaterialId", String.valueOf(m.getId()));
        full.put("scmSupplierCode", scmSupplierCode);
        full.put("materialName", m.getName());
        full.put("specification", m.getSpeci());
        full.put("model", m.getModel());
        full.put("price", m.getPrice());
        full.put("salePrice", m.getSalePrice());
        full.put("registerNo", m.getRegisterNo());
        full.put("registerName", m.getRegisterName());
        full.put("medicalName", m.getMedicalName());
        full.put("medicalNo", m.getMedicalNo());
        full.put("brand", m.getBrand());
        full.put("useto", m.getUseto());
        full.put("quality", m.getQuality());
        full.put("functionDesc", m.getFunction());
        full.put("isWay", m.getIsWay());
        full.put("udiCode", m.getUdiNo());
        full.put("countryNo", m.getCountryNo());
        full.put("countryName", m.getCountryName());
        full.put("description", m.getDescription());
        full.put("periodDate", m.getPeriodDate());
        full.put("packageSpeci", m.getPackageSpeci());
        full.put("minPackageQty", m.getMinPackageQty());
        full.put("pinyinCode", m.getReferredName());
        full.put("spdMaterialCode", m.getCode());
        full.put("isGz", m.getIsGz());
        full.put("isBilling", m.getIsBilling());
        if (m.getFdFactory() != null)
        {
            full.put("manufacturerName", m.getFdFactory().getFactoryName());
        }
        if (m.getFdUnit() != null)
        {
            full.put("unitName", m.getFdUnit().getUnitName());
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("spdMaterialId", full.get("spdMaterialId"));
        item.put("scmSupplierCode", scmSupplierCode);
        item.put("materialName", full.get("materialName"));
        for (String f : whitelist)
        {
            if (StringUtils.isEmpty(f) || "materialName".equals(f) || "spdMaterialId".equals(f)
                || "scmSupplierCode".equals(f))
            {
                continue;
            }
            if (full.containsKey(f) && full.get(f) != null)
            {
                item.put(f, full.get(f));
            }
        }
        return item;
    }

    private List<String> loadPushEnabledFields(String tid)
    {
        List<SpdScmMaterialPushFieldCfg> cfgs = fieldCfgMapper.selectByTenantId(tid);
        List<String> out = new ArrayList<>();
        for (SpdScmMaterialPushFieldCfg c : cfgs)
        {
            if (c != null && "1".equals(c.getPushEnabled()) && StringUtils.isNotEmpty(c.getFieldCode()))
            {
                out.add(c.getFieldCode());
            }
        }
        return out;
    }

    private List<String> loadApplyEnabledFields(String tid)
    {
        List<SpdScmMaterialPushFieldCfg> cfgs = fieldCfgMapper.selectByTenantId(tid);
        List<String> out = new ArrayList<>();
        for (SpdScmMaterialPushFieldCfg c : cfgs)
        {
            if (c != null && "1".equals(c.getApplyEnabled()) && StringUtils.isNotEmpty(c.getFieldCode())
                && !isIronLock(c.getFieldCode()))
            {
                out.add(c.getFieldCode());
            }
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncFromPlatform(String keyword)
    {
        String hospitalCode = hospitalCodeOrThrow();
        String tid = tenantId();
        String batchId = UUID7.generateUUID7();
        String oper = currentUser();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("hospitalCode", hospitalCode);
        if (StringUtils.isNotEmpty(keyword))
        {
            payload.put("keyword", keyword.trim());
        }

        SpdScmMaterialSyncBatch batch = new SpdScmMaterialSyncBatch();
        batch.setId(batchId);
        batch.setTenantId(tid);
        batch.setHospitalCode(hospitalCode);
        batch.setSyncBy(oper);
        batch.setItemCount(0);
        batch.setResultStatus("1");

        int upserted = 0;
        try
        {
            Object data = spdScmBridgeClient.invoke(SpdBridgeActions.MATERIAL_ARCHIVE_PULL, hospitalCode, tid, payload);
            List<Map<String, Object>> rows = toMapList(data);
            for (Map<String, Object> row : rows)
            {
                if (upsertMirror(tid, hospitalCode, batchId, oper, row))
                {
                    upserted++;
                }
            }
            batch.setItemCount(upserted);
            batch.setResultMsg("synced=" + upserted);
            syncBatchMapper.insert(batch);
        }
        catch (Exception e)
        {
            batch.setItemCount(upserted);
            batch.setResultStatus("2");
            String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            batch.setResultMsg(StringUtils.substring(msg, 0, 480));
            syncBatchMapper.insert(batch);
            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }
            throw new ServiceException("同步平台档案失败：" + msg);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("syncBatchId", batchId);
        out.put("itemCount", upserted);
        out.put("resultStatus", batch.getResultStatus());
        return out;
    }

    private boolean upsertMirror(String tid, String hospitalCode, String batchId, String oper,
        Map<String, Object> row)
    {
        String scmArchiveId = firstStr(row, "id", "scmArchiveId", "scm_archive_id");
        if (StringUtils.isEmpty(scmArchiveId))
        {
            return false;
        }
        String materialName = firstStr(row, "materialName", "material_name");
        if (StringUtils.isEmpty(materialName))
        {
            materialName = "-";
        }
        Date scmUpdateTime = parseDate(firstObj(row, "update_time", "updateTime", "scmUpdateTime", "scm_update_time"));
        String spdMaterialId = firstStr(row, "spdMaterialId", "spd_material_id");
        String scmSupplierCode = firstStr(row, "scmSupplierCode", "scm_supplier_code");
        String spdSupplierId = null;
        if (StringUtils.isNotEmpty(scmSupplierCode))
        {
            // reverse lookup not required; keep null unless spd material has supplier
            if (StringUtils.isNotEmpty(spdMaterialId))
            {
                try
                {
                    FdMaterial local = fdMaterialMapper.selectFdMaterialById(Long.parseLong(spdMaterialId));
                    if (local != null && local.getSupplierId() != null)
                    {
                        spdSupplierId = String.valueOf(local.getSupplierId());
                    }
                }
                catch (NumberFormatException ignored)
                {
                }
            }
        }

        SpdScmMaterialArchive existing = archiveMapper.selectByTenantAndScmArchiveId(tid, scmArchiveId);
        SpdScmMaterialArchive mirror = existing != null ? existing : new SpdScmMaterialArchive();
        if (existing == null)
        {
            mirror.setId(UUID7.generateUUID7());
            mirror.setTenantId(tid);
            mirror.setScmArchiveId(scmArchiveId);
            mirror.setCreateBy(oper);
            mirror.setApplyStatus("0");
        }
        boolean timeChanged = existing == null
            || (scmUpdateTime != null && (existing.getScmUpdateTime() == null
                || scmUpdateTime.after(existing.getScmUpdateTime())));

        mirror.setHospitalCode(firstNonEmpty(firstStr(row, "hospitalCode", "hospital_code"), hospitalCode));
        mirror.setScmSupplierCode(scmSupplierCode);
        mirror.setSpdMaterialId(spdMaterialId);
        mirror.setSpdSupplierId(spdSupplierId);
        mirror.setMaterialName(materialName);
        mirror.setPinyinCode(firstStr(row, "pinyinCode", "pinyin_code"));
        mirror.setSpecification(firstStr(row, "specification", "speci"));
        mirror.setModel(firstStr(row, "model"));
        mirror.setUnitName(firstStr(row, "unitName", "unit_name"));
        mirror.setPrice(toDecimal(firstObj(row, "price")));
        mirror.setSalePrice(toDecimal(firstObj(row, "salePrice", "sale_price")));
        mirror.setRegisterNo(firstStr(row, "registerNo", "register_no"));
        mirror.setRegisterName(firstStr(row, "registerName", "register_name"));
        mirror.setManufacturerName(firstStr(row, "manufacturerName", "manufacturer_name"));
        mirror.setUdiCode(firstStr(row, "udiCode", "udi_code"));
        mirror.setMedicalName(firstStr(row, "medicalName", "medical_name"));
        mirror.setMedicalNo(firstStr(row, "medicalNo", "medical_no"));
        mirror.setBrand(firstStr(row, "brand"));
        mirror.setPayloadJson(JSON.toJSONString(row));
        mirror.setScmUpdateTime(scmUpdateTime);
        mirror.setLastSyncBatchId(batchId);
        mirror.setUpdateBy(oper);
        if (timeChanged)
        {
            mirror.setApplyStatus("0");
        }

        if (existing == null)
        {
            archiveMapper.insert(mirror);
        }
        else
        {
            archiveMapper.update(mirror);
        }
        return true;
    }

    @Override
    public List<SpdScmMaterialArchive> listMirrors(String keyword, String applyStatus)
    {
        return archiveMapper.selectList(tenantId(), StringUtils.trim(keyword), StringUtils.trim(applyStatus));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> apply(String mirrorId, List<String> fields)
    {
        if (StringUtils.isEmpty(mirrorId))
        {
            throw new ServiceException("mirrorId 不能为空");
        }
        String tid = tenantId();
        SpdScmMaterialArchive mirror = archiveMapper.selectById(mirrorId);
        if (mirror == null || !tid.equals(mirror.getTenantId()))
        {
            throw new ServiceException("镜像不存在");
        }
        ensureFreshness(mirror);

        if (StringUtils.isEmpty(mirror.getSpdMaterialId()))
        {
            throw new ServiceException("镜像未绑定院内产品ID，无法应用");
        }
        Long materialId;
        try
        {
            materialId = Long.parseLong(mirror.getSpdMaterialId().trim());
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("院内产品ID无效：" + mirror.getSpdMaterialId());
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(materialId);
        if (material == null)
        {
            throw new ServiceException("院内产品不存在：" + materialId);
        }
        SecurityUtils.ensureTenantAccess(material.getTenantId());

        ensureDefaultFieldCfg();
        List<String> applyEnabled = loadApplyEnabledFields(tid);
        Set<String> requested = new LinkedHashSet<>();
        if (fields == null || fields.isEmpty())
        {
            requested.addAll(applyEnabled);
        }
        else
        {
            for (String f : fields)
            {
                if (StringUtils.isEmpty(f) || isIronLock(f))
                {
                    continue;
                }
                String camel = toCamel(f);
                if (!applyEnabled.contains(camel) && !applyEnabled.contains(f))
                {
                    // 允许显式传入但须在应用白名单中
                    continue;
                }
                requested.add(applyEnabled.contains(camel) ? camel : f);
            }
        }
        if (requested.isEmpty())
        {
            throw new ServiceException("没有可应用的字段（已过滤铁锁/未启用应用）");
        }

        Map<String, Object> source = buildApplySource(mirror);
        String beforeJson = JSON.toJSONString(material);
        List<String> applied = new ArrayList<>();
        for (String field : requested)
        {
            if (isIronLock(field))
            {
                continue;
            }
            if (applyFieldToMaterial(material, field, source))
            {
                applied.add(field);
            }
        }
        if (applied.isEmpty())
        {
            throw new ServiceException("所选字段在镜像中均无有效值，未写入");
        }

        material.setUpdateBy(currentUser());
        fdMaterialService.updateFdMaterial(material);
        FdMaterial after = fdMaterialMapper.selectFdMaterialById(materialId);

        String logId = UUID7.generateUUID7();
        SpdScmMaterialApplyLog log = new SpdScmMaterialApplyLog();
        log.setId(logId);
        log.setTenantId(tid);
        log.setSyncBatchId(mirror.getLastSyncBatchId());
        log.setMirrorId(mirror.getId());
        log.setScmArchiveId(mirror.getScmArchiveId());
        log.setSpdMaterialId(String.valueOf(materialId));
        log.setAppliedFieldsJson(JSON.toJSONString(applied));
        log.setBeforeJson(beforeJson);
        log.setAfterJson(JSON.toJSONString(after));
        log.setApplyBy(currentUser());
        applyLogMapper.insert(log);

        String status = applied.containsAll(new LinkedHashSet<>(applyEnabled)) ? "1" : "2";
        SpdScmMaterialArchive upd = new SpdScmMaterialArchive();
        upd.setId(mirror.getId());
        upd.setApplyStatus(status);
        upd.setLastApplyLogId(logId);
        upd.setUpdateBy(currentUser());
        archiveMapper.update(upd);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("applyLogId", logId);
        out.put("appliedFields", applied);
        out.put("applyStatus", status);
        return out;
    }

    /**
     * MAT-B-005：镜像 scm_update_time 落后于平台正式档则禁止应用
     */
    private void ensureFreshness(SpdScmMaterialArchive mirror)
    {
        Date platformTime = null;
        if (StringUtils.isNotEmpty(mirror.getPayloadJson()))
        {
            try
            {
                JSONObject payload = JSON.parseObject(mirror.getPayloadJson());
                if (payload != null)
                {
                    platformTime = parseDate(firstNonNull(payload.get("update_time"), payload.get("updateTime")));
                }
            }
            catch (Exception ignored)
            {
            }
        }
        try
        {
            String hospitalCode = StringUtils.isNotEmpty(mirror.getHospitalCode())
                ? mirror.getHospitalCode() : hospitalCodeOrThrow();
            Map<String, Object> pullPayload = new LinkedHashMap<>();
            pullPayload.put("hospitalCode", hospitalCode);
            if (StringUtils.isNotEmpty(mirror.getSpdMaterialId()))
            {
                pullPayload.put("keyword", mirror.getSpdMaterialId());
            }
            else if (StringUtils.isNotEmpty(mirror.getMaterialName()))
            {
                pullPayload.put("keyword", mirror.getMaterialName());
            }
            Object data = spdScmBridgeClient.invoke(SpdBridgeActions.MATERIAL_ARCHIVE_PULL, hospitalCode, tenantId(),
                pullPayload);
            for (Map<String, Object> row : toMapList(data))
            {
                String id = firstStr(row, "id", "scmArchiveId", "scm_archive_id");
                if (mirror.getScmArchiveId() != null && mirror.getScmArchiveId().equals(id))
                {
                    Date live = parseDate(firstObj(row, "update_time", "updateTime"));
                    if (live != null)
                    {
                        platformTime = live;
                    }
                    break;
                }
            }
        }
        catch (ServiceException e)
        {
            // 桥不可用时退回 payload 内时间比对
            if (platformTime == null)
            {
                throw e;
            }
        }
        catch (Exception e)
        {
            if (platformTime == null)
            {
                throw new ServiceException("新鲜度校验失败，请先重新同步：" + e.getMessage());
            }
        }

        if (platformTime != null && mirror.getScmUpdateTime() != null && platformTime.after(mirror.getScmUpdateTime()))
        {
            throw new ServiceException("平台档案已更新，请先重新同步后再应用（MAT-B-005）");
        }
    }

    private Map<String, Object> buildApplySource(SpdScmMaterialArchive mirror)
    {
        Map<String, Object> source = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(mirror.getPayloadJson()))
        {
            try
            {
                JSONObject payload = JSON.parseObject(mirror.getPayloadJson());
                if (payload != null)
                {
                    for (String k : payload.keySet())
                    {
                        source.put(k, payload.get(k));
                        source.put(toCamel(k), payload.get(k));
                    }
                }
            }
            catch (Exception ignored)
            {
            }
        }
        putIfAbsent(source, "materialName", mirror.getMaterialName());
        putIfAbsent(source, "specification", mirror.getSpecification());
        putIfAbsent(source, "model", mirror.getModel());
        putIfAbsent(source, "unitName", mirror.getUnitName());
        putIfAbsent(source, "price", mirror.getPrice());
        putIfAbsent(source, "salePrice", mirror.getSalePrice());
        putIfAbsent(source, "registerNo", mirror.getRegisterNo());
        putIfAbsent(source, "registerName", mirror.getRegisterName());
        putIfAbsent(source, "manufacturerName", mirror.getManufacturerName());
        putIfAbsent(source, "udiCode", mirror.getUdiCode());
        putIfAbsent(source, "medicalName", mirror.getMedicalName());
        putIfAbsent(source, "medicalNo", mirror.getMedicalNo());
        putIfAbsent(source, "brand", mirror.getBrand());
        putIfAbsent(source, "pinyinCode", mirror.getPinyinCode());
        return source;
    }

    private boolean applyFieldToMaterial(FdMaterial m, String field, Map<String, Object> source)
    {
        String key = toCamel(field);
        Object val = source.containsKey(key) ? source.get(key) : source.get(field);
        if (val == null && source.containsKey(toSnake(key)))
        {
            val = source.get(toSnake(key));
        }
        // aliases from payload / function_desc
        if (val == null && "functionDesc".equals(key))
        {
            val = firstNonNull(source.get("function_desc"), source.get("function"));
        }
        if (val == null && "udiCode".equals(key))
        {
            val = firstNonNull(source.get("udi_code"), source.get("udiNo"));
        }
        if (val == null && "specification".equals(key))
        {
            val = source.get("speci");
        }
        if (val == null && "pinyinCode".equals(key))
        {
            val = firstNonNull(source.get("pinyin_code"), source.get("referredName"));
        }
        if (val == null)
        {
            return false;
        }
        switch (key)
        {
            case "materialName":
                m.setName(str(val));
                return true;
            case "specification":
                m.setSpeci(str(val));
                return true;
            case "model":
                m.setModel(str(val));
                return true;
            case "price":
                m.setPrice(toDecimal(val));
                return m.getPrice() != null;
            case "salePrice":
                m.setSalePrice(toDecimal(val));
                return m.getSalePrice() != null;
            case "registerNo":
                m.setRegisterNo(str(val));
                return true;
            case "registerName":
                m.setRegisterName(str(val));
                return true;
            case "medicalName":
                m.setMedicalName(str(val));
                return true;
            case "medicalNo":
                m.setMedicalNo(str(val));
                return true;
            case "brand":
                m.setBrand(str(val));
                return true;
            case "useto":
                m.setUseto(str(val));
                return true;
            case "quality":
                m.setQuality(str(val));
                return true;
            case "functionDesc":
                m.setFunction(str(val));
                return true;
            case "isWay":
                m.setIsWay(str(val));
                return true;
            case "udiCode":
                m.setUdiNo(str(val));
                return true;
            case "countryNo":
                m.setCountryNo(str(val));
                return true;
            case "countryName":
                m.setCountryName(str(val));
                return true;
            case "description":
                m.setDescription(str(val));
                return true;
            case "periodDate":
                Date d = parseDate(val);
                if (d == null)
                {
                    return false;
                }
                m.setPeriodDate(d);
                return true;
            case "packageSpeci":
                m.setPackageSpeci(str(val));
                return true;
            case "minPackageQty":
                m.setMinPackageQty(toDecimal(val));
                return m.getMinPackageQty() != null;
            case "pinyinCode":
                m.setReferredName(str(val));
                return true;
            case "isGz":
                m.setIsGz(str(val));
                return true;
            case "isBilling":
                m.setIsBilling(str(val));
                return true;
            case "unitName":
            case "manufacturerName":
            case "spdMaterialCode":
                // 无直接可写列或属铁锁，跳过
                return false;
            default:
                return false;
        }
    }

    @Override
    public List<SpdScmMaterialPushFieldCfg> listFieldCfg()
    {
        ensureDefaultFieldCfg();
        return fieldCfgMapper.selectByTenantId(tenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldCfg(List<SpdScmMaterialPushFieldCfg> list)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }
        String tid = tenantId();
        ensureDefaultFieldCfg();
        String oper = currentUser();
        for (SpdScmMaterialPushFieldCfg in : list)
        {
            if (in == null || StringUtils.isEmpty(in.getFieldCode()))
            {
                continue;
            }
            String code = toCamel(in.getFieldCode().trim());
            if (isIronLock(code))
            {
                // 铁锁字段强制不可应用
                in.setApplyEnabled("0");
            }
            SpdScmMaterialPushFieldCfg existing = fieldCfgMapper.selectByTenantAndField(tid, code);
            if (existing == null)
            {
                SpdScmMaterialPushFieldCfg row = new SpdScmMaterialPushFieldCfg();
                row.setId(UUID7.generateUUID7());
                row.setTenantId(tid);
                row.setFieldCode(code);
                row.setFieldLabel(StringUtils.isNotEmpty(in.getFieldLabel()) ? in.getFieldLabel() : code);
                row.setPushEnabled(normalizeFlag(in.getPushEnabled(), "1"));
                row.setApplyEnabled(normalizeFlag(in.getApplyEnabled(), "1"));
                if (isIronLock(code))
                {
                    row.setApplyEnabled("0");
                }
                row.setSortNo(in.getSortNo() != null ? in.getSortNo() : 0);
                row.setCreateBy(oper);
                fieldCfgMapper.insert(row);
            }
            else
            {
                existing.setFieldLabel(StringUtils.isNotEmpty(in.getFieldLabel()) ? in.getFieldLabel()
                    : existing.getFieldLabel());
                if (in.getPushEnabled() != null)
                {
                    existing.setPushEnabled(normalizeFlag(in.getPushEnabled(), existing.getPushEnabled()));
                }
                if (in.getApplyEnabled() != null)
                {
                    existing.setApplyEnabled(normalizeFlag(in.getApplyEnabled(), existing.getApplyEnabled()));
                }
                if (isIronLock(code))
                {
                    existing.setApplyEnabled("0");
                }
                if (in.getSortNo() != null)
                {
                    existing.setSortNo(in.getSortNo());
                }
                existing.setUpdateBy(oper);
                fieldCfgMapper.updateByTenantAndField(existing);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureDefaultFieldCfg()
    {
        String tid = tenantId();
        List<SpdScmMaterialPushFieldCfg> existing = fieldCfgMapper.selectByTenantId(tid);
        if (existing != null && !existing.isEmpty())
        {
            return;
        }
        String oper = currentUser();
        int sort = 10;
        for (String[] seed : DEFAULT_FIELD_SEED)
        {
            SpdScmMaterialPushFieldCfg row = new SpdScmMaterialPushFieldCfg();
            row.setId(UUID7.generateUUID7());
            row.setTenantId(tid);
            row.setFieldCode(seed[0]);
            row.setFieldLabel(seed[1]);
            row.setPushEnabled(seed[2]);
            row.setApplyEnabled(seed[3]);
            row.setSortNo(sort);
            row.setCreateBy(oper);
            fieldCfgMapper.insert(row);
            sort += 10;
        }
    }

    private static boolean isIronLock(String field)
    {
        return field != null && IRON_LOCK_FIELDS.contains(field);
    }

    private static String normalizeFlag(String v, String def)
    {
        if ("1".equals(v) || "0".equals(v))
        {
            return v;
        }
        if ("true".equalsIgnoreCase(v))
        {
            return "1";
        }
        if ("false".equalsIgnoreCase(v))
        {
            return "0";
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> toMapList(Object data)
    {
        List<Map<String, Object>> out = new ArrayList<>();
        if (data == null)
        {
            return out;
        }
        JSONArray arr;
        if (data instanceof JSONArray)
        {
            arr = (JSONArray) data;
        }
        else if (data instanceof List)
        {
            arr = JSON.parseArray(JSON.toJSONString(data));
        }
        else
        {
            arr = JSON.parseArray(JSON.toJSONString(data));
        }
        if (arr == null)
        {
            return out;
        }
        for (int i = 0; i < arr.size(); i++)
        {
            JSONObject o = arr.getJSONObject(i);
            if (o != null)
            {
                out.add(new LinkedHashMap<>(o));
            }
        }
        return out;
    }

    private static void putIfAbsent(Map<String, Object> m, String k, Object v)
    {
        if (v != null && !m.containsKey(k))
        {
            m.put(k, v);
        }
    }

    private static Object firstObj(Map<String, Object> row, String... keys)
    {
        if (row == null)
        {
            return null;
        }
        for (String k : keys)
        {
            if (row.containsKey(k) && row.get(k) != null)
            {
                return row.get(k);
            }
        }
        return null;
    }

    private static String firstStr(Map<String, Object> row, String... keys)
    {
        return str(firstObj(row, keys));
    }

    private static String firstNonEmpty(String a, String b)
    {
        return StringUtils.isNotEmpty(a) ? a : b;
    }

    private static Object firstNonNull(Object a, Object b)
    {
        return a != null ? a : b;
    }

    private static String str(Object o)
    {
        return o == null ? null : String.valueOf(o).trim();
    }

    private static BigDecimal toDecimal(Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof BigDecimal)
        {
            return (BigDecimal) o;
        }
        try
        {
            return new BigDecimal(String.valueOf(o).trim());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static Date parseDate(Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof Date)
        {
            return (Date) o;
        }
        if (o instanceof Number)
        {
            return new Date(((Number) o).longValue());
        }
        String s = String.valueOf(o).trim();
        if (s.isEmpty())
        {
            return null;
        }
        s = s.replace('T', ' ');
        try
        {
            if (s.length() >= 19)
            {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.substring(0, 19));
            }
            if (s.length() >= 10)
            {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s.substring(0, 10));
            }
        }
        catch (ParseException ignored)
        {
        }
        return null;
    }

    private static String toCamel(String s)
    {
        if (s == null || s.indexOf('_') < 0)
        {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == '_')
            {
                up = true;
            }
            else if (up)
            {
                sb.append(Character.toUpperCase(c));
                up = false;
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String toSnake(String s)
    {
        if (s == null || s.indexOf('_') >= 0)
        {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (Character.isUpperCase(c))
            {
                sb.append('_').append(Character.toLowerCase(c));
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
