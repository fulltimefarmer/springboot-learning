 package com.nike.gcsc.workflow.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.NativeHistoricProcessInstanceQuery;
import org.activiti.engine.history.ProcessInstanceHistoryLog;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.nike.gcsc.util.BeanMapper;
import com.nike.gcsc.workflow.api.WorkFlowService;
import com.nike.gcsc.workflow.common.WorkFlowResponse;
import com.nike.gcsc.workflow.constant.ActivityTypeConstants;
import com.nike.gcsc.workflow.constant.BpmsActivityTypeEnum;
import com.nike.gcsc.workflow.constant.ErrorEnum;
import com.nike.gcsc.workflow.constant.ImgTypeEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;
import com.nike.gcsc.workflow.dto.CurrentTaskDbResultDto;
import com.nike.gcsc.workflow.dto.ParticipatedHistoryResultDto;
import com.nike.gcsc.workflow.dto.param.CommentDto;
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
import com.nike.gcsc.workflow.dto.result.ProcessLogDto.ProcessLogNodeDto;
import com.nike.gcsc.workflow.dto.result.StartProcessResultDto;
import com.nike.gcsc.workflow.dto.result.TaskAuditResultDto;
import com.nike.gcsc.workflow.dto.result.TaskDto;
import com.nike.gcsc.workflow.exception.InvalidParameterlException;
import com.nike.gcsc.workflow.exception.TaskApprovedException;
import com.nike.gcsc.workflow.mapper.ParticipatedHistoryMapper;
import com.nike.gcsc.workflow.mapper.TaskQueryMapper;
import com.nike.gcsc.workflow.util.CommentUtils;
import com.nike.gcsc.workflow.util.ConstantUtils;
import com.nike.gcsc.workflow.util.MapUtils;
import com.nike.gcsc.workflow.util.ProcessVersionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author roger yang
 * @date 6/27/2019
 */
