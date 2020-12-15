package org.jasig.cas.authentication.handler;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.MessageDescriptor;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.jasig.cas.authentication.model.DataBaseProperties;
import org.jasig.cas.authentication.model.User;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author qinpan
 * @version 1.0
 * @date 2020/10/27 2:28 下午
 */

public class CustomUsernamePasswordAuthentication extends AbstractUsernamePasswordAuthenticationHandler {

    private final static Logger logger = LoggerFactory.getLogger(CustomUsernamePasswordAuthentication.class);

    private JdbcTemplate jdbcTemplate;

    public CustomUsernamePasswordAuthentication(String name, ServicesManager servicesManager,
                                                PrincipalFactory principalFactory, Integer order, JdbcTemplate jdbcTemplate) {
        super(name, servicesManager, principalFactory, order);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
            UsernamePasswordCredential credential, String originalPassword)
            throws GeneralSecurityException, PreventedException {
        String username = credential.getUsername();
        String password = credential.getPassword();
        logger.info("认证用户 username = {}", username);

        String sql = "select id, username, password FROM tb_user where username = ?";
        User info = (User) jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));
        if (info == null) {
            logger.info("用户不存在！");
            throw new AccountException("用户不存在!");
        }

        if (!password.equals(info.getPassword())) {
            logger.info("密码错误!");
            throw new FailedLoginException("密码错误!");
        } else {
            logger.info("校验成功");
            //可自定义返回给客户端的多个属性信息
            HashMap<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("id", info.getId());
            returnInfo.put("username", info.getUserName());
            returnInfo.put("password", info.getPassword());
            Map<String,Object> resultMap = new HashMap<>(4);
            resultMap.put("loginStatus", JSON.toJSONString(info, SerializerFeature.WriteNullStringAsEmpty));

            final List<MessageDescriptor> list = new ArrayList<>();
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username, resultMap), list);

        }
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }
}
