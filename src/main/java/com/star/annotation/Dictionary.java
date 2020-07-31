package com.star.annotation;

import java.lang.annotation.*;

/**
 * dictionary annotation
 *
 * @author star [137879802@qq.com]
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dictionary {

    /**
     * no
     *
     * @return no
     */
    int no() default 0;

    /**
     * field name (property name is used by default)
     * @return field name
     */
    String fieldName() default "";

}
