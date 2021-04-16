 package com.nike.gcsc.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.NativeHistoricProcessInstanceQuery;
import org.activiti.engine.history.ProcessInstanceHistoryLogQuery;
import org.activiti.engine.impl.ProcessInstanceHistoryLogImpl;
import org.activiti.engine.impl.persistence.entity.CommentEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nike.gcsc.workflow.common.WorkFlowResponse;
import com.nike.gcsc.workflow.constant.ErrorEnum;
import com.nike.gcsc.workflow.constant.ImgTypeEnum;
import com.nike.gcsc.workflow.constant.ProcessEnum;
import com.nike.gcsc.workflow.constant.ProcessNodeContants;
import com.nike.gcsc.workflow.constant.TenantEnum;
import com.nike.gcsc.workflow.dto.ParticipatedHistoryResultDto;
import com.nike.gcsc.workflow.dto.param.HistoryQueryDto;
import com.nike.gcsc.workflow.dto.param.ListLogDto;
import com.nike.gcsc.workflow.dto.param.PageQueryDto;
import com.nike.gcsc.workflow.dto.param.ParticipatedQueryDto;
import com.nike.gcsc.workflow.dto.param.ProcessStartDto;
import com.nike.gcsc.workflow.dto.param.StatisticsRoleQueryDto;
import com.nike.gcsc.workflow.dto.param.StatisticsUsersQueryDto;
import com.nike.gcsc.workflow.dto.param.TaskAuditDto;
import com.nike.gcsc.workflow.dto.param.TaskQueryDto;
import com.nike.gcsc.workflow.dto.result.MyParticipatedHistoryResultDto;
import com.nike.gcsc.workflow.dto.result.MyStartHistoryResultDto;
import com.nike.gcsc.workflow.dto.result.PageResultDto;
import com.nike.gcsc.workflow.dto.result.ProcessLogDto;
import com.nike.gcsc.workflow.dto.result.StartProcessResultDto;
import com.nike.gcsc.workflow.dto.result.TaskAuditResultDto;
import com.nike.gcsc.workflow.dto.result.TaskDto;
import com.nike.gcsc.workflow.mapper.ParticipatedHistoryMapper;
import com.nike.gcsc.workflow.service.WorkFlowServiceImpl;
import com.nike.gcsc.workflow.util.CommentUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author roger yang
 * @date 7/02/2019
 */
@RunWith(PowerMockRunner.class)
@Slf4j
public class WorkFlowServiceTest {
    @InjectMocks
    WorkFlowServiceImpl workFlowServiceImpl;
    
