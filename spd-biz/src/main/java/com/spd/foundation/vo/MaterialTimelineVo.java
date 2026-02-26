package com.spd.foundation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 产品档案时间轴项 VO（合并启用停用记录与变更记录，按时间排序）
 *
 * @author spd
 */
public class MaterialTimelineVo {

    /** 事件时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date eventTime;

    /** 类型：enable=启用，disable=停用，change=字段变更 */
    private String type;

    /** 操作人 */
    private String operator;

    /** 标题（如：启用、停用、字段变更） */
    private String title;

    /** 描述（启用/停用原因，或变更的字段名列表） */
    private String description;

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
