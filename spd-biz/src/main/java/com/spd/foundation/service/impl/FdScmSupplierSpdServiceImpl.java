package com.spd.foundation.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.caigou.mapper.SpdScmSupplierBindMapper;
import com.spd.caigou.mapper.SpdScmTenantBindMapper;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.service.IFdScmSupplierSpdService;
import com.spd.foundation.service.IFdSupplierService;
import com.spd.system.service.ISysConfigService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FdScmSupplierSpdServiceImpl implements IFdScmSupplierSpdService
{
    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8081";

    @Autowired
    private SpdScmTenantBindMapper spdScmTenantBindMapper;

    @Autowired
    private SpdScmSupplierBindMapper spdScmSupplierBindMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private FdSupplierMapper fdSupplierMapper;

    @Autowired
    private IFdSupplierService fdSupplierService;

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

    private String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));
        if (StringUtils.isEmpty(ip))
        {
            ip = DEFAULT_INTERFACE_IP;
        }
        if (!port.matches("\\d{1,5}"))
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        int portNum = Integer.parseInt(port);
        if (portNum < 1 || portNum > 65535)
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        return "http://" + ip + ":" + port;
    }

    private JSONObject httpGetJson(String pathWithQuery) throws Exception
    {
        String url = buildInterfaceBaseUrl() + pathWithQuery;
        String body = HttpUtils.sendGet(url, "", "UTF-8");
        if (StringUtils.isEmpty(body))
        {
            throw new ServiceException("前置机无响应");
        }
        JSONObject root = JSON.parseObject(body);
        if (root == null || root.getIntValue("code") != 200)
        {
            String msg = root != null ? root.getString("msg") : "解析失败";
            throw new ServiceException(StringUtils.isNotEmpty(msg) ? msg : "前置机返回异常");
        }
        return root;
    }

    @Override
    public List<Map<String, Object>> listScmSuppliersForTenantHospital()
    {
        try
        {
            String hc = URLEncoder.encode(hospitalCodeOrThrow(), StandardCharsets.UTF_8.name());
            JSONObject root = httpGetJson("/api/spd/scmSupplier/listByHospital?hospitalCode=" + hc);
            com.alibaba.fastjson2.JSONArray arr = root.getJSONArray("data");
            List<Map<String, Object>> out = new ArrayList<>();
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    JSONObject o = arr.getJSONObject(i);
                    if (o != null)
                    {
                        out.add(new LinkedHashMap<>(o));
                    }
                }
            }
            return out;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("拉取平台供应商列表失败：" + e.getMessage());
        }
    }

    @Override
    public JSONObject loadScmSupplierProfile(String scmSupplierCode)
    {
        if (StringUtils.isEmpty(scmSupplierCode))
        {
            throw new ServiceException("平台供应商编码不能为空");
        }
        try
        {
            String hc = URLEncoder.encode(hospitalCodeOrThrow(), StandardCharsets.UTF_8.name());
            String sc = URLEncoder.encode(scmSupplierCode.trim(), StandardCharsets.UTF_8.name());
            String tid = URLEncoder.encode(tenantId(), StandardCharsets.UTF_8.name());
            JSONObject root = httpGetJson("/api/spd/scmSupplier/profile?hospitalCode=" + hc + "&supplierCode=" + sc
                + "&spdTenantId=" + tid);
            JSONObject data = root.getJSONObject("data");
            return data != null ? data : new JSONObject();
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("拉取平台供应商档案失败：" + e.getMessage());
        }
    }

    @Override
    public JSONObject buildExportPayload(Long spdSupplierId)
    {
        String scmCode = resolveScmSupplierCode(spdSupplierId);
        if (StringUtils.isEmpty(scmCode))
        {
            throw new ServiceException("未找到该供应商的平台编码绑定，无法下载");
        }
        JSONObject profile = loadScmSupplierProfile(scmCode);
        boolean bound = profile.getBooleanValue("hospitalSupplierBound");
        @SuppressWarnings("unchecked")
        Map<String, Object> supplier = profile.getObject("supplier", Map.class);
        if (supplier == null || supplier.isEmpty())
        {
            throw new ServiceException("平台未返回供应商数据");
        }
        if (!bound)
        {
            int n = purchaseOrderMapper.countOrderWithScmSupplierSnapshot(tenantId(), spdSupplierId, scmCode);
            if (n <= 0)
            {
                throw new ServiceException("该供应商与医院在平台无供货绑定，且本院无带平台供应商编码的采购订单记录，不允许下载");
            }
        }
        JSONObject out = new JSONObject();
        out.put("exportScope", bound ? "FULL" : "LIMITED");
        out.put("scmSupplierCode", scmCode);
        out.put("spdSupplierId", spdSupplierId);
        out.put("supplier", bound ? supplier : stripLimitedSupplier(supplier));
        return out;
    }

    private Map<String, Object> stripLimitedSupplier(Map<String, Object> src)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        String[] keys = { "supplierCode", "companyName", "companyShortName", "contactPerson", "contactPhone",
            "taxNumber", "province", "city", "district", "address" };
        for (String k : keys)
        {
            if (src.containsKey(k))
            {
                m.put(k, src.get(k));
            }
        }
        return m;
    }

    @Override
    public String resolveScmSupplierCode(Long spdSupplierId)
    {
        if (spdSupplierId == null)
        {
            return null;
        }
        SpdScmSupplierBind b = spdScmSupplierBindMapper.selectByTenantAndSupplier(tenantId(), String.valueOf(spdSupplierId));
        if (b == null || StringUtils.isEmpty(b.getScmSupplierCode()))
        {
            return null;
        }
        return b.getScmSupplierCode().trim();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeScmIntoFdSupplier(Long spdSupplierId, boolean overwriteNonEmpty)
    {
        FdSupplier local = fdSupplierMapper.selectFdSupplierById(spdSupplierId);
        if (local == null)
        {
            throw new ServiceException("院内供应商不存在");
        }
        SecurityUtils.ensureTenantAccess(local.getTenantId());
        String scmCode = resolveScmSupplierCode(spdSupplierId);
        if (StringUtils.isEmpty(scmCode))
        {
            throw new ServiceException("请先维护平台供应商编码绑定");
        }
        JSONObject profile = loadScmSupplierProfile(scmCode);
        @SuppressWarnings("unchecked")
        Map<String, Object> scm = profile.getObject("supplier", Map.class);
        if (scm == null || scm.isEmpty())
        {
            throw new ServiceException("平台供应商数据为空");
        }
        applyScmMapToFdSupplier(local, scm, overwriteNonEmpty);
        fdSupplierService.updateFdSupplier(local);
    }

    private void applyScmMapToFdSupplier(FdSupplier t, Map<String, Object> scm, boolean overwriteNonEmpty)
    {
        putStrIf(t::getName, t::setName, str(scm.get("companyName")), overwriteNonEmpty);
        putStrIf(t::getContacts, t::setContacts, str(scm.get("contactPerson")), overwriteNonEmpty);
        putStrIf(t::getContactsPhone, t::setContactsPhone, str(scm.get("contactPhone")), overwriteNonEmpty);
        putStrIf(t::getTaxNumber, t::setTaxNumber, str(scm.get("taxNumber")), overwriteNonEmpty);
        putStrIf(t::getLegalPerson, t::setLegalPerson, str(scm.get("legalPerson")), overwriteNonEmpty);
        putStrIf(t::getEmail, t::setEmail, str(scm.get("email")), overwriteNonEmpty);
        putStrIf(t::getWebsite, t::setWebsite, str(scm.get("website")), overwriteNonEmpty);
        String addr = joinAddr(str(scm.get("province")), str(scm.get("city")), str(scm.get("district")), str(scm.get("address")));
        if (StringUtils.isNotEmpty(addr) && (overwriteNonEmpty || StringUtils.isEmpty(t.getAddress())))
        {
            t.setAddress(addr);
        }
        Object reg = scm.get("registeredCapital");
        if (reg != null)
        {
            try
            {
                BigDecimal bd = new BigDecimal(String.valueOf(reg));
                if (overwriteNonEmpty || t.getRegMoney() == null)
                {
                    t.setRegMoney(bd);
                }
            }
            catch (Exception ignored)
            {
            }
        }
        Object qd = scm.get("qualificationExpiryDate");
        if (qd != null)
        {
            String qs = String.valueOf(qd);
            if (qs.length() >= 10 && (overwriteNonEmpty || t.getValidTime() == null))
            {
                try
                {
                    t.setValidTime(com.spd.common.utils.DateUtils.parseDate(qs.substring(0, 10)));
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }

    private void putStrIf(java.util.function.Supplier<String> getter, java.util.function.Consumer<String> setter,
        String val, boolean overwriteNonEmpty)
    {
        if (StringUtils.isEmpty(val))
        {
            return;
        }
        String cur = getter.get();
        if (overwriteNonEmpty || StringUtils.isEmpty(cur))
        {
            setter.accept(val);
        }
    }

    private static String str(Object o)
    {
        return o == null ? null : String.valueOf(o).trim();
    }

    private static String joinAddr(String p, String c, String d, String a)
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(p))
        {
            sb.append(p);
        }
        if (StringUtils.isNotEmpty(c))
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(c);
        }
        if (StringUtils.isNotEmpty(d))
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(d);
        }
        if (StringUtils.isNotEmpty(a))
        {
            if (sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(a);
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
