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
 * @date 6/27/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStartDto implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 5240790797508751959L;
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
     * authentication userName
     */
    @NotBlank
    private String startUser;
    /**
     * business key
     */
    @NotBlank
    private String bussinessKey;
    /**
     * assignee to one group
     */
    private String assigneeGroup;
    /**
     * assignee to one user
     */
    private String assigneeUser;
}
