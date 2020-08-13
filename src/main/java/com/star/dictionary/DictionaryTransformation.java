package com.star.dictionary;

import com.star.annotation.Dictionary;
import com.star.annotation.DictionaryIgnore;
import com.star.constants.StarConstants;
import com.star.pojo.dto.DataDictionary;
import com.star.service.IDataDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

/**
 * dictionary processing
 *
 * @author star [137879802@qq.com]
 */
@Slf4j
@Component
public class DictionaryTransformation {

    @Value(value = "${kiwifruit.data.dictionary.enable:true}")
    private String CLOSE;

    @Autowired
    IDataDictionary iDataDictionary;

    /**
     * dictionary conversion
     *
     * @param data   object
     * @param method method object
     */
    public void transformation(Object data, Method method) {
        /**
         * process ignore
         */
        if (handlerMethodIgnore(method) || isClose()) {
            return;
        }
        if (null != data) {
            /**
             * convert dictionary data
             */
            if (data instanceof Collection) {
                if (data instanceof List) {
                    recursionData((List) data);
                }
                return;
            }
            handleDataDictionary(data);
        }
    }

    /**
     * determine whether to turn off the conversion
     *
     * @return
     */
    private boolean isClose() {
        return StarConstants.FALSE.equals(CLOSE) ? true : false;
    }

    /**
     * recursive loop
     *
     * @param list
     */
    private void recursionData(List list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (Object object : list) {
                if (object instanceof List) {
                    recursionData((List) object);
                } else {
                    handleDataDictionary(object);
                }
            }
        }
    }

    /**
     * determine whether dictionary conversion is ignored
     *
     * @param method
     * @return boolean
     */
    private boolean handlerMethodIgnore(Method method) {
        if (null != method) {
            Annotation annotation = method.getDeclaredAnnotation(DictionaryIgnore.class);
            if (null != annotation) {
                log.info("Method [{}] ignores the translation dictionary", method.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * conversion dictionary
     *
     * @param data
     */
    private void handleDataDictionary(Object data) {
        try {
            if (data != null) {
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    // 按照属性查询字典
                    if (null != field.getDeclaredAnnotation(DictionaryIgnore.class)) {
                        log.info("the property [{}] ignores the conversion dictionary", field.getName());
                        return;
                    }
                    /**
                     * list in loop object
                     */
                    String capitalizeName = captureName(field.getName());
                    String type = field.getGenericType().toString();
                    String getMethodName = StarConstants.GET.concat(capitalizeName);
                    if (type.startsWith(StarConstants.LIST)) {
                        if (isMethodExist(data.getClass(), getMethodName)) {
                            Method getValueMethod = data.getClass().getMethod(getMethodName);
                            recursionData((List) getValueMethod.invoke(data));
                        }
                    } else if (!StarConstants.STRING.equals(type) && type.startsWith("class")) {
                        if (isMethodExist(data.getClass(), getMethodName)) {
                            Method getValueMethod = data.getClass().getMethod(getMethodName);
                            handleDataDictionary(getValueMethod.invoke(data));
                        }
                    } else {
                        transformationDict(data, field, capitalizeName);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error("access exception", e);
        } catch (InvocationTargetException e) {
            log.error("execution exception", e);
        } catch (NoSuchMethodException e) {
            log.error("method not found", e);
        }
    }

    /**
     * conversion dictionary
     *
     * @param data
     * @param field
     * @param capitalizeName
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void transformationDict(Object data, Field field, String capitalizeName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = data.getClass();
        String getMethodName = StarConstants.GET.concat(capitalizeName);
        String setMethodName = StarConstants.SET.concat(capitalizeName);
        if (isMethodExist(clazz, getMethodName) && isMethodExist(clazz, setMethodName)) {
            Dictionary dictionary = field.getDeclaredAnnotation(Dictionary.class);
            if (null == dictionary) {
                return;
            }
            // dictionary collection
            List<DataDictionary> list = iDataDictionary.queryDictionary(field.getDeclaredAnnotation(Dictionary.class), field.getName());
            if (!CollectionUtils.isEmpty(list)) {
                Map<String, String> map = new HashMap<>(list.size());
                list.stream().forEach(e -> map.put(e.getDictCode(), e.getDictDesc()));
                // get property value
                Method getValueMethod = clazz.getMethod(getMethodName);
                String source = String.valueOf(getValueMethod.invoke(data));
                String target = hasText(source) ? map.get(source) : null;
                if (hasText(target)) {
                    // conversion dictionary
                    if (StarConstants.STRING.equals(field.getGenericType().toString())) {
                        Method setValueMethod = clazz.getMethod(setMethodName, String.class);
                        setValueMethod.invoke(data, target);
                    }
                }
            }
        }
    }

    /**
     * capture name
     *
     * @param name
     * @return String
     */
    private String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * whether the method exists or not
     *
     * @param clazz
     * @param methodName
     * @return boolean
     */
    private boolean isMethodExist(Class clazz, String methodName) {
        if (StringUtils.hasText(methodName)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (methodName.equals(method.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

}
