package io.github.admiral.soldier;

import io.github.admiral.spokesman.Spokesman;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


/**
 * This annotation is going to indicates a class is a barrack class, which should implement methods that subscribe
 * another Soldier or {@link Spokesman}.
 *
 * @author Jiahao hwang
 * @version 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Barrack {

    /** The name of the soldier, defaults to the class name.*/
    String name() default "";

}
