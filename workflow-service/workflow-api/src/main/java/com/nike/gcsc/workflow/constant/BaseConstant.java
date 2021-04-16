package com.nike.gcsc.workflow.constant;

/**
 * base constant, all custom enumerations must extend this class 
 * @author roger yang
 * @date 1/13/2019
 */
public interface BaseConstant<T> {
    T getCode();
    String getDesc();

}