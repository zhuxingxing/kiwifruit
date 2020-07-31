package com.star.annotation;

import java.lang.annotation.*;

/**
 * ignore dictionary
 *
 * @author star [137879802@qq.com]
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictionaryIgnore {

}
