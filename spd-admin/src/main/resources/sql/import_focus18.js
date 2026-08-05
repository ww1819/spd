/**
 * 将 focus18_import_data.tsv 导入 fd_focus18
 * 用法: node import_focus18.js
 */
const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');

const TENANT_ID = 'hengsui-third-001';
const TSV_PATH = path.join(__dirname, 'focus18_import_data.tsv');

const DB = {
  host: 'rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com',
  port: 3306,
  user: 'spd',
  password: 'Spd@456ww',
  database: 'aspt',
  charset: 'utf8mb4',
};

function parseTsv(text) {
  const rows = [];
  let row = [];
  let field = '';
  let inQuotes = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inQuotes) {
      if (c === '"') {
        if (text[i + 1] === '"') {
          field += '"';
          i++;
        } else {
          inQuotes = false;
        }
      } else {
        field += c;
      }
    } else if (c === '"') {
      inQuotes = true;
    } else if (c === '\t') {
      row.push(field);
      field = '';
    } else if (c === '\n' || (c === '\r' && text[i + 1] === '\n')) {
      if (c === '\r') i++;
      row.push(field);
      field = '';
      if (row.some((x) => x !== '')) rows.push(row);
      row = [];
    } else if (c !== '\r') {
      field += c;
    }
  }
  if (field.length || row.length) {
    row.push(field);
    if (row.some((x) => x !== '')) rows.push(row);
  }
  return rows;
}

function clean(s) {
  if (s == null) return null;
  const t = String(s).replace(/\s+/g, ' ').trim();
  return t === '' ? null : t;
}

async function main() {
  const text = fs.readFileSync(TSV_PATH, 'utf8');
  const rows = parseTsv(text);
  if (rows.length < 2) throw new Error('TSV empty');

  const header = rows[0].map((h) => h.replace(/\s+/g, ''));
  console.log('header cols', header.length, header);

  // Expected: 耗材类别,总序号,产品序号,耗材分类代码,一级分类...,二级...,三级...,通用名代码,医保通用名,材质代码,材质,特征代码,特征参数
  const dataRows = rows.slice(1);
  let lastCategory = null;
  const records = [];
  for (const r of dataRows) {
    while (r.length < 13) r.push('');
    let category = clean(r[0]);
    if (category) lastCategory = category;
    else category = lastCategory;

    const classCode = clean(r[3]);
    if (!classCode || !/^C/i.test(classCode)) continue;

    records.push({
      parent_id: 0,
      category,
      class_code: classCode,
      level1: clean(r[4]),
      level2: clean(r[5]),
      level3: clean(r[6]),
      generic_code: clean(r[7]),
      medical_generic_name: clean(r[8]),
      material_code: clean(r[9]),
      material: clean(r[10]),
      feature_code: clean(r[11]),
      feature_param: clean(r[12]),
      tenant_id: TENANT_ID,
      del_flag: 0,
      create_by: 'import',
      create_time: new Date(),
    });
  }

  console.log('records to insert', records.length);
  if (!records.length) throw new Error('no data rows');

  const categories = [...new Set(records.map((x) => x.category).filter(Boolean))];
  console.log('categories', categories.length);
  categories.forEach((c) => console.log(' -', c));

  if (process.argv.includes('--dry-run')) {
    console.log('dry-run only, skip DB');
    return;
  }

  const conn = await mysql.createConnection(DB);
  try {
    await conn.beginTransaction();

    const [[{ cntBefore }]] = await conn.query(
      'SELECT COUNT(*) AS cntBefore FROM fd_focus18 WHERE tenant_id=? AND del_flag=0',
      [TENANT_ID]
    );
    console.log('before count', cntBefore);

    // 清理该租户已有数据后全量导入（可重入）
    await conn.query('DELETE FROM fd_focus18 WHERE tenant_id=?', [TENANT_ID]);

    const sql = `INSERT INTO fd_focus18
      (parent_id, category, class_code, level1, level2, level3, generic_code, medical_generic_name,
       material_code, material, feature_code, feature_param, del_flag, create_by, create_time, tenant_id)
      VALUES ?`;

    const batchSize = 500;
    let inserted = 0;
    for (let i = 0; i < records.length; i += batchSize) {
      const batch = records.slice(i, i + batchSize).map((x) => [
        x.parent_id,
        x.category,
        x.class_code,
        x.level1,
        x.level2,
        x.level3,
        x.generic_code,
        x.medical_generic_name,
        x.material_code,
        x.material,
        x.feature_code,
        x.feature_param,
        x.del_flag,
        x.create_by,
        x.create_time,
        x.tenant_id,
      ]);
      await conn.query(sql, [batch]);
      inserted += batch.length;
      console.log('inserted', inserted);
    }

    const [[{ cntAfter }]] = await conn.query(
      'SELECT COUNT(*) AS cntAfter FROM fd_focus18 WHERE tenant_id=? AND del_flag=0',
      [TENANT_ID]
    );
    console.log('after count', cntAfter);

    await conn.commit();
    console.log('OK');
  } catch (e) {
    await conn.rollback();
    throw e;
  } finally {
    await conn.end();
  }
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
