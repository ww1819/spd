# 重启应用步骤

## 方法1：使用IDE重启
1. 在IDE中停止应用（点击停止按钮）
2. 等待5-10秒
3. 重新启动应用

## 方法2：清理并重新编译（如果方法1不行）
```bash
# 进入项目根目录
cd spd

# 清理编译文件
mvn clean

# 重新编译
mvn compile

# 重新打包（如果需要）
mvn package

# 重启应用
```

## 方法3：检查数据库连接
确认应用连接的数据库与创建表的数据库是同一个：
- 数据库地址：rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com:3306
- 数据库名：aspt
- 用户名：spd

## 验证步骤
1. 在远程数据库中执行：`SELECT COUNT(*) FROM sys_print_setting;`
2. 如果返回0或正常数字，说明表存在
3. 重启应用
4. 刷新浏览器页面
5. 检查是否还有错误
