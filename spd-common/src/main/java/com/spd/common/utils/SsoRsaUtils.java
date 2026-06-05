package com.spd.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * SSO 密码 RSA 解密（对方使用 SPD 公钥加密，SPD 使用私钥解密）。
 */
public final class SsoRsaUtils
{
    private static final String RSA = "RSA";

    private SsoRsaUtils()
    {
    }

    /**
     * 使用 PKCS#8 PEM 私钥解密 Base64 密文，失败返回 null。
     */
    public static String decryptByPrivateKeyPem(String cipherBase64, String privateKeyPem)
    {
        if (StringUtils.isEmpty(cipherBase64) || StringUtils.isEmpty(privateKeyPem))
        {
            return null;
        }
        try
        {
            PrivateKey privateKey = loadPrivateKey(privateKeyPem);
            byte[] encrypted = Base64.getDecoder().decode(cipherBase64.replaceAll("\\s+", ""));
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static PrivateKey loadPrivateKey(String pem) throws Exception
    {
        String normalized = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance(RSA).generatePrivate(spec);
    }
}
