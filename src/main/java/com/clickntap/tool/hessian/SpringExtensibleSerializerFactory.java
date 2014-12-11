package com.clickntap.tool.hessian;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.SerializerFactory;

import java.util.List;

public class SpringExtensibleSerializerFactory extends SerializerFactory {

    public void setSerializerFactories(List<AbstractSerializerFactory> factories) {
        for (AbstractSerializerFactory factory : factories) {
            addFactory(factory);
        }
    }

}