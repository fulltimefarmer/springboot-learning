 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 6/27/2019
 */
@AllArgsConstructor
public enum TenantEnum implements BaseConstant<String> {
    CIG("cig","cig"),
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
