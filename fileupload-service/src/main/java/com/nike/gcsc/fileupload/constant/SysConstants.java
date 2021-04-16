 package com.nike.gcsc.fileupload.constant;

import com.nike.gcsc.common.BaseConstant;

import lombok.AllArgsConstructor;

/**
 * @author roger yang
 * @date 10/31/2019
 */
@AllArgsConstructor
public enum SysConstants implements BaseConstant<String> {
    CIG("cig","CIG SYSTEM")
    ;
    private String code;
    private String desc;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
