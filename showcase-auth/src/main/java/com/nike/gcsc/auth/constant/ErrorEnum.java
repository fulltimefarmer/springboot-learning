package com.nike.gcsc.auth.constant;

import com.nike.gcsc.common.BaseConstant;

public enum ErrorEnum implements BaseConstant<Integer> {

    /**
     * ---------- Gloable common enum start ---------------
     */
    SUCCESS(2000, "Success"),
    AUTH_INVALIDATED(4010, "Auth Invalidated."),
    NOT_FOUND(4040, "Not Found."),
    PARAM_INVALID(4221, "Paramter Is Not Valid."),
    BAD_REQUEST(4320, "Bad Request."),
    NOT_CHANGE(4400, "Data Not Change."),
    ALREADY_EXIST(4500, "Already Exist."),
    SERVER_EXCEPTION(5000, "Server Exception."),
    API_UNAVAILABLE(5031, "Api Unavailable."),



    FAILED(9999, "Failed.")
    /**
     *---------- Gloable common enum end ---------------
     */

    ;

    private Integer code;
    private String desc;

    ErrorEnum(Integer code, String desc) {
        this.code = code;
        this.setDesc(desc);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
