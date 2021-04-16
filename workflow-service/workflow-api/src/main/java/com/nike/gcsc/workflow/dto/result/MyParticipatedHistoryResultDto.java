 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyParticipatedHistoryResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8249047294562569245L;
    /**
     * bussiness key
     */
    private String bussinessKey;
    /**
     * create process instance id
     */
    private String processInstanceId;
    /**
     * approved is true, reject is false, myParticipatedHistory have value, else is null
     */
    private Boolean approved;
    /**
     * process bpmn id
     */
    private String processDefinitionKey;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
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
