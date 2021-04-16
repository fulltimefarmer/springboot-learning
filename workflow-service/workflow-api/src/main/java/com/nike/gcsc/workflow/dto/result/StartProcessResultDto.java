 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 6/27/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartProcessResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4335437523561943736L;
    /**
     * create process instance id
     */
    private String processInstanceId;
    /**
     * business key
     */
    private String bussinessKey;
    /**
     * bpmn version, default value is: 1
     */
    private Integer processVersion;
    /**
     * if must send email,the email node key
     */
    private List<String> emailNodeKeys;
}
