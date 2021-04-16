 package com.nike.gcsc.workflow.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nike.gcsc.workflow.common.WorkFlowResponse;
import com.nike.gcsc.workflow.constant.ImgTypeEnum;
import com.nike.gcsc.workflow.dto.param.CurrentTaskQueryDto;
import com.nike.gcsc.workflow.dto.param.HistoryQueryDto;
import com.nike.gcsc.workflow.dto.param.ListLogDto;
import com.nike.gcsc.workflow.dto.param.PageQueryDto;
import com.nike.gcsc.workflow.dto.param.ParticipatedQueryDto;
import com.nike.gcsc.workflow.dto.param.ProcessInfoQueryDto;
import com.nike.gcsc.workflow.dto.param.ProcessStartDto;
import com.nike.gcsc.workflow.dto.param.StatisticsRoleQueryDto;
import com.nike.gcsc.workflow.dto.param.StatisticsUsersQueryDto;
import com.nike.gcsc.workflow.dto.param.TaskAuditDto;
import com.nike.gcsc.workflow.dto.param.TaskQueryDto;
import com.nike.gcsc.workflow.dto.result.CurrentTaskResultDto;
import com.nike.gcsc.workflow.dto.result.MyParticipatedHistoryResultDto;
import com.nike.gcsc.workflow.dto.result.MyStartHistoryResultDto;
import com.nike.gcsc.workflow.dto.result.PageResultDto;
import com.nike.gcsc.workflow.dto.result.ProcessInfoResultDto;
import com.nike.gcsc.workflow.dto.result.ProcessLogDto;
import com.nike.gcsc.workflow.dto.result.StartProcessResultDto;
import com.nike.gcsc.workflow.dto.result.TaskAuditResultDto;
import com.nike.gcsc.workflow.dto.result.TaskDto;

/**
 * swagger doc for dev : http://10.73.5.252:10001/swagger-ui.html
 * @author roger yang
 * @date 6/27/2019
 */
@RequestMapping("/workflow/v1")
public interface WorkFlowService {
    
    /**
     * batch start process
     * @param dtos
     * @return
     */
    @PostMapping("/batch-start-process")
    WorkFlowResponse<List<StartProcessResultDto>> batchStartProcess(@RequestBody List<ProcessStartDto> dtos);
    
    /**
     * start process
     * @param dto
     * @return ProcessInstanceId
     */
    @PostMapping("/start-process")
    WorkFlowResponse<StartProcessResultDto> startProcess(@RequestBody ProcessStartDto dto);
    
    /**
     * query to be processed task
     * @param dto
     * @return
     */
    @PostMapping("/list-tasks")
    WorkFlowResponse<List<TaskDto>> listTask(@RequestBody TaskQueryDto dto);
    
    /**
     * count to be processed task
     * @param dto
     * @return
     */
    @PostMapping("/count-tasks")
    WorkFlowResponse<Long> countTask(@RequestBody TaskQueryDto dto);
    
    /**
     * page query to be processed task
     * @param dto
     * @return
     */
    @PostMapping("/page-tasks")
    WorkFlowResponse<PageResultDto<TaskDto>> pageListTask(@RequestBody PageQueryDto<TaskQueryDto> dto);
    
    /**
     * audit task
     * @param dto
     * @return
     */
    @PostMapping("/audit")
    WorkFlowResponse<TaskAuditResultDto> audit(@RequestBody TaskAuditDto dto);
    /**
     * batch audit task
     * @param dto
     * @return
     */
    @PostMapping("/batch-audit")
    WorkFlowResponse<List<TaskAuditResultDto>> batchAudit(@RequestBody List<TaskAuditDto> dtos);
    
    /**
     * list process logs
     * @param processInstanceId
     * @return
     */
    @GetMapping("/logs")
    WorkFlowResponse<ProcessLogDto> listLog(@RequestParam("processInstanceId") String processInstanceId);
    
    /**
     * list process logs
     * @param dto
     * @return
     */
    @PostMapping("/logs2")
    WorkFlowResponse<ProcessLogDto> listLog(@RequestBody ListLogDto dto);
    
    /**
     * query all my start process
     * @param dto
     * @return
     */
    @PostMapping("/my-start-history")
    WorkFlowResponse<PageResultDto<MyStartHistoryResultDto>> myStartHistory(@RequestBody PageQueryDto<HistoryQueryDto> dto);
    
    /**
     * query all my participated process
     * @param dto
     * @return
     */
    @PostMapping("/my-participated-history")
    WorkFlowResponse<PageResultDto<MyParticipatedHistoryResultDto>> myParticipatedHistory(@RequestBody PageQueryDto<ParticipatedQueryDto> dto);
    
    /**
     * show process diagram with base64 img, Highlight activity node
     * @param processInstanceId
     * @param imgType {@link ImgTypeEnum} if null, default png
     * @return
     */
    @GetMapping("/show-diagram")
    WorkFlowResponse<String> showDiagram(@RequestParam("processInstanceId") String processInstanceId, @RequestParam(value="imgType",required=false) String imgType);
    
    /**
     * show process diagram with base64 img, Highlight all end node
     * @param processInstanceId
     * @param imgType {@link ImgTypeEnum} if null, default png
     * @return
     */
    @GetMapping("/show-diagram-highlight-all")
    WorkFlowResponse<String> showDiagramHighlightAll(@RequestParam("processInstanceId") String processInstanceId, @RequestParam(value="imgType",required=false) String imgType);
    
    /**
     * list role's pending record, if processDefinitionKey have value ,filter with it
     * @param dto
     * @return
     */
    @PostMapping("/statistics-task-roles")
    WorkFlowResponse<Long> statisticsTaskForRoles(@RequestBody @Valid StatisticsRoleQueryDto dto);
    
    /**
     * list users's pending record, if processDefinitionKey have value ,filter with it
     * @param dto
     * @return
     */
    @PostMapping("/statistics-task-users")
    WorkFlowResponse<Long> statisticsTaskForUsers(@RequestBody @Valid StatisticsUsersQueryDto dto);
    
    /**
     * query current process's task
     * @param dto
     * @return
     */
    @PostMapping("/query-current-process-tasks")
    WorkFlowResponse<List<CurrentTaskResultDto>> queryCurrentProcessTasks(@RequestBody @Valid CurrentTaskQueryDto dto);
    
    /**
     * query process info by bussiness key
     * @param dto
     * @return
     */
    @PostMapping("/query-process-info")
    WorkFlowResponse<ProcessInfoResultDto> queryProcessInfoByBussinessKey(@RequestBody @Valid ProcessInfoQueryDto dto);
}
