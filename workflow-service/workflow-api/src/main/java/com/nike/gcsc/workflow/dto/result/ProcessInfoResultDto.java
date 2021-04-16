 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 2/28/2020
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfoResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5018401910113841002L;
    /**
     * workflow process Instance id
     */
    private String processInstanceId;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
}
