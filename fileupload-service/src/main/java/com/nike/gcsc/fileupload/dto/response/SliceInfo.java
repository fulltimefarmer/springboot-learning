package com.nike.gcsc.fileupload.dto.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author roger yang
 * @date 12/30/2019
 */
@Getter
@Setter
public class SliceInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3573658830807768959L;

    private Long fileStart;

    private Long fileEnd;
}
