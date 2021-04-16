 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * 
 * @author roger yang
 * @date 6/13/2019
 */
@AllArgsConstructor
public enum ErrorEnum implements BaseConstant<String> {
    GLOBAL_SUCCESS("200","success"),
    GLOBAL_BAD_REQUEST("400", "Bad Request"),
    GLOBAL_NOT_FOUND("404", "Not Found"),
    GLOBAL_SYSTEM_EXCEPTION("500", "system error"),
    
    TASK_IS_AUDITED("1001","taskId:{0} is audited,please check"),
    PROCESS_NOT_FOUND("1002","process instance not found"),
    PROCESS_IS_ENDED("1003","process instance is ended"),
    NODE_IS_AUDITED("1004","processInstanceId:{0}, node:{1} is audited,please check"),
    ;
    
    private String code;
    private String desc;
    
    /**
     * @return the code
     */
    @Override
    public String getCode() {
        return code;
    }
    /**
     * @return the desc
     */
    @Override
    public String getDesc() {
        return desc;
    }
}
