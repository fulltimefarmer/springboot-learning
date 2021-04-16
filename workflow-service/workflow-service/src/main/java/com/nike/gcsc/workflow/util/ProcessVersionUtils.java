 package com.nike.gcsc.workflow.util;

import org.apache.commons.lang3.StringUtils;

/**
 * get workflow bpmn version
 * @author roger yang
 * @date 2/28/2020
 */
public class ProcessVersionUtils {
    private static final Integer ONE = Integer.valueOf(1);
    
    public static Integer getBpmnVersion(String processDefinitionId) {
        if(StringUtils.isBlank(processDefinitionId)) {
            return ONE;
        }
        String[] arr = processDefinitionId.split(":");
        return Integer.valueOf(arr[1]);
    }
}
