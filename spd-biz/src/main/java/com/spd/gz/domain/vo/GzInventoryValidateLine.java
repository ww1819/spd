package com.spd.gz.domain.vo;

import java.io.Serializable;

/**
 * 单行库存校验结果（用于弹窗列表）
 */
public class GzInventoryValidateLine implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 明细序号（从 1 开始） */
    private int lineNo;
    /** 院内码 */
    private String inHospitalCode;
    /** 批次号 */
    private String batchNo;
    /** 耗材名称 */
    private String materialName;
    /** 原因说明 */
    private String reason;

    public int getLineNo()
    {
        return lineNo;
    }

    public void setLineNo(int lineNo)
    {
        this.lineNo = lineNo;
    }

    public String getInHospitalCode()
    {
        return inHospitalCode;
    }

    public void setInHospitalCode(String inHospitalCode)
    {
        this.inHospitalCode = inHospitalCode;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
