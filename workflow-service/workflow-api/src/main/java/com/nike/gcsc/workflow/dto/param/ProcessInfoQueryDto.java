 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.nike.gcsc.workflow.constant.ProcessEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 2/28/2020
 */
@Getter
@Setter
public class ProcessInfoQueryDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 355386555185106800L;
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
    /**
     * business key
     */
    @NotBlank
    private String bussinessKey;
}
