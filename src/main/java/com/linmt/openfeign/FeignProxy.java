package com.linmt.openfeign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class FeignProxy implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(FeignProxy.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("类{}，执行了{}方法，参数={}", method.getDeclaringClass().getName(), method.getName(),args);
        return "张三";
    }
}