    @Mock
    private ProcessEngine processEngine;
    @Mock
    private IdentityService identityService;
    @Mock
    private TaskService taskService;
    @Mock
    private RuntimeService runtimeService;
    @Mock
    private HistoryService historyService;
    @Mock
    private ManagementService managementService;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ParticipatedHistoryMapper participatedHistoryMapper;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testStartProcess() {
        log.info("run testStartProcess...");
        ProcessStartDto dto = ProcessStartDto.builder()
            .assigneeGroup("SD")
            .assigneeUser("xx@nike.com")
            .bussinessKey("XXXXX")
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .startUser("test@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        
        mockStartCommon();
        
        WorkFlowResponse<StartProcessResultDto> result = workFlowServiceImpl.startProcess(dto);
        assertTrue(result.getSuccess());
    }
    
    @Test
    public void testBatchStartProcess() {
        log.info("run testBatchStartProcess...");
        List<ProcessStartDto> dtos = new ArrayList<>(2);
        
        ProcessStartDto dto = ProcessStartDto.builder()
            .assigneeGroup("SD")
            .assigneeUser("xx@nike.com")
            .bussinessKey("XXXXX")
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .startUser("test@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        ProcessStartDto dto2 = ProcessStartDto.builder()
            .assigneeGroup("SD")
            .bussinessKey("yyyyy")
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .startUser("test@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        dtos.add(dto);
        dtos.add(dto2);
        
        mockStartCommon();
        
        
        WorkFlowResponse<List<StartProcessResultDto>> result = workFlowServiceImpl.batchStartProcess(dtos);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().size(), 2);
    }
    
    @Test
    public void testListTask() {
        log.info("run testListTask...");
        TaskQueryDto dto = TaskQueryDto.builder()
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .userGroup("SD")
            .userName("auditor@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        
        NativeTaskQuery query = mock(NativeTaskQuery.class);
        when(taskService.createNativeTaskQuery()).thenReturn(query);
        when(query.sql(anyString())).thenReturn(query);
        when(query.parameter(anyString(), any())).thenReturn(query);
        
        ProcessInstanceQuery instanceQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceId(any())).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceIds(any())).thenReturn(instanceQuery);
        ExecutionEntityImpl instance = new ExecutionEntityImpl();
        instance.setProcessInstanceId("123");
        when(instanceQuery.singleResult()).thenReturn(instance);
        when(instanceQuery.list()).thenReturn(Arrays.asList(instance));
        
        List<Task> tasks = new ArrayList<>();
        TaskEntityImpl task =new TaskEntityImpl();
        task.setProcessInstanceId("123");
        tasks.add(task);
        
        when(query.list()).thenReturn(tasks);
        WorkFlowResponse<List<TaskDto>> result = workFlowServiceImpl.listTask(dto);
        
        assertTrue(result.getSuccess());
        assertEquals(result.getData().size(), 1);
    }
    
    @Test
    public void testCountTask() {
        log.info("run testCountTask...");
        TaskQueryDto dto = TaskQueryDto.builder()
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .userGroup("SD")
            .userName("auditor@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        
        NativeTaskQuery query = mock(NativeTaskQuery.class);
        when(taskService.createNativeTaskQuery()).thenReturn(query);
        when(query.sql(anyString())).thenReturn(query);
        when(query.parameter(anyString(), any())).thenReturn(query);
        
        ProcessInstanceQuery instanceQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceId(any())).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceIds(any())).thenReturn(instanceQuery);
        
        ExecutionEntityImpl instance = new ExecutionEntityImpl();
        instance.setProcessInstanceId("123");
        
        when(instanceQuery.singleResult()).thenReturn(instance);
        when(instanceQuery.list()).thenReturn(Arrays.asList(instance));
        
        when(query.count()).thenReturn(1L);
        WorkFlowResponse<Long> result = workFlowServiceImpl.countTask(dto);
        
        assertTrue(result.getSuccess());
        assertEquals(result.getData().longValue(), 1L);
    }
    
    @Test
    public void testPageListTask() {
        log.info("run testPageListTask...");
        PageQueryDto<TaskQueryDto> dto = new PageQueryDto<>();
        dto.setPageNo(1);
        dto.setPageSize(10);
        TaskQueryDto paramDto = TaskQueryDto.builder()
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .userGroup("SD")
            .userName("auditor@nike.com")
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        dto.setParamDto(paramDto);
        
        NativeTaskQuery query = mock(NativeTaskQuery.class);
        when(taskService.createNativeTaskQuery()).thenReturn(query);
        when(query.sql(anyString())).thenReturn(query);
        when(query.parameter(anyString(), any())).thenReturn(query);
        
        ProcessInstanceQuery instanceQuery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceId(any())).thenReturn(instanceQuery);
        when(instanceQuery.processInstanceIds(any())).thenReturn(instanceQuery);
        
        ExecutionEntityImpl instance = new ExecutionEntityImpl();
        instance.setProcessInstanceId("123");
        
        when(instanceQuery.singleResult()).thenReturn(instance);
        when(instanceQuery.list()).thenReturn(Arrays.asList(instance));
        
        List<Task> tasks = new ArrayList<>();
        TaskEntityImpl task =new TaskEntityImpl();
        task.setProcessInstanceId("123");
        tasks.add(task);
        
