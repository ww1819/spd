package com.spd.equipment.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备文件对象 equipment_file
 * 
 * @author spd
 * @date 2024-01-01
 */
public class EquipmentFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private String fileId;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String equipmentId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件URL */
    private String url;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文档类型 */
    private String documentType;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String originalFilename;

    public void setFileId(String fileId) 
    {
        this.fileId = fileId;
    }

    public String getFileId() 
    {
        return fileId;
    }

    public void setEquipmentId(String equipmentId) 
    {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentId() 
    {
        return equipmentId;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }

    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }

    public void setDocumentType(String documentType) 
    {
        this.documentType = documentType;
    }

    public String getDocumentType() 
    {
        return documentType;
    }

    public void setOriginalFilename(String originalFilename) 
    {
        this.originalFilename = originalFilename;
    }

    public String getOriginalFilename() 
    {
        return originalFilename;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("fileId", getFileId())
            .append("equipmentId", getEquipmentId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("url", getUrl())
            .append("fileType", getFileType())
            .append("documentType", getDocumentType())
            .append("originalFilename", getOriginalFilename())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
