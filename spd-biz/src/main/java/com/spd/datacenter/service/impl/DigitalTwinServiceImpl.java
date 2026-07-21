package com.spd.datacenter.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.StringUtils;
import com.spd.datacenter.mapper.DigitalTwinMapper;
import com.spd.datacenter.service.IDigitalTwinService;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.service.IStkInventoryService;

/**
 * 数字孪生监控大屏：五区布局 + 三色状态
 */
@Service
public class DigitalTwinServiceImpl implements IDigitalTwinService
{
    public static final String ZONE_PENDING_CHECK = "PENDING_CHECK";
    public static final String ZONE_QUALIFIED = "QUALIFIED";
    public static final String ZONE_UNQUALIFIED = "UNQUALIFIED";
    public static final String ZONE_RETURN = "RETURN";
    public static final String ZONE_PENDING_SHIP = "PENDING_SHIP";

    private static final String[] ZONE_ORDER = {
        ZONE_PENDING_CHECK, ZONE_QUALIFIED, ZONE_UNQUALIFIED, ZONE_RETURN, ZONE_PENDING_SHIP
    };

    private static final Map<String, String> ZONE_LABELS = new LinkedHashMap<>();
    static
    {
        ZONE_LABELS.put(ZONE_PENDING_CHECK, "待验区");
        ZONE_LABELS.put(ZONE_QUALIFIED, "合格区");
        ZONE_LABELS.put(ZONE_UNQUALIFIED, "不合格区");
        ZONE_LABELS.put(ZONE_RETURN, "退货区");
        ZONE_LABELS.put(ZONE_PENDING_SHIP, "待发区");
    }

    @Autowired
    private DigitalTwinMapper digitalTwinMapper;

    @Autowired
    private IStkInventoryService stkInventoryService;

    @Override
    public Map<String, Object> overview(Long warehouseId)
    {
        Map<String, Object> totals = digitalTwinMapper.selectInventoryTotals(warehouseId);
        Map<String, Object> ioCount = digitalTwinMapper.selectTodayIoBillCount(warehouseId);
        if (totals == null)
        {
            totals = new HashMap<>();
        }
        if (ioCount == null)
        {
            ioCount = new HashMap<>();
        }

        StkInventory q = new StkInventory();
        q.setWarehouseId(warehouseId);
        q.setAlertStatus("1");
        List<Map<String, Object>> invAlerts = stkInventoryService.selectInventoryAlertList(q);
        List<Map<String, Object>> expAlerts = stkInventoryService.selectExpiryAlertList(q);

        Map<String, Object> shelves = shelves(warehouseId);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> zones = (List<Map<String, Object>>) shelves.get("zones");

        Map<String, Object> body = new HashMap<>(16);
        body.put("totalQty", toBd(totals.get("totalQty")).setScale(2, RoundingMode.HALF_UP));
        body.put("totalAmt", toBd(totals.get("totalAmt")).setScale(2, RoundingMode.HALF_UP));
        body.put("materialSkuCount", toLong(totals.get("materialSkuCount")));
        body.put("todayInboundBillCount", toLong(ioCount.get("todayInboundBillCount")));
        body.put("todayOutboundBillCount", toLong(ioCount.get("todayOutboundBillCount")));
        body.put("inventoryAlertCount", invAlerts != null ? invAlerts.size() : 0);
        body.put("expiryAlertCount", expAlerts != null ? expAlerts.size() : 0);
        body.put("zoneOccupancy", buildZoneOccupancy(zones));
        body.put("syncTime", System.currentTimeMillis());
        return body;
    }

