 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.nike.gcsc.workflow.constant.ProcessEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 12/24/2019
 */
@Getter
@Setter
public class CurrentTaskQueryDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8990221655808353715L;
    /**
     * {@link TenantEnum}
     */
    @NotBlank
    private String tenantId;
    
    /**
     * process bpmn id
     * {@link ProcessEnum}
     */
    @NotBlank
    private String processDefinitionKey;
    
    private List<String> bussinessKeys;
    
    private List<String> processInstanceIds;
}
