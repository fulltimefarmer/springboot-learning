 package com.nike.gcsc.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nike.gcsc.workflow.dto.ParticipatedHistoryResultDto;
import com.nike.gcsc.workflow.dto.param.ParticipatedQueryDto;

/**
 * @author roger yang
 * @date 9/12/2019
 */
public interface ParticipatedHistoryMapper {
    
    long countParticipatedHistoryApprovedResult(@Param("dto") ParticipatedQueryDto dto);
    
    List<ParticipatedHistoryResultDto> queryParticipatedHistoryApprovedResult(@Param("dto") ParticipatedQueryDto dto, @Param("pageSize")Integer pageSize, @Param("startRow")Integer startRow);
}
