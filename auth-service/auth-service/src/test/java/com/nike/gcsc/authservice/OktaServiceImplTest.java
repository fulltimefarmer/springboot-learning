package com.nike.gcsc.authservice;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.nike.gcsc.auth.constant.AuthConstant;
import com.nike.gcsc.auth.entity.Group;
import com.nike.gcsc.auth.entity.Okta;
import com.nike.gcsc.auth.entity.Permission;
import com.nike.gcsc.auth.entity.Token;
import com.nike.gcsc.auth.model.okta.OktaIntrospect;
import com.nike.gcsc.auth.model.okta.OktaToken;
import com.nike.gcsc.auth.repository.GroupRepository;
import com.nike.gcsc.auth.repository.OktaRepository;
import com.nike.gcsc.auth.repository.PermissionRepository;
import com.nike.gcsc.auth.repository.TokenRepository;
import com.nike.gcsc.auth.repository.UserRepository;
import com.nike.gcsc.auth.service.impl.AuthServiceImpl;
import com.nike.gcsc.auth.utils.HttpUtils;
import com.nike.gcsc.authapi.response.UserPermissionDto;
import com.nike.gcsc.util.BeanMapper;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@PrepareForTest({HttpUtils.class, AuthConstant.class, BeanMapper.class})
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@Slf4j
public class OktaServiceImplTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    OktaRepository oktaRepository;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    GroupRepository groupRepository;

    @Mock
    PermissionRepository permissionRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    UserRepository userRepository;

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
        
        ReflectionTestUtils.setField(authService, "enableSkipOkta", false);

