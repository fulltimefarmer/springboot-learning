 package com.nike.gcsc.workflow.constant;

import lombok.AllArgsConstructor;

/**
 * 
 * @author roger yang
 * @date 6/13/2019
 */
@AllArgsConstructor
public enum ImgTypeEnum implements BaseConstant<String> {
    PNG("png","png"),
    JPG("jpg", "jpg"),
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
