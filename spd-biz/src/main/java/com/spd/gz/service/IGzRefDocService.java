package com.spd.gz.service;

import java.util.List;

import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.vo.GzAcceptanceRefPreviewVo;

/**
 * 高值单据引用（低敏感查询）：备货验收/出库等
 */
public interface IGzRefDocService
{
    /** 已审核的备货验收单列表（入库 101） */
    List<GzOrder> listAuditedAcceptance(GzOrder query);

    /** 指定验收单在指定仓库下仍有库存的备货行（用于出库引用） */
    List<GzDepotInventory> listAcceptanceDepotLines(Long acceptanceOrderId, Long warehouseId);

    /** 引用确认预览：可带入行 + 无库存/已占用缺失条码 */
    GzAcceptanceRefPreviewVo previewAcceptanceRef(Long acceptanceOrderId, Long warehouseId, Long excludeShipmentId);

    /** 已审核备货出库单 */
    List<GzShipment> listAuditedShipment(GzShipment query);

    /** 出库单明细中，在指定科室仍有库存的行（用于退库引用） */
    List<GzShipmentEntry> listShipmentLinesForTk(Long shipmentId, Long departmentId);
}
