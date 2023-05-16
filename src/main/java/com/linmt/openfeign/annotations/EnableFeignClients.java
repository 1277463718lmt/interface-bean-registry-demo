package com.linmt.openfeign.annotations;

import com.linmt.openfeign.FeignClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignClientsRegistrar.class)
public @interface EnableFeignClients {
    String[] basePackages() default {};
}
