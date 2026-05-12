/**
 * 为 gz_order 增加 audit_by 列（与 GzOrderMapper / table.sql 一致）。
 * 用法（PowerShell 示例）：
 *   $env:MYSQL_HOST="rm-xxx.mysql.rds.aliyuncs.com"
 *   $env:MYSQL_PORT="3306"
 *   $env:MYSQL_USER="spd"
 *   $env:MYSQL_PASSWORD="***"
 *   $env:MYSQL_DATABASE="aspt"
 *   cd spd/scripts && npm install mysql2@3 --no-save && node run-add-gz-order-audit-by.js
 */
const mysql = require('mysql2/promise');

const host = process.env.MYSQL_HOST || '127.0.0.1';
const port = Number(process.env.MYSQL_PORT || 3306);
const user = process.env.MYSQL_USER;
const password = process.env.MYSQL_PASSWORD;
const database = process.env.MYSQL_DATABASE;

if (!user || password === undefined) {
  console.error('请设置环境变量 MYSQL_USER、MYSQL_PASSWORD（及 MYSQL_HOST / MYSQL_DATABASE 等）');
  process.exit(1);
}

const sql =
  "ALTER TABLE `gz_order` ADD COLUMN `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人' AFTER `audit_date`";

(async () => {
  const c = await mysql.createConnection({ host, port, user, password, database });
  try {
    await c.query(sql);
    console.log('ALTER TABLE 执行成功: gz_order.audit_by 已添加');
  } catch (e) {
    if (e && e.code === 'ER_DUP_FIELDNAME') {
      console.log('列已存在，跳过: audit_by');
    } else {
      console.error(e.message || e);
      process.exit(1);
    }
  } finally {
    await c.end();
  }
})();
