package com.nike.gcsc.auth.controller.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: tom fang
 * @date: 2019/8/20 14:05
 **/
@Data
public class PageBaseRequest<T> implements Serializable {

	private static final long serialVersionUID = 8618643989559542543L;
	
	private int pageNum = 0;
    private int pageSize = 10;
    private T options;
    
}
