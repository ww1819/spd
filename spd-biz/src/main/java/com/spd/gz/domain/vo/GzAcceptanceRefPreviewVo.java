package com.spd.gz.domain.vo;

import java.util.ArrayList;
import java.util.List;

import com.spd.gz.domain.GzDepotInventory;

/**
 * 备货出库引用验收单确认预览
 */
public class GzAcceptanceRefPreviewVo
{
    private Long applyDepartmentId;
    private List<GzDepotInventory> availableLines = new ArrayList<>();
    private List<GzAcceptanceRefMissingBarcodeVo> missingBarcodes = new ArrayList<>();

    public Long getApplyDepartmentId()
    {
        return applyDepartmentId;
    }

    public void setApplyDepartmentId(Long applyDepartmentId)
    {
        this.applyDepartmentId = applyDepartmentId;
    }

    public List<GzDepotInventory> getAvailableLines()
    {
        return availableLines;
    }

    public void setAvailableLines(List<GzDepotInventory> availableLines)
    {
        this.availableLines = availableLines;
    }

    public List<GzAcceptanceRefMissingBarcodeVo> getMissingBarcodes()
    {
        return missingBarcodes;
    }

    public void setMissingBarcodes(List<GzAcceptanceRefMissingBarcodeVo> missingBarcodes)
    {
        this.missingBarcodes = missingBarcodes;
    }
}
