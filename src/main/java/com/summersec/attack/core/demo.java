package com.summersec.attack.core;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @ClassName: demo
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/7/12 16:03
 * @Version: v1.0.0
 * @Description:
 **/
public class demo {
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
    public static void main(String[] args) throws Exception {
        System.out.println(demo.getRandomString(10));
    }
}
