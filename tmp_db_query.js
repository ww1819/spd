const mysql = require('mysql2/promise');

(async () => {
  const c = await mysql.createConnection({
    host: 'rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com',
    port: 3306,
    user: 'spd',
    password: 'Spd@456ww',
    database: 'aspt',
    connectTimeout: 30000
  });

  const run = async (title, sql) => {
    console.log('\n=== ' + title + ' ===');
    const [rows] = await c.query(sql);
    console.log(JSON.stringify(rows, null, 2));
    return rows;
  };

  await run('fd_material G0070065_3', `
    SELECT id, code, name, tenant_id, is_gz, his_charge_item_id, del_flag
    FROM fd_material
    WHERE code = 'G0070065_3' OR code LIKE 'G0070065%'
    ORDER BY tenant_id, id
  `);

  await run('unified mirror 升月/127577/5966', `
    SELECT id, tenant_id, charge_item_id, item_name, patient_name, inpatient_no,
           his_inpatient_charge_id, charge_at, process_status, value_level, visit_kind
    FROM his_patient_charge_mirror_unified
    WHERE tenant_id = 'hengsui-third-001'
      AND (patient_name LIKE '%升月%' OR inpatient_no = '127577' OR trim(charge_item_id) = '5966')
    ORDER BY charge_at DESC
    LIMIT 30
  `);

  await run('high join match', `
    SELECT m.id, m.patient_name, m.charge_item_id, trim(m.charge_item_id) AS charge_trim,
           m.charge_at, fm_high.charge_item_id AS fm_high_match
    FROM his_patient_charge_mirror_unified m
    LEFT JOIN (
      SELECT DISTINCT fm.tenant_id AS tenant_id, trim(fm.his_charge_item_id) AS charge_item_id
      FROM fd_material fm
      WHERE fm.tenant_id = 'hengsui-third-001'
        AND (fm.del_flag = 0 OR fm.del_flag IS NULL)
        AND trim(fm.is_gz) = '1'
        AND fm.his_charge_item_id IS NOT NULL
        AND trim(fm.his_charge_item_id) != ''
    ) fm_high ON fm_high.tenant_id = m.tenant_id AND fm_high.charge_item_id = trim(m.charge_item_id)
    WHERE m.tenant_id = 'hengsui-third-001'
      AND (m.patient_name LIKE '%升月%' OR m.inpatient_no = '127577' OR trim(m.charge_item_id) = '5966')
    ORDER BY m.charge_at DESC
    LIMIT 20
  `);

  await run('his_charge_item_mirror 5966', `
    SELECT charge_item_id, item_code, item_name, value_level, deleted_flag
    FROM his_charge_item_mirror
    WHERE tenant_id = 'hengsui-third-001'
      AND (trim(charge_item_id) = '5966' OR item_code LIKE '%G0070065%')
    LIMIT 10
  `);

  await run('materials bound to 5966', `
    SELECT id, code, name, is_gz, his_charge_item_id, del_flag
    FROM fd_material
    WHERE tenant_id = 'hengsui-third-001'
      AND trim(his_charge_item_id) = '5966'
      AND (del_flag = 0 OR del_flag IS NULL)
  `);

  await c.end();
})().catch((e) => {
  console.error(e);
  process.exit(1);
});
