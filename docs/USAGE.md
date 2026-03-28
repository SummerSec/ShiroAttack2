# ShiroAttack2 完整使用说明

> 本工具仅授权用于企业内部安全自查检测，请勿用于非法用途。

## 目录

- [环境要求与启动](#环境要求与启动)
- [目录结构](#目录结构)
- [界面总览](#界面总览)
- [功能详解](#功能详解)
  - [目标与请求配置](#1-目标与请求配置)
  - [检测与攻击基础参数](#2-检测与攻击基础参数)
  - [检测日志](#3-检测日志)
  - [命令执行](#4-命令执行)
  - [内存马注入](#5-内存马注入)
  - [修改 Shiro Key](#6-修改-shiro-key)
  - [回显生成（jEG）](#7-回显生成jeg)
  - [内存马生成（jMG）](#8-内存马生成jmg)
  - [Key 生成](#9-key-生成)
  - [代理设置](#10-代理设置)
- [攻击流程](#攻击流程)
- [常见问题](#常见问题)

---

## 环境要求与启动

- Java 8（推荐 Zulu JDK 8）
- 无需额外安装，下载 fat JAR 直接运行：

```bash
java -jar shiro_attack-{version}-SNAPSHOT-all.jar
```

---

## 目录结构

```
./
├── shiro_attack-{version}-SNAPSHOT-all.jar
├── data/
│   └── shiro_keys.txt   # Key 字典，每行一个 Base64 编码的 AES Key
└── lib/                 # CommonsBeanutils 各版本 JAR（可选，用于不同 CB 版本 gadget）
    ├── commons-beanutils-1.8.3.jar
    └── commons-beanutils-1.9.2.jar
```

`shiro_keys.txt` 每行写一个 Base64 格式的 Shiro Key，工具启动时自动加载。

---

## 界面总览

主界面分为三个区域：

1. **顶部**：目标配置、请求参数、检测与攻击基础参数
2. **中部 Tab 区**：各功能操作面板
3. **底部**：代理状态显示、GitHub 链接

Tab 列表：`检测日志` | `命令执行` | `内存马注入` | `修改 Shiro Key` | `回显生成` | `内存马生成` | `Key 生成`

---

## 功能详解

### 1. 目标与请求配置

| 字段 | 说明 |
|------|------|
| 请求方法 | GET / POST，支持 POST 型 Shiro 探测 |
| 目标地址 | 完整 URL，如 `http://target/path` |
| 超时(s) | HTTP 请求超时时间（秒） |
| Header | 自定义请求头，格式：`K1:V1&&&K2:V2`，多个用 `&&&` 分隔 |
| POST 数据 | POST 请求体，格式：`a=1&b=2` |

**自定义请求头示例：**
```
X-Forwarded-For:127.0.0.1&&&User-Agent:Mozilla/5.0
```

### 2. 检测与攻击基础参数

| 字段 | 说明 |
|------|------|
| rememberMe 关键词 | 默认 `rememberMe`，可修改以适配自定义关键词 |
| 指定密钥 | 手动输入指定 Key 进行验证 |
| Gadget | 选择反序列化利用链（CommonsBeanutils 版本） |
| 回显方式 | Tomcat / Spring / AllEcho（DFS）/ NoEcho / 反弹 Shell |
| 检测是否为 Shiro | 发送探测请求，判断目标是否存在 Shiro rememberMe |
| 爆破密钥 | 使用 `data/shiro_keys.txt` 字典逐一爆破 Key |
| 检测当前利用链 | 用已获取的 Key 验证当前 Gadget 是否可用 |
| 爆破利用链及回显 | 自动遍历所有 Gadget + 回显方式组合（较慢） |
| 停止 | 中断当前爆破任务 |

**Gadget 选项说明：**

| Gadget | 适用场景 |
|--------|----------|
| CommonsBeanutils1 (CB 1.8.3) | 目标 classpath 含 commons-beanutils 1.8.3 |
| CommonsBeanutils2 (CB 1.9.2) | 目标 classpath 含 commons-beanutils 1.9.2 |
| CommonsBeanutils1 AttrCompare | 特殊场景（AttrCompare 排序器） |

**回显方式说明：**

| 回显方式 | 说明 |
|----------|------|
| Tomcat | 通过 Tomcat Request/Response 对象直接回显 |
| Spring | 通过 Spring RequestContextHolder 回显 |
| AllEcho (DFS) | 使用 DFS 算法遍历所有可能对象，兼容性最强，但较慢 |
| NoEcho | 不回显，仅执行命令（适合反弹 Shell 等场景） |
| 反弹 Shell | 发送反弹 Shell payload |

### 3. 检测日志

显示 Shiro 检测、Key 爆破、Gadget 验证的实时日志输出。

### 4. 命令执行

在成功获取 Key 和 Gadget 后，在此 Tab 输入命令并执行：

1. 在顶部输入框填写命令（如 `whoami`、`id`）
2. 点击「执行」
3. 命令回显结果显示在下方文本区

> 需先完成 Key 爆破和 Gadget 检测，确认回显方式后再执行命令。

### 5. 内存马注入

将内存马注入目标服务器：

| 字段 | 说明 |
|------|------|
| 类型 | 内存马类型，见下表 |
| 路径 | 内存马访问路径，如 `/shell` |
| 密码 | 内存马连接密码 |

**支持的内存马类型：**

| 类型 | 说明 |
|------|------|
| GodzillaFilter | 哥斯拉 Filter 型 |
| GodzillaServlet | 哥斯拉 Servlet 型 |
| BehinderFilter | 冰蝎 Filter 型 |
| AntSwordFilter | 蚁剑 Filter 型 |
| NeoreGeorgFilter | NeoreGeorg 代理 Filter |
| reGeorgFilter | reGeorg 代理 Filter |
| BastionFilter | 堡垒机 Filter 型 |
| BastionEncryFilter | 堡垒机加密 Filter 型 |

注入成功后，使用对应的 webshell 管理工具连接目标路径。

### 6. 修改 Shiro Key

通过内存马方式动态替换目标服务器的 Shiro rememberMe AES Key。

> **警告**：此操作可能导致目标业务异常，请在授权场景下谨慎使用。

| 字段 | 说明 |
|------|------|
| 方式 | 注入变体，见下表 |
| 目标 Key | 要替换成的新 Key（Base64 格式），支持历史记录（最多 30 条） |
| 清空历史 | 清除保存的历史 Key 记录 |
| 执行 | 执行 Key 替换并验证 |

**注入变体说明：**

| 变体 | 说明 | 风险 |
|------|------|------|
| filterConfigs -> shiroFilterFactoryBean | 标准 Spring 路径，推荐首选 | 低 |
| getFilterRegistration -> shiroFilterFactoryBean | 备选 Spring 路径 | 低 |
| filterConfigs -> 常见 Shiro 名依次匹配 | 自动匹配常见 Filter 名（shiroFilter、shiro 等） | 低 |
| getFilterRegistration -> 常见 Shiro 名依次匹配 | 同上，备选路径 | 低 |
| filterConfigs -> 包含 shiro 的名称扫描 | 模糊扫描含 shiro 字符串的 Filter | 中 |
| **高风险**: 全候选 rememberMeManager 扫描 | 替换所有候选 rememberMeManager，多节点场景 | **高** |

注入后工具会自动验证：
- 新 Key 可用 + 旧 Key 失效 → **成功**
- 新 Key 可用 + 旧 Key 仍可用 → **部分成功**（疑似多 rememberMeManager 或多节点）
- 新 Key 验证失败 → **未确认成功**，尝试其他变体

**使用前提：** 需先完成 Key 爆破，获取当前有效 Key 和 Gadget。

### 7. 回显生成（jEG）

基于 `java-echo-generator`（jEG）的独立回显 Payload 生成器，可脱离 Shiro 攻击链单独使用。

| 字段 | 说明 |
|------|------|
| 来源 | `Legacy`（原有回显链路）或 `jEG`（第三方生成器） |
| 服务端类型 | 目标服务器类型（Tomcat、Spring 等） |
| 模型 | 回显执行模型 |
| 格式 | Payload 输出格式 |

点击「生成」后，回显 Payload 显示在下方文本区，可直接复制使用。

> 需提前安装 `jeg-core` 到本地 Maven，详见 [THIRD_PARTY_GENERATORS.md](./THIRD_PARTY_GENERATORS.md)。

### 8. 内存马生成（jMG）

基于 `java-memshell-generator`（jMG）的独立内存马生成器，支持多种工具和 Shell 类型组合。

| 字段 | 说明 |
|------|------|
| 来源 | `Legacy`（原有内存马链路）或 `jMG`（第三方生成器） |
| 工具 | 目标 webshell 管理工具 |
| 服务端 | 目标服务器类型 |
| Shell 类型 | 内存马类型 |
| 格式 | Payload 输出格式 |
| Gadget | 反序列化利用链（可选） |

**支持的工具：**

| 工具 | 说明 |
|------|------|
| Godzilla（哥斯拉） | 哥斯拉 webshell 管理工具 |
| AntSword（蚁剑） | 蚁剑 webshell 管理工具 |
| Behinder（冰蝎） | 冰蝎 webshell 管理工具 |
| NeoreGeorg | NeoreGeorg 代理工具 |
| reGeorg | reGeorg 代理工具 |

**支持的 Shell 类型：**

| Shell 类型 | 说明 |
|------------|------|
| Filter | Servlet Filter 型（兼容性好，推荐） |
| Servlet | Servlet 型 |
| Interceptor | Spring MVC Interceptor 型 |
| HandlerMethod | Spring MVC HandlerMethod 型 |
| TomcatValve | Tomcat Valve 型 |

点击「生成」后，内存马 Payload 显示在下方文本区，可直接复制使用。

> 需提前安装 `jmg-sdk` 到本地 Maven，详见 [THIRD_PARTY_GENERATORS.md](./THIRD_PARTY_GENERATORS.md)。

### 9. Key 生成

随机生成符合 Shiro AES 加密要求的 Key：

1. 点击「生成 Key」按钮
2. 下方文本区显示生成的 Base64 格式 AES Key
3. 可将生成的 Key 复制到「修改 Shiro Key」中使用

### 10. 代理设置

通过菜单栏「设置 → 代理」打开代理配置对话框：

| 字段 | 说明 |
|------|------|
| 代理类型 | HTTP / HTTPS / SOCKS |
| 代理地址 | 代理服务器 IP |
| 代理端口 | 代理服务器端口 |
| 用户名 | 代理认证用户名（可选） |
| 密码 | 代理认证密码（可选） |

配置后底部状态栏显示当前代理状态。

---

## 攻击流程

标准攻击流程分为以下步骤：

```
1. 填写目标 URL
          ↓
2. 点击「检测是否为 Shiro」
   → 确认目标存在 Shiro rememberMe
          ↓
3. 点击「爆破密钥」
   → 使用 data/shiro_keys.txt 字典爆破
   → 成功后日志显示有效 Key
          ↓
4. 选择 Gadget 和回显方式，点击「检测当前利用链」
   → 确认利用链可用
          ↓
5. 切换到「命令执行」Tab，输入命令执行
          ↓
6. 按需切换到「内存马注入」进行持久化
```

**可选流程：**

- **修改 Shiro Key**：完成步骤 4 后，切换到「修改 Shiro Key」Tab 执行
- **jEG/jMG 独立使用**：无需完成攻击流程，直接在对应 Tab 填写参数生成 Payload

---

## 常见问题

详见 [FAQ.md](./FAQ.md)

**补充说明：**

1. **AllEcho（DFS）较慢**：DFS 算法本身存在延迟，爆破利用链时建议手动逐一尝试
2. **4.3 版本可打但高版本不行**：已知问题，暂无定论，建议逐一手动测试各 Gadget
3. **Key 替换后仍存在旧 Key**：疑似多节点或多 rememberMeManager，尝试「高风险」变体
4. **jEG/jMG 生成失败**：检查本地 Maven 是否正确安装对应 JAR，见 [THIRD_PARTY_GENERATORS.md](./THIRD_PARTY_GENERATORS.md)
5. **代理不生效**：确认代理地址和端口填写正确
