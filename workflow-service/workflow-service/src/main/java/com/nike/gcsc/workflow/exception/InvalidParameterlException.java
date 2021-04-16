 package com.nike.gcsc.workflow.exception;

 /**
 * @author roger yang
 * @date 6/14/2019
 */
public class InvalidParameterlException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -7820700770747088661L;

    /**
     * 
     */
    public InvalidParameterlException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InvalidParameterlException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidParameterlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public InvalidParameterlException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidParameterlException(Throwable cause) {
        super(cause);
    }

}
