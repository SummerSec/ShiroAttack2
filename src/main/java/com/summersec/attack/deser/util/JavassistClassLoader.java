package com.summersec.attack.deser.util;

/**
 * @ClassName: JavassistClassLoader
 * @Description: TODO
 * @Author: Summer
 * @Date: 2022/1/24 16:34
 * @Version: v1.0.0
 * @Description:
 **/
public class JavassistClassLoader extends ClassLoader {
    public JavassistClassLoader(){
        super(Thread.currentThread().getContextClassLoader());
    }
}