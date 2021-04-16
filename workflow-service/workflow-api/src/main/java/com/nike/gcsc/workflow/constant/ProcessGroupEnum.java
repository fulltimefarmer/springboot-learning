 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 7/18/2019
 */
@AllArgsConstructor
public enum ProcessGroupEnum implements BaseConstant<String> {
    MOD_POUSHENG("POUSHENG MOD","POUSHENG MOD"),
    MOD_TOPSPORTS("TOPSPORTS MOD","TOPSPORTS MOD"),
    MOD_OTHERS("OTHERS MOD","OTHERS MOD")
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
