const mysql = require('mysql2/promise');
(async () => {
  const c = await mysql.createConnection({
    host: 'rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com',
    port: 3306,
    user: 'spd',
    password: 'Spd@456ww',
    database: 'aspt'
  });
  const codes = ['G2607161526000208', 'G2607161526000206', 'G2607031053000137'];
  for (const code of codes) {
    const [f] = await c.query(
      `SELECT bill_no, lx, qty, flow_time, origin_business_type FROM gz_dep_flow
       WHERE in_hospital_code=? ORDER BY flow_time`,
      [code]
    );
    const [inv] = await c.query(
      `SELECT id, qty FROM gz_dep_inventory WHERE in_hospital_code=? AND tenant_id='hengsui-third-001'`,
      [code]
    );
    console.log('\n', code, 'qty=', inv[0] && inv[0].qty);
    console.table(f);
  }
  const [cols] = await c.query(`SHOW COLUMNS FROM gz_dep_flow`);
  console.log('dep_flow cols', cols.map(x => x.Field).join(','));
  await c.end();
})().catch(console.error);
