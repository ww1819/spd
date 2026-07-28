const mysql = require('mysql2/promise');
(async () => {
  const c = await mysql.createConnection({
    host: 'rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com',
    port: 3306,
    user: 'spd',
    password: 'Spd@456ww',
    database: 'aspt'
  });
  const [r] = await c.query(`SELECT id, in_hospital_code, qty, department_id FROM gz_dep_inventory WHERE id IN (159, 668) OR department_id=668 AND in_hospital_code LIKE 'G260716%'`);
  console.table(r);
  // Check if update with wrong id could happen - any row 668?
  const [r668] = await c.query(`SELECT id, in_hospital_code, qty FROM gz_dep_inventory WHERE id=668`);
  console.log('id=668:', r668);
  await c.end();
})().catch(console.error);
