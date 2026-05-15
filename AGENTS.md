# AGENTS.md — ShiroAttack2

## 构建与运行

```bash
# 前置步骤：安装本地 JAR 到 Maven（仅首次需要）
mvn install:install-file -Dfile=libs/jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar

# 打包 fat JAR
mvn clean package -DskipTests
# 产物路径: target/shiro_attack-5.1.0-all.jar
```

- **仅 Java 8**（source/target 8，推荐 Zulu JDK 8+JavaFX）
- GUI 入口：`com.summersec.attack.UI.Main`
- CLI 入口：`com.summersec.attack.CLI.MainCLI` — 支持命令：`detect`, `crack`, `exec`, `memshell`, `changekey`, `gui`
- 运行依赖：`./data/shiro_keys.txt`（Base64 Key，每行一个）和 `./lib/`（CB JAR）
- 无测试套件，仅 release workflow 作为 CI

## CLI 速查

```
java -cp shiro_attack-5.1.0-all.jar com.summersec.attack.CLI.MainCLI <command> [options]
```

| 命令 | 必选参数 | 关键选项 |
|------|----------|----------|
| `detect` | `-u` url | `--cbc` / `--gcm` |
| `crack` | `-u` url | `-K` key（验证单个）或省略（字典爆破） |
| `exec` | `-u`, `-K` key, `-c` cmd | `-g` gadget, `-e` echo（默认 AllEcho）, `--json` |
| `memshell` | `-u`, `-K`, `-t` type | `--pass`, `--path` |
| `changekey` | `-u`, `-K`, `--newkey` | `--variant` |

AES 模式：`--cbc`（Shiro ≤1.2.4），`--gcm`（Shiro ≥1.2.5）。

## 注意事项

- **Gadget 自动探测顺序**（`MainCLI:215-222`）：优先尝试 String/AttrCompare/ObjectToStringComparator 变体（无需 commons-collections），回退到依赖 `ComparableComparator` 的 CB 变体
- **发布**：推送 tag（`v*` 或 `X.Y.Z`）触发 Release，可选版本说明 `docs/releases/<tag>.md`
- **Allatori 混淆**：配置已存在 `allatori/allatori.xml`，但 pom.xml 中已**注释掉**
- **资源文件 `data/shiro_key.txt` 为空**——运行时必须提供真实 key 字典

## 核心架构

| 路径 | 用途 |
|------|------|
| `attack.CLI.MainCLI` | CLI 入口、参数解析、Gadget 自动探测 |
| `attack.UI.Main` | JavaFX GUI 入口 |
| `attack.core.AttackService` | 编排 Key 爆破、Gadget 探测、命令执行 |
| `attack.Encrypt/` | AES-CBC / AES-GCM 加密 |
| `attack.deser.payloads/` | CommonsBeanutils gadget 链实现 |
| `attack.deser.echo/` | 回显 Payload（Tomcat/Spring/DFS/AllEcho/Reverse/NoEcho）|
| `attack.deser.plugins/` | `KeyEcho`（Key 探测）、`InjectMemTool`（内存马） |
| `attack.deser.util.Gadgets` | 通过 Javassist 构造 TemplatesImpl |
| `org.apache.shiro.*` | 内嵌 Shiro 加密源码（可自定义） |
| `com.summersec.x/` | 内存马 Filter/Servlet 类（哥斯拉、冰蝎、蚁剑等） |
| `lib/1.8.3/`, `lib/1.9.2/` | 通过 `systemPath` 加载的 CB JAR |
| `libs/` | jEG-core（`jeg-core`）、jmg-sdk（`jmg-sdk`）本地 JAR |

## 风格

- 不写注释，除非要求
- 已有 `CLAUDE.md` 的所有内容仍然有效
- CLI 详细用法参考 `.claude/skills/shiro-attack-cli/SKILL.md`
