/**
 * 从 focus18_import_data.tsv 生成可重复执行的生产 SQL 脚本
 * 用法: node gen_focus18_data_sql.js
 */
const fs = require('fs');
const path = require('path');

const TENANT_ID = 'hengsui-third-001';
const TSV_PATH = path.join(__dirname, 'focus18_import_data.tsv');
const OUT_PATH = path.join(__dirname, 'mysql', 'material', 'focus18_data.sql');
const BATCH = 200;

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

function sqlStr(v) {
  if (v == null) return 'NULL';
  return "'" + String(v).replace(/\\/g, '\\\\').replace(/'/g, "''") + "'";
}

function main() {
  const text = fs.readFileSync(TSV_PATH, 'utf8');
  const rows = parseTsv(text);
  if (rows.length < 2) throw new Error('TSV empty');

  let lastCategory = null;
  const records = [];
  for (const r of rows.slice(1)) {
    while (r.length < 13) r.push('');
    let category = clean(r[0]);
    if (category) lastCategory = category;
    else category = lastCategory;
    const classCode = clean(r[3]);
    if (!classCode || !/^C/i.test(classCode)) continue;
    records.push({
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
    });
  }

  const lines = [];
  lines.push('-- =============================================================================');
  lines.push('-- 18类重点耗材明细数据（可重复执行）');
  lines.push('-- 租户：hengsui-third-001（衡水市第三人民医院）');
  lines.push('-- 行数：' + records.length);
  lines.push('-- 说明：');
  lines.push('--   1) 确保表 fd_focus18 存在（见 create / table 脚本）');
  lines.push('--   2) 先删除该租户下 create_by=import 的旧明细，再全量插入');
  lines.push('--   3) 由 SqlInitRunner 在系统启动时自动执行（material/focus18_data.sql）');
  lines.push('-- =============================================================================');
  lines.push('');
  lines.push('CREATE TABLE IF NOT EXISTS fd_focus18 (');
  lines.push("  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',");
  lines.push("  parent_id bigint(20) DEFAULT 0 COMMENT '上级ID(0为根)',");
  lines.push("  category varchar(100) DEFAULT NULL COMMENT '耗材类别',");
  lines.push("  class_code varchar(100) DEFAULT NULL COMMENT '耗材分类代码',");
  lines.push("  level1 varchar(200) DEFAULT NULL COMMENT '一级分类(学科/品类)',");
  lines.push("  level2 varchar(200) DEFAULT NULL COMMENT '二级分类(用途/品目)',");
  lines.push("  level3 varchar(200) DEFAULT NULL COMMENT '三级分类(部位/功能/品种)',");
  lines.push("  generic_code varchar(100) DEFAULT NULL COMMENT '通用名代码',");
  lines.push("  medical_generic_name varchar(200) DEFAULT NULL COMMENT '医保通用名',");
  lines.push("  material_code varchar(100) DEFAULT NULL COMMENT '材质代码',");
  lines.push("  material varchar(200) DEFAULT NULL COMMENT '材质',");
  lines.push("  feature_code varchar(100) DEFAULT NULL COMMENT '特征代码',");
  lines.push("  feature_param varchar(500) DEFAULT NULL COMMENT '特征参数',");
  lines.push("  remark varchar(500) DEFAULT NULL COMMENT '备注',");
  lines.push("  del_flag int(1) DEFAULT 0 COMMENT '删除标识(0正常 1删除)',");
  lines.push("  create_by varchar(64) DEFAULT NULL COMMENT '创建者',");
  lines.push("  create_time datetime DEFAULT NULL COMMENT '创建时间',");
  lines.push("  update_by varchar(64) DEFAULT NULL COMMENT '更新者',");
  lines.push("  update_time datetime DEFAULT NULL COMMENT '更新时间',");
  lines.push("  delete_by varchar(64) DEFAULT NULL COMMENT '删除者',");
  lines.push("  delete_time datetime DEFAULT NULL COMMENT '删除时间',");
  lines.push("  tenant_id varchar(64) DEFAULT NULL COMMENT '租户ID',");
  lines.push('  PRIMARY KEY (id),');
  lines.push('  KEY idx_fd_focus18_tenant (tenant_id),');
  lines.push('  KEY idx_fd_focus18_parent (tenant_id, parent_id),');
  lines.push('  KEY idx_fd_focus18_class_code (tenant_id, class_code),');
  lines.push('  KEY idx_fd_focus18_generic (tenant_id, generic_code)');
  lines.push(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='18类重点耗材维护';");
  lines.push('/');
  lines.push('');
  lines.push("DELETE FROM fd_focus18 WHERE tenant_id = '" + TENANT_ID + "' AND create_by = 'import';");
  lines.push('/');
  lines.push('');

  const cols =
    '(parent_id, category, class_code, level1, level2, level3, generic_code, medical_generic_name, ' +
    'material_code, material, feature_code, feature_param, del_flag, create_by, create_time, tenant_id)';

  for (let i = 0; i < records.length; i += BATCH) {
    const batch = records.slice(i, i + BATCH);
    const values = batch.map((x) => {
      return (
        '(0, ' +
        [
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
        ]
          .map(sqlStr)
          .join(', ') +
        ", 0, 'import', NOW(), '" +
        TENANT_ID +
        "')"
      );
    });
    lines.push('INSERT INTO fd_focus18 ' + cols + ' VALUES');
    lines.push(values.join(',\n') + ';');
    lines.push('/');
    lines.push('');
  }

  fs.mkdirSync(path.dirname(OUT_PATH), { recursive: true });
  fs.writeFileSync(OUT_PATH, lines.join('\n'), 'utf8');
  console.log('wrote', OUT_PATH);
  console.log('records', records.length);
  console.log('batches', Math.ceil(records.length / BATCH));
  console.log('bytes', fs.statSync(OUT_PATH).size);
}

main();
