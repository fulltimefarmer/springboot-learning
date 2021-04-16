 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CurrentTaskResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7537153450113041014L;
    
    /**
     * task id
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
     * this task candidate users
     */
    private Set<String> users;
    /**
     * this task candidate groups
     */
    private Set<String> groups;
    
    /**
     * start date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;
    
    /**
     * end date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;
}
