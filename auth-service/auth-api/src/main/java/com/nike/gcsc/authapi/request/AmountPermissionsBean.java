package com.nike.gcsc.authapi.request;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

@Data
public class AmountPermissionsBean implements Serializable {

	private static final long serialVersionUID = -6698770246445115698L;

    private Long groupId;
    private Set<Long> permissionIds;

}
