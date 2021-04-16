package com.nike.gcsc.authapi.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccessDto implements Serializable {
	
	private static final long serialVersionUID = -3541438754931127974L;

	private boolean isAccess;
	private String username;
	
}
