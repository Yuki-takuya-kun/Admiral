package io.github.admiral.spokesman;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Spokesman {

    String name() default "";

    @AliasFor(annotation = Component.class)
    String value() default "";

    /** Soldiers' name that should push to http client. If its length is zero, then all content will be push to
     * frontend.*/
    String[] needShows() default {};

}
