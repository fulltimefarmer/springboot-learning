package com.nike.gcsc.authservice;

import com.google.common.collect.Lists;
import com.nike.gcsc.auth.dto.Okta;
import com.nike.gcsc.auth.model.okta.OktaIntrospect;
import com.nike.gcsc.auth.model.okta.OktaToken;
import com.nike.gcsc.auth.repository.OktaRepository;
import com.nike.gcsc.auth.service.impl.AuthServiceImpl;
import com.nike.gcsc.auth.utils.HttpUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@PrepareForTest({HttpUtils.class})
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class OktaServiceImplTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    OktaRepository oktaRepository;

    @Before
    public void setup() {
        PowerMockito.mockStatic(HttpUtils.class);
        MockitoAnnotations.initMocks(this);
        /*ReflectionTestUtils.setField(oktaService, "introspectUrl", "introspectUrlTest");
        ReflectionTestUtils.setField(oktaService, "tokenUrl", "tokenUrlTest");*/
    }

    @Test
    public void add(){
        Long  resp = 10L;
        Okta saved = new Okta();
        saved.setId(resp);
        when(oktaRepository.save(any())).thenReturn(saved);
        Okta param = new Okta();
        param.setId(1L);
        Long add = authService.add(param);
        TestCase.assertEquals(saved.getId(),add);
    }

    @Test
    public void delete(){
        Long  id = 10L;
       try{
           PowerMockito.doNothing().when(oktaRepository).deleteById(id);
           authService.delete(10L);
           Assert.assertTrue(true);
       } catch (Exception e){
           Assert.assertTrue(false);
       }
    }

    @Test
    public void deleteBatchByIdList(){
        Long id1 = 1L;
        Long id2 = 2L;
        Long id3 = 3L;
        ArrayList<Long> lists = Lists.newArrayListWithCapacity(3);
        lists.add(id1);
        lists.add(id2);
        lists.add(id3);
        try{
            PowerMockito.doNothing().when(oktaRepository).deleteBatchByIdList(lists);
            authService.deleteBatchByIdList(lists);
            Assert.assertTrue(true);
        }catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void edit(){
        Okta okta = new Okta();
        okta.setId(1L);
        okta.setAppName("cig");
        PowerMockito.when(oktaRepository.save(okta)).thenReturn(okta);
        authService.edit(okta);
        Assert.assertTrue(true);
    }

    @Test
    public void findAll(){
        Pageable pageable = PowerMockito.mock(Pageable.class);
        Page<Okta> page = authService.findAll(0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void findByAttributes(){
        Okta query = new Okta();
        query.setAppName("cig");
        Pageable pageable = PowerMockito.mock(Pageable.class);
        Page<Okta> page = authService.findByAttributes(query, 0, 10);
        Assert.assertEquals(null, page);
    }

    @Test
    public void findByClientId(){
        String clientId = "gcsc.nike.cig";
        Okta okta = new Okta();
        okta.setClientId(clientId);
        PowerMockito.when(oktaRepository.findByClientId(clientId)).thenReturn(okta);
        Okta byClientId = authService.findByClientId(clientId);
        Assert.assertEquals(byClientId.getClientId(),clientId);
    }

    @Test
    public void getTokenByCode(){
        ReflectionTestUtils.setField(authService, "tokenUrl", "tokenUrlTest");
        String tokenStr = "testToken";
        OktaToken token = new OktaToken();
        token.setAccessToken(tokenStr);

        OktaIntrospect introspect = new OktaIntrospect();
        introspect.setSub("zhangSan");
        try {
            when(HttpUtils.httpPost(anyString(),Mockito.anyMap(),Mockito.anyMap(), any())).thenReturn(token).thenReturn(introspect);
        } catch (InstantiationException | IllegalAccessException e) {
            Assert.assertTrue(false);
        }
        Okta okta = new Okta();
        okta.setClientId("gcsc.nike.cig");
        okta.setSecret("testSecret");
        String redirectUrl = "testRedirectUrl";
        String code = "testcode";
        String tokenByCode = authService.getTokenByCode(okta, redirectUrl, code);
        Assert.assertEquals(tokenByCode,tokenStr);
    }

}
