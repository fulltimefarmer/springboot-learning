 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import com.nike.gcsc.workflow.constant.ProcessNodeContants;

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
public class TaskAuditDto extends CommentDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3440772860199180838L;
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!
     * taskId and processInstanceId/nodeKey must have a value
     */
    /**
     * task id
     */
    private String taskId;
    
    /**
     * create process instance id
     */
    private String processInstanceId;
    /**
     * {@link ProcessNodeContants.code}
     */
    private String nodeKey;
    /**
     * auditor
     */
    @NotBlank
    private String auditor;
    /**
     * if next node must concurrent people audit,please set this value
     */
    private List<String> nextNodeAsigneeUserList;
    /**
     * process control parameters
     */
    private Map<String, Object> controlParameters;
}
