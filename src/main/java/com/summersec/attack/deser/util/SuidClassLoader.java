package com.summersec.attack.deser.util;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SuidClassLoader extends ClassLoader{

    private Map<String, byte[]> classByteMap = new HashMap<>();
    private Map<String, Class> cacheClass = new HashMap<>();

    public void addClass(String className, byte[] classByte ){
        classByteMap.put(className,classByte);
    }

    public void addJar(byte[] jarByte) throws Exception{
        File tempFile = null;
        JarFile jarFile = null;
        tempFile = File.createTempFile("tempJarFile", "jar");
        FileUtils.writeByteArrayToFile(tempFile, jarByte);
        jarFile = new JarFile(tempFile);
        readJar(jarFile);
    }

    private void readJar(JarFile jar) throws IOException, IOException {
        Enumeration<JarEntry> en = jar.entries();
        while (en.hasMoreElements()){
            JarEntry je = en.nextElement();
            String name = je.getName();
            if (name.endsWith(".class")){
                String clss = name.replace(".class", "").replaceAll("/", ".");
                if(this.findLoadedClass(clss) != null) {
                    continue;
                }
                InputStream input = jar.getInputStream(je);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                byte[] cc = baos.toByteArray();
                input.close();
                classByteMap.put(clss, cc);
            }
        }
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class clazz = cacheClass.get(name);
            if (clazz != null) {
                return clazz;
            }
            try {
                clazz = findClass(name);
                if (null != clazz) {
                    cacheClass.put(name, clazz);
                }else{
                    clazz = super.loadClass(name, resolve);
                }
            } catch (ClassNotFoundException e) {
                clazz = super.loadClass(name, resolve);
            }

            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] result = classByteMap.get(name);
        if ( result == null){
            throw new ClassNotFoundException();
        } else {
            return super.defineClass(name,result,0,result.length);
        }
    }

    public void cleanLoader(){
        if (classByteMap != null){
            classByteMap.clear();
        }
        if (cacheClass != null){
            cacheClass.clear();
        }
    }
}
