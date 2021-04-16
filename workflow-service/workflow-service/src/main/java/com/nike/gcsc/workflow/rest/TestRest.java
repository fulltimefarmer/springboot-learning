 package com.nike.gcsc.workflow.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nike.gcsc.workflow.api.WorkFlowService;
import com.nike.gcsc.workflow.common.WorkFlowResponse;
import com.nike.gcsc.workflow.dto.param.CurrentTaskQueryDto;
import com.nike.gcsc.workflow.dto.result.CurrentTaskResultDto;

import io.swagger.annotations.ApiOperation;

/**
 * @author roger yang
 * @date 1/10/2020
 */
@RestController
@RequestMapping("/test")
public class TestRest {
    @Resource
    private WorkFlowService workFlowService;
    
    @ApiOperation(value = "health check", notes = "health check")
    @GetMapping(value = "/public/health_check")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("ALIVE", HttpStatus.OK);
    }
    
    @ApiOperation(value = "test queryCurrentProcessTasks method")
    @PostMapping(value = "/api/query-current-process-tasks")
    public WorkFlowResponse<List<CurrentTaskResultDto>> queryCurrentProcessTasks(@RequestBody @Valid CurrentTaskQueryDto dto) {
        return workFlowService.queryCurrentProcessTasks(dto);
    }
}
