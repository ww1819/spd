package com.spd.gz.domain.vo;

/**
 * 引用验收单时当前仓库无库存的条码行
 */
public class GzAcceptanceRefMissingBarcodeVo
{
    private String barcodeLineId;
    private String inHospitalCode;
    private String materialName;

    public String getBarcodeLineId()
    {
        return barcodeLineId;
    }

    public void setBarcodeLineId(String barcodeLineId)
    {
        this.barcodeLineId = barcodeLineId;
    }

    public String getInHospitalCode()
    {
        return inHospitalCode;
    }

    public void setInHospitalCode(String inHospitalCode)
    {
        this.inHospitalCode = inHospitalCode;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }
}
