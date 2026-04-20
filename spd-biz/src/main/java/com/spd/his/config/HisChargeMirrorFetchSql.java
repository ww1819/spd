package com.spd.his.config;

/**
 * HIS 计费镜像抓取：SQL Server 内置视图 SQL（两个参数均为 datetime：窗口起含、止不含）。
 */
public final class HisChargeMirrorFetchSql
{
    private HisChargeMirrorFetchSql()
    {
    }

    public static final String SQLSERVER_INPATIENT_RANGE =
        "SELECT inpatient_charge_id, patient_id, patient_name, inpatient_no, dept_code, dept_name, "
            + "doctor_id, doctor_name, charge_item_id, item_name, spec_model, batch_no, expire_date, "
            + "use_date, charge_date, quantity, unit_price, total_amount, charge_operator, remark "
            + "FROM dbo.v_inpatient_consumable_charge "
            + "WHERE CASE WHEN ISDATE(NULLIF(LTRIM(RTRIM(charge_date)), '')) = 1 "
            + "THEN CONVERT(datetime, NULLIF(LTRIM(RTRIM(charge_date)), '')) END >= ? "
            + "AND CASE WHEN ISDATE(NULLIF(LTRIM(RTRIM(charge_date)), '')) = 1 "
            + "THEN CONVERT(datetime, NULLIF(LTRIM(RTRIM(charge_date)), '')) END < ?";

    public static final String SQLSERVER_OUTPATIENT_RANGE =
        "SELECT outpatient_charge_id, patient_id, patient_name, outpatient_no, clinic_code, clinic_name, "
            + "doctor_id, doctor_name, charge_item_id, item_name, spec_model, batch_no, expire_date, "
            + "charge_date, quantity, unit_price, total_amount, charge_operator, payment_type, receipt_no, remark "
            + "FROM dbo.v_outpatient_consumable_charge "
            + "WHERE CASE WHEN ISDATE(NULLIF(LTRIM(RTRIM(charge_date)), '')) = 1 "
            + "THEN CONVERT(datetime, NULLIF(LTRIM(RTRIM(charge_date)), '')) END >= ? "
            + "AND CASE WHEN ISDATE(NULLIF(LTRIM(RTRIM(charge_date)), '')) = 1 "
            + "THEN CONVERT(datetime, NULLIF(LTRIM(RTRIM(charge_date)), '')) END < ?";
}
