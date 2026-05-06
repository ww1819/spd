package com.spd.hc.service;

import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.StkDepInventory;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzOrderEntry;
import com.spd.gz.domain.GzOrderEntryInhospitalcodeList;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.domain.GzRefundGoodsEntry;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.StkInventory;

/**
 * 高低值条码：审核落库、主档、流水（与 table.sql 中 hc_* / stk_lv_* / gz_*_flow 对齐）
 */
public interface IHcBarcodeLifecycleService {

    void onLowValueInbound101(StkIoBill bill, StkIoBillEntry entry, StkInventory insertedInventory, FdWarehouse warehouse);

    void onLowValueOutbound201(StkIoBill bill, StkIoBillEntry entry, StkInventory warehouseInventory, StkDepInventory depInventory, FdWarehouse warehouse);

    void onLowValueReturn301(StkIoBill bill, StkIoBillEntry entry, StkInventory inventory, FdWarehouse warehouse);

    void onLowValueTk401(StkIoBill bill, StkIoBillEntry entry, StkInventory warehouseInventory, StkDepInventory depInventory, FdWarehouse warehouse);

    void fillGzOrderInhospitalcodeSnapshots(GzOrder order, GzOrderEntry orderEntry, GzOrderEntryInhospitalcodeList row);

    void onGzPrepAcceptUnit(GzOrder order, GzOrderEntry orderEntry, GzOrderEntryInhospitalcodeList inhospitalRow, GzDepotInventory depotRow);

    void onGzShipmentDepInventoryInserted(GzShipment shipment, GzShipmentEntry shipmentEntry, GzDepInventory depInventory, GzDepotInventory depotSource);

    /** 高值出库审核：备货库存扣减写 gz_wh_flow（lx=CK），与 gz_depot_inventory 变动对齐 */
    void onGzShipmentWarehouseOutbound(GzShipment shipment, GzShipmentEntry shipmentEntry);

    void onGzRefundStockLine(GzRefundGoods bill, GzRefundGoodsEntry entry);

    void onGzRefundGoodsLine(GzRefundGoods bill, GzRefundGoodsEntry entry);

    /** 科室批量消耗/反消耗：高值（按院内码关联主档） */
    void onDeptBatchConsumeGz(DeptBatchConsume bill, DeptBatchConsumeEntry entry, GzDepInventory gzLine);

    /** 科室批量消耗/反消耗：低值（按科室库存定数包码，支持逗号分隔多码按数量拆分） */
    void onDeptBatchConsumeLv(DeptBatchConsume bill, DeptBatchConsumeEntry entry, StkDepInventory depLine);
}
