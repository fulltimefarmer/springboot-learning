package com.nike.gcsc.authapi.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class UserPermissionDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1958493753367215670L;
    
    private String username;
    private String displayName;
    private List<GroupDto> groupList;
    private List<PermissionDto> permissionList;

    private Long ext;

}
