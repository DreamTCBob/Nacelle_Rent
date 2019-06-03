package com.manager.nacelle_rent.config;

import com.manager.nacelle_rent.entity.User;
import com.manager.nacelle_rent.service.UserService;
import com.manager.nacelle_rent.utils.CookieUtil;
import com.manager.nacelle_rent.utils.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    /**
     * 获取授权信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("————权限认证————");
        // String userId = (String) principalCollection.getPrimaryPrincipal();
        // String password = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 根据用户名查询当前用户拥有的角色和权限
        // User user = userService.webLoad(userId);
        // String roleName = user.getUserRole();
//        String perms = user.getUserPerm();
//        String[] permLists = perms.split(",");

        // authorizationInfo.addRole(roleName);  // 将角色名称提供给info
        //Set<String> permissionNames = new HashSet<>(Arrays.asList(permLists));
        //  authorizationInfo.setStringPermissions(permissionNames);       // 将权限名称提供给info
        return authorizationInfo;
    }


    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    //重写获取用户信息的方法
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        System.out.println("————身份认证方法————");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = new User();
        // 取出用户名和密码
        String id = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        {
            //进行jwt验证
            try {
                JwtUtil.verifyToken(password);
            } catch (Exception ex) {
                //解析id
                String realId = id.split(",")[0];
                boolean loginWithPhone = id.split(",")[1].equals("1");
                if(loginWithPhone){
                    user = userService.androidLoad(realId);
                }else{
                    user = userService.webLoad(realId);
                }
                System.out.println("user：" + user.getUserId());
                if (!password.equals(user.getUserPassword())) {
                    throw new AccountException("密码不正确");
                }
            }
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, password, getName());
            return authenticationInfo;
        }
    }
}
