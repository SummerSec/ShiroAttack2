# AGENTS.md — ShiroAttack2

## 构建

```bash
# 首次构建前安装本地 JAR（仅需一次）
mvn install:install-file -Dfile=libs/jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar

# 打包 fat JAR
mvn clean package -DskipTests
# 产物: target/shiro_attack-5.1.1-all.jar
```

**本地环境**（Windows）：`JAVA_HOME=C:\Program Files\Zulu\zulu-8`，Maven 在 `D:\apache-maven-3.9.9\bin\mvn.cmd`。Java 8 必须带 JavaFX（推荐 Zulu 8+fx）。

`pom.xml` 的 `bootclasspath` 引用了 `rt.jar` + `jce.jar`——在纯 JDK 11+ 上编译会失败，必须用 JDK 8。

`assembly.xml` 使用 `<scope>test</scope>` 解包依赖——system scope 的 JAR（`lib/`、`libs/`）通过此机制打进 fat JAR。

**无测试套件**，无 lint/typecheck。仅 release workflow 作为 CI。

## 入口

| 模式 | 类 |
|------|----|
| JavaFX GUI | `com.summersec.attack.UI.Main` |
| CLI | `com.summersec.attack.CLI.MainCLI` — 命令: `detect`, `crack`, `exec`, `memshell`, `changekey`, `gui` |

CLI 支持 `--json` 输出机器可读结果。

## 架构要点

- `attack.core.AttackService` — 编排 Key 爆破、Gadget 探测、命令执行、内存马注入的单一入口
- `attack.deser.util.Gadgets` — 通过 Javassist 构造 `TemplatesImpl`，把回显/内存马类嵌入 translet
- `attack.Encrypt/` — AES-CBC（Shiro ≤1.2.4，`CbcEncrypt`） / AES-GCM（Shiro ≥1.2.5，`GcmEncrypt`），CLI 用 `--cbc` / `--gcm` 指定
- `com.summersec.x/` — 内存马 Filter/Servlet 源码（哥斯拉、冰蝎、蚁剑、reGeorg 等），通过 Javassist 编译后 Base64 编码发送给目标，由 `InjectMemTool` 在目标反序列化时 `defineClass` 加载
- `org.apache.shiro.*` — 内嵌 Shiro 1.2.4 加密源码，可独立定制

### MemBytes 模式

`MemBytes.getBytes(option)` 默认返回 `MEM_TOOLS` 中硬编码的 Base64 字节码。调用 `MemBytes.setDynamicMode(true)` 后可改用 Javassist 运行时编译 `com.summersec.x.*` 源码。

### Gadget 自动探测顺序（`MainCLI:215-222`）

优先尝试 String/AttrCompare/ObjectToStringComparator 变体（无需目标 commons-collections），回退到依赖 `ComparableComparator` 的 CB1 变体。

## SystemPath 依赖

| JAR | 位置 | 用途 |
|-----|------|------|
| `commons-beanutils-1.8.3.jar` | `lib/1.8.3/` | CB 1.8.3 gadget |
| `commons-beanutils-1.9.2.jar` | `lib/1.9.2/` | CB 1.9.2 gadget |
| `jEG-Core-1.0.0.jar` | `libs/` | 加密库 |
| `jmg-sdk-1.0.9.jar` | `libs/` | 内存马生成 SDK |

## 发布

推送 tag（`v*` 或 `X.Y.Z`）触发 Release。构建产物为 `*-all.jar`，release bundle 额外包含 `lib/` 和 `data/`。可选版本说明放 `docs/releases/<tag>.md`。Allatori 混淆配置已注释。

## 运行时依赖

- `data/shiro_key.txt` — Base64 Key 字典（每行一个），**文件本身为空**，必须提供真实 key
- `lib/` — CB JAR，用于运行时 gadget 链 classpath
