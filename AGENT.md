# AGENT.md

## Local build environment

- Maven path: `D:\apache-maven-3.9.9\bin\mvn.cmd`
- JDK path: `C:\Program Files\Zulu\zulu-8`
- `JAVA_HOME`: `C:\Program Files\Zulu\zulu-8`

## Build notes

- When running Maven in this repository, prefer invoking Maven by full path instead of relying on PATH.
- Before Maven commands, explicitly set `JAVA_HOME` to `C:\Program Files\Zulu\zulu-8`.
- On Windows shell usage, prefer `cmd /c` with explicit `set JAVA_HOME=...` and PATH setup when needed.

## Recommended command

```bat
cmd /c "set JAVA_HOME=C:\Program Files\Zulu\zulu-8 && set PATH=%JAVA_HOME%\bin;%PATH% && \"D:\apache-maven-3.9.9\bin\mvn.cmd\" -q -DskipTests package"
```
