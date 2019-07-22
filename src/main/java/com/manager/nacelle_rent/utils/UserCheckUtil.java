package com.manager.nacelle_rent.utils;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.manager.nacelle_rent.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.Map;

public class UserCheckUtil {
    public static JSONObject checkUser(String id, String password, String roleName) {
        //进行身份验证
        Subject subject = SecurityUtils.getSubject();
        JSONObject jsonObject = new JSONObject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(id, password);
        //进行验证，这里可以捕获异常，然后返回对应信息
        try {
            subject.login(usernamePasswordToken);
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            System.out.println("身份验证成功了");
            if(roleName != null){
                System.out.println("开始验证权限");
                try {
                    Map<String, Claim> claims = JwtUtil.verifyToken(password);
                    String role = claims.get("userRole").asString();
                    if(roleName.contains(role)){
                        jsonObject.put("result",1);
                    }
//                    if(role.equals(roleName)){
//                        jsonObject.put("result",1);
//                    }
                     else{
                        jsonObject.put("result",-1);   //-1表示没有权限
                        return jsonObject;
                    }
                }catch(Exception ex){
                    jsonObject.put("result",0);   //尚未登录
                    return jsonObject;
                }
            }
            System.out.println("准备返回数据");
            jsonObject.put("result",1);
            jsonObject.put("userInfo",user);
            return jsonObject;
        } catch (Exception ex) {
            ex.printStackTrace();
            jsonObject.put("result",0);   //尚未登录
            return jsonObject;
        }
    }
}

