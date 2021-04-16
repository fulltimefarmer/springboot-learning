 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 6/27/2019
 */
@Getter
@Setter
public class TaskDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1022882969816347176L;
    /**
     * task id
     */
    private String taskId;
    /**
     * TaskDefinitionKey
     */
    private String taskKey;
    /**
     * TaskDefinitionName
     */
    private String taskName;
    /**
     * run to this task’s date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date taskCreateDate;
    
    /**
     * workflow process Instance id
     */
    private String processInstanceId;
    /**
     * bussiness key
     */
    private String processBussinessKey;
    /**
     * start user
     */
    private String processStartUser;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
    /**
     * process start date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date processStartDate;
}
