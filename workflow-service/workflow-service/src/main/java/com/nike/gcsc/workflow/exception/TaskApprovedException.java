 package com.nike.gcsc.workflow.exception;

 /**
 * @author roger yang
 * @date 6/14/2019
 */
public class TaskApprovedException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -7820700770747088661L;

    /**
     * 
     */
    public TaskApprovedException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public TaskApprovedException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public TaskApprovedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TaskApprovedException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TaskApprovedException(Throwable cause) {
        super(cause);
    }

}
