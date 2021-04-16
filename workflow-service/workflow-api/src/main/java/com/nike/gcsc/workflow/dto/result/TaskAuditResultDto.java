package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/05/2019
 */
@Getter
@Setter
public class TaskAuditResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6807109789327848761L;
    /**
     * audit task id
     */
    private String auditTaskId;
    /**
     * workflow process Instance id
     */
    private String processInstanceId;
    /**
     * bussiness key
     */
    private String processBussinessKey;
    /**
     * if must send email,the email node key
     */
    private List<String> emailNodeKeys;
    /**
     * the prcess instance is end
     */
    private boolean end;

}
