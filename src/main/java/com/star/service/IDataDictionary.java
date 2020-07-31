package com.star.service;

import com.star.annotation.Dictionary;
import com.star.pojo.dto.DataDictionary;

import java.util.List;

/**
 * data dictionary interface
 *
 * @author star [137879802@qq.com]
 */
public interface IDataDictionary {

    /**
     * query dictionary
     *
     * @param dictionary Dictionary annotation
     * @param name field name
     * @return List data dictionary list
     */
    List<DataDictionary> queryDictionary(Dictionary dictionary, String name);

}
