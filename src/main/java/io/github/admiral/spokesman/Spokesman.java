package io.github.admiral.spokesman;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Spokesman {

    @AliasFor(annotation = Component.class)
    String value() default "";

    String name() default "";

}
