# jEG / jMG 集成说明

## 本地安装

先将第三方 JAR 安装到本地 Maven 仓库。

```bash
mvn install:install-file -Dfile=jEG-Core-1.0.0.jar -DgroupId=jeg -DartifactId=jeg-core -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=jmg-sdk-1.0.9.jar -DgroupId=jmg -DartifactId=jmg-sdk -Dversion=1.0.9 -Dpackaging=jar
```

## 已接入依赖

- `jeg:jeg-core:1.0.0`
- `jmg:jmg-sdk:1.0.9`

## 兼容策略

- 默认可以继续使用 Legacy 逻辑。
- 当选择第三方来源失败时，会自动回退 Legacy。
- 原有 Shiro 检测、密钥爆破、利用链测试、命令执行、内存马注入流程不变。
