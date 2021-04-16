 package com.nike.gcsc.workflow.dto;

import java.io.Serializable;

import com.nike.gcsc.workflow.constant.TenantEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 6/28/2019
 */
@Getter
@Setter
public class DeploymentResultDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2484706642615883672L;
    
    /**
     * activiti process table id
     * eg:20001
     */
    private String deploymentId;
    /**
     * {@link TenantEnum}
     */
    private String tenantId;
    /**
     * bpmn id
     * eg:soldToClosure
     */
    private String processDefinitionId;
    /**
     * bpmn name
     * eg:soldToClosure
     */
    private String processDefinitionName;
    /**
     * bpmn file name
     */
    private String resourceName;
    /**
     * bpmn file path
     */
    private String resourcePath;
    /**
     * bpmn png name
     */
    private String diagramResourceName;
    /**
     * bpmn png path
     */
    private String diagramResourcePath;
    /**
     * deploy version
     */
    private Integer version;
}
