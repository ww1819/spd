package com.spd.warehouse.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdWarehouse;
import lombok.Getter;
import lombok.Setter;

/**
 * 期初库存导入主表 stk_initial_import
 *
 * @author spd
 */
@Getter
@Setter
public class StkInitialImport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键（UUID7） */
    private String id;
    /** 期初单号 */
    private String billNo;
    /** 所属仓库ID */
    private Long warehouseId;
    /** 导入操作人 */
    private String importOperator;
    /** 导入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;
    /** 库存生成时间（审核时写入） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stockGenTime;
    /** 单据状态 0=待审核 1=已审核 */
    private Integer billStatus;
    /** 删除标志 */
    private Integer delFlag;
    /** 审核人 */
    private String auditBy;
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /** 仓库对象 */
    private FdWarehouse warehouse;
    /** 明细列表 */
    private List<StkInitialImportEntry> entryList;
}