    @Override
    public Map<String, Object> shelves(Long warehouseId)
    {
        List<Map<String, Object>> locations = digitalTwinMapper.selectLocations(warehouseId);
        if (locations == null)
        {
            locations = new ArrayList<>();
        }
        List<Map<String, Object>> stockAgg = digitalTwinMapper.selectLocationStockAgg(warehouseId);
        Map<Long, Map<String, Object>> stockByLoc = new HashMap<>();
        if (stockAgg != null)
        {
            for (Map<String, Object> row : stockAgg)
            {
                Long locId = toLong(row.get("locationId"));
                if (locId != null)
                {
                    stockByLoc.put(locId, row);
                }
            }
        }

        // 按区自动补坐标
        Map<String, Integer> zoneCounters = new HashMap<>();
        for (String z : ZONE_ORDER)
        {
            zoneCounters.put(z, 0);
        }

        Map<String, List<Map<String, Object>>> byZone = new LinkedHashMap<>();
        for (String z : ZONE_ORDER)
        {
            byZone.put(z, new ArrayList<>());
        }

        for (Map<String, Object> loc : locations)
        {
            String zone = resolveZoneType(loc);
            loc.put("zoneType", zone);
            loc.put("zoneName", ZONE_LABELS.getOrDefault(zone, zone));

            ensureAutoLayout(loc, zone, zoneCounters);

            Long locId = toLong(loc.get("locationId"));
            Map<String, Object> stock = locId != null ? stockByLoc.get(locId) : null;
            applyStatusColor(loc, stock);

            byZone.computeIfAbsent(zone, k -> new ArrayList<>()).add(loc);
        }

        List<Map<String, Object>> zones = new ArrayList<>();
        for (String z : ZONE_ORDER)
        {
            List<Map<String, Object>> slots = byZone.getOrDefault(z, Collections.emptyList());
            Map<String, List<Map<String, Object>>> shelvesMap = groupByShelf(slots);
            List<Map<String, Object>> shelfList = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> e : shelvesMap.entrySet())
            {
                Map<String, Object> shelf = new HashMap<>();
                shelf.put("shelfCode", e.getKey());
                List<Map<String, Object>> slotsSorted = new ArrayList<>(e.getValue());
                slotsSorted.sort(Comparator
                    .comparing((Map<String, Object> m) -> toInt(m.get("layerNo")), Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(m -> toInt(m.get("slotNo")), Comparator.nullsLast(Integer::compareTo)));
                shelf.put("slots", slotsSorted);
                shelf.put("statusColor", worstColor(slotsSorted));
                shelfList.add(shelf);
            }
            Map<String, Object> zoneNode = new HashMap<>();
            zoneNode.put("zoneType", z);
            zoneNode.put("zoneName", ZONE_LABELS.get(z));
            zoneNode.put("shelfCount", shelfList.size());
            zoneNode.put("slotCount", slots.size());
            zoneNode.put("occupiedCount", countOccupied(slots));
            zoneNode.put("shelves", shelfList);
            zoneNode.put("slots", slots);
            zones.add(zoneNode);
        }

        Map<String, Object> result = new HashMap<>(4);
        result.put("zones", zones);
        result.put("warehouseId", warehouseId);
        result.put("syncTime", System.currentTimeMillis());
        return result;
    }

    @Override
    public Map<String, Object> alerts(Long warehouseId)
    {
        StkInventory q = new StkInventory();
        q.setWarehouseId(warehouseId);
        q.setAlertStatus("1");
        List<Map<String, Object>> inventoryAlerts = stkInventoryService.selectInventoryAlertList(q);
        List<Map<String, Object>> expiryAlerts = stkInventoryService.selectExpiryAlertList(q);
        if (inventoryAlerts == null)
        {
            inventoryAlerts = new ArrayList<>();
        }
        if (expiryAlerts == null)
        {
            expiryAlerts = new ArrayList<>();
        }

        Map<String, Long> materialToLoc = buildMaterialHintMap(warehouseId);

        for (Map<String, Object> a : inventoryAlerts)
        {
            a.put("alertType", "INVENTORY");
            a.put("statusColor", "red");
            String code = str(a.get("materialCode"));
            if (materialToLoc.containsKey(code))
            {
                a.put("locationId", materialToLoc.get(code));
            }
        }
        for (Map<String, Object> a : expiryAlerts)
        {
            a.put("alertType", "EXPIRY");
            Integer daysObj = toInt(a.get("daysRemaining"));
            int days = daysObj != null ? daysObj : 999;
            a.put("statusColor", days < 0 ? "red" : "yellow");
            String code = str(a.get("materialCode"));
            if (materialToLoc.containsKey(code))
            {
                a.put("locationId", materialToLoc.get(code));
            }
        }

        Map<String, Object> body = new HashMap<>(4);
        body.put("inventoryAlerts", inventoryAlerts.size() > 100 ? inventoryAlerts.subList(0, 100) : inventoryAlerts);
        body.put("expiryAlerts", expiryAlerts.size() > 100 ? expiryAlerts.subList(0, 100) : expiryAlerts);
        body.put("syncTime", System.currentTimeMillis());
        return body;
    }

