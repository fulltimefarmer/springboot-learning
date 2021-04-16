 package com.nike.gcsc.workflow.dto.result;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 7/01/2019
 */
@Getter
@Setter
public class PageResultDto<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1821958129091925404L;
    private long count;
    private List<T> data;
}
