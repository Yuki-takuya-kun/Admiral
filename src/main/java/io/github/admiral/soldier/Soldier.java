package io.github.admiral.soldier;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Soldier {

    String name() default "";
    String[] subscribe() default {};
}