    @Override
    public List<Map<String, Object>> ioRealtime(Long warehouseId, Integer limit)
    {
        int lim = limit == null || limit <= 0 ? 30 : Math.min(limit, 100);
        List<Map<String, Object>> list = digitalTwinMapper.selectIoRealtime(warehouseId, lim);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> locationDetail(Long locationId, Long warehouseId)
    {
        if (locationId == null)
        {
            return new ArrayList<>();
        }
        List<Map<String, Object>> list = digitalTwinMapper.selectLocationStockDetail(locationId, warehouseId);
        return list != null ? list : new ArrayList<>();
    }

    private Map<String, Long> buildMaterialHintMap(Long warehouseId)
    {
        Map<String, Long> map = new HashMap<>();
        List<Map<String, Object>> hints = digitalTwinMapper.selectMaterialLocationHints(warehouseId);
        if (hints == null)
        {
            return map;
        }
        for (Map<String, Object> h : hints)
        {
            String code = str(h.get("materialCode"));
            Long locId = toLong(h.get("locationId"));
            if (StringUtils.isNotEmpty(code) && locId != null && !map.containsKey(code))
            {
                map.put(code, locId);
            }
        }
        return map;
    }

    private List<Map<String, Object>> buildZoneOccupancy(List<Map<String, Object>> zones)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if (zones == null)
        {
            return list;
        }
        for (Map<String, Object> z : zones)
        {
            Map<String, Object> row = new HashMap<>();
            int slotCount = toInt(z.get("slotCount")) != null ? toInt(z.get("slotCount")) : 0;
            int occupied = toInt(z.get("occupiedCount")) != null ? toInt(z.get("occupiedCount")) : 0;
            row.put("zoneType", z.get("zoneType"));
            row.put("zoneName", z.get("zoneName"));
            row.put("slotCount", slotCount);
            row.put("occupiedCount", occupied);
            BigDecimal rate = slotCount <= 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(occupied * 100.0 / slotCount).setScale(1, RoundingMode.HALF_UP);
            row.put("occupancyRate", rate);
            list.add(row);
        }
        return list;
    }

    private String resolveZoneType(Map<String, Object> loc)
    {
        String zone = str(loc.get("zoneType"));
        if (StringUtils.isNotEmpty(zone))
        {
            return zone;
        }
        String name = str(loc.get("locationName"));
        if (name.contains("待验") || name.contains("验收"))
        {
            return ZONE_PENDING_CHECK;
        }
        if (name.contains("不合格") || name.contains("隔离"))
        {
            return ZONE_UNQUALIFIED;
        }
        if (name.contains("退货"))
        {
            return ZONE_RETURN;
        }
        if (name.contains("待发") || name.contains("发货"))
        {
            return ZONE_PENDING_SHIP;
        }
        return ZONE_QUALIFIED;
    }

