package com.nike.gcsc.auth.controller;

import com.nike.gcsc.auth.constant.ErrorEnum;
import com.nike.gcsc.auth.controller.request.PageBaseRequest;
import com.nike.gcsc.auth.entity.Okta;
import com.nike.gcsc.auth.service.AuthService;
import com.nike.gcsc.authapi.request.OktaRequestBean;
import com.nike.gcsc.authapi.response.OktaDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.util.BeanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/okta")
public class OktaController {

    @Autowired
    AuthService oktaService;

    @PostMapping("/check_exist_or_add")
    public GlobalResponse<Long> checkExistOrAddOkta(@RequestBody OktaRequestBean bean) {
    	Okta okta = BeanMapper.map(bean, Okta.class);
    	Page<Okta> oktaPage = oktaService.findByAttributes(okta, 0, 10);
    	if(oktaPage.getContent().size()>0) {
    		return GlobalResponse.buildFail(ErrorEnum.PARAM_INVALID);
    	}
    	Long oktaId = oktaService.add(okta);
        return GlobalResponse.buildSuccess(oktaId);
    }

    @DeleteMapping("/delete/{id}")
    public GlobalResponse<Boolean> delete(@PathVariable(name="id") Long id) {
        try{
        	oktaService.delete(id);
        } catch(Exception e){
            log.error("OktaController:delete:{}", id);

        }
        return GlobalResponse.buildSuccess(true);
    }

    @PutMapping("/update/{id}")
    public GlobalResponse<Boolean> update(@PathVariable(name="id") Long id, @RequestBody OktaRequestBean bean) {
    	Okta param = BeanMapper.map(bean, Okta.class);
    	param.setId(id);
    	oktaService.edit(param);
    	return GlobalResponse.buildSuccess(true);
    }

    @GetMapping("/find_all")
    public GlobalResponse<List<OktaDto>> findAll() {
    	Page<Okta> oktas = oktaService.findAll(0, Integer.MAX_VALUE);
    	List<OktaDto> oktaDtoList = BeanMapper.mapList(oktas.getContent(),OktaDto.class );
        return GlobalResponse.buildSuccess(oktaDtoList);
    }

    @GetMapping("/find_by_project/{projectName}")
    GlobalResponse<List<OktaDto>> getByProjectName(@PathVariable(name = "projectName") String projectName) {
    	List<Okta> oktaList = oktaService.findByProjectName(projectName);
    	List<OktaDto> oktaDtoList = BeanMapper.mapList(oktaList, OktaDto.class );
        return GlobalResponse.buildSuccess(oktaDtoList);
    }
    
    @GetMapping("/find_by_client_id/{clientId}")
    public GlobalResponse<OktaDto> findByClientId(@PathVariable(name="clientId") String clientId) {
    	Okta okta = oktaService.findByClientId(clientId);
    	OktaDto oktaDto = BeanMapper.map(okta, OktaDto.class);
        return GlobalResponse.buildSuccess(oktaDto);
    }

    @GetMapping("/find_by_describe/{describe}")
    public GlobalResponse<OktaDto> findByDescribe(@PathVariable(name="describe") String describe) {
        Okta okta = oktaService.findByDescribe(describe);
        OktaDto oktaDto = BeanMapper.map(okta, OktaDto.class);
        return GlobalResponse.buildSuccess(oktaDto);
    }

    @PostMapping("/find_by_page")
    public GlobalResponse<Page<Okta>> findByAttributes(@RequestBody PageBaseRequest<Okta> pageRequest) {
    	Page<Okta> oktaPage = oktaService.findByAttributes(pageRequest.getOptions(), pageRequest.getPageNum(),pageRequest.getPageSize());
        return GlobalResponse.buildSuccess(oktaPage);
    }

}
