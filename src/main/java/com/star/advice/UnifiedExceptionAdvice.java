package com.star.advice;

import com.star.exception.BusinessException;
import com.star.pojo.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * unified exception handling
 *
 * @author star [137879802@qq.com]
 */
@RestControllerAdvice
@Slf4j
public class UnifiedExceptionAdvice {

    @Value(value = "${kiwifruit.error.message:服务器开小差了}")
    private String ERR_MESSAGE;

    /**
     * all exception handling
     *
     * @param e abnormal information
     * @return ResponseEntity ResponseEntity ResponseVo
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVo> handleBindException(Exception e) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        ResponseVo response = new ResponseVo();
        HttpStatus httpStatus = HttpStatus.OK;
        log.error("Exception", e);
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(businessException.getMessage());
        } else {
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(ERR_MESSAGE.trim());
        }
        ResponseEntity<ResponseVo> responseEntity = new ResponseEntity<ResponseVo>(response, headers, httpStatus);
        return responseEntity;
    }

}
