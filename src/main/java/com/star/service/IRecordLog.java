package com.star.service;

import com.star.pojo.dto.LogContent;

/**
 * logging interface
 *
 * @author star [137879802@qq.com]
 */
public interface IRecordLog {

    /**
     * save log
     *
     * @param recordLog record log
     */
    void saveLog(LogContent recordLog);

}
