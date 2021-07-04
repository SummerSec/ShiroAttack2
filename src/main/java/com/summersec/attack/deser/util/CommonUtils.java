package com.summersec.attack.deser.util;

import com.summersec.attack.utils.HttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static String readStringFromInputStream(InputStream inputStream) throws Exception{
        StringBuilder stringBuilder = new StringBuilder("");
        byte[] bytes = new byte[1024];
        int n = 0;
        while ((n=inputStream.read(bytes)) != -1){
            stringBuilder.append(new String(bytes,0,n));
        }
        return stringBuilder.toString();
    }


//    public static byte[] getDetectText() throws Exception{
//        InputStream inputStream = HttpUtils.class.getClassLoader().getResourceAsStream("detect.txt");
//        // 读取字节流还是用 ByteArrayOutputStream
//        // 将数据读到 byteArrayOutputStream 中
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        int n;
//        while ((n=inputStream.read()) != -1){
//            byteArrayOutputStream.write(n);
//        }
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        return bytes;
//    }

    public static Map<String,String> normalProxy(Map<String,String> paramContext) {
        String myProxy = paramContext.get("MyProxy");
        String host = myProxy.split(":")[0];
        String port = myProxy.split(":")[1];
        Map<String,String> proxy = new HashMap<>();
        proxy.put("host",host);
        proxy.put("port", String.valueOf(port));
        return proxy;
    }

    public static void main(String[] args) {
        String myProxy = "127.0.0.1:7890";
        String host = myProxy.split(":")[0];
        int port = Integer.parseInt(myProxy.split(":")[1]);
        System.out.println(host);
        System.out.println(port);
    }
}
