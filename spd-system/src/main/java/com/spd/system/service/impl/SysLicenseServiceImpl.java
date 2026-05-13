package com.spd.system.service.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spd.common.constant.HttpStatus;
import com.spd.common.exception.ServiceException;
import com.spd.common.license.LicenseCrypto;
import com.spd.common.license.LicensePayload;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.system.domain.SysConfig;
import com.spd.system.domain.SysLicense;
import com.spd.system.domain.vo.LicenseStatusVo;
import com.spd.system.mapper.SysLicenseMapper;
import com.spd.system.service.ISysConfigService;
import com.spd.system.service.ISysLicenseService;

/**
 * 系统离线授权实现
 */
@Service
public class SysLicenseServiceImpl implements ISysLicenseService
{
    private static final Logger log = LoggerFactory.getLogger(SysLicenseServiceImpl.class);

    private static final Long LICENSE_ROW_ID = 1L;

    /** 系统医院名称所在参数：sys_config.config_id */
    private static final Long SYS_CONFIG_ID_HOSPITAL_NAME = 7L;

    @Autowired
    private SysLicenseMapper sysLicenseMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ISysConfigService sysConfigService;

    @Value("${spd.license.publicKeyPem:}")
    private String publicKeyPemConfig;

    private volatile PublicKey cachedPublicKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PublicKey getOrLoadPublicKey()
    {
        if (cachedPublicKey != null)
        {
            return cachedPublicKey;
        }
        synchronized (this)
        {
            if (cachedPublicKey != null)
            {
                return cachedPublicKey;
            }
            try
            {
                String pem = publicKeyPemConfig;
                if (StringUtils.isEmpty(pem))
                {
                    Resource r = resourceLoader.getResource("classpath:license/public.pem");
                    if (!r.exists())
                    {
                        log.warn("classpath:license/public.pem 不存在且未配置 spd.license.publicKeyPem");
                        return null;
                    }
                    try (InputStream in = r.getInputStream())
                    {
                        pem = IOUtils.toString(in, StandardCharsets.UTF_8);
                    }
                }
                cachedPublicKey = LicenseCrypto.readPublicKeyFromPem(pem);
                return cachedPublicKey;
            }
            catch (Exception e)
            {
                log.error("加载离线授权公钥失败", e);
                return null;
            }
        }
    }

    private SysLicense ensureLicenseRow()
    {
        SysLicense row = sysLicenseMapper.selectSysLicenseById(LICENSE_ROW_ID);
        if (row != null)
        {
            return row;
        }
        Date now = new Date();
        SysLicense insert = new SysLicense();
        insert.setId(LICENSE_ROW_ID);
        insert.setInstanceId(IdUtils.fastUUID());
        insert.setExpireTime(null);
        insert.setPayloadJson(null);
        insert.setSignature(null);
        insert.setCreateTime(now);
        try
        {
            sysLicenseMapper.insertSysLicense(insert);
        }
        catch (Exception e)
        {
            log.debug("sys_license 插入冲突或失败，尝试重新读取: {}", e.getMessage());
        }
        row = sysLicenseMapper.selectSysLicenseById(LICENSE_ROW_ID);
        if (row == null)
        {
            throw new ServiceException("sys_license 表未初始化或缺少 id=1 行，请确认已执行库表初始化（如 material/table.sql 中的 sys_license 建表）");
        }
        return row;
    }

    @Override
    public boolean isCurrentlyValid()
    {
        return getLicenseDenyHttpCode() == null;
    }

    @Override
    public Integer getLicenseDenyHttpCode()
    {
        if (getOrLoadPublicKey() == null)
        {
            return HttpStatus.LICENSE_NOT_ACTIVATED;
        }
        SysLicense row;
        try
        {
            row = ensureLicenseRow();
        }
        catch (Exception e)
        {
            log.error("读取 sys_license 失败", e);
            return HttpStatus.LICENSE_NOT_ACTIVATED;
        }
        if (row.getExpireTime() == null)
        {
            return HttpStatus.LICENSE_NOT_ACTIVATED;
        }
        long now = System.currentTimeMillis();
        if (row.getExpireTime().getTime() < now)
        {
            return HttpStatus.LICENSE_EXPIRED;
        }
        return null;
    }

