 package com.nike.gcsc.workflow.dto;

import java.io.Serializable;
import java.util.Date;

import com.nike.gcsc.workflow.constant.ProcessNodeContants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 12/24/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentTaskDbResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7537153450113041014L;
    /**
     * taskId
     */
    private String taskId;

    /**
     * bussiness key
     */
    private String bussinessKey;
    
    /**
     * create process instance id
     */
    private String processInstanceId;
    /**
     * process bpmn id
     */
    private String processDefinitionKey;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
    /**
     * {@link ProcessNodeContants.code}
     */
    private String nodeKey;
    /**
     * {@link ProcessNodeContants.desc}
     */
    private String nodeName;
    /**
     * this task candidate user
     */
    private String user;
    /**
     * this task candidate group
     */
    private String group;
    
    /**
     * start date
     */
    private Date startDate;
    
    /**
     * end date
     */
    private Date endDate;
}
