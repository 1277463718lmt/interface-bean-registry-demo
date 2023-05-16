package com.linmt.openfeign;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class FeignClientFactoryBean implements FactoryBean<Object> {
    private Class<?> type;

    public FeignClientFactoryBean(Class<?> type) {
        this.type = type;
    }

    @Override
    public Object getObject() {
        return this.getTarget();
    }

    /**
     * 通过动态代理获取代理对象（重要）
     * @param <T>
     * @return
     */
    <T> T getTarget() {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new FeignProxy());
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
