 package com.nike.gcsc.auth.exception;

 /**
 * @author roger yang
 * @date 11/28/2019
 */
public class HttpBadRequestException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6484799022961362609L;

    /**
     * 
     */
    public HttpBadRequestException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public HttpBadRequestException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public HttpBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public HttpBadRequestException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public HttpBadRequestException(Throwable cause) {
        super(cause);
    }
    
    
}
