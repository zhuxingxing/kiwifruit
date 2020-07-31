package com.star.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * log content
 *
 * @author star [137879802@qq.com]
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogContent {

    /**
     * business ID
     */
    public String businessId;

    /**
     * code
     */
    public String code;

    /**
     * description
     */
    public String desc;

    /**
     * operation
     */
    public String operation;

    /**
     * content
     */
    public String content;

    /**
     * request content
     */
    public String request;

    /**
     * response content
     */
    public String response;

    /**
     * stat
     */
    public String stat;

}
