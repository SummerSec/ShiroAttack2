#

<h1 align="center">ShiroAttack2</h1>
<h3 align="center">A fast exploitation tool for the Shiro-550 (Apache Shiro rememberMe deserialization) vulnerability</h3>
<p align="center">
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="ShiroAttack2" src="https://img.shields.io/badge/ShiroAttack2-green"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Forks" src="https://img.shields.io/github/forks/SummerSec/ShiroAttack2"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Release" src="https://img.shields.io/github/release/SummerSec/ShiroAttack2.svg"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Stars" src="https://img.shields.io/github/stars/SummerSec/ShiroAttack2.svg?style=social&label=Stars"></a>
  <a href="https://github.com/SummerSec"><img alt="Follower" src="https://img.shields.io/github/followers/SummerSec.svg?style=social&label=Follow"></a>
  <a href="https://twitter.com/SecSummers"><img alt="SecSummers" src="https://img.shields.io/twitter/follow/SecSummers.svg"></a>
</p>

---

> Language / 语言切换：[中文](./README.md) | **[English](./README_EN.md)**

Full usage guide: [docs/USAGE.md](./docs/USAGE.md)

![image-20211130113559530](./docs/readme.png)

## Introduction

ShiroAttack2 is a JavaFX GUI tool for detecting and exploiting **Apache Shiro rememberMe AES deserialization vulnerabilities (CVE-2016-4437 / Shiro-550)**.

Update notes are published at **https://shiro.sumsec.me/**

## Features

- JavaFX GUI, ready to run out of the box
- Handles targets with no third-party dependencies on classpath
- Supports multiple CommonsBeanutils gadget versions (1.8.3 / 1.9.2 / AttrCompare)
- Memshell injection (Filter / Servlet, supports Godzilla, AntSword, Behinder, NeoreGeorg, reGeorg)
- Direct command echo (Tomcat / Spring / DFS-AllEcho)
- Custom rememberMe keyword support
- Automated gadget + key bruteforce
- HTTP/HTTPS proxy support with authentication
- Shiro Key replacement via memshell injection (**may disrupt target service**)
- DFS-based AllEcho for maximum compatibility
- Custom request headers: `K1:V1&&&K2:V2`
- POST-based Shiro detection and exploitation
- AES Key generator

## New Features

### Shiro Key Replacement (Enhanced)

Dynamically replaces the target server's Shiro rememberMe AES Key via memshell injection. After injection, the tool automatically verifies that the new key works and the old key is invalidated.

| Variant | Description |
|---------|-------------|
| filterConfigs -> shiroFilterFactoryBean | Standard Spring injection path (recommended) |
| getFilterRegistration -> shiroFilterFactoryBean | Alternative Spring path |
| filterConfigs -> common Shiro name match | Tries common Shiro Filter names automatically |
| getFilterRegistration -> common Shiro name match | Same as above, alternative path |
| filterConfigs -> name scan containing "shiro" | Fuzzy scan for Filters with "shiro" in their name |
| **High Risk**: full rememberMeManager scan | Replaces all candidate rememberMeManagers (multi-node scenarios) |

Supports history of up to 30 previously used Keys.

### Echo Generator (jEG Module)

Standalone echo payload generator based on [java-echo-generator](https://github.com/c0ny1/java-echo-generator) (`jeg-core`):

- Source: `Legacy` (built-in echo chain) or `jEG` (third-party generator)
- Freely combine server type, execution model, and output format
- Automatically falls back to Legacy on failure

### Memshell Generator (jMG Module)

Standalone memshell generator based on [java-memshell-generator](https://github.com/pen4uin/java-memshell-generator) (`jmg-sdk`):

- Source: `Legacy` (built-in memshell) or `jMG` (third-party generator)
- Supported tools: Godzilla, AntSword, Behinder, NeoreGeorg, reGeorg
- Supported servers: Tomcat, Spring MVC
- Supported shell types: Filter, Servlet, Interceptor, HandlerMethod, TomcatValve
- Automatically falls back to Legacy on failure

### Third-party Dependency Installation

Before building, install the following JARs to your local Maven repository:

```bash
mvn install:install-file -Dfile=jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar
```

See [docs/THIRD_PARTY_GENERATORS.md](./docs/THIRD_PARTY_GENERATORS.md) for details.

## Documentation

| Document | Description |
|----------|-------------|
| [docs/USAGE.md](./docs/USAGE.md) | Full feature usage guide |
| [docs/FAQ.md](./docs/FAQ.md) | Frequently asked questions |
| [docs/memshell.md](./docs/memshell.md) | Memshell notes |
| [docs/BypassWaf.md](./docs/BypassWaf.md) | WAF bypass |
| [docs/NoGadget.md](./docs/NoGadget.md) | No-gadget scenarios |
| [docs/THIRD_PARTY_GENERATORS.md](./docs/THIRD_PARTY_GENERATORS.md) | jEG/jMG integration guide |

## Usage

Run the fat JAR directly (all dependencies bundled):

```bash
java -jar shiro_attack-{version}-SNAPSHOT-all.jar
```

**Directory structure:**

```
./
├── shiro_attack-{version}-SNAPSHOT-all.jar
├── data/
│   └── shiro_keys.txt   # Key dictionary, one Base64-encoded AES Key per line
└── lib/                 # Optional: CommonsBeanutils JARs for different gadget versions
```

Create a `data` folder in the same directory as the JAR, and place your `shiro_keys.txt` Key dictionary inside it.

## Attack Workflow

```
1. Enter target URL
         ↓
2. Click "Detect Shiro"
   → Confirm target has Shiro rememberMe
         ↓
3. Click "Bruteforce Key"
   → Uses data/shiro_keys.txt dictionary
   → Valid key shown in log on success
         ↓
4. Select Gadget + Echo type, click "Test Gadget"
   → Confirm exploit chain is usable
         ↓
5. Switch to "Command Exec" tab, run commands
         ↓
6. Optionally switch to "Memshell Inject" for persistence
```

## Build from Source

```bash
# Requires Java 8 and Maven
mvn clean package -DskipTests
# Output: target/shiro_attack-{version}-SNAPSHOT-all.jar
```

## FAQ

See [docs/FAQ.md](./docs/FAQ.md)

---

## Disclaimer

This tool is intended **solely for authorized internal security assessments**.

The author assumes no responsibility for any direct or indirect consequences arising from the use of this tool. Users bear full responsibility for their actions.

Do not use this tool for any illegal activities. Do not use it for commercial purposes without authorization. Comply with applicable cybersecurity laws and regulations.

This tool is authorized only for internal enterprise security audits.

---

![Star History](https://starchart.cc/SummerSec/ShiroAttack2.svg)
