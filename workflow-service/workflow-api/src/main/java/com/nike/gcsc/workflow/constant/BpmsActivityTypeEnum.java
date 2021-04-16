package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BpmsActivityTypeEnum implements BaseConstant<String>  {
    
    START_EVENT("startEvent", "StartEvent"),
    END_EVENT("endEvent", "EndEvent"),
    USER_TASK("userTask", "UserTask"),
    RECEIVE_TASK("receiveTask", "ReceiveTask"),
    EXCLUSIVE_GATEWAY("exclusiveGateway", "ExclusiveGateway"),
    PARALLEL_GATEWAY("parallelGateway", "ParallelGateway"),
    INCLUSIVE_GATEWAY("inclusiveGateway", "InclusiveGateway");
    
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