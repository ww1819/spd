# -*- coding: utf-8 -*-
import pandas as pd
import sys

def read_excel_file(filename):
    """读取Excel文件"""
    try:
        df = pd.read_excel(filename, engine='xlrd')
        return df
    except Exception as e:
        print(f"Error reading Excel file: {e}")
        return None

def get_parent_code(code):
    """根据编码获取父编码"""
    if pd.isna(code) or code == '':
        return None
    
    code_str = str(code).strip()
    
    # 如果是4位编码（如6801），父编码是0（顶级）
    if len(code_str) == 4:
        return '0'
    # 如果是6位编码（如680101），父编码是前4位（如6801）
    elif len(code_str) == 6:
        return code_str[:4]
    # 如果是8位编码（如68010101），父编码是前6位（如680101）
    elif len(code_str) == 8:
        return code_str[:6]
    else:
        return '0'

def generate_sql(df):
    """生成SQL插入语句"""
    sql_statements = []
    data_list = []
    
    # 收集所有数据
    for index, row in df.iterrows():
        try:
            code = str(row.iloc[2]).strip() if pd.notna(row.iloc[2]) else None
            name = str(row.iloc[3]).strip() if pd.notna(row.iloc[3]) else None
            
            if not code or not name or code == 'nan' or name == 'nan':
                continue
            
            parent_code = get_parent_code(code)
            data_list.append({
                'code': code,
                'name': name.replace("'", "''"),  # 转义单引号
                'parent_code': parent_code
            })
        except Exception as e:
            continue
    
    # 按编码长度排序，确保先插入父级
    data_list.sort(key=lambda x: (len(x['code']), x['code']))
    
    # 生成插入语句（先插入所有记录，parent_id暂时设为0）
    for item in data_list:
        sql = f"INSERT INTO fd_category68 (parent_id, category68_code, category68_name, del_flag, create_time) VALUES (0, '{item['code']}', '{item['name']}', 0, NOW());"
        sql_statements.append(sql)
    
    # 生成更新parent_id的语句
    sql_statements.append("\n-- 更新parent_id关系\n")
    
    # 按编码长度排序，确保先更新父级
    for item in data_list:
        if item['parent_code'] and item['parent_code'] != '0':
            # 使用子查询来获取父ID
            sql = f"UPDATE fd_category68 SET parent_id = (SELECT category68_id FROM (SELECT category68_id FROM fd_category68 WHERE category68_code = '{item['parent_code']}' LIMIT 1) AS tmp) WHERE category68_code = '{item['code']}';"
            sql_statements.append(sql)
    
    return sql_statements

if __name__ == "__main__":
    filename = "68分类.xls"
    df = read_excel_file(filename)
    
    if df is None:
        print("Failed to read Excel file")
        sys.exit(1)
    
    print(f"Read {len(df)} rows from Excel file")
    
    # 生成SQL
    sql_statements = generate_sql(df)
    
    # 保存SQL到文件
    output_file = "insert_category68.sql"
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- 68分类数据插入SQL\n")
        f.write("-- 按编码层级关系生成\n\n")
        for sql in sql_statements:
            f.write(sql + "\n")
    
    insert_count = len([s for s in sql_statements if s.startswith('INSERT')])
    update_count = len([s for s in sql_statements if s.startswith('UPDATE')])
    
    print(f"\nGenerated {insert_count} INSERT statements")
    print(f"Generated {update_count} UPDATE statements")
    print(f"SQL saved to {output_file}")

