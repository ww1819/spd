package com.spd.tools.license;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 生成 RSA2048 签发密钥对：私钥 PKCS#8 PEM（仅用于签发工具）、公钥 PEM（拷贝到 spd-system classpath:license/public.pem）。
 *
 * <pre>
 *   java -cp "license-generator.jar:lib/*" com.spd.tools.license.LicenseSigningKeyGenMain [输出目录，默认 keys]
 * </pre>
 */
public final class LicenseSigningKeyGenMain
{
    private LicenseSigningKeyGenMain()
    {
    }

    public static void main(String[] args) throws Exception
    {
        String dirName = args.length > 0 ? args[0] : "keys";
        File dir = new File(dirName).getAbsoluteFile();
        if (!dir.exists() && !dir.mkdirs())
        {
            System.err.println("无法创建目录: " + dir);
            System.exit(1);
        }
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        PrivateKey priv = kp.getPrivate();
        PublicKey pub = kp.getPublic();

        String privPem = toPkcs8Pem(priv.getEncoded());
        String pubPem = toPublicPem(pub.getEncoded());

        File privFile = new File(dir, "license_signing_private_pkcs8.pem");
        File pubFile = new File(dir, "license_signing_public.pem");
        java.nio.file.Files.write(privFile.toPath(), privPem.getBytes(StandardCharsets.UTF_8));
        java.nio.file.Files.write(pubFile.toPath(), pubPem.getBytes(StandardCharsets.UTF_8));
        System.out.println("已写入私钥（勿提交仓库、勿进客户包）: " + privFile.getAbsolutePath());
        System.out.println("已写入公钥（请复制内容覆盖 spd-system/src/main/resources/license/public.pem）: " + pubFile.getAbsolutePath());
    }

    private static String toPkcs8Pem(byte[] pkcs8)
    {
        String b64 = Base64.getEncoder().encodeToString(pkcs8);
        return "-----BEGIN PRIVATE KEY-----\n" + wrap64(b64) + "\n-----END PRIVATE KEY-----\n";
    }

    private static String toPublicPem(byte[] spki)
    {
        String b64 = Base64.getEncoder().encodeToString(spki);
        return "-----BEGIN PUBLIC KEY-----\n" + wrap64(b64) + "\n-----END PUBLIC KEY-----\n";
    }

    private static String wrap64(String b64)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b64.length(); i += 64)
        {
            sb.append(b64, i, Math.min(i + 64, b64.length())).append('\n');
        }
        return sb.toString().trim();
    }
}
