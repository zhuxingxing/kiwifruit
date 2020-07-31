package com.star.aspect;

import com.alibaba.fastjson.JSON;
import com.star.access.LogMemberAccess;
import com.star.annotation.RecordLog;
import com.star.pojo.dto.LogContent;
import com.star.service.IRecordLog;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * unified processing of operation log interception
 *
 * @author star [137879802@qq.com]
 */
@Aspect
@Component
@Slf4j
public class RecordLogAspect {

    @Autowired
    IRecordLog iRecordLog;

    /**
     * pointcut
     */
    @Pointcut("@annotation(com.star.annotation.RecordLog)")
    public void aspect() {
    }

    /***
     * interception
     *
     * @param joinPoint join point
     * @param response response data
     */
    @AfterReturning(value = "aspect()", returning = "response")
    public void record(JoinPoint joinPoint, Object response) {
        recordLog(joinPoint, response, "成功");
    }


    /***
     * interception error
     *
     * @param joinPoint join point
     */
    @AfterThrowing(value = "aspect()")
    public void record(JoinPoint joinPoint) {
        recordLog(joinPoint, null, "失败");
    }

    /**
     * record log
     *
     * @param joinPoint join point
     * @param response response data
     * @param success success
     */
    private void recordLog(JoinPoint joinPoint, Object response, String success) {
        try {
            if (null == joinPoint) {
                return;
            }
            String responseContent = this.handleResponse(response);
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            if (null == signature) {
                return;
            }
            Method method = signature.getMethod();
            RecordLog annotation = method.getAnnotation(RecordLog.class);
            if (null == annotation) {
                return;
            }
            String content = annotation.content();
            Map<String, Object> map = handleRequest(method.getParameters(), joinPoint.getArgs());
            OgnlContext context = new OgnlContext(null, null, new LogMemberAccess(true));
            String requestContent = null;
            if (!CollectionUtils.isEmpty(map)) {
                requestContent = JSON.toJSONString(map);
                content = String.valueOf(Ognl.getValue(annotation.content(), context, map));
            }
            String businessId = null;
            if (annotation.isAdd()) {
                businessId = StringUtils.hasText(responseContent) ? responseContent : null;
            } else {
                if (!CollectionUtils.isEmpty(map) && StringUtils.hasText(annotation.pk())) {
                    Object biz = Ognl.getValue(annotation.pk(), context, map);
                    businessId = null == biz ? null : biz.toString();
                }
            }
            LogContent logContent = LogContent.builder()
                    .businessId(businessId)
                    .code(annotation.code())
                    .desc(annotation.desc())
                    .operation(annotation.operation())
                    .content(content)
                    .request(requestContent)
                    .response(responseContent)
                    .stat(success)
                    .build();
            iRecordLog.saveLog(logContent);
        } catch (Exception e) {
            log.error("记录日志失败:", e);
        }
    }

    /**
     * processing request content
     *
     * @param parameters parameters
     * @return Map<String, Object> map
     */
    private Map<String, Object> handleRequest(Parameter[] parameters, Object[] args) {
        Map<String, Object> map = null;
        try {
            if (null != parameters && null != args) {
                int length = parameters.length;
                int argsLength = args.length;
                map = new HashMap<>(length);
                for (int i = 0; i < length; i++) {
                    Parameter parameter = parameters[i];
                    if (i < argsLength) {
                        Object arg = args[i];
                        if (arg instanceof MultipartFile) {
                            arg = null;
                        }
                        String key = parameter.getName();
                        map.put(key, arg);
                    }
                }
            }
        } catch (Exception e) {
            log.error("handling returned data exception：{}", e.getMessage());
        }
        return map;
    }

    /**
     * processing response content
     *
     * @param response response data
     * @return String content
     */
    private String handleResponse(Object response) {
        String content = null;
        try {
            if (null != response) {
                content = JSON.toJSONString(response);
            }
        } catch (Exception e) {
            log.error("handling returned data exception：{}", e.getMessage());
            content = String.valueOf(response);
        }
        return content;
    }

}
