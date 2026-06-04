#

<h1 align="center">ShiroAttack2</h1>
<h3 align="center">A fast exploitation tool for Shiro-550 (Apache Shiro rememberMe deserialization)</h3>
<p align="center">
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="ShiroAttack2" src="https://img.shields.io/badge/ShiroAttack2-green"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Forks" src="https://img.shields.io/github/forks/SummerSec/ShiroAttack2"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Release" src="https://img.shields.io/github/release/SummerSec/ShiroAttack2.svg"></a>
  <a href="https://github.com/SummerSec/ShiroAttack2"><img alt="Stars" src="https://img.shields.io/github/stars/SummerSec/ShiroAttack2.svg?style=social&label=Stars"></a>
  <a href="https://github.com/SummerSec"><img alt="Follower" src="https://img.shields.io/github/followers/SummerSec.svg?style=social&label=Follow"></a>
  <a href="https://twitter.com/SecSummers"><img alt="SecSummers" src="https://img.shields.io/twitter/follow/SecSummers.svg"></a>
</p>

> Language / 语言切换：[中文](./README.md) | **[English](./README_EN.md)**

Full usage guide: [docs/USAGE.md](./docs/USAGE.md)

![ShiroAttack2 5.x](docs/readme.png)

---

## Why Shiro-550 Still Works

CVE-2016-4437, discovered in 2016, remains exploitable for three reasons:

**Default key.** Shiro ≤1.2.4 hardcodes an AES key in `CookieRememberMeManager`: `kPH+bIxk5D2deZiIxcaaaA==`. Tutorials and scaffold code have been copying this value for years.

**Keys are hard to rotate.** rememberMe requires the same key on client and server. Once embedded in configs, Docker images, and source repos, replacing it requires updating all nodes.

**Low exploitation cost.** GUI clicks for shell access. CLI for scripting. The lower the cost, the larger the attack surface.

## Attack Flow

```
Detect ── Send rememberMe=yes, check for Set-Cookie: rememberMe=deleteMe
           Shiro 1.x always returns deleteMe on invalid cookies

Crack ── Serialize SimplePrincipalCollection, encrypt with candidate keys
          Response without deleteMe = valid key found

Gadget ── Encrypt full payload (gadget chain + TemplatesImpl echo class) with confirmed key
Exec ── rememberMe cookie carries gadget payload, command embedded in Authorization header
Memshell ── Inject Filter/Servlet via same gadget chain, no longer needs rememberMe
Key Replace ── Swap Shiro's AES key via memshell mechanism, old key invalidated
```

## CLI Mode

Since 5.0, the CLI mode shares the same `AttackService` (1000+ lines) without modification. It works by subclassing `TextArea` to intercept logging output and injecting a mock `MainController` via the `ControllersFactory` registry. CLI initializes JavaFX headlessly with a `JFXPanel` — no window needed.

```bash
java -cp shiro_attack-<version>.jar com.summersec.attack.CLI.MainCLI <command> [options]
```

| Command | Purpose |
|---------|---------|
| `detect` | Check if target runs Shiro framework |
| `crack` | Brute-force or verify Shiro AES key |
| `exec` | Execute system commands (auto gadget detection) |
| `memshell` | Inject memshell (Godzilla/Behinder/AntSword etc.) |
| `changekey` | Replace target's Shiro key |
| `gui` | Launch JavaFX GUI |

With `--json`, output splits into two channels: lines starting with `{` are structured JSON logs (parseable by AI/scripts). Plain text lines are raw command output (grab with `tail -1`).

AES mode: `--cbc` (Shiro ≤1.2.4), `--gcm` (Shiro ≥1.2.5).

Gadget auto-detection prioritizes String/AttrCompare/ObjectToStringComparator variants (no commons-collections dependency), falling back to CB variants requiring `ComparableComparator`.

See [@skills/shiro-attack-cli/SKILL.md](./@skills/shiro-attack-cli/SKILL.md) for detailed CLI usage (structured as an AI Agent skill descriptor).

## Features

