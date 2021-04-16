 package com.nike.gcsc.workflow.dto.param;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiParam;
import lombok.Setter;

import lombok.Getter;

/**
 * @author roger yang
 * @date 7/01/2019
 */
@Getter
@Setter
public class PageQueryDto<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1241991163762862713L;
    @NotNull
    @Min(value = 1)
    private Integer pageSize;
    @NotNull
    @Min(value = 1)
    private Integer pageNo;
    @NotNull
    @Valid
    private T paramDto;
    
    @ApiParam(hidden = true)
    public Integer getStartRow() {
        return (pageNo-1)*pageSize;
    }
}
