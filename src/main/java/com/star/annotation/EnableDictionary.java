package com.star.annotation;

import com.star.config.EnableDictionaryConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * enable dictionary
 *
 * @author star [137879802@qq.com]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({EnableDictionaryConfig.class})
public @interface EnableDictionary {
}
