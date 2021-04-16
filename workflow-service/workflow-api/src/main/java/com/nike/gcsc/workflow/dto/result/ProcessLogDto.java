 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nike.gcsc.workflow.constant.ProcessEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/01/2019
 */
@Getter
@Setter
public class ProcessLogDto implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 6156518818196927452L;
    /**
     * start user
     */
    private String startUser;
    /**
     * start datetime
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;
    /**
     * if process is end,this can have value
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;
    /**
     * process bpmn id
     * {@link ProcessEnum}
     */
    private String processDefinitionKey;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
    /**
     * create process instance id
     */
    private String processInstanceId;
    /**
     * bussiness key
     */
    private String bussinessKey;
    
    private List<ProcessLogNodeDto> nodes;
    
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessLogNodeDto implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -508798946412000103L;
        private String taskId;
        private String nodeName;
        private String nodeType;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        private Date startTime;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        private Date endTime;
        private String assignee;
        private String opinion;
        private Boolean approved;
    }
}