    @Override
    public String getLicenseDenyMessage()
    {
        Integer code = getLicenseDenyHttpCode();
        if (code == null)
        {
            return null;
        }
        if (Integer.valueOf(HttpStatus.LICENSE_EXPIRED).equals(code))
        {
            return "离线授权已过期，请导入新的注册码";
        }
        if (getOrLoadPublicKey() == null)
        {
            return "未配置离线授权公钥（license/public.pem），系统无法校验授权";
        }
        return "系统尚未激活离线授权，请联系管理员导入注册码";
    }

    @Override
    public LicenseStatusVo getStatus()
    {
        SysLicense row = ensureLicenseRow();
        LicenseStatusVo vo = new LicenseStatusVo();
        vo.setInstanceId(row.getInstanceId());
        vo.setExpireTime(row.getExpireTime());
        vo.setActivated(row.getExpireTime() != null);
        vo.setValid(isCurrentlyValid());
        return vo;
    }

    @Override
    public void activate(String licenseCode, String customerId, String updateBy)
    {
        applyLicenseAfterVerify(licenseCode, customerId, "hc", updateBy);
    }

    @Override
    public void activateAnonymousFromLogin(String licenseCode, String customerId, String systemType)
    {
        applyLicenseAfterVerify(licenseCode, customerId, systemType, "login");
    }

    private void applyLicenseAfterVerify(String licenseCode, String customerId, String systemType, String updateBy)
    {
        PublicKey publicKey = getOrLoadPublicKey();
        if (publicKey == null)
        {
            throw new ServiceException("未配置离线授权公钥，无法激活");
        }
        if (StringUtils.isEmpty(licenseCode))
        {
            throw new ServiceException("注册码不能为空");
        }
        SysLicense row = ensureLicenseRow();
        LicensePayload payload;
        byte[] sigBytes;
        try
        {
            payload = LicenseCrypto.decodePayloadFromLicense(licenseCode.trim());
            sigBytes = LicenseCrypto.decodeSignatureFromLicense(licenseCode.trim());
        }
        catch (Exception e)
        {
            throw new ServiceException("注册码格式无效");
        }
        try
        {
            if (!LicenseCrypto.verify(payload, sigBytes, publicKey))
            {
                throw new ServiceException("注册码签名校验失败");
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("注册码校验异常");
        }

        if (payload.isHospitalBinding())
        {
            SysConfig hospitalCfg = sysConfigService.selectConfigById(SYS_CONFIG_ID_HOSPITAL_NAME);
            if (hospitalCfg == null || StringUtils.isEmpty(hospitalCfg.getConfigValue()))
            {
                throw new ServiceException("医院名称错误");
            }
            String dbName = LicensePayload.normalizeHospitalName(hospitalCfg.getConfigValue());
            String licName = LicensePayload.normalizeHospitalName(payload.getHospitalName());
            if (StringUtils.isEmpty(dbName) || !dbName.equals(licName))
            {
                throw new ServiceException("医院名称错误");
            }
        }
        else if (LicenseCrypto.isInstanceBinding(payload))
        {
            if (StringUtils.isEmpty(payload.getInstanceId()) || !payload.getInstanceId().equals(row.getInstanceId()))
            {
                throw new ServiceException("注册码与当前数据库实例不匹配");
            }
        }
        else
        {
            throw new ServiceException("不支持的授权版本或载荷");
        }

        java.time.Instant expireInstant;
        try
        {
            expireInstant = LicenseCrypto.parseExpireInstant(payload);
        }
        catch (Exception e)
        {
            throw new ServiceException("到期时间格式无效");
        }
        if (expireInstant.isBefore(java.time.Instant.now()))
        {
            throw new ServiceException("注册码已过期，请使用新的到期时间重新签发");
        }
        Date expireDate = Date.from(expireInstant);
        String payloadJson;
        try
        {
            payloadJson = objectMapper.writeValueAsString(payload);
        }
        catch (Exception e)
        {
            payloadJson = null;
        }
        String sigB64 = Base64.getEncoder().encodeToString(sigBytes);
        SysLicense upd = new SysLicense();
        upd.setId(LICENSE_ROW_ID);
        upd.setExpireTime(expireDate);
        upd.setPayloadJson(payloadJson);
        upd.setSignature(sigB64);
        upd.setUpdateBy(updateBy);
        upd.setUpdateTime(new Date());
        sysLicenseMapper.updateSysLicenseActivation(upd);
    }
}