- JavaFX GUI + CLI dual mode, shared attack logic
- Multiple CommonsBeanutils gadget versions (1.8.3 / 1.9.2 / AttrCompare / ObjectToStringComparator)
- Auto AES mode switching: tries CBC and GCM, locks whichever hits
- Memshell injection (Filter / Servlet / Interceptor / HandlerMethod / TomcatValve)
- Echo types: TomcatEcho / SpringEcho / DFS-AllEcho / ReverseEcho / NoEcho
- Third-party generator integration (jEG for echo, jMG for memshell) with automatic Legacy fallback
- Shiro Key replacement (6 injection paths, auto verifies old and new keys)
- Custom headers, Cookie merging, POST-based detection
- `--json` structured output for scripting and AI
- HTTP/HTTPS proxy with authentication
- AES Key generator

## Build

```bash
# Install local JARs (required once)
mvn install:install-file -Dfile=libs/jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar

# Build fat JAR (Java 8)
mvn clean package -DskipTests
# Output: target/shiro_attack-5.1.1-all.jar
```

## Download & Run

Two artifact types per release:

- `shiro_attack-<version>-<jdk>.jar`: standalone executable
- `shiro_attack-<version>-<jdk>-bundle.zip`: full bundle with `data/` and `lib/`

Runtime directory structure:

```
./
├── shiro_attack-{version}-{jdk}.jar
├── data/
│   └── shiro_keys.txt   # Key dictionary, one Base64 key per line
└── lib/                 # CommonsBeanutils JARs
```

Releases are auto-built by GitHub Actions on tag push (`v*` or `X.Y.Z`). Optional release notes at `docs/releases/<tag>.md`.

## Documentation

| Document | Description |
|----------|-------------|
| [docs/USAGE.md](./docs/USAGE.md) | Full feature usage guide |
| [docs/FAQ.md](./docs/FAQ.md) | Frequently asked questions |
| [docs/ShiroAttack2-v5-guide.md](./docs/ShiroAttack2-v5-guide.md) | In-depth 5.x feature walkthrough |
| [docs/memshell.md](./docs/memshell.md) | Memshell notes |
| [docs/BypassWaf.md](./docs/BypassWaf.md) | WAF bypass |
| [docs/NoGadget.md](./docs/NoGadget.md) | No-gadget scenarios |
| [docs/THIRD_PARTY_GENERATORS.md](./docs/THIRD_PARTY_GENERATORS.md) | jEG/jMG integration guide |
| [@skills/shiro-attack-cli/SKILL.md](./@skills/shiro-attack-cli/SKILL.md) | AI Agent skill descriptor |

## ⚠️ Disclaimer

**Disclaimer:**

This documentation is intended **solely for authorized security testing** and **academic research**. Any use of this content for illegal activities is **strictly prohibited**.

**Compliance Requirements:**
- All penetration testing must be conducted only after obtaining **written authorization**
- Users must comply with applicable laws and regulations, including but not limited to the Computer Fraud and Abuse Act (CFAA), GDPR, and local cybersecurity laws
- Unauthorized security testing against systems without explicit permission is **illegal**
- Users assume all legal risks and responsibilities

**Usage Guidelines:**
- **Do NOT** perform security testing on any system without authorization
- **Do NOT** use this tool for any form of cybercrime
- **Do NOT** exploit vulnerability information obtained through this tool without authorization
- **Do NOT** distribute this tool to unauthorized users

**Liability Notice:**
- The developers and maintainers of this project (`ShiroAttack2`) assume **no liability** for any consequences arising from the use of this tool
- This tool is provided for security research and authorized testing purposes only
- Users must ensure they have obtained **explicit written authorization** before using this tool
- By using this tool, you agree to abide by all the above terms

**Applicable Laws:**
- Computer Fraud and Abuse Act (CFAA), 18 U.S.C. § 1030
- General Data Protection Regulation (GDPR)
- Cybersecurity Law of the People's Republic of China
- Data Security Law of the People's Republic of China
- Personal Information Protection Law of the People's Republic of China

**Reporting Vulnerabilities:** If you discover a security vulnerability, please disclose it responsibly. Do not publicly disclose vulnerability details without coordination.

---

![Star History](https://starchart.cc/SummerSec/ShiroAttack2.svg)
