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
public class TaskQueryDto implements Serializable {
    
    /**
     *
     */
    private static final long serialVersionUID = 8394440090845075848L;
    /**
     * {@link TenantEnum}
     */
    @NotBlank
    private String tenantId;
    /**
     * authentication userName
     * Most of the time you do not need to pass a value to this parameter,please use userGroup parameter
     */
    private String userName;
    /**
     * authentication user group
     */
    @NotBlank
    private String userGroup;
    /**
     * process definition id
     * {@link ProcessEnum}
     */
    @NotBlank
    private String processDefinitionKey;
}
