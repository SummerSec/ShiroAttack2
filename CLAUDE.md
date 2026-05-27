# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# 前置步骤：安装本地 JAR 到 Maven（仅首次需要）
mvn install:install-file -Dfile=libs/jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar

# 打包为可执行 fat JAR（含所有依赖）
mvn clean package -DskipTests

# 产物路径: target/shiro_attack-5.1.1-all.jar

# 运行 CLI（命令行模式，无需 GUI）
java -cp target/shiro_attack-5.1.1-all.jar com.summersec.attack.CLI.MainCLI <command> [options]

# CLI 可用命令: detect | crack | exec | memshell | changekey | gui
# 详见 .claude/skills/shiro-attack-cli/SKILL.md

# 运行（需要 JavaFX 运行时，Java 8）
java -jar target/shiro_attack-5.1.1-all.jar
```

本项目无测试套件；仅有 GitHub Release CI（推送 tag 触发）。

## 插件市场

本项目同时也是 Claude Code 插件市场，通过 `.claude-plugin/marketplace.json` 注册了 `shiro-attack-cli` 技能。

```bash
# 安装（来源为 GitHub）
claude plugins add github:SummerSec/ShiroAttack2
```

支持平台：Claude Code (`.claude-plugin/`)、Codex CLI (`.codex-plugin/`)、Cursor (`.cursor-plugin/`)。skill 源文件在 `skills/shiro-attack-cli/SKILL.md`。

## 架构概述

ShiroAttack2 是一个利用 Apache Shiro rememberMe AES 反序列化漏洞（Shiro-550）的 JavaFX GUI 工具。

### 攻击流程

1. **密钥爆破** — `deser/plugins/keytest/KeyEcho.java` 将 `SimplePrincipalCollection` 序列化后用候选密钥（`resources/data/shiro_key.txt`）逐一加密，通过响应判断有效密钥
2. **Gadget 链选择** — 用户在 UI 中选择适合目标 classpath 的 `CommonsBeanutils` 变体（1.8.3 / 1.9.2 / AttrCompare）
3. **回显类型选择** — Tomcat / Spring / DFS-AllEcho / NoEcho / 反弹 Shell
4. **Payload 构造** — `deser/util/Gadgets.java` 用 Javassist 将回显类嵌入 `TemplatesImpl` translet，Gadget 链包装后序列化
5. **加密发送** — `Encrypt/CbcEncrypt.java`（AES-CBC）或 `Encrypt/GcmEncrypt.java`（AES-GCM）加密后 Base64 编码，写入 rememberMe cookie 发送

### 主要包职责

| 包 | 职责 |
|---|---|
| `attack.UI` | JavaFX 入口 (`Main`) 和主窗口控制器 |
| `attack.core` | `AttackService` — 串联爆破、Gadget、加密、发送的编排层 |
| `attack.Encrypt` | AES-CBC / AES-GCM 加密；`KeyGenerator` 生成新密钥 |
| `attack.deser.payloads` | CommonsBeanutils gadget 链实现 |
| `attack.deser.echo` | 各平台回显 Payload 生成（Tomcat/Spring/DFS/Reverse/NoEcho） |
| `attack.deser.plugins` | 内存马注入（`InjectMemTool`）、密钥探测（`KeyEcho`）|
| `attack.deser.frame` | Shiro cookie 序列化/加密封装 |
| `attack.deser.util` | `Gadgets`（TemplatesImpl 构造）、反射工具、JavaVersion |
| `attack.utils` | HTTP 请求（Hutool）、AES 工具、控制台输出 |
| `attack.entity` | `ControllersFactory`（JavaFX 控制器注册表）、`RequestInfo` |
| `org.apache.shiro.*` | 内嵌 Shiro 加密源码（`AesCipherService` 等），便于定制 |

### 关键依赖说明

- `lib/` 目录下的本地 JAR（`commons-beanutils-1.8.3.jar` 等）通过 `pom.xml` 中 `systemPath` 引入，是 Gadget 链的实际攻击依赖，与常规 Maven 依赖分开管理
- `assembly.xml` 配置将 test-scope 依赖也解包进 fat JAR
- Allatori 混淆配置已存在但在 `pom.xml` 中被注释，发布时可按需启用