    private void ensureAutoLayout(Map<String, Object> loc, String zone, Map<String, Integer> zoneCounters)
    {
        if (loc.get("posX") != null && loc.get("posY") != null)
        {
            if (StringUtils.isEmpty(str(loc.get("shelfCode"))))
            {
                loc.put("shelfCode", defaultShelfCode(zone, toInt(loc.get("posX"))));
            }
            return;
        }
        int idx = zoneCounters.getOrDefault(zone, 0);
        zoneCounters.put(zone, idx + 1);
        // 每区网格：4 列，列间距 2.2m，行间距 1.8m；各区有基准偏移
        int col = idx % 4;
        int row = idx / 4;
        double[] base = zoneBaseOffset(zone);
        loc.put("posX", BigDecimal.valueOf(base[0] + col * 2.2).setScale(2, RoundingMode.HALF_UP));
        loc.put("posY", BigDecimal.valueOf(base[1] + row * 1.8).setScale(2, RoundingMode.HALF_UP));
        if (loc.get("posZ") == null)
        {
            loc.put("posZ", BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(str(loc.get("shelfCode"))))
        {
            String prefix = zoneShelfPrefix(zone);
            loc.put("shelfCode", prefix + String.format("%02d", col + 1));
        }
        if (loc.get("layerNo") == null)
        {
            loc.put("layerNo", (row % 4) + 1);
        }
        if (loc.get("slotNo") == null)
        {
            loc.put("slotNo", (idx % 4) + 1);
        }
    }

    private double[] zoneBaseOffset(String zone)
    {
        switch (zone)
        {
            case ZONE_PENDING_CHECK:
                return new double[] { 0, 0 };
            case ZONE_QUALIFIED:
                return new double[] { 10, 0 };
            case ZONE_UNQUALIFIED:
                return new double[] { 22, 0 };
            case ZONE_RETURN:
                return new double[] { 22, 8 };
            case ZONE_PENDING_SHIP:
                return new double[] { 0, 8 };
            default:
                return new double[] { 10, 0 };
        }
    }

    private String zoneShelfPrefix(String zone)
    {
        switch (zone)
        {
            case ZONE_PENDING_CHECK:
                return "T";
            case ZONE_QUALIFIED:
                return "A";
            case ZONE_UNQUALIFIED:
                return "B";
            case ZONE_RETURN:
                return "R";
            case ZONE_PENDING_SHIP:
                return "S";
            default:
                return "X";
        }
    }

    private String defaultShelfCode(String zone, Integer posX)
    {
        return zoneShelfPrefix(zone) + "01";
    }

    private void applyStatusColor(Map<String, Object> loc, Map<String, Object> stock)
    {
        if (stock == null)
        {
            loc.put("qty", BigDecimal.ZERO);
            loc.put("amt", BigDecimal.ZERO);
            loc.put("skuCount", 0);
            loc.put("statusColor", "empty");
            loc.put("alertReason", "");
            return;
        }
        BigDecimal qty = toBd(stock.get("qty"));
        loc.put("qty", qty.setScale(2, RoundingMode.HALF_UP));
        loc.put("amt", toBd(stock.get("amt")).setScale(2, RoundingMode.HALF_UP));
        loc.put("skuCount", toLong(stock.get("skuCount")));
        loc.put("minDaysToExpiry", stock.get("minDaysToExpiry"));

        int stockLevel = toInt(stock.get("stockAlertLevel")) != null ? toInt(stock.get("stockAlertLevel")) : 0;
        int expiryLevel = toInt(stock.get("expiryAlertLevel")) != null ? toInt(stock.get("expiryAlertLevel")) : 0;
        int level = Math.max(stockLevel, expiryLevel);

        List<String> reasons = new ArrayList<>();
        if (stockLevel >= 2)
        {
            reasons.add("库存低于下限");
        }
        else if (stockLevel == 1)
        {
            reasons.add("库存超出上限");
        }
        if (expiryLevel >= 2)
        {
            reasons.add("已过期");
        }
        else if (expiryLevel == 1)
        {
            reasons.add("近效期预警");
        }

        if (level >= 2)
        {
            loc.put("statusColor", "red");
        }
        else if (level == 1)
        {
            loc.put("statusColor", "yellow");
        }
        else if (qty.compareTo(BigDecimal.ZERO) > 0)
        {
            loc.put("statusColor", "green");
        }
        else
        {
            loc.put("statusColor", "empty");
        }
        loc.put("alertReason", String.join("；", reasons));
    }

    private Map<String, List<Map<String, Object>>> groupByShelf(List<Map<String, Object>> slots)
    {
        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        for (Map<String, Object> s : slots)
        {
            String code = str(s.get("shelfCode"));
            if (StringUtils.isEmpty(code))
            {
                code = "未编号";
            }
            map.computeIfAbsent(code, k -> new ArrayList<>()).add(s);
        }
        return map;
    }

    private String worstColor(List<Map<String, Object>> slots)
    {
        int rank = 0;
        for (Map<String, Object> s : slots)
        {
            String c = str(s.get("statusColor"));
            int r = colorRank(c);
            if (r > rank)
            {
                rank = r;
            }
        }
        if (rank >= 3)
        {
            return "red";
        }
        if (rank == 2)
        {
            return "yellow";
        }
        if (rank == 1)
        {
            return "green";
        }
        return "empty";
    }

    private int colorRank(String c)
    {
        if ("red".equals(c))
        {
            return 3;
        }
        if ("yellow".equals(c))
        {
            return 2;
        }
        if ("green".equals(c))
        {
            return 1;
        }
        return 0;
    }

    private int countOccupied(List<Map<String, Object>> slots)
    {
        int n = 0;
        for (Map<String, Object> s : slots)
        {
            if (!"empty".equals(str(s.get("statusColor"))))
            {
                n++;
            }
        }
        return n;
    }

    private static BigDecimal toBd(Object v)
    {
        if (v == null)
        {
            return BigDecimal.ZERO;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
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

    private static Long toLong(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof Number)
        {
            return ((Number) v).longValue();
        }
        try
        {
            return Long.parseLong(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static Integer toInt(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof Number)
        {
            return ((Number) v).intValue();
        }
        try
        {
            return Integer.parseInt(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static String str(Object v)
    {
        return v == null ? "" : String.valueOf(v).trim();
    }
}
