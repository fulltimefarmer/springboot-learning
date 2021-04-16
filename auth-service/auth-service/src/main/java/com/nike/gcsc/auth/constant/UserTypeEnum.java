package com.nike.gcsc.auth.constant;

/**
 * @author: tom fang
 * @date: 2019/8/19 13:23
 **/
public enum UserTypeEnum {

    /**
     * 1 nike user
     */
    NIKE_USER(1, "nike user"),

    /**
     * 2 external user
     */
    EXTERNAL_USER(2, "external user");

    private Integer code;
    private String desc;

    UserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
