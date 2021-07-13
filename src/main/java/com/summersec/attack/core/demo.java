package com.summersec.attack.core;

import java.io.File;
import java.io.FileInputStream;

/**
 * @ClassName: demo
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/7/12 16:03
 * @Version: v1.0.0
 * @Description:
 **/
public class demo {
    public static void main(String[] args) throws Exception {
        String filename =  "src/main/resources/commons-beanutils-1.8.3.txt";
        FileInputStream fis = new FileInputStream(new File(filename));
        System.out.println(fis.read());
    }
}
