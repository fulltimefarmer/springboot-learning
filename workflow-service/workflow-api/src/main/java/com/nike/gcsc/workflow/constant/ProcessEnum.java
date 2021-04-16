 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 6/27/2019
 */
@AllArgsConstructor
public enum ProcessEnum implements BaseConstant<String> {
    /**
     * process id(processDefinitionKey),process name(processDefinitionName)
     */
    SOLD_TO_CLOSURE("soldToClosureId","soldToClosure"),
    SHIP_TO_CLOSURE("shipToClosureId","shipToClosure"),
    BILL_TO_CLOSURE("billToClosureId","billToClosure"),
    
    SOLD_TO_CREATION("soldToCreationId","soldToCreation"),
    SHIP_TO_CREATION("shipToCreationId","shipToCreation"),
    
    SOLD_TO_CHANGE("soldToChangeId","soldToChange"),
    SHIP_TO_CHANGE("shipToChangeId","shipToChange"),
    BILL_TO_CHANGE("billToChangeId","billToChange"),
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
