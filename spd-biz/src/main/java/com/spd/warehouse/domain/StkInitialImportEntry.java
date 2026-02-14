package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;
import lombok.Getter;
import lombok.Setter;

/**
 * 期初库存导入明细表 stk_initial_import_entry
 *
 * @author spd
 */
@Getter
@Setter
public class StkInitialImportEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键（UUID7） */
    private String id;
    /** 期初导入主表ID */
    private String parenId;
    /** 耗材ID */
    private Long materialId;
    /** 所属仓库ID */
    private Long warehouseId;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 数量 */
    private BigDecimal qty;
    /** 金额 */
    private BigDecimal amt;
    /** 批次号（自动生成） */
    private String batchNo;
    /** 批号 */
    private String batchNumber;
    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 生产厂家ID */
    private Long factoryId;
    /** 供应商ID */
    private Long supplierId;
    /** 删除标志 */
    private Integer delFlag;
    /** 排序 */
    private Integer sortOrder;

    /** 第三方系统库存明细ID */
    private String thirdPartyDetailId;
    /** 第三方系统产品档案ID */
    private String thirdPartyMaterialId;

    /** 耗材对象 */
    private FdMaterial material;
}
