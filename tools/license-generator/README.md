# 离线注册码签发工具（内部专用）

与运行时使用同一套规则：**RSA SHA256withRSA**，注册码为单行：`Base64Url(JSON).Base64(签名)`。

## 1. 密钥（公钥在系统里，私钥只在签发机上）


| 文件     | 位置                                                    | 说明                                                               |
| ------ | ----------------------------------------------------- | ---------------------------------------------------------------- |
| **私钥** | `keys/license_signing_private_pkcs8.pem`（**勿提交 Git**） | PKCS#8 PEM，`BEGIN PRIVATE KEY`。仅本工具使用；丢失则无法再签出与现网公钥一致的注册码。       |
| **公钥** | 后端 `spd-system/src/main/resources/license/public.pem` | 与私钥成对；已随应用打包，用于验签。更换密钥后须重新部署该文件（或配置 `spd.license.publicKeyPem`）。 |


### 首次生成密钥对

在 `tools/license-generator` 目录执行（需先 `mvn package`）：

```bat
java -cp "target\license-generator-3.8.6.jar;target\lib\*" com.spd.tools.license.LicenseSigningKeyGenMain keys
```

会在 `keys/` 下生成私钥与 `license_signing_public.pem`。将 **公钥文件全文** 覆盖到 `spd-system/src/main/resources/license/public.pem` 后重新打包后端。

## 2. 签发注册码（私钥文件 + 医院名称 + 到期时间）

**医院名称**须与现场库 **`sys_config` 中 `config_id = 7` 的 `config_value`** 一致（与系统里配置的医院名称相同）。

**方式 A：命令行直接写医院全称**

```bat
java -cp "target\license-generator-3.8.6.jar;target\lib\*" com.spd.tools.license.LicenseGeneratorMain ^
  --key-file keys\license_signing_private_pkcs8.pem ^
  --hospital "与 sys_config.config_id=7 的 config_value 完全一致" ^
  --expire 2030-12-31T23:59:59Z
```

**方式 B：从 MySQL 读取 `sys_config.config_id=7` 的 `config_value`**（`target\lib` 已含 mysql 驱动）

```bat
java -cp "target\license-generator-3.8.6.jar;target\lib\*" com.spd.tools.license.LicenseGeneratorMain ^
  --key-file keys\license_signing_private_pkcs8.pem ^
  --expire 2030-12-31T23:59:59Z ^
  --jdbc-url "jdbc:mysql://127.0.0.1:3306/yourdb?useSSL=false&characterEncoding=utf8" ^
  --jdbc-user root ^
  --jdbc-password yourpass
```

控制台输出 **一行注册码**。

## 3. 现场使用（只输入一行码）

1. **登录页**：底部 **「前端 v… · 后端 v…」** 版本行在 **约 1.2 秒内连续点击 4 次**，弹出「系统授权」，粘贴注册码后点 **注册**（无需再选组织机构，只要注册码内医院名与 `sys_config` id=7 一致）。
2. **登录后**：系统管理 → **离线授权** 仍可导入。

## 4. 版本说明

- **v2（推荐）**：`hospitalName` + `expireAt`，医院名与 **`sys_config.config_id=7` 的 `config_value`** 一致；**无需实例 ID**。  
- **v1（兼容）**：旧 `instanceId` 码仍支持。

## 5. Linux / macOS

classpath 用 `:`，路径用 `/`。

## 6. 登录页内嵌生成器（浏览器本地签名）

在 **`spd-ui` 项目根** 配置（勿把真实口令提交到公开仓库）：

```properties
VUE_APP_LICENSE_BUILDER_SECRET=你的签发口令
```

执行 `npm run build` 后，登录页隐藏入口（版本行约 1.2 秒内连点 4 次）里会出现 **「生成注册码」**：先验证口令，再选择 **PKCS#8 私钥 PEM**、填写医院全称（须与 **`sys_config.config_id=7` 的 `config_value`** 一致）与到期日，在浏览器内完成 RSA 签名（**私钥不会上传服务器**）。请使用 **Chrome / Edge** 等支持 Web Crypto 的浏览器。

说明：口令写进前端打包结果，仅作**弱保护**；高安全场景请使用上文命令行工具签发。
