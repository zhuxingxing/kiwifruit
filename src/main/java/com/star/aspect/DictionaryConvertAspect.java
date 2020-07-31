package com.star.aspect;

import com.star.dictionary.DictionaryTransformation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * dictionary interception
 *
 * @author star [137879802@qq.com]
 */
@Aspect
@Component
@Slf4j
public class DictionaryConvertAspect {

    @Autowired
    DictionaryTransformation dictionary;

    /**
     * pointcut
     */
    @Pointcut("@annotation(com.star.annotation.DictionaryConvert)")
    public void aspect() {
    }

    /**
     * interception
     *
     * @param joinPoint breakthrough point
     * @param response  response
     */
    @AfterReturning(value = "aspect()", returning = "response")
    public void afterReturning(JoinPoint joinPoint, Object response) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        dictionary.transformation(response, method);
    }

}
