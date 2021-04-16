 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;
import java.util.List;

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
 * @date 7/01/2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryQueryDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8249047294562569245L;
    /**
     * {@link TenantEnum}
     */
    @NotBlank
    private String tenantId;
    /**
     * user
     */
    @NotBlank
    private String user;
    /**
     * process bpmn id
     * {@link ProcessEnum}
     */
    @NotBlank
    private String processDefinitionKey;
    /**
     * Bussiness key
     */
    private List<String> processBussinessKeys;
    /**
     * process instance ids
     */
    private List<String> processInstanceIds;
    /**
     * process is finish flag
     * if null,query all
     */
    private Boolean finished;
    
    /**
     * if true: sort by processInstanceId asc, else desc
     * default: false/ desc
     */
    private boolean sortByProcessInstanceIdAsc;
}
