package com.nike.gcsc.authapi.request;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AmountUserBean implements Serializable {

	private static final long serialVersionUID = -6698380246446114998L;
	@NotNull
	@Min(value = 1)
    private Long projectId;
	@NotNull
	@Min(value = 1)
    private Long groupId;
	@NotNull
	@Min(value = 1)
    private Long userId;

}
