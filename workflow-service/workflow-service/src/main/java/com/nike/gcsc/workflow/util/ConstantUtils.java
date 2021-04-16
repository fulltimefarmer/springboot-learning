package com.nike.gcsc.workflow.util;

import com.nike.gcsc.workflow.constant.BaseConstant;

/**
 * get enum desc by codes util class
 * @author roger yang
 * @date 6/13/2019
 */
public class ConstantUtils {
    
    @SuppressWarnings("rawtypes")
    public static <T extends Enum<T> & BaseConstant>  String getEnumValue(Class<T> clazz ,
            Object codeValue) {
        String result = null;
        T[] enums = clazz.getEnumConstants();
        for(T obj : enums){
            if(obj.getCode().equals(codeValue)){
                result = obj.getDesc();
                break;
            }
        }
        return result;
    }
}