@RestController
@Slf4j
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ParticipatedHistoryMapper participatedHistoryMapper;
    @Autowired
    private TaskQueryMapper taskQueryMapper;
    
    /**
     * sql batch insert size
     */
    private static final int BATCH_SIZE = 2000;
    private static final String TENANTID_ILLEGAL = "TenantId is illegal";
    private static final String PROCESS_DEFINITION_KEY = "processDefinitionKey";
    private static final String TENANT_ID = "tenantId";
    private static final String AND_APPEND = " and (";
    
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public WorkFlowResponse<List<StartProcessResultDto>> batchStartProcess(@RequestBody @Valid List<ProcessStartDto> dtos) {
        log.info("begin batchStartProcess method, param:{}", JSONObject.toJSONString(dtos));
        if(CollectionUtils.isEmpty(dtos)) {
            throw new NullPointerException("input param can't be empty");
        }
        List<StartProcessResultDto> result = new ArrayList<>(dtos.size());
        for(ProcessStartDto dto : dtos) {
            result.add(startOneProcess(dto));
        }
        WorkFlowResponse<List<StartProcessResultDto>> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end batchStartProcess method, result:{}", JSONObject.toJSONString(result));
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public WorkFlowResponse<StartProcessResultDto> startProcess(@RequestBody @Valid ProcessStartDto dto) {
        log.info("begin startProcess method, param:{}", JSONObject.toJSONString(dto));
        StartProcessResultDto result = startOneProcess(dto);
        WorkFlowResponse<StartProcessResultDto> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end startProcess method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }

    @Override
    public WorkFlowResponse<List<TaskDto>> listTask(@RequestBody @Valid TaskQueryDto dto) {
        log.info("begin listTask method, param:{}", JSONObject.toJSONString(dto));
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        
        NativeTaskQuery query = createOneTypeTaskQuery(dto, false);
        List<Task> tasks = query.list();
        List<TaskDto> result = null;
        if(!CollectionUtils.isEmpty(tasks)) {
            List<ProcessInstance> instances = this.batchQueryProcess(tasks);
            result = tasks.stream().map(item -> assemblyTask(item, instances)).collect(Collectors.toList());
        }
        WorkFlowResponse<List<TaskDto>> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end listTask method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<Long> countTask(@RequestBody @Valid TaskQueryDto dto) {
        log.info("begin countTask method, param:{}", JSONObject.toJSONString(dto));
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        NativeTaskQuery query = createOneTypeTaskQuery(dto, true);
        WorkFlowResponse<Long> resp = WorkFlowResponse.buildSuccess(query.count());
        log.info("end countTask method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<PageResultDto<TaskDto>> pageListTask(@RequestBody @Valid PageQueryDto<TaskQueryDto> dto) {
        log.info("begin pageListTask method, param:{}", JSONObject.toJSONString(dto));
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getParamDto().getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        NativeTaskQuery query = createOneTypeTaskQuery(dto.getParamDto(), true);
        PageResultDto<TaskDto> result = new PageResultDto<>();
        long count = query.count();
        result.setCount(count);
        
        if(count > 0) {
            query = createOneTypeTaskQuery(dto.getParamDto(), false);
            List<Task> tasks = query.listPage(dto.getStartRow(), dto.getPageSize());
            if(!CollectionUtils.isEmpty(tasks)) {
                List<ProcessInstance> instances = this.batchQueryProcess(tasks);
                result.setData(tasks.stream().map(item -> assemblyTask(item, instances)).collect(Collectors.toList()));
            }
        }
        WorkFlowResponse<PageResultDto<TaskDto>> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end pageListTask method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public WorkFlowResponse<TaskAuditResultDto> audit(@RequestBody @Valid TaskAuditDto dto) {
        log.info("begin audit method, param:{}", JSONObject.toJSONString(dto));
        WorkFlowResponse<TaskAuditResultDto> resp = WorkFlowResponse.buildSuccess(null);
        auditOne(dto, resp);
        log.info("end audit method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public WorkFlowResponse<List<TaskAuditResultDto>> batchAudit(@RequestBody @Valid List<TaskAuditDto> dtos) {
        log.info("begin batchAudit method, param:{}", JSONObject.toJSONString(dtos));
        if(CollectionUtils.isEmpty(dtos)) {
            throw new NullPointerException("input param can't be empty");
        }
        WorkFlowResponse<List<TaskAuditResultDto>> resp = WorkFlowResponse.buildSuccess(new ArrayList<>());
        for(TaskAuditDto dto : dtos) {
            checkTask(dto, resp);
            if(!resp.getSuccess()) {
                break;
            }
        }
        if(resp.getSuccess()) {
            for(TaskAuditDto dto : dtos) {
                WorkFlowResponse<TaskAuditResultDto> one = WorkFlowResponse.buildSuccess(null);
                auditOne(dto, one);
                if(Boolean.FALSE.equals(one.getSuccess())) {
                    throw new TaskApprovedException(one.getErrorMsg());
                } else {
                    if(null != one.getData()) {
                        resp.getData().add(one.getData());
                    }
                }
            }
        }
        log.info("end batchAudit method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<ProcessLogDto> listLog(String processInstanceId) {
        log.info("begin listLog method, processInstanceId:{}", processInstanceId);
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(null == instance) {
            return WorkFlowResponse.buildFail(ErrorEnum.PROCESS_NOT_FOUND);
        }
        WorkFlowResponse<ProcessLogDto> resp = privateListLog(instance);
        log.info("end listLog method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<ProcessLogDto> listLog(@RequestBody @Valid ListLogDto dto) {
        log.info("begin listLog method, param:{}", JSONObject.toJSONString(dto));
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceTenantId(dto.getTenantId())
            .processInstanceBusinessKey(dto.getProcessBussinessKey())
            .processDefinitionKey(dto.getProcessDefinitionKey())
            .singleResult();
        if(null == instance) {
            return WorkFlowResponse.buildFail(ErrorEnum.PROCESS_NOT_FOUND);
        }
        WorkFlowResponse<ProcessLogDto> resp = privateListLog(instance);
        log.info("end listLog method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<PageResultDto<MyStartHistoryResultDto>> myStartHistory(@RequestBody @Valid PageQueryDto<HistoryQueryDto> dto) {
        log.info("begin myStartHistory method, param:{}", JSONObject.toJSONString(dto));
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getParamDto().getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        
        PageResultDto<MyStartHistoryResultDto> result = new PageResultDto<>();
        
        NativeHistoricProcessInstanceQuery nativeQuery = getMyStartNativeHistoricProcessInstanceQuery(dto, true);
        
        long count = nativeQuery.count();
        result.setCount(count);
        if(count > 0) {
            nativeQuery = getMyStartNativeHistoricProcessInstanceQuery(dto, false);
            List<HistoricProcessInstance> process = nativeQuery.listPage(dto.getStartRow(), dto.getPageSize());
            if(!CollectionUtils.isEmpty(process)) {
                processStartHistoryRow(process, result, dto.getParamDto().getProcessDefinitionKey());
            }
        }
        WorkFlowResponse<PageResultDto<MyStartHistoryResultDto>> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end myStartHistory method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<PageResultDto<MyParticipatedHistoryResultDto>> myParticipatedHistory(@RequestBody @Valid PageQueryDto<ParticipatedQueryDto> dto) {
        log.info("begin myParticipatedHistory method, param:{}", JSONObject.toJSONString(dto));
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getParamDto().getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        PageResultDto<MyParticipatedHistoryResultDto> result = new PageResultDto<>();
        
        long count = participatedHistoryMapper.countParticipatedHistoryApprovedResult(dto.getParamDto());
        
        result.setCount(count);
        if(count > 0) {
            List<ParticipatedHistoryResultDto> process = participatedHistoryMapper.queryParticipatedHistoryApprovedResult(dto.getParamDto(),dto.getPageSize(),dto.getStartRow());
            if(!CollectionUtils.isEmpty(process)) {
                processParticipatedHistoryRow(process, result, dto.getParamDto().getProcessDefinitionKey());
            }
        }
        WorkFlowResponse<PageResultDto<MyParticipatedHistoryResultDto>> resp = WorkFlowResponse.buildSuccess(result);
        log.info("end myParticipatedHistory method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<String> showDiagram(String processInstanceId, String imgType) {
        log.info("begin showDiagram method, processInstanceId:{}, imgType:{}", processInstanceId, imgType);
        imgType = processImgType(imgType);
        if(StringUtils.isBlank(imgType)) {
            return WorkFlowResponse.buildFail(ErrorEnum.GLOBAL_BAD_REQUEST);
        }
        
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(null == processInstance) {
            return WorkFlowResponse.buildFail(ErrorEnum.PROCESS_IS_ENDED);
        }
        BpmnModel model = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> actives = runtimeService.getActiveActivityIds(processInstance.getId());
        InputStream is = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator().generateDiagram(model, imgType, actives);
        
        WorkFlowResponse<String> resp = WorkFlowResponse.buildSuccess(getBase64Img(is, imgType));
        log.info("end showDiagram method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    /**
     * reference: https://www.cnblogs.com/leemup/p/activiti6_trace.html
     */
    @Override
    public WorkFlowResponse<String> showDiagramHighlightAll(String processInstanceId, String imgType) {
        log.info("begin showDiagramHighlightAll method, processInstanceId:{},imgType:{}", processInstanceId, imgType);
        
        imgType = processImgType(imgType);
        if(StringUtils.isBlank(imgType)) {
            return WorkFlowResponse.buildFail(ErrorEnum.GLOBAL_BAD_REQUEST);
        }
        
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(null == processInstance) {
            return WorkFlowResponse.buildFail(ErrorEnum.PROCESS_IS_ENDED);
        }
        
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
        List<String> executedActivityIdList = new ArrayList<String>();
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            executedActivityIdList.add(activityInstance.getActivityId());
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> flowIds = this.getExecutedFlows(bpmnModel, historicActivityInstanceList);
        
        InputStream is = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator().generateDiagram(bpmnModel, imgType, executedActivityIdList, flowIds);
        
        WorkFlowResponse<String> resp = WorkFlowResponse.buildSuccess(getBase64Img(is, imgType));
        log.info("end showDiagramHighlightAll method, result:{}", JSONObject.toJSONString(resp));
        return resp;
    }
    
    @Override
    public WorkFlowResponse<Long> statisticsTaskForRoles(@RequestBody @Valid StatisticsRoleQueryDto dto) {
        log.info("begin statisticsTaskForRoles method, param is:{}", JSONObject.toJSONString(dto));
        if(StringUtils.isNotBlank(dto.getProcessDefinitionKey()) && null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        NativeTaskQuery query = taskService.createNativeTaskQuery()
            .sql(buildStatisticsRoleSql(dto))
            .parameter(PROCESS_DEFINITION_KEY, dto.getProcessDefinitionKey())
            .parameter(TENANT_ID, dto.getTenantId());
        long count = query.count();
        log.info("end statisticsTaskForRoles method, result is:{}", count);
        return WorkFlowResponse.buildSuccess(count);
    }

    @Override
    public WorkFlowResponse<Long> statisticsTaskForUsers(@RequestBody @Valid StatisticsUsersQueryDto dto) {
        log.info("begin statisticsTaskForUsers method, param is:{}", JSONObject.toJSONString(dto));
        if(StringUtils.isNotBlank(dto.getProcessDefinitionKey()) && null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        NativeTaskQuery query = taskService.createNativeTaskQuery()
            .sql(buildStatisticsUsersSql(dto))
            .parameter(PROCESS_DEFINITION_KEY, dto.getProcessDefinitionKey())
            .parameter(TENANT_ID, dto.getTenantId());
        long count = query.count();
        log.info("end statisticsTaskForUsers method, result is:{}", count);
        return WorkFlowResponse.buildSuccess(count);
    }
    
    @Override
    public WorkFlowResponse<List<CurrentTaskResultDto>> queryCurrentProcessTasks(@RequestBody @Valid CurrentTaskQueryDto dto) {
        if(CollectionUtils.isEmpty(dto.getBussinessKeys()) && CollectionUtils.isEmpty(dto.getProcessInstanceIds())) {
            return WorkFlowResponse.buildFail(ErrorEnum.GLOBAL_BAD_REQUEST);
        }
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        
        List<CurrentTaskDbResultDto> dbResult = null;
        if(CollectionUtils.isEmpty(dto.getBussinessKeys())) {
            dbResult = taskQueryMapper.queryCurrentProcessTasksWithProcessInstance(dto);
        } else {
            dbResult = taskQueryMapper.queryCurrentProcessTasksWithBussinessKey(dto);
        }
        if(!CollectionUtils.isEmpty(dbResult)) {
            Map<String, List<CurrentTaskDbResultDto>> map = dbResult.stream().parallel().collect(Collectors.groupingBy(v -> v.getTaskId()));
            
            List<CurrentTaskResultDto> result = map.entrySet().stream().parallel().map(item -> {
                List<CurrentTaskDbResultDto> list = item.getValue();
                CurrentTaskResultDto rt = BeanMapper.map(list.get(0), CurrentTaskResultDto.class);
                rt.setUsers(list.stream().map(v -> v.getUser()).collect(Collectors.toSet()));
                rt.setGroups(list.stream().map(v -> v.getGroup()).collect(Collectors.toSet()));
               return rt; 
            }).collect(Collectors.toList());
            
            return WorkFlowResponse.buildSuccess(result);
        }
        return WorkFlowResponse.buildSuccess(Collections.emptyList());
    }
    
    @Override
    public WorkFlowResponse<ProcessInfoResultDto> queryProcessInfoByBussinessKey(@Valid ProcessInfoQueryDto dto) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceTenantId(dto.getTenantId())
            .processInstanceBusinessKey(dto.getBussinessKey())
            .processDefinitionKey(dto.getProcessDefinitionKey())
            .singleResult();
        if(null == instance) {
            return WorkFlowResponse.buildFail(ErrorEnum.PROCESS_NOT_FOUND);
        }
        ProcessInfoResultDto result = ProcessInfoResultDto.builder()
            .processInstanceId(instance.getId())
            .processVersion(ProcessVersionUtils.getBpmnVersion(instance.getProcessDefinitionId()))
            .build();
        return WorkFlowResponse.buildSuccess(result);
    }
    
    private String processImgType(String imgType) {
        if(null == imgType) {
            imgType = ImgTypeEnum.PNG.getCode();
        } else {
            imgType = ConstantUtils.getEnumValue(ImgTypeEnum.class, imgType);
        }
        return imgType;
    }
    
    private List<String> getExecutedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> flowIdList = new ArrayList<>();
        List<FlowNode> historicFlowNodeList = new LinkedList<>();
        List<HistoricActivityInstance> finishedActivityInstanceList = new LinkedList<HistoricActivityInstance>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            historicFlowNodeList.add((FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true));
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstanceList.add(historicActivityInstance);
            }
        }
        
        FlowNode currentFlowNode = null;
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstanceList) {
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlowList = currentFlowNode.getOutgoingFlows();
            
            FlowNode targetFlowNode = null;
            if (BpmsActivityTypeEnum.PARALLEL_GATEWAY.getCode().equals(currentActivityInstance.getActivityType())
                    || BpmsActivityTypeEnum.INCLUSIVE_GATEWAY.getCode().equals(currentActivityInstance.getActivityType())) {
                for (SequenceFlow sequenceFlow : sequenceFlowList) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicFlowNodeList.contains(targetFlowNode)) {
                        flowIdList.add(sequenceFlow.getId());
                    }
                }
            } else {
                processNotGateway(sequenceFlowList, historicActivityInstances, flowIdList);
            }
        }
        return flowIdList;
    }
    
    private void processNotGateway(List<SequenceFlow> sequenceFlowList, List<HistoricActivityInstance> historicActivityInstances, List<String> flowIdList) {
        List<Map<String, String>> tempMapList = new LinkedList<>();
        for (SequenceFlow sequenceFlow : sequenceFlowList) {
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                    tempMapList.add(MapUtils.toMap("flowId", sequenceFlow.getId(), "activityStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime())));
                }
            }
        }
        
        long earliestStamp = 0L;
        String flowId = null;
        for (Map<String, String> map : tempMapList) {
            long activityStartTime = Long.parseLong(map.get("activityStartTime"));
            if (earliestStamp == 0 || earliestStamp >= activityStartTime) {
                earliestStamp = activityStartTime;
                flowId = map.get("flowId");
            }
        }
        flowIdList.add(flowId);
    }
    
    private StartProcessResultDto startOneProcess(ProcessStartDto dto) {
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, dto.getTenantId())) {
            throw new InvalidParameterlException(TENANTID_ILLEGAL);
        }
        
        identityService.setAuthenticatedUserId(dto.getStartUser());
        
        Map<String, Object> variables = new HashMap<>();
        if(StringUtils.isNotBlank(dto.getAssigneeGroup())) {
            variables.put("group", dto.getAssigneeGroup());
        }
        if(StringUtils.isNotBlank(dto.getAssigneeUser())) {
            variables.put("user", dto.getAssigneeUser());
        }
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(dto.getProcessDefinitionKey(), dto.getBussinessKey(), variables, dto.getTenantId());
        StartProcessResultDto result = StartProcessResultDto.builder()
                .processInstanceId(processInstance.getProcessInstanceId())
                .bussinessKey(dto.getBussinessKey())
                .processVersion(ProcessVersionUtils.getBpmnVersion(processInstance.getProcessDefinitionId()))
                .build();
        
        List<Execution> list = runtimeService.createExecutionQuery().executionTenantId(dto.getTenantId()).processInstanceId(processInstance.getProcessInstanceId()).list();
        List<String> activityIds = getEmailNodeAndTrigger(list);
        if(!CollectionUtils.isEmpty(activityIds)) {
            result.setEmailNodeKeys(activityIds);
        }
        
        return result;
    }
    
    private void auditOne(TaskAuditDto dto, WorkFlowResponse<TaskAuditResultDto> resp) {
        Task task = checkTask(dto, resp);
        if(null == task) {
            return;
        }
        if(null == dto.getOpinion()) {
            dto.setOpinion(StringUtils.EMPTY);
        }
        String assignee = task.getAssignee();
        if(StringUtils.isBlank(assignee)) {
            // claim this task
            taskService.claim(task.getId(), dto.getAuditor());
        }
        Map<String,Object> variables = new HashMap<>(2);
        variables.put(task.getTaskDefinitionKey()+"_approved", dto.getApproved());
        variables.put("opinion", dto.getOpinion());
        
        if(!CollectionUtils.isEmpty(dto.getNextNodeAsigneeUserList())) {
            variables.put("assigneeList", dto.getNextNodeAsigneeUserList());
        }
        if(!CollectionUtils.isEmpty(dto.getControlParameters())) {
            variables.putAll(dto.getControlParameters());
        }
        
        String allApprovedKey = task.getTaskDefinitionKey() + "_all_approved";
        Boolean allApproved = runtimeService.getVariable(task.getExecutionId(), allApprovedKey, Boolean.class);
        if(null == allApproved) {
            runtimeService.setVariable(task.getExecutionId(), allApprovedKey, dto.getApproved());
        } else {
            if(Boolean.TRUE.equals(allApproved) && Boolean.FALSE.equals(dto.getApproved())) {
                runtimeService.setVariable(task.getExecutionId(), allApprovedKey, dto.getApproved());
            }
        }
        
        // add comment
        taskService.addComment(task.getId(), task.getProcessInstanceId(), CommentUtils.generateComment(dto.getApproved(), dto.getOpinion()));
        // end this task
        taskService.complete(task.getId(), variables);
        
        TaskAuditResultDto rt = new TaskAuditResultDto();
        rt.setAuditTaskId(task.getId());
        rt.setProcessInstanceId(task.getProcessInstanceId());
        
        // list execution
        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
        if(CollectionUtils.isEmpty(list)) {
            rt.setEnd(Boolean.TRUE);
            ProcessInstanceHistoryLog history = historyService.createProcessInstanceHistoryLogQuery(task.getProcessInstanceId()).singleResult();
            rt.setProcessBussinessKey(history.getBusinessKey());
            log.info("processInstanceId:{} is end", task.getProcessInstanceId());
        } else {
            processNext(rt, list, task, dto);
        }
        resp.setData(rt);
    }
    
    private void processNext(TaskAuditResultDto rt, List<Execution> list, Task task, TaskAuditDto dto) {
        Optional<ExecutionEntityImpl> bk = list.stream().map(item -> (ExecutionEntityImpl)item).filter(item -> StringUtils.isNotBlank(item.getBusinessKey())).findFirst();
        if(bk.isPresent()) {
            rt.setProcessBussinessKey(bk.get().getBusinessKey());
        }
        
        // if next node is send email,trigger this node
        List<String> activityIds = getEmailNodeAndTrigger(list);
        if(!CollectionUtils.isEmpty(activityIds)) {
            rt.setEmailNodeKeys(activityIds);
        }
        
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        if(CollectionUtils.isEmpty(tasks)) {
            rt.setEnd(Boolean.TRUE);
        }
        
        if(!CollectionUtils.isEmpty(dto.getNextNodeAsigneeUserList()) && !CollectionUtils.isEmpty(tasks) && tasks.size()==dto.getNextNodeAsigneeUserList().size()) {
            for(int i=0; i<tasks.size(); i++) {
                taskService.addCandidateUser(tasks.get(i).getId(), dto.getNextNodeAsigneeUserList().get(i));
            }
        }
    }
    
    private List<String> getEmailNodeAndTrigger(List<Execution> list) {
        if(!CollectionUtils.isEmpty(list)) {
            List<String> result = new ArrayList<>();
            Execution execution = null;
            String activityId = null;
            for(int i=0; i<list.size(); i++) {
                execution = list.get(i);
                activityId = execution.getActivityId();
                if(!StringUtils.isEmpty(activityId) && activityId.startsWith("sendEmail")) {
                    runtimeService.trigger(execution.getId());
                    result.add(activityId);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
    
    private Task checkTask(TaskAuditDto dto, WorkFlowResponse<? extends Object> resp) {
        String taskId = dto.getTaskId();
        Task task = null;
        if(StringUtils.isBlank(taskId)) {
            if(StringUtils.isAnyBlank(dto.getNodeKey(), dto.getProcessInstanceId())) {
                resp.setError(ErrorEnum.GLOBAL_BAD_REQUEST);
                return null;
            } 
            task = taskService.createTaskQuery().processInstanceId(dto.getProcessInstanceId()).taskDefinitionKey(dto.getNodeKey()).singleResult();
        } else {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
        }
        if(null == task) {
            if(StringUtils.isBlank(taskId)) {
                resp.setError(ErrorEnum.NODE_IS_AUDITED, dto.getProcessInstanceId(), dto.getNodeKey());
            } else {
                resp.setError(ErrorEnum.TASK_IS_AUDITED, taskId);
            }
            return null;
        }
        return task;
    }
    
    private String getBase64Img(InputStream is, String imgType) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            int b;
            while ((b = is.read()) != -1) {
                bos.write(b);
            }
            byte[] bytes = bos.toByteArray();
            return "data:image/".concat(imgType).concat(";base64,").concat(new String(Base64Utils.encode(bytes)));
        } catch (IOException e) {
            log.error("get bpmn image base64 error", e);
            return null;
        }
    }
    
    private NativeTaskQuery createOneTypeTaskQuery(TaskQueryDto dto, boolean count) {
        if(StringUtils.isAllBlank(dto.getUserGroup(), dto.getUserName())) {
            throw new InvalidParameterlException("One of the userName and userGroup entries must have a value");
        }
        NativeTaskQuery query = taskService.createNativeTaskQuery()
            .sql(buildListTaskSql(dto, count))
            .parameter(PROCESS_DEFINITION_KEY, dto.getProcessDefinitionKey())
            .parameter("userGroup", dto.getUserGroup())
            .parameter("userName", dto.getUserName())
            .parameter(TENANT_ID, dto.getTenantId());
        return query;
    }
    
    private TaskDto assemblyTask(Task item, List<ProcessInstance> instances) {
        TaskDto task = new TaskDto();
        task.setTaskId(item.getId());
        task.setTaskName(item.getName());
        task.setTaskCreateDate(item.getCreateTime());
        task.setTaskKey(item.getTaskDefinitionKey());
        
        ProcessInstance instance = getProcessInstanceFromList(instances, item.getProcessInstanceId());
        task.setProcessInstanceId(item.getProcessInstanceId());
        task.setProcessStartUser(instance.getStartUserId());
        task.setProcessBussinessKey(instance.getBusinessKey());
        task.setProcessStartDate(instance.getStartTime());
        task.setProcessVersion(instance.getProcessDefinitionVersion());
       return task; 
    }
    
    private String buildStatisticsRoleSql(StatisticsRoleQueryDto dto) {
        StringBuilder sql = buildBaseTaskQueryCountSql();
        if(StringUtils.isNotBlank(dto.getProcessDefinitionKey())) {
            sql.append(" and D.KEY_ = #{processDefinitionKey} ");
        }
        sql.append(" and D.TENANT_ID_=#{tenantId}");
        sql.append(" and ( ");
        dto.getUserGroups().stream().forEach(item -> {
            sql.append("I.GROUP_ID_='").append(item).append("' or ");
        });
        sql.delete(sql.length()-3, sql.length());
        sql.append(")");
        return sql.toString();
    }
    
    private String buildStatisticsUsersSql(StatisticsUsersQueryDto dto) {
        StringBuilder sql = buildBaseTaskQueryCountSql();
        if(StringUtils.isNotBlank(dto.getProcessDefinitionKey())) {
            sql.append(" and D.KEY_ = #{processDefinitionKey} ");
        }
        sql.append(" and D.TENANT_ID_=#{tenantId}");
        sql.append(" and ( ");
        dto.getUsers().stream().forEach(item -> {
            sql.append("I.USER_ID_='").append(item).append("' or ");
        });
        sql.delete(sql.length()-3, sql.length());
        sql.append(")");
        return sql.toString();
    }
    
    private String buildListTaskSql(TaskQueryDto dto, boolean count) {
        StringBuilder sql = null;
        if(count) {
            sql = buildBaseTaskQueryCountSql();
        } else {
            sql = buildBaseTaskQueryDataSql();
        }
        sql.append(" and D.KEY_ = #{processDefinitionKey} ");
        sql.append(" and D.TENANT_ID_=#{tenantId}");
        sql.append(AND_APPEND);
        appendUserOrGroupSql(dto.getUserName(), dto.getUserGroup(), sql);
        sql.append(") ");
        if(!count) {
            sql.append(" order by RES.CREATE_TIME_");
        } 
        return sql.toString();
    }
    
    private StringBuilder buildBaseTaskQueryCountSql() {
        StringBuilder sql = new StringBuilder("select count(*) FROM ");
        buildBaseTaskQuerySql(sql);
        return sql;
    }
    
    private StringBuilder buildBaseTaskQueryDataSql() {
        StringBuilder sql = new StringBuilder("select distinct RES.* FROM ");
        buildBaseTaskQuerySql(sql);
        return sql;
    }
    
    private void buildBaseTaskQuerySql(StringBuilder sql) {
        sql.append(managementService.getTableName(Task.class))
           .append(" RES ")
           .append(" inner join ACT_RU_IDENTITYLINK I on I.TASK_ID_ = RES.ID_ ")
           .append(" inner join ACT_RE_PROCDEF D on RES.PROC_DEF_ID_ = D.ID_ ")
           .append(" where  RES.ASSIGNEE_ is null and (I.TYPE_ = 'candidate' or I.TYPE_ = 'participant') ");
    }
    
    private void appendUserOrGroupSql(String userName, String userGroup, StringBuilder sql) {
        if(StringUtils.isNotBlank(userGroup)) {
            sql.append("I.GROUP_ID_=#{userGroup}");
        }
        if(StringUtils.isNotBlank(userGroup) && StringUtils.isNotBlank(userName)) {
            sql.append(" or ");
        }
        if(StringUtils.isNotBlank(userName)) {
            sql.append(" I.USER_ID_=#{userName}");
        }
    }
    
    private List<ProcessInstance> batchQueryProcess(List<Task> tasks) {
        List<ProcessInstance> instances = new ArrayList<>(tasks.size());
        if(tasks.size() > BATCH_SIZE) {
            boolean flag = true;
            int start = 0;
            while(flag) {
                int end = start+BATCH_SIZE;
                if (end >= tasks.size()) {
                    flag = false;
                    end = tasks.size();
                }
                instances.addAll(runtimeService.createProcessInstanceQuery().processInstanceIds(getProcessInstanceIds(tasks.subList(start, end))).list());
                
                start = end;
            }
        } else {
            instances.addAll(runtimeService.createProcessInstanceQuery().processInstanceIds(getProcessInstanceIds(tasks)).list());
        }
        
        return instances;
    }
    
    private Set<String> getProcessInstanceIds(List<Task> tasks) {
        if(CollectionUtils.isEmpty(tasks)) {
            return Collections.emptySet();
        }
        return tasks.stream().map(item -> item.getProcessInstanceId()).collect(Collectors.toSet());
    }
    
    private ProcessInstance getProcessInstanceFromList(List<ProcessInstance> instances, String processInstanceId) {
        return instances.stream().filter(item -> item.getProcessInstanceId().equals(processInstanceId)).findFirst().orElse(null);
    }
    
    private String buildMyStartQueryCountSQL(HistoryQueryDto dto) {
        StringBuilder sql = new StringBuilder("select count(distinct RES.ID_) ");
        buildMyStartQueryBaseSQL(dto, sql);
        return sql.toString();
    }
    
    private String buildMyStartQueryListSQL(HistoryQueryDto dto) {
        StringBuilder sql = new StringBuilder("select distinct RES.* ");
        buildMyStartQueryBaseSQL(dto, sql);
        sql.append(" order by RES.PROC_INST_ID_+0 ");
        sql.append(dto.isSortByProcessInstanceIdAsc() ? "asc" : "desc");
        return sql.toString();
    }
    
    private void buildMyStartQueryBaseSQL(HistoryQueryDto dto, StringBuilder sql) {
        sql.append(" from ACT_HI_PROCINST RES left join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_ ");
        sql.append(" WHERE DEF.KEY_ = #{processDefinitionKey} and RES.START_USER_ID_ = #{user} ");
        if(Boolean.TRUE.equals(dto.getFinished())) {
            sql.append(" and RES.END_TIME_ is not NULL");
        } else if(Boolean.FALSE.equals(dto.getFinished())) {
            sql.append(" and RES.END_TIME_ is NULL");
        }
        sql.append(" and DEF.TENANT_ID_=#{tenantId}");
        if(!CollectionUtils.isEmpty(dto.getProcessBussinessKeys())) {
            sql.append(AND_APPEND);
            dto.getProcessBussinessKeys().stream().forEach(item -> sql.append(" RES.BUSINESS_KEY_='").append(item).append("' or"));
            sql.delete(sql.length()-2, sql.length());
            sql.append(")");
        }
        if(!CollectionUtils.isEmpty(dto.getProcessInstanceIds())) {
            sql.append(AND_APPEND);
            dto.getProcessBussinessKeys().stream().forEach(item -> sql.append(" RES.PROC_INST_ID_='").append(item).append("' or"));
            sql.delete(sql.length()-2, sql.length());
            sql.append(")");
        }
    }
    
    private NativeHistoricProcessInstanceQuery getMyStartNativeHistoricProcessInstanceQuery(PageQueryDto<HistoryQueryDto> dto, boolean count) {
        NativeHistoricProcessInstanceQuery nativeQuery = historyService.createNativeHistoricProcessInstanceQuery();
        if(count) {
            nativeQuery.sql(buildMyStartQueryCountSQL(dto.getParamDto()));
        } else {
            nativeQuery.sql(buildMyStartQueryListSQL(dto.getParamDto()));
        }
        nativeQuery.parameter(PROCESS_DEFINITION_KEY, dto.getParamDto().getProcessDefinitionKey());
        nativeQuery.parameter("user", dto.getParamDto().getUser());
        nativeQuery.parameter(TENANT_ID, dto.getParamDto().getTenantId());
        return nativeQuery;
    }
    
    private WorkFlowResponse<ProcessLogDto> privateListLog(HistoricProcessInstance instance) {
        ProcessLogDto result = new ProcessLogDto();
        result.setStartUser(instance.getStartUserId());
        result.setStartDate(instance.getStartTime());
        result.setBussinessKey(instance.getBusinessKey());
        result.setEndDate(instance.getEndTime());
        result.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        result.setProcessInstanceId(instance.getId());
        result.setProcessVersion(ProcessVersionUtils.getBpmnVersion(instance.getProcessDefinitionId()));
        
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(instance.getId()).list();
        
        List<ProcessLogNodeDto> nodes = list.stream().filter(item -> !item.getActivityType().endsWith("Gateway")).map(item -> {
            ProcessLogNodeDto node = ProcessLogNodeDto.builder()
                .startTime(item.getStartTime())
                .endTime(item.getEndTime())
                .nodeName(item.getActivityName())
                .nodeType(item.getActivityType())
                .assignee(item.getAssignee())
                .taskId(item.getTaskId())
                .build();
            if(ActivityTypeConstants.USER_TASK.equals(item.getActivityType()) && null != item.getEndTime()) {
                List<Comment> comments = taskService.getTaskComments(item.getTaskId());
                if(!CollectionUtils.isEmpty(comments)) {
                    String comment = comments.get(0).getFullMessage();
                    CommentDto commentDto = CommentUtils.getCommentObject(comment);
                    node.setApproved(commentDto.getApproved());
                    node.setOpinion(commentDto.getOpinion());
                }
            }
            return node;
        }).sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime())).collect(Collectors.toList());
        result.setNodes(nodes);
        return WorkFlowResponse.buildSuccess(result);
    }
    
    private void processStartHistoryRow(List<HistoricProcessInstance> process, PageResultDto<MyStartHistoryResultDto> result, String processDefinitionKey) {
        List<MyStartHistoryResultDto> list = process.stream().map(item -> {
            return buildStartHistoryDto(item, processDefinitionKey);
        }).collect(Collectors.toList());
        result.setData(list);
    }
    
    private void processParticipatedHistoryRow(List<ParticipatedHistoryResultDto> process, PageResultDto<MyParticipatedHistoryResultDto> result, String processDefinitionKey) {
        List<MyParticipatedHistoryResultDto> list = process.stream().map(item -> {
            return buildParticipatedHistoryDto(item, processDefinitionKey);
        }).collect(Collectors.toList());
        result.setData(list);
    }
    
    private MyStartHistoryResultDto buildStartHistoryDto(HistoricProcessInstance item, String processDefinitionKey) {
        return MyStartHistoryResultDto.builder()
            .bussinessKey(item.getBusinessKey())
            .processInstanceId(item.getId())
            .startDate(item.getStartTime())
            .processDefinitionKey(processDefinitionKey)
            .processVersion(ProcessVersionUtils.getBpmnVersion(item.getProcessDefinitionId()))
            .endDate(item.getEndTime())
            .build();
    } 
    
    private MyParticipatedHistoryResultDto buildParticipatedHistoryDto(ParticipatedHistoryResultDto item, String processDefinitionKey) {
        MyParticipatedHistoryResultDto dto = MyParticipatedHistoryResultDto.builder()
            .bussinessKey(item.getBussinessKey())
            .processInstanceId(item.getProcessInstanceId())
            .startDate(item.getStartDate())
            .processDefinitionKey(processDefinitionKey)
            .processVersion(item.getProcessVersion())
            .endDate(item.getEndDate())
            .build();
        if(StringUtils.isNotBlank(item.getApproved())) {
            dto.setApproved(item.getApproved().startsWith("true"));
        }
        return dto;
    }
}
