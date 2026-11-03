const mysql = require('mysql2/promise');

(async () => {
  const conn = await mysql.createConnection({
    host: 'rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com',
    port: 3306,
    user: 'spd',
    password: 'Spd@456ww',
    database: 'aspt',
    ssl: false,
  });

  const [r] = await conn.query(`
    UPDATE sys_menu
    SET menu_name = '组织机构维护',
        remark = '耗材系统客户（租户）/组织机构维护',
        update_by = '1',
        update_time = NOW()
    WHERE menu_id = 2100
       OR (component = 'material/system/customer/index' AND menu_type = 'C')
  `);
  console.log('affected', r.affectedRows);

  const [rows] = await conn.query(`
    SELECT menu_id, menu_name, path, component, perms
    FROM sys_menu
    WHERE menu_id = 2100 OR component = 'material/system/customer/index'
  `);
  console.log(rows);
  await conn.end();
})().catch((e) => {
  console.error(e);
  process.exit(1);
});
