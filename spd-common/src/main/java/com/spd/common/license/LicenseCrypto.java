package com.spd.common.license;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.common.utils.StringUtils;

/**
 * 离线授权：规范 JSON 序列化、RSA SHA256withRSA 签名与验签、注册码编解码。
 */
public final class LicenseCrypto
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SIGN_ALG = "SHA256withRSA";

    private LicenseCrypto()
    {
    }

    /**
     * 用于签名的规范 UTF-8 字节：键按字典序排列后的紧凑 JSON。
     */
    public static byte[] canonicalJsonBytes(LicensePayload payload) throws Exception
    {
        Map<String, Object> sorted = new TreeMap<>();
        sorted.put("expireAt", payload.getExpireAt());
        if (payload.isHospitalBinding())
        {
            sorted.put("hospitalName", LicensePayload.normalizeHospitalName(payload.getHospitalName()));
            sorted.put("version", payload.getVersion());
        }
        else
        {
            sorted.put("instanceId", payload.getInstanceId());
            sorted.put("version", payload.getVersion());
        }
        return MAPPER.writeValueAsString(sorted).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] sign(LicensePayload payload, PrivateKey privateKey) throws Exception
    {
        Signature sig = Signature.getInstance(SIGN_ALG);
        sig.initSign(privateKey);
        sig.update(canonicalJsonBytes(payload));
        return sig.sign();
    }

    public static boolean verify(LicensePayload payload, byte[] signature, PublicKey publicKey) throws Exception
    {
        Signature sig = Signature.getInstance(SIGN_ALG);
        sig.initVerify(publicKey);
        sig.update(canonicalJsonBytes(payload));
        return sig.verify(signature);
    }

    /**
     * 注册码：Base64Url(payloadJson).Base64(signature)
     */
    public static String encodeLicense(LicensePayload payload, byte[] signature) throws Exception
    {
        Map<String, Object> sorted = new LinkedHashMap<>();
        sorted.put("version", payload.getVersion());
        if (payload.isHospitalBinding())
        {
            sorted.put("hospitalName", LicensePayload.normalizeHospitalName(payload.getHospitalName()));
            sorted.put("expireAt", payload.getExpireAt());
        }
        else
        {
            sorted.put("instanceId", payload.getInstanceId());
            sorted.put("expireAt", payload.getExpireAt());
        }
        String payloadJson = MAPPER.writeValueAsString(sorted);
        String partA = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String partB = Base64.getEncoder().encodeToString(signature);
        return partA + "." + partB;
    }

    public static LicensePayload decodePayloadFromLicense(String licenseCode) throws Exception
    {
        int dot = licenseCode.indexOf('.');
        if (dot <= 0 || dot >= licenseCode.length() - 1)
        {
            throw new IllegalArgumentException("授权码格式无效");
        }
        String payloadB64 = licenseCode.substring(0, dot);
        byte[] jsonBytes = Base64.getUrlDecoder().decode(payloadB64);
        return MAPPER.readValue(jsonBytes, LicensePayload.class);
    }

    public static byte[] decodeSignatureFromLicense(String licenseCode) throws Exception
    {
        int dot = licenseCode.indexOf('.');
        if (dot <= 0 || dot >= licenseCode.length() - 1)
        {
            throw new IllegalArgumentException("授权码格式无效");
        }
        return Base64.getDecoder().decode(licenseCode.substring(dot + 1));
    }

    public static PublicKey readPublicKeyFromPem(String pem) throws Exception
    {
        if (pem.contains("BEGIN CERTIFICATE"))
        {
            String body = pem.replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(body);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(decoded));
            return cert.getPublicKey();
        }
        String body = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(body);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    /**
     * PKCS#8 PEM（BEGIN PRIVATE KEY）
     */
    public static PrivateKey readPrivateKeyPkcs8FromPem(String pem) throws Exception
    {
        String body = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(body);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static PrivateKey readPrivateKeyFromPemFile(java.io.File file) throws Exception
    {
        String pem = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return readPrivateKeyPkcs8FromPem(pem);
    }

    public static Instant parseExpireInstant(LicensePayload payload)
    {
        return Instant.parse(payload.getExpireAt());
    }

    /** 校验 v1 载荷结构 */
    public static boolean isInstanceBinding(LicensePayload p)
    {
        return p.getVersion() == 1 && StringUtils.isNotEmpty(p.getInstanceId());
    }
}
