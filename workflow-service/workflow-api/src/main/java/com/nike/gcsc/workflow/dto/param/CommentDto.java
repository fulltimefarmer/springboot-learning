 package com.nike.gcsc.workflow.dto.param;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/02/2019
 */
@Getter
@Setter
public class CommentDto {
    /**
     * approved flag
     */
    @NotNull
    private Boolean approved;
    /**
     * approval opinion
     */
    private String opinion;
}
