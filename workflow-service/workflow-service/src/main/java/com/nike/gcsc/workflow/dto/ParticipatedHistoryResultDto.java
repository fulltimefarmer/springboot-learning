 package com.nike.gcsc.workflow.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/01/2019
 */
@Getter
@Setter
public class ParticipatedHistoryResultDto implements Serializable {
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
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
    /**
     * comment
     */
    private String approved;
    /**
     * start date
     */
    private Date startDate;
    /**
     * end date
     */
    private Date endDate;
}
