package com.spd.tools.license;

import java.io.File;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.spd.common.license.LicenseCrypto;
import com.spd.common.license.LicensePayload;

/**
 * 离线注册码签发（须持有私钥 PEM，与运行环境 public.pem 成对）。
 *
 * <pre>
 * 方式一（推荐）：
 *   java -cp "license-generator.jar:lib/*" com.spd.tools.license.LicenseGeneratorMain \\
 *       --key-file keys/license_signing_private_pkcs8.pem \\
 *       --hospital "与 sys_config.config_id=7 的 config_value 一致" \\
 *       --expire 2030-12-31T23:59:59Z
 *
 * 方式二（从库读医院名称：sys_config.config_id=7 的 config_value，须 classpath 含 mysql 驱动）：
 *   ... LicenseGeneratorMain \\
 *       --key-file ... \\
 *       --expire ... \\
 *       --jdbc-url jdbc:mysql://127.0.0.1:3306/yourdb?useSSL=false \\
 *       --jdbc-user root --jdbc-password secret
 *
 * 生成密钥对（首次部署）：java -cp ... com.spd.tools.license.LicenseSigningKeyGenMain [输出目录，默认 keys]
 * </pre>
 */
public final class LicenseGeneratorMain
{
    private static final int SYS_CONFIG_ID_HOSPITAL_NAME = 7;

    private LicenseGeneratorMain()
    {
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            printUsage();
            System.exit(1);
        }
        ArgMap m = ArgMap.parse(args);
        if (m.has("help") || m.has("h"))
        {
            printUsage();
            return;
        }
        String keyFile = m.getRequired("--key-file");
        PrivateKey privateKey = LicenseCrypto.readPrivateKeyFromPemFile(new File(keyFile));

        String expireAt = m.getRequired("--expire");
        String hospital = m.get("--hospital");
        if (hospital == null || hospital.trim().isEmpty())
        {
            String jdbcUrl = m.get("--jdbc-url");
            String jdbcUser = m.get("--jdbc-user");
            String jdbcPassword = m.get("--jdbc-password");
            if (jdbcUrl == null || jdbcUser == null)
            {
                System.err.println("缺少 --hospital，或提供完整 --jdbc-url/--jdbc-user（及可选 --jdbc-password）从 sys_config(config_id=7) 读取");
                System.exit(2);
            }
            hospital = fetchHospitalNameFromSysConfig(jdbcUrl, jdbcUser, jdbcPassword != null ? jdbcPassword : "");
            if (hospital == null || hospital.isEmpty())
            {
                System.err.println("未在 sys_config 中找到 config_id=" + SYS_CONFIG_ID_HOSPITAL_NAME + " 或 config_value 为空");
                System.exit(3);
            }
        }

        LicensePayload payload = new LicensePayload();
        payload.setVersion(2);
        payload.setHospitalName(hospital.trim());
        payload.setExpireAt(expireAt.trim());
        byte[] sig = LicenseCrypto.sign(payload, privateKey);
        String code = LicenseCrypto.encodeLicense(payload, sig);
        System.out.println(code);
    }

    private static String fetchHospitalNameFromSysConfig(String url, String user, String password) throws Exception
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, password))
        {
            String sql = "SELECT config_value FROM sys_config WHERE config_id = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setInt(1, SYS_CONFIG_ID_HOSPITAL_NAME);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        return rs.getString(1);
                    }
                }
            }
        }
        return null;
    }

    private static void printUsage()
    {
        System.err.println("Usage:");
        System.err.println("  LicenseGeneratorMain --key-file <private.pem> --hospital \"名称\" --expire <ISO-8601>");
        System.err.println("  LicenseGeneratorMain --key-file <private.pem> --expire <ISO-8601> --jdbc-url ... --jdbc-user ... [--jdbc-password ...]");
        System.err.println("  （JDBC 方式从 sys_config.config_id=7 读取 config_value 作为医院名称）");
    }

    private static final class ArgMap
    {
        private final java.util.Map<String, String> map = new java.util.LinkedHashMap<>();

        static ArgMap parse(String[] args)
        {
            ArgMap a = new ArgMap();
            for (int i = 0; i < args.length; i++)
            {
                String k = args[i];
                if (!k.startsWith("--"))
                {
                    continue;
                }
                if (i + 1 < args.length && !args[i + 1].startsWith("--"))
                {
                    a.map.put(k, args[i + 1]);
                    i++;
                }
                else
                {
                    a.map.put(k, "");
                }
            }
            return a;
        }

        boolean has(String key)
        {
            return map.containsKey(key);
        }

        String get(String key)
        {
            String v = map.get(key);
            return v == null || v.isEmpty() ? null : v;
        }

        String getRequired(String key)
        {
            String v = get(key);
            if (v == null)
            {
                System.err.println("缺少参数: " + key);
                System.exit(1);
            }
            return v;
        }
    }
}