        when(query.listPage(anyInt(), anyInt())).thenReturn(tasks);
        when(query.count()).thenReturn(1L);
        
        WorkFlowResponse<PageResultDto<TaskDto>> result = workFlowServiceImpl.pageListTask(dto);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getCount(), 1L);
        assertEquals(result.getData().getData().size(), 1);
        
        tasks = new ArrayList<>(3000);
        for(int i=0; i<3000; i++) {
            task =new TaskEntityImpl();
            task.setProcessInstanceId("123");
            tasks.add(task);
        }
        when(query.listPage(anyInt(), anyInt())).thenReturn(tasks);
        when(query.count()).thenReturn(3000L);
        
        result = workFlowServiceImpl.pageListTask(dto);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getCount(), 3000L);
        assertEquals(result.getData().getData().size(), 3000);
    }
    
    @Test
    public void testAudit() {
        log.info("run testAudit...");
        Task task = mock(TaskEntityImpl.class);
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(anyString())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        
        mockAuditCommon();
        
        TaskAuditDto dto = new TaskAuditDto();
        dto.setApproved(Boolean.TRUE);
        dto.setAuditor("auditor@nike.com");
        dto.setOpinion("this is opinion");
        dto.setTaskId("task_id");
        
        Map<String, Object> map = new HashMap<>();
        map.put("xx", "y");
        dto.setControlParameters(map);
        dto.setNextNodeAsigneeUserList(Arrays.asList("xxx"));
        
        WorkFlowResponse<TaskAuditResultDto> result = workFlowServiceImpl.audit(dto);
        assertTrue(result.getSuccess());
        
        ExecutionQuery query = mock(ExecutionQuery.class);
        when(runtimeService.createExecutionQuery()).thenReturn(query);
        when(query.processInstanceId(anyString())).thenReturn(query);
        
        dto.setApproved(Boolean.FALSE);
        when(runtimeService.getVariable(any(), any(), any())).thenReturn(Boolean.TRUE);
        PowerMockito.doNothing().when(runtimeService).setVariable(any(), any(), any());
        
        result = workFlowServiceImpl.audit(dto);
        assertTrue(result.getSuccess());
    }
    
    @Test
    public void testBatchAudit() {
        log.info("run testBatchAudit...");
        Task task = mock(TaskEntityImpl.class);
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(anyString())).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        
        mockAuditCommon();
        
        List<TaskAuditDto> dtos = new ArrayList<>(2);
        
        TaskAuditDto dto = new TaskAuditDto();
        dto.setApproved(Boolean.TRUE);
        dto.setAuditor("auditor@nike.com");
        dto.setOpinion("this is opinion");
        dto.setTaskId("task_id");
        
        Map<String, Object> map = new HashMap<>();
        map.put("xx", "y");
        dto.setControlParameters(map);
        dto.setNextNodeAsigneeUserList(Arrays.asList("xxx"));
        
        TaskAuditDto dto2 = new TaskAuditDto();
        dto2.setApproved(Boolean.TRUE);
        dto2.setAuditor("auditor@nike.com");
        dto2.setOpinion("this is opinion");
        dto2.setTaskId("task_id2");
        dto2.setControlParameters(map);
        dto2.setNextNodeAsigneeUserList(Arrays.asList("xxx","yyyy"));
        
        dtos.add(dto);
        dtos.add(dto2);
        
        WorkFlowResponse<List<TaskAuditResultDto>> result = workFlowServiceImpl.batchAudit(dtos);
        assertTrue(result.getSuccess());
        
        ExecutionQuery query = mock(ExecutionQuery.class);
        when(runtimeService.createExecutionQuery()).thenReturn(query);
        when(query.processInstanceId(anyString())).thenReturn(query);
        
        result = workFlowServiceImpl.batchAudit(dtos);
        assertTrue(result.getSuccess());
    }
    
    @Test
    public void testListLog() {
        log.info("run testListLog...");
        
        HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processInstanceId(anyString())).thenReturn(query);
        
        HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(query.singleResult()).thenReturn(instance);
       
        commonMockListLog();
        
        WorkFlowResponse<ProcessLogDto> result = workFlowServiceImpl.listLog("xxx");
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getNodes().size(), 3);
        assertNotNull(result.getData().getNodes().get(1).getOpinion());
    }
    
    @Test
    public void testListLog2() {
        log.info("run testListLog2...");
        
        HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processInstanceTenantId(anyString())).thenReturn(query);
        when(query.processInstanceBusinessKey(anyString())).thenReturn(query);
        when(query.processDefinitionKey(anyString())).thenReturn(query);
        
        HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(query.singleResult()).thenReturn(instance);
       
        commonMockListLog();
        
        ListLogDto dto = ListLogDto.builder()
            .tenantId(TenantEnum.CIG.getCode())
            .processBussinessKey("xx")
            .processDefinitionKey(ProcessEnum.BILL_TO_CHANGE.getCode())
            .build();
        
        WorkFlowResponse<ProcessLogDto> result = workFlowServiceImpl.listLog(dto);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getNodes().size(), 3);
        assertNotNull(result.getData().getNodes().get(1).getOpinion());
    }
    
    @Test
    public void testMyStartHistory() {
        log.info("run testMyStartHistory...");
        PageQueryDto<HistoryQueryDto> dto = new PageQueryDto<>();
        dto.setPageNo(1);
        dto.setPageSize(10);
        HistoryQueryDto paramDto = HistoryQueryDto.builder()
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .user("auditor@nike.com")
            .finished(Boolean.TRUE)
            .tenantId(TenantEnum.CIG.getCode())
            .processBussinessKeys(Arrays.asList("xxx"))
            .build();
        dto.setParamDto(paramDto);
        
        NativeHistoricProcessInstanceQuery nativeCountQuery = mock(NativeHistoricProcessInstanceQuery.class);
        when(historyService.createNativeHistoricProcessInstanceQuery()).thenReturn(nativeCountQuery);
        when(nativeCountQuery.sql(anyString())).thenReturn(nativeCountQuery);
        when(nativeCountQuery.parameter(anyString(), anyString())).thenReturn(nativeCountQuery);
        when(nativeCountQuery.count()).thenReturn(1L);
        
        
        List<HistoricProcessInstance> process = new ArrayList<>();
        HistoricProcessInstance instance = new HistoricProcessInstanceEntityImpl();
        process.add(instance);
        when(nativeCountQuery.listPage(anyInt(), anyInt())).thenReturn(process);
        
        WorkFlowResponse<PageResultDto<MyStartHistoryResultDto>> result = workFlowServiceImpl.myStartHistory(dto);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getCount(), 1L);
        assertEquals(result.getData().getData().size(), 1);
    }
    
    @Test
    public void testMyParticipatedHistory() {
        log.info("run testMyParticipatedHistory...");
        PageQueryDto<ParticipatedQueryDto> dto = new PageQueryDto<>();
        dto.setPageNo(1);
        dto.setPageSize(10);
        ParticipatedQueryDto paramDto = ParticipatedQueryDto.builder()
            .processDefinitionKey(ProcessEnum.SOLD_TO_CLOSURE.getCode())
            .user("auditor@nike.com")
            .approved(Boolean.TRUE)
            .taskName(ProcessNodeContants.SOLD_TO_CLOSURE.SD_AUDIT.getDesc())
            .processBussinessKeys(Arrays.asList("1","2"))
            .tenantId(TenantEnum.CIG.getCode())
            .build();
        dto.setParamDto(paramDto);
        
        when(participatedHistoryMapper.countParticipatedHistoryApprovedResult(any())).thenReturn(1L);
        
        
        List<ParticipatedHistoryResultDto> process = new ArrayList<>();
        ParticipatedHistoryResultDto instance = new ParticipatedHistoryResultDto();
        process.add(instance);
        when(participatedHistoryMapper.queryParticipatedHistoryApprovedResult(any(), any(), any())).thenReturn(process);
        
        WorkFlowResponse<PageResultDto<MyParticipatedHistoryResultDto>> result = workFlowServiceImpl.myParticipatedHistory(dto);
        assertTrue(result.getSuccess());
        assertEquals(result.getData().getCount(), 1L);
        assertEquals(result.getData().getData().size(), 1);
    }
    
    @Test
    public void testShowDiagram() throws IOException {
        ExecutionEntityImpl processInstance = mock(ExecutionEntityImpl.class);
        
        ProcessInstanceQuery pquery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(pquery);
        when(pquery.processInstanceId(anyString())).thenReturn(pquery);
        when(pquery.singleResult()).thenReturn(processInstance);
        
        BpmnModel model = new BpmnModel();
        when(repositoryService.getBpmnModel(anyString())).thenReturn(model);
        
        List<String> actives = new ArrayList<>();
        actives.add("startEvent");
        when(runtimeService.getActiveActivityIds(anyString())).thenReturn(actives);
        
        InputStream is = org.apache.commons.io.IOUtils.toInputStream("xxxxx", "UTF-8");
        
        ProcessEngineConfiguration config = mock(ProcessEngineConfiguration.class);
        ProcessDiagramGenerator generator = mock(DefaultProcessDiagramGenerator.class);
        when(processEngine.getProcessEngineConfiguration()).thenReturn(config);
        when(config.getProcessDiagramGenerator()).thenReturn(generator);
        when(generator.generateDiagram(anyObject(), anyString(), any())).thenReturn(is);
        
        WorkFlowResponse<String> result = workFlowServiceImpl.showDiagram("xxx", ImgTypeEnum.PNG.getCode());
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        
        result = workFlowServiceImpl.showDiagram("xxx", null);
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        
        result = workFlowServiceImpl.showDiagram("xxx", "fadfasdf");
        assertFalse(result.getSuccess());
        assertEquals(result.getErrorCode(), ErrorEnum.GLOBAL_BAD_REQUEST.getCode());
    }
    
    @Test
    public void testShowDiagramHighlightAll() throws IOException {
        ExecutionEntityImpl processInstance = mock(ExecutionEntityImpl.class);
        
        ProcessInstanceQuery pquery = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(pquery);
        when(pquery.processInstanceId(anyString())).thenReturn(pquery);
        when(pquery.singleResult()).thenReturn(processInstance);
        
        List<HistoricActivityInstance> historicActivityInstanceList = new ArrayList<>();
        HistoricActivityInstanceEntityImpl impl = new HistoricActivityInstanceEntityImpl();
        impl.setActivityId("startEvent");
        impl.setEndTime(new Date());
        impl.setActivityType("startEvent");
        HistoricActivityInstanceEntityImpl impl2 = new HistoricActivityInstanceEntityImpl();
        impl2.setActivityId("exclusiveGateway");
        impl2.setEndTime(new Date());
        impl2.setActivityType("exclusiveGateway");
        HistoricActivityInstanceEntityImpl impl3 = new HistoricActivityInstanceEntityImpl();
        impl3.setActivityId("parallelGateway");
        impl3.setEndTime(new Date());
        impl.setActivityType("parallelGateway");
        historicActivityInstanceList.add(impl);
        historicActivityInstanceList.add(impl2);
        historicActivityInstanceList.add(impl3);
        
        HistoricActivityInstanceQuery hquery = mock(HistoricActivityInstanceQuery.class);
        when(historyService.createHistoricActivityInstanceQuery()).thenReturn(hquery);
        when(hquery.processInstanceId(anyString())).thenReturn(hquery);
        when(hquery.orderByHistoricActivityInstanceId()).thenReturn(hquery);
        when(hquery.asc()).thenReturn(hquery);
        when(hquery.list()).thenReturn(historicActivityInstanceList);
        
        BpmnModel model = mock(BpmnModel.class);
        when(repositoryService.getBpmnModel(anyString())).thenReturn(model);
        
        StartEvent node = mock(StartEvent.class);
        ExclusiveGateway gateway = mock(ExclusiveGateway.class);
        org.activiti.bpmn.model.Process process = mock(org.activiti.bpmn.model.Process.class);
        when(model.getMainProcess()).thenReturn(process);
        when(process.getFlowElement(anyString(), anyBoolean())).thenReturn(node).thenReturn(gateway);
        
        InputStream is = org.apache.commons.io.IOUtils.toInputStream("xxxxx", "UTF-8");
        
        ProcessEngineConfiguration config = mock(ProcessEngineConfiguration.class);
        ProcessDiagramGenerator generator = mock(DefaultProcessDiagramGenerator.class);
        when(processEngine.getProcessEngineConfiguration()).thenReturn(config);
        when(config.getProcessDiagramGenerator()).thenReturn(generator);
        when(generator.generateDiagram(anyObject(), anyString(), any(), any())).thenReturn(is);
        
        WorkFlowResponse<String> result = workFlowServiceImpl.showDiagramHighlightAll("xxx", ImgTypeEnum.JPG.getCode());
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        
        result = workFlowServiceImpl.showDiagramHighlightAll("xxx", null);
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        
        result = workFlowServiceImpl.showDiagramHighlightAll("xxx", "fadfasdf");
        assertFalse(result.getSuccess());
        assertEquals(result.getErrorCode(), ErrorEnum.GLOBAL_BAD_REQUEST.getCode());
    }
    
    @Test
    public void testStatisticsTaskForRoles() {
        mockStatisticsCommon();
        
        Set<String> set = new HashSet<>();
        set.add("LD");
        StatisticsRoleQueryDto dto = StatisticsRoleQueryDto.builder()
            .tenantId(TenantEnum.CIG.getCode())
            .userGroups(set)
            .build();
        WorkFlowResponse<Long> resp = workFlowServiceImpl.statisticsTaskForRoles(dto);
        assertTrue(resp.getSuccess());
        assertTrue(resp.getData() == 2);
    }
    
    @Test
    public void testStatisticsTaskForUsers() {
        mockStatisticsCommon();
        
        Set<String> set = new HashSet<>();
        set.add("xxx@nike.com");
        StatisticsUsersQueryDto dto = StatisticsUsersQueryDto.builder()
            .tenantId(TenantEnum.CIG.getCode())
            .users(set)
            .build();
        WorkFlowResponse<Long> resp = workFlowServiceImpl.statisticsTaskForUsers(dto);
        assertTrue(resp.getSuccess());
        assertTrue(resp.getData() == 2);
    }
    
    private void mockStatisticsCommon() {
        NativeTaskQuery query = mock(NativeTaskQuery.class);
        when(taskService.createNativeTaskQuery()).thenReturn(query);
        when(query.sql(anyString())).thenReturn(query);
        when(query.parameter(anyString(), any())).thenReturn(query);
        when(query.count()).thenReturn(2L);
    }
    
    private void mockStartCommon() {
        PowerMockito.doNothing().when(identityService).setAuthenticatedUserId(any());
        ProcessInstance processInstance = mock(ExecutionEntityImpl.class);
        
        when(runtimeService.startProcessInstanceByKeyAndTenantId(any(), any(), any(), any())).thenReturn(processInstance);
        
        ExecutionQuery query = mock(ExecutionQuery.class);
        when(runtimeService.createExecutionQuery()).thenReturn(query);
        when(query.executionTenantId(anyString())).thenReturn(query);
        when(query.processInstanceId(anyString())).thenReturn(query);
        
        PowerMockito.doNothing().when(runtimeService).trigger(anyString());
        
        List<Execution> list = new ArrayList<>();
        ExecutionEntityImpl process = new ExecutionEntityImpl();
        ExecutionEntityImpl exec = new ExecutionEntityImpl();
        exec.setId("sendEmailToXX");
        list.add(process);
        list.add(exec);
        when(query.list()).thenReturn(list);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(anyString())).thenReturn(taskQuery);
        TaskEntityImpl task = mock(TaskEntityImpl.class);
        task.setTaskDefinitionKey("xxxx");
        when(taskQuery.singleResult()).thenReturn(task);
    }
    
    private void mockAuditCommon() {
        PowerMockito.doNothing().when(taskService).claim(anyString(), anyString());
        Comment comment = mock(CommentEntityImpl.class);
        when(taskService.addComment(any(),any(),any(),any())).thenReturn(comment);
        
        PowerMockito.doNothing().when(taskService).complete(anyString());;
        
        ExecutionQuery query = mock(ExecutionQuery.class);
        when(runtimeService.createExecutionQuery()).thenReturn(query);
        when(query.processInstanceId(anyString())).thenReturn(query);
        
        PowerMockito.doNothing().when(runtimeService).trigger(anyString());
        
        List<Execution> list = new ArrayList<>();
        ExecutionEntityImpl exec = mock(ExecutionEntityImpl.class);
        when(exec.getActivityId()).thenReturn("sendEmailToXX");
        list.add(new ExecutionEntityImpl());
        list.add(exec);
        when(query.list()).thenReturn(list);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(anyString())).thenReturn(taskQuery);
        when(taskQuery.taskId(anyString())).thenReturn(taskQuery);
        TaskEntityImpl task = mock(TaskEntityImpl.class);
        task.setTaskDefinitionKey("xxxx");
        when(taskQuery.singleResult()).thenReturn(task);
        when(taskQuery.list()).thenReturn(Arrays.asList(task));
        
        PowerMockito.doNothing().when(taskService).addCandidateUser(anyString(), anyString());
        
        HistoricProcessInstanceEntityImpl instance = mock(HistoricProcessInstanceEntityImpl.class);
        instance.setBusinessKey("bk");
        
        HistoricProcessInstanceQuery hquery = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(hquery);
        when(hquery.processInstanceId(anyString())).thenReturn(hquery);
        
        when(hquery.singleResult()).thenReturn(instance);
        
        ProcessInstanceHistoryLogQuery hisQuery = mock(ProcessInstanceHistoryLogQuery.class);
        when(historyService.createProcessInstanceHistoryLogQuery(anyString())).thenReturn(hisQuery);
        ProcessInstanceHistoryLogImpl hlog = mock(ProcessInstanceHistoryLogImpl.class);
        when(hlog.getBusinessKey()).thenReturn("xxxx");
        when(hisQuery.singleResult()).thenReturn(hlog);
    }
    
    private void commonMockListLog() {
        HistoricActivityInstanceQuery iQuery = mock(HistoricActivityInstanceQuery.class);
        when(historyService.createHistoricActivityInstanceQuery()).thenReturn(iQuery);
        when(iQuery.processInstanceId(anyString())).thenReturn(iQuery);
        
        List<HistoricActivityInstance> list = new ArrayList<>();
        HistoricActivityInstanceEntityImpl start = new HistoricActivityInstanceEntityImpl();
        start.setActivityType("startEvent");
        start.setEndTime(new Date());
        start.setStartTime(new Date());
        HistoricActivityInstanceEntityImpl userTask = new HistoricActivityInstanceEntityImpl();
        userTask.setActivityType("userTask");
        userTask.setEndTime(new Date());
        userTask.setStartTime(new Date());
        HistoricActivityInstanceEntityImpl end = new HistoricActivityInstanceEntityImpl();
        end.setActivityType("endEvent");
        end.setEndTime(new Date());
        end.setStartTime(new Date());
        list.add(start);
        list.add(userTask);
        list.add(end);
        
        when(iQuery.list()).thenReturn(list);
        
        List<Comment> comments = new ArrayList<>();
        CommentEntityImpl comment = new CommentEntityImpl();
        comment.setFullMessage(CommentUtils.generateComment(Boolean.TRUE, "this is comment"));
        comments.add(comment);
        
        when(taskService.getTaskComments(anyString())).thenReturn(comments);
    }
}
