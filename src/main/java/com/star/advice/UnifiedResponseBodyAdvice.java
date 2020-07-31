package com.star.advice;

import com.alibaba.fastjson.JSON;
import com.star.constants.StarConstants;
import com.star.dictionary.DictionaryTransformation;
import com.star.pojo.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * unified response result processing
 *
 * @author star [137879802@qq.com]
 */
@Slf4j
@RestControllerAdvice
public class UnifiedResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Value(value = "${kiwifruit.unified.response.enable:true}")
    private String CLOSE;

    @Autowired
    DictionaryTransformation dictionary;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return StarConstants.FALSE.equals(CLOSE) ? false : true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ResponseVo response = new ResponseVo();
        response.setCode(200);
        if (null == body) {
            return response;
        } else if (body instanceof ResponseVo) {
            dictionary.transformation(response, methodParameter);
            return body;
        } else if (body instanceof String) {
            response.setData(body);
            return JSON.toJSONString(response);
        } else {
            response.setData(body);
        }
        dictionary.transformation(response, methodParameter);
        return response;
    }

}
