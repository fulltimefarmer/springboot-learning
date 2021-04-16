 package com.nike.gcsc.workflow.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nike.gcsc.workflow.common.WorkFlowResponse;
import com.nike.gcsc.workflow.constant.ErrorEnum;
import com.nike.gcsc.workflow.constant.TenantEnum;
import com.nike.gcsc.workflow.dto.DeploymentResultDto;
import com.nike.gcsc.workflow.util.ConstantUtils;

import io.swagger.annotations.ApiParam;

/**
 * @author roger yang
 * @date 6/28/2019
 */
@RestController
public class DeployRest {
    @Autowired
    private RepositoryService repositoryService;
    
    @GetMapping("/v1/public/deployment/list")
    public WorkFlowResponse<List<DeploymentResultDto>> listDeploy() {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().list();
        List<DeploymentResultDto> result = null;
        if(!CollectionUtils.isEmpty(processList)) {
            result = processList.stream().map(item -> {
                DeploymentResultDto dto = new DeploymentResultDto();
                dto.setTenantId(item.getTenantId());
                dto.setDeploymentId(item.getDeploymentId());
                dto.setDiagramResourcePath(item.getDiagramResourceName());
                dto.setDiagramResourceName(getFileName(dto.getDiagramResourcePath()));
                dto.setProcessDefinitionId(item.getId());
                dto.setProcessDefinitionName(item.getName());
                dto.setResourcePath(item.getResourceName());
                dto.setResourceName(getFileName(dto.getResourcePath()));
                dto.setVersion(item.getVersion());
                return dto; 
            }).collect(Collectors.toList());
        }
        return WorkFlowResponse.buildSuccess(result);
    }
    
    @PostMapping("/v1/public/deployment/deploy")
    public WorkFlowResponse<Void> addDeploy(@RequestParam("file") MultipartFile file, @RequestParam(name="tenatId", required = true) @ApiParam(name = "tenatId", allowableValues = "cig") String tenatId) throws IOException {
        if(null == ConstantUtils.getEnumValue(TenantEnum.class, tenatId)) {
            return WorkFlowResponse.buildFail(ErrorEnum.GLOBAL_BAD_REQUEST);
        }
        repositoryService.createDeployment().addZipInputStream(new ZipInputStream(file.getInputStream())).tenantId(tenatId).deploy();
        return WorkFlowResponse.buildSuccess(null);
    }
    
    @DeleteMapping("/v1/api/deployment/delete")
    public WorkFlowResponse<Boolean> deleteDeploymet(@RequestParam("deploymentId") String deploymentId) {
        repositoryService.deleteDeployment(deploymentId);
        return WorkFlowResponse.buildSuccess(Boolean.TRUE);
    }
    
    //@DeleteMapping("/v1/api/deployment/delete-all")
    public WorkFlowResponse<Boolean> deleteAllDeployment() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        if(!CollectionUtils.isEmpty(list)) {
            list.forEach(item -> {
                repositoryService.deleteDeployment(item.getId());
            });
        }
        return WorkFlowResponse.buildSuccess(Boolean.TRUE);
    }
    
    private String getFileName(String path) {
        if(StringUtils.isNotBlank(path)) {
            String[] arr = path.split("\\\\");
            return arr[arr.length-1];
        }
        return null;
    }
}
