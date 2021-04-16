 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.nike.gcsc.workflow.constant.ProcessEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/31/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListLogDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3371339639593564166L;
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
    
    @NotBlank
    private String processBussinessKey;
}
