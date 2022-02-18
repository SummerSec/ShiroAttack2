package com.summersec.attack.deser.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class StandardExecutorClassLoader extends URLClassLoader {

    private final static String baseDir = System.getProperty("user.dir") + File.separator + "lib" + File.separator;

    public StandardExecutorClassLoader(String version) {
        // 将 Parent 设置为 null
        super(new URL[] {}, null);

        loadResource(version);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 测试时可打印看一下
//        System.out.println("Class loader: " + name);
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch(ClassNotFoundException e) {
            return StandardExecutorClassLoader.class.getClassLoader().loadClass(name);
        }
    }

    private void loadResource(String version) {
        String jarPath = baseDir + version;

        // 加载对应版本目录下的 Jar 包
        tryLoadJarInDir(jarPath);
        // 加载对应版本目录下的 lib 目录下的 Jar 包
//        tryLoadJarInDir(jarPath + File.separator + "lib");
    }

    private void tryLoadJarInDir(String dirPath) {
        System.out.println("Try load jar in dir: " + dirPath);
        File dir = new File(dirPath);
        // 自动加载目录下的jar包
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    this.addURL(file);
                    continue;
                }
            }
        }
    }

    private void addURL(File file) {
        try {
            super.addURL(new URL("file", null, file.getCanonicalPath()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
