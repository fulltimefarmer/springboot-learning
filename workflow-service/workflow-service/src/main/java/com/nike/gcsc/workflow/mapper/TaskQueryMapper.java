 package com.nike.gcsc.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nike.gcsc.workflow.dto.CurrentTaskDbResultDto;
import com.nike.gcsc.workflow.dto.param.CurrentTaskQueryDto;

/**
 * @author roger yang
 * @date 9/12/2019
 */
public interface TaskQueryMapper {
    
    List<CurrentTaskDbResultDto> queryCurrentProcessTasksWithProcessInstance(@Param("dto") CurrentTaskQueryDto dto);
    
    List<CurrentTaskDbResultDto> queryCurrentProcessTasksWithBussinessKey(@Param("dto") CurrentTaskQueryDto dto);
}
