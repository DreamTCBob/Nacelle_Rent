package com.manager.nacelle_rent.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;

@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean shirFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 定义过滤器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new ShiroLoginFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        shiroFilterFactoryBean.setLoginUrl("/login");

//        //WEB端超级管理员，需要角色权限 “superWebAdmin”
//        filterChainDefinitionMap.put("/superWebAdmin/**", "roles[superWebAdmin]");
//        //WEB端普通管理员，需要角色权限 “commonWebAdmin”
//        filterChainDefinitionMap.put("/commonWebAdmin/**", "roles[commonWebAdmin]");
//        //开放登陆接口
//        filterChainDefinitionMap.put("/webLogin", "authc");
//        //开放退出接口
//        filterChainDefinitionMap.put("/quitLoad", "authc");
        filterChainDefinitionMap.put("/checkRegister","anon");
        //其余接口一律拦截
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        System.out.println("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(userRealm());
        return securityManager;
    }

    /**
     * 自定义身份认证 realm;
     * <p>
     * 必须写这个类，并加上 @Bean 注解，目的是注入 userRealm，
     * 否则会影响 userRealm类 中其他类的依赖注入
     */
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }
}
