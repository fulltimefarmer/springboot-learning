package com.nike.gcsc.authapi.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

	private static final long serialVersionUID = 4802980417758652514L;

	private Long id;
    private String email;
    private String username;
    private String displayName;
    private Integer type;
    private String remark;
    private Integer status;
}
