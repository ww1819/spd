package com.spd.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.dashboard.mapper.HomeUserWorkspaceMapper;

/**
 * 首页默认风格（简洁/完整）、菜单点击统计
 */
@Service
public class HomeUserWorkspaceService
{
    private static final Set<String> SKIP_PATHS = new HashSet<String>(Arrays.asList(
        "/", "/index", "/login", "/register", "/sso-callback", "/404", "/401", "/user/profile"));

    @Autowired
    private HomeUserWorkspaceMapper homeUserWorkspaceMapper;

    public String currentHomeView()
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            return "simple";
        }
        String view = homeUserWorkspaceMapper.selectHomeView(userId, tenantKey());
        return normalizeView(view);
    }

    public void saveHomeView(String homeView)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            throw new IllegalArgumentException("未登录");
        }
        String view = normalizeView(homeView);
        homeUserWorkspaceMapper.upsertHomeView(UUID7.generateUUID7(), userId, tenantKey(), view);
    }

    public void recordMenuHit(String rawPath, String rawTitle)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            return;
        }
        String path = normalizePath(rawPath);
        if (path == null)
        {
            return;
        }
        String title = StringUtils.isEmpty(rawTitle) ? null : StringUtils.substring(rawTitle.trim(), 0, 64);
        homeUserWorkspaceMapper.upsertMenuHit(UUID7.generateUUID7(), userId, tenantKey(), path, title);
    }

    public List<Map<String, Object>> topMenus(int limit)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            return new ArrayList<Map<String, Object>>();
        }
        int n = limit < 1 ? 8 : Math.min(limit, 20);
        List<Map<String, Object>> list = homeUserWorkspaceMapper.selectTopMenus(userId, tenantKey(), n);
        return list != null ? list : new ArrayList<Map<String, Object>>();
    }

    /**
     * 近 N 日 KPI 序列（租户级轻量聚合，供 sparkline / 较昨日）。库存不做历史趋势。
     */
    public Map<String, Object> kpiTrend(int days)
    {
        int n = days < 2 ? 7 : Math.min(days, 14);
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(n - 1);
        ZoneId zone = ZoneId.systemDefault();
        Date begin = Date.from(start.atStartOfDay(zone).toInstant());
        Date end = Date.from(today.atTime(23, 59, 59).atZone(zone).toInstant());
        List<String> dayList = new ArrayList<String>(n);
        Map<String, Integer> index = new HashMap<String, Integer>(n * 2);
        for (int i = 0; i < n; i++)
        {
            String key = start.plusDays(i).toString();
            dayList.add(key);
            index.put(key, Integer.valueOf(i));
        }
        BigDecimal[] inArr = zeros(n);
        BigDecimal[] outArr = zeros(n);
        BigDecimal[] retArr = zeros(n);
        BigDecimal[] applyArr = zeros(n);
        BigDecimal[] purchaseArr = zeros(n);
        List<Map<String, Object>> ioRows = homeUserWorkspaceMapper.selectHomeIoQtyTrendByDay(begin, end);
        fillTrend(ioRows, index, "inCount", inArr);
        fillTrend(ioRows, index, "outCount", outArr);
        fillTrend(ioRows, index, "returnCount", retArr);
        fillTrend(homeUserWorkspaceMapper.selectHomeApplyQtyTrendByDay(begin, end), index, "applyCount", applyArr);
        fillTrend(homeUserWorkspaceMapper.selectHomePurchaseQtyTrendByDay(begin, end), index, "purchaseCount", purchaseArr);

        Map<String, Object> body = new HashMap<String, Object>(12);
        body.put("days", dayList);
        body.put("inCount", asList(inArr));
        body.put("outCount", asList(outArr));
        body.put("returnCount", asList(retArr));
        body.put("applyCount", asList(applyArr));
        body.put("purchaseCount", asList(purchaseArr));
        int todayIdx = n - 1;
        int yIdx = n >= 2 ? n - 2 : 0;
        body.put("today", snapshot(inArr, outArr, retArr, applyArr, purchaseArr, todayIdx));
        body.put("yesterday", snapshot(inArr, outArr, retArr, applyArr, purchaseArr, yIdx));
        return body;
    }

    private static void fillTrend(List<Map<String, Object>> rows, Map<String, Integer> index,
        String field, BigDecimal[] target)
    {
        if (rows == null || rows.isEmpty())
        {
            return;
        }
        for (Map<String, Object> row : rows)
        {
            if (row == null)
            {
                continue;
            }
            Integer i = index.get(dayKey(row.get("dayStr")));
            if (i == null)
            {
                continue;
            }
            target[i.intValue()] = nz(row.get(field));
        }
    }

    private static Map<String, Object> snapshot(BigDecimal[] inArr, BigDecimal[] outArr, BigDecimal[] retArr,
        BigDecimal[] applyArr, BigDecimal[] purchaseArr, int i)
    {
        Map<String, Object> m = new HashMap<String, Object>(8);
        m.put("inCount", inArr[i]);
        m.put("outCount", outArr[i]);
        m.put("returnCount", retArr[i]);
        m.put("applyCount", applyArr[i]);
        m.put("purchaseCount", purchaseArr[i]);
        return m;
    }

    private static List<BigDecimal> asList(BigDecimal[] arr)
    {
        List<BigDecimal> list = new ArrayList<BigDecimal>(arr.length);
        for (BigDecimal v : arr)
        {
            list.add(v != null ? v : BigDecimal.ZERO);
        }
        return list;
    }

    private static BigDecimal[] zeros(int n)
    {
        BigDecimal[] arr = new BigDecimal[n];
        for (int i = 0; i < n; i++)
        {
            arr[i] = BigDecimal.ZERO;
        }
        return arr;
    }

    private static String dayKey(Object v)
    {
        if (v == null)
        {
            return "";
        }
        String s = String.valueOf(v).trim();
        return s.length() >= 10 ? s.substring(0, 10) : s;
    }

    private static BigDecimal nz(Object v)
    {
        if (v == null)
        {
            return BigDecimal.ZERO;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
        }
        if (v instanceof Number)
        {
            return BigDecimal.valueOf(((Number) v).doubleValue());
        }
        try
        {
            return new BigDecimal(String.valueOf(v));
        }
        catch (Exception e)
        {
            return BigDecimal.ZERO;
        }
    }

    private static String tenantKey()
    {
        String tenantId = SecurityUtils.getCustomerId();
        return tenantId == null ? "" : tenantId;
    }

    private static String normalizeView(String homeView)
    {
        if (homeView == null)
        {
            return "simple";
        }
        String v = homeView.trim().toLowerCase();
        if ("full".equals(v) || "complete".equals(v))
        {
            return "full";
        }
        return "simple";
    }

    private static String normalizePath(String rawPath)
    {
        if (StringUtils.isEmpty(rawPath))
        {
            return null;
        }
        String path = rawPath.trim();
        int q = path.indexOf('?');
        if (q >= 0)
        {
            path = path.substring(0, q);
        }
        int hash = path.indexOf('#');
        if (hash >= 0)
        {
            path = path.substring(0, hash);
        }
        if (path.length() > 255 || !path.startsWith("/") || path.contains("..") || path.contains(" "))
        {
            return null;
        }
        if (path.length() > 1 && path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        if (SKIP_PATHS.contains(path) || path.startsWith("/redirect") || path.startsWith("/tenant-switch"))
        {
            return null;
        }
        return path;
    }
}
