package com.spd.foundation.dto;

import java.util.List;

/**
 * 产品档案批量修改（仅非空字段会应用到选中记录）
 */
public class MaterialBatchUpdateDto {

    private List<Long> ids;

    private Long storeroomId;

    private Long financeCategoryId;

    private String materialCategoryId;

    /** 1启用 2停用 */
    private String isUse;

    /** 1是 2否 */
    private String isBilling;

    private String isGz;

    private String isFollow;

    private String isProcure;

    private String isSunshineProcurement;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getStoreroomId() {
        return storeroomId;
    }

    public void setStoreroomId(Long storeroomId) {
        this.storeroomId = storeroomId;
    }

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public String getMaterialCategoryId() {
        return materialCategoryId;
    }

    public void setMaterialCategoryId(String materialCategoryId) {
        this.materialCategoryId = materialCategoryId;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getIsBilling() {
        return isBilling;
    }

    public void setIsBilling(String isBilling) {
        this.isBilling = isBilling;
    }

    public String getIsGz() {
        return isGz;
    }

    public void setIsGz(String isGz) {
        this.isGz = isGz;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public String getIsProcure() {
        return isProcure;
    }

    public void setIsProcure(String isProcure) {
        this.isProcure = isProcure;
    }

    public String getIsSunshineProcurement() {
        return isSunshineProcurement;
    }

    public void setIsSunshineProcurement(String isSunshineProcurement) {
        this.isSunshineProcurement = isSunshineProcurement;
    }

    public boolean hasAnyPatchField() {
        return storeroomId != null
            || financeCategoryId != null
            || (materialCategoryId != null && !materialCategoryId.isEmpty())
            || (isUse != null && !isUse.isEmpty())
            || (isBilling != null && !isBilling.isEmpty())
            || (isGz != null && !isGz.isEmpty())
            || (isFollow != null && !isFollow.isEmpty())
            || (isProcure != null && !isProcure.isEmpty())
            || (isSunshineProcurement != null && !isSunshineProcurement.isEmpty());
    }
}
