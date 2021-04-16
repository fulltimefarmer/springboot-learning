package com.nike.gcsc.authapi.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class GroupRequestBean implements Serializable {

	private static final long serialVersionUID = 800029124574677047L;
	
    private String projectName;
    private Long projectId;
    private String groupName;
    private String level;
    private String remark;
}
