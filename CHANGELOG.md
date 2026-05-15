# Changelog

## [5.1.0] — 2026-05-15

### 新增
- **CLI 命令行模式**：支持 `detect` / `crack` / `exec` / `memshell` / `changekey` / `gui` 六个子命令，无需 GUI 即可单目标快速利用
- CLI `--json` 输出模式：日志行以结构化 JSON 输出，方便 AI / 脚本解析
- 新增 `OutputSink` 接口、`CliOutputSink`、`ConsoleTextArea`，通过 TextArea 子类化实现日志输出的 GUI/CLI 双模式分发，零修改 AttackService 核心代码
- CLI `memshell` 命令：支持哥斯拉、冰蝎、蚁剑、NeoreGeorg、reGeorg 的 Filter/Servlet 型内存马注入
- CLI `changekey` 命令：支持 6 种注入路径变体的 Shiro Key 动态替换
- CLI 自动探测优先选择无 `commons-collections` 依赖的 Gadget 变体（String / AttrCompare / ObjectToStringComparator）
- 新增 AES-CBC + AES-GCM 双模式同时爆破 Key 功能
- 新增一键下载最新 Key 字典功能

### 修复
- 修复 `CommonsBeanutils1_183` 等 `_183` 变体中 `BeanComparator.serialVersionUID` 被错误覆写为 1.9.2 值的问题（复原为 1.8.3 的正确值 `-3490850999041592962L`）
- 修复 `_183` 变体中 serialVersionUID 注释与代码相互矛盾的问题
- 修复自定义 Content-Type 时请求头出现重复 Content-Type 行

### 文档
- 新增 `skills/shiro-attack-cli/SKILL.md` — 中文 AI Agent CLI 使用 Skill 指南
- 新增 `CHANGELOG.md`
- 更新中英文 README 加入 CLI 使用章节
- 更新 CLAUDE.md 加入 CLI 构建/运行指令与架构细节

## [5.0.2]

### 新增
- 下载 Key 字典功能与 UI 调整
- AES-CBC + AES-GCM 双模式爆破

### 修复
- 自定义 Content-Type 时请求头出现重复 Content-Type 行
- macOS Apple Silicon + Zulu JDK 8 下 AWT native library 加载崩溃

## [5.0.1]

### 新增
- 请求头合并逻辑：先全局头再以单次请求头覆盖，Cookie 合并保留
- `bodyHttpRequest` 对 GET 空 body 使用 `flattenHutoolResponse`

### 修复
- `bodyHttpRequest` 中 `|` 逻辑条件修正为 `||`
- 统一 jMG 与内置内存马 Shiro 注入流程

## [5.0.0]

### 新增
- 重构 UI，集成 jEG / jMG 模块
- Shiro Key 内存马替换功能增强版（6 种注入路径变体、历史 Key 记录）
- Echo Generator（jEG）、Memshell Generator（jMG）
- 第三方生成失败时自动回退到 Legacy
- 全自动化 Release 构建流程