//        RedisTemplate redisTemplate = PowerMockito.mock(RedisTemplate.class);
        ValueOperations mock = PowerMockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(mock);

        String tokenStr = "testToken";
        OktaToken token = new OktaToken();
        token.setAccessToken(tokenStr);

        OktaIntrospect introspect = new OktaIntrospect();
        introspect.setSub("zhangSan");
        introspect.setUsername("zhangSan");
        try {
            when(HttpUtils.httpPost(anyString(),Mockito.anyMap(),Mockito.anyMap(), any())).thenReturn(token).thenReturn(introspect);
        } catch (InstantiationException | IllegalAccessException e) {
            Assert.assertTrue(false);
        }

        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        List<Group> groupList = new ArrayList<>();
        Group g1 = new Group();
        groupList.add(g1);
        when(groupRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(groupList);
        List<Permission> permissionList = new ArrayList<>();
        Permission p1 = new Permission();
        permissionList.add(p1);
        when(permissionRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(permissionList);


        Okta okta = new Okta();
        okta.setClientId("gcsc.nike.cig");
        okta.setSecret("testSecret");
        String redirectUrl = "testRedirectUrl";
        String code = "testcode";
        String tokenByCode = authService.getTokenByCode(okta, redirectUrl, code);
        Assert.assertEquals(tokenByCode,tokenStr);
        
        
        
        ReflectionTestUtils.setField(authService, "enableSkipOkta", true);
        
        String tokenByCode1 = authService.getTokenByCode(okta, redirectUrl, code);
        
        Assert.assertEquals(tokenByCode1,code);
    }

    @Test
    public void getTokenByPwd() {
    	ReflectionTestUtils.setField(authService, "enableSkipOkta", true);
    	
//    	RedisTemplate redisTemplate = PowerMockito.mock(RedisTemplate.class);
        ValueOperations mock = PowerMockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(mock);
    	
        String username = "test";
        String password = "test";
        Long projectId = 1L;

        when(redisTemplate.hasKey(any())).thenReturn(false);
        
//        PowerMockito.doNothing(tokenRepository.save(any(OktaToken.class)));

        String tokenResp = authService.getTokenByPwd(username, password, projectId);
        Assert.assertNotNull(tokenResp);
        
        ReflectionTestUtils.setField(authService, "enableSkipOkta", false);
        when(redisTemplate.hasKey(any())).thenReturn(true);
		when(redisTemplate.opsForValue().get(any())).thenReturn("6");

        String tokenResp1 = authService.getTokenByPwd(username, password, projectId);
        Assert.assertNotNull(tokenResp1);


        
        
        ReflectionTestUtils.setField(authService, "enableSkipOkta", false);
        when(redisTemplate.hasKey(any())).thenReturn(true);
        when(redisTemplate.opsForValue().get(any())).thenReturn("1");
        when(redisTemplate.delete(anyString())).thenReturn(true);
        String tokenResp5 = authService.getTokenByPwd(username, password, projectId);
        Assert.assertNotNull(tokenResp5);

        ReflectionTestUtils.setField(authService, "enableSkipOkta", false);
        when(redisTemplate.hasKey(any())).thenReturn(true);
        when(redisTemplate.opsForValue().get(any())).thenReturn("1");
//        when(userRepository.count((org.springframework.data.domain.Example<User>) any(Example.class))).thenReturn(2L);
        String tokenResp3 = authService.getTokenByPwd(username, password, projectId);
        Assert.assertNotNull(tokenResp3);
        
        
        ReflectionTestUtils.setField(authService, "enableSkipOkta", false);
        when(redisTemplate.hasKey(any())).thenReturn(true).thenReturn(false);
        when(redisTemplate.opsForValue().get(any())).thenReturn("1");
//        when(userRepository.count((org.springframework.data.domain.Example<User>) any(Example.class))).thenReturn(2L);
        String tokenResp4 = authService.getTokenByPwd(username, password, projectId);
        Assert.assertNotNull(tokenResp4);
        
        
        
      ReflectionTestUtils.setField(authService, "enableSkipOkta", false);
      when(redisTemplate.hasKey(any())).thenReturn(true);
      when(redisTemplate.opsForValue().get(any())).thenReturn("1");
      when(userRepository.count(any())).thenReturn(1L);
      when(redisTemplate.delete(anyString())).thenReturn(true);
      String tokenResp2 = authService.getTokenByPwd(username, password, projectId);
      Assert.assertNotNull(tokenResp2);
        
        
    }
    
    @Test
    public void getUserPermissionByToken() {
    	
    	String token = "test";
    	ValueOperations mock = PowerMockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(mock);
        
        UserPermissionDto result = null;
        
        when(mock.get(anyString())).thenReturn(result);
        Token t = new Token();
        
        when(tokenRepository.findByToken(token)).thenReturn(t);
        
        List<Group> groupList = new ArrayList<>();
        Group g1 = new Group();
        groupList.add(g1);
        when(groupRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(groupList);
        List<Permission> permissionList = new ArrayList<>();
        Permission p1 = new Permission();
        permissionList.add(p1);
        when(permissionRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(permissionList);
        UserPermissionDto userPermissionByToken = authService.getUserPermissionByToken(token);
        Assert.assertNotNull(userPermissionByToken);
        
        
        when(tokenRepository.findByToken(token)).thenReturn(null);
        UserPermissionDto userPermissionByToken1 = authService.getUserPermissionByToken(token);
        Assert.assertNull(userPermissionByToken1);
        
        result = UserPermissionDto.builder().build();
        when(mock.get(anyString())).thenReturn(result);
        UserPermissionDto userPermissionByToken2 = authService.getUserPermissionByToken(token);
        Assert.assertNotNull(userPermissionByToken2);
    }
    
    @Test
    public void reflashTokenCache() {
    	ValueOperations mock = PowerMockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(mock);
        
        String secret = "superNova" + LocalDate.now().getDayOfMonth();
    	int second = 1;
    	List<Token> tokens = new ArrayList<>();
    	Token t = new Token();
    	t.setExpireTime(Instant.now().getEpochSecond()+10000);
    	t.setUsername("test");
    	t.setProjectId(4L);
    	tokens.add(t);
    	when(tokenRepository.findByCreateTime(any(Integer.class))).thenReturn(tokens);
    	
    	List<Group> groupList = new ArrayList<>();
        Group g1 = new Group();
        groupList.add(g1);
        when(groupRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(groupList);
        List<Permission> permissionList = new ArrayList<>();
        Permission p1 = new Permission();
        permissionList.add(p1);
        when(permissionRepository.findByProjectidAndUsername(any(Long.class),any(String.class))).thenReturn(permissionList);
        
        Boolean reflashTokenCache = authService.reflashTokenCache(secret, second);
        Assert.assertTrue(reflashTokenCache);
        
    	
    }
}
