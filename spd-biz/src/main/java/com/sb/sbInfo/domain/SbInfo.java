package com.sb.sbInfo.domain;

import com.spd.common.core.domain.BaseEntity;

/**
 * 设备信息对象 sb_info
 */
public class SbInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 设备名称 */
    private String name;

    /** 设备类型 */
    private String type;

    /** 设备编号 */
    private String code;

    /** 设备状态 */
    private String status;

    /** 设备位置 */
    private String location;

    /** 设备描述 */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
