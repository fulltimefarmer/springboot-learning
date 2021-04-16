package com.nike.gcsc.workflow.common;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Set;

import com.nike.gcsc.workflow.constant.BaseConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 6/13/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowResponse<T> implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8753813726294333866L;
    private Boolean success;
    private String errorCode;
    private String errorMsg;
    private T data;
    
    public void setError(String code, String msg, Object... formatArguments) {
        success = Boolean.FALSE;
        errorCode = code;
        if(formatArguments.length > 0) {
            errorMsg = MessageFormat.format(msg, formatArguments);
        } else {
            errorMsg = msg;
        }
    }
    
    public void setError(BaseConstant<String> responseCode, Object... formatArguments) {
        setError(responseCode.getCode(), responseCode.getDesc(), formatArguments);
    }
    
    public void setError(String code, String msg, Set<? extends Object> detail) {
        setError(code, msg);
    }
    
    public void setError(BaseConstant<String> responseCode, Set<? extends Object> detail) {
        setError(responseCode.getCode(), responseCode.getDesc());
    }

    public static <T> WorkFlowResponse<T> buildSuccess(T data) {
        return new WorkFlowResponse<T>(true, null, null, data);
    }
    
    public static <T> WorkFlowResponse<T> buildFail(BaseConstant<String> responseCode) {
        return new WorkFlowResponse<T>(false, responseCode.getCode(), responseCode.getDesc(), null);
    }

    public static <T> WorkFlowResponse<T> buildFail(BaseConstant<String> responseCode, String errorMsg) {
        return new WorkFlowResponse<T>(false, responseCode.getCode(), errorMsg, null);
    }
    
    public static <T> WorkFlowResponse<T> buildFail(String errorCode, String errorMsg) {
        return new WorkFlowResponse<T>(false, errorCode, errorMsg, null);
    }

    public static <T> WorkFlowResponse<T> buildAll(Boolean success, String code, String message, T data) {
        return new WorkFlowResponse<T>(success, code, message, data);
    }
    public static <T> WorkFlowResponse<T> buildFail(BaseConstant<String> responseCode,T data) {
        return new WorkFlowResponse<T>(false, responseCode.getCode(), null, data);
    }

}
