package io.github.admiral.soldier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is to annotate the method that subscribe which event.
 *
 * @author Jiahao Hwang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Soldier {

    /** It indicates this method will subscribe what commands.*/
    String[] subscribes() default {};

    /** It indicates this method will name what command.*/
    String name() default "";
}
