package com.star.annotation;

import java.lang.annotation.*;

/**
 * record log
 *
 * @author star [137879802@qq.com]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RecordLog {

    /**
     * natural key
     *
     * @return pk
     */
    String pk() default "id";

    /**
     * content
     *
     * @return content
     */
    String content();

    /**
     * code
     *
     * @return code
     */
    String code() default "";

    /**
     * describe
     *
     * @return describe
     */
    String desc();

    /**
     * operation
     *
     * @return operation
     */
    String operation() default "";


    /**
     * add or not (configure when business ID needs to be recorded when adding)
     *
     * @return isAdd
     */
    boolean isAdd() default false;

}
