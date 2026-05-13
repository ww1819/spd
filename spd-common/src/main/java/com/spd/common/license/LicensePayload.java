package com.spd.common.license;

import java.io.Serializable;
import java.text.Normalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spd.common.utils.StringUtils;

/**
 * 离线授权 payload（与签发工具共用字段）
 */
public class LicensePayload implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 协议版本：1=实例ID绑定；2=医院名称绑定（推荐） */
    private int version = 2;

    /** v1：与 sys_license.instance_id 一致 */
    private String instanceId;

    /** v2：与 sys_config.config_id=7 的 config_value（医院名称）一致，规范化后比对 */
    private String hospitalName;

    /** ISO-8601 到期时间，如 2030-12-31T23:59:59Z */
    private String expireAt;

    /**
     * 医院名称规范化：NFKC、去首尾空白、连续空白压成单空格
     */
    public static String normalizeHospitalName(String name)
    {
        if (name == null)
        {
            return "";
        }
        String t = Normalizer.normalize(name.trim(), Normalizer.Form.NFKC);
        return t.replaceAll("\\s+", " ");
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getHospitalName()
    {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName)
    {
        this.hospitalName = hospitalName;
    }

    public String getExpireAt()
    {
        return expireAt;
    }

    public void setExpireAt(String expireAt)
    {
        this.expireAt = expireAt;
    }

    /**
     * 是否按医院名称签名的 v2 载荷（计算属性，勿参与 JSON 序列化，否则落库后反序列化会因无 setter 失败）
     */
    @JsonIgnore
    public boolean isHospitalBinding()
    {
        return version >= 2 && StringUtils.isNotEmpty(hospitalName);
    }
}
