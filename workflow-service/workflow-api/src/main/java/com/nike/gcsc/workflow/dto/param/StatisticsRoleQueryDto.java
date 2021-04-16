 package com.nike.gcsc.workflow.dto.param;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.nike.gcsc.workflow.constant.ProcessEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author roger yang
 * @date 8/27/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsRoleQueryDto {
    /**
     * user group
     */
    @NotEmpty
    private Set<String> userGroups;
    
    /**
     * {@link TenantEnum}
     */
    @NotBlank
    private String tenantId;
    
    /**
     * process definition id, if null,query all
     * {@link ProcessEnum}
     */
    private String processDefinitionKey;
}
