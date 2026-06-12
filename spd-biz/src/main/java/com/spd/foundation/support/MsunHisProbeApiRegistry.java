package com.spd.foundation.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 枣强众阳 HIS 联调接口（SPD → scminterface 白名单路径）。
 */
public final class MsunHisProbeApiRegistry
{
    public static final class ApiDef
    {
        private final String key;
        private final String httpMethod;
        private final String pathSuffix;
        private final boolean forceMaterialOrDrug;
        private final boolean invalidFlagSweep;

        ApiDef(String key, String httpMethod, String pathSuffix, boolean forceMaterialOrDrug, boolean invalidFlagSweep)
        {
            this.key = key;
            this.httpMethod = httpMethod;
            this.pathSuffix = pathSuffix;
            this.forceMaterialOrDrug = forceMaterialOrDrug;
            this.invalidFlagSweep = invalidFlagSweep;
        }

        public String getKey()
        {
            return key;
        }

        public String getHttpMethod()
        {
            return httpMethod;
        }

        public String getPathSuffix()
        {
            return pathSuffix;
        }

        public boolean isForceMaterialOrDrug()
        {
            return forceMaterialOrDrug;
        }

        public boolean isInvalidFlagSweep()
        {
            return invalidFlagSweep;
        }
    }

    private static final Map<String, ApiDef> MAP = new HashMap<>(16);

    static
    {
        register(new ApiDef("env", "GET", "/probe/env", false, false));
        register(new ApiDef("depts", "GET", "/probe/depts", false, false));
        register(new ApiDef("identities", "GET", "/probe/identities", false, false));
        register(new ApiDef("identitiesSample", "GET", "/probe/identities/sample", false, false));
        register(new ApiDef("identitiesAll", "GET", "/probe/identities/all", false, false));
        register(new ApiDef("drugDict", "GET", "/query/drug-dict-infos", true, true));
        register(new ApiDef("dictCategory", "GET", "/query/dict-category", false, false));
        register(new ApiDef("suppliers", "GET", "/query/drug-suppliers", true, false));
        register(new ApiDef("producers", "GET", "/query/drug-producers", true, false));
        register(new ApiDef("mergeStocks", "GET", "/query/merge-stock-infos", false, false));
        register(new ApiDef("batchStocks", "GET", "/query/drug-batch-stocks", false, false));
        register(new ApiDef("ykInstock", "POST", "/query/yk-instock", false, false));
    }

    private MsunHisProbeApiRegistry()
    {
    }

    private static void register(ApiDef def)
    {
        MAP.put(def.getKey(), def);
    }

    public static ApiDef require(String apiKey)
    {
        ApiDef def = MAP.get(apiKey);
        if (def == null)
        {
            throw new IllegalArgumentException("不支持的联调接口: " + apiKey);
        }
        return def;
    }

    public static Map<String, ApiDef> all()
    {
        return Collections.unmodifiableMap(MAP);
    }

    /** 主数据同步类型 → 可选预拉取探针 apiKey */
    public static String probeKeyForSyncType(String syncType)
    {
        if (syncType == null)
        {
            return null;
        }
        switch (syncType)
        {
            case "depts":
                return "depts";
            case "identities":
                return "identitiesAll";
            case "suppliers":
                return "suppliers";
            case "producers":
                return "producers";
            case "categories":
                return "dictCategory";
            case "materials":
                return null;
            default:
                return null;
        }
    }
}
