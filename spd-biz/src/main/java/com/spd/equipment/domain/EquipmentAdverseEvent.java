package com.spd.equipment.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备不良事件 equipment_adverse_event
 *
 * @author spd
 */
public class EquipmentAdverseEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 UUID7（32 位无连字符） */
    private String id;

    /** 租户 ID（varchar，与 equipment_info.tenant_id 一致） */
    private String tenantId;

    /** 设备主键（varchar 外键 equipment_info.id） */
    private String equipmentId;

    private String eventCode;

    /** 行内「当前设备档案编码」 */
    private String archiveCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date eventDate;

    /** 报告人（用户 ID，与前端下拉一致） */
    private Long reporter;

    private String eventType;

    private String eventLevel;

    private String eventDescription;

    private String handlingMeasures;

    private String handlingResult;

    /** 删除标志（0 未删除，1 已删除） */
    private String delFlag;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getEquipmentId()
    {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId)
    {
        this.equipmentId = equipmentId;
    }

    public String getEventCode()
    {
        return eventCode;
    }

    public void setEventCode(String eventCode)
    {
        this.eventCode = eventCode;
    }

    public String getArchiveCode()
    {
        return archiveCode;
    }

    public void setArchiveCode(String archiveCode)
    {
        this.archiveCode = archiveCode;
    }

    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public Long getReporter()
    {
        return reporter;
    }

    public void setReporter(Long reporter)
    {
        this.reporter = reporter;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getEventLevel()
    {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel)
    {
        this.eventLevel = eventLevel;
    }

    public String getEventDescription()
    {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription)
    {
        this.eventDescription = eventDescription;
    }

    public String getHandlingMeasures()
    {
        return handlingMeasures;
    }

    public void setHandlingMeasures(String handlingMeasures)
    {
        this.handlingMeasures = handlingMeasures;
    }

    public String getHandlingResult()
    {
        return handlingResult;
    }

    public void setHandlingResult(String handlingResult)
    {
        this.handlingResult = handlingResult;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
