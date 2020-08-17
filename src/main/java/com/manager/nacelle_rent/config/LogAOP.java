package com.manager.nacelle_rent.config;

import cn.hutool.db.sql.SqlBuilder;
import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Aspect
@Component
public class LogAOP {
    @Pointcut("execution(* com.manager.nacelle_rent.controller..*.*(..))")
    public void webLog()
    {
    }
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        System.out.println("方法的返回值 : ");
        JSONObject jsonObject = (JSONObject) ret;
        if (jsonObject == null) return;
        for (String s : jsonObject.keySet()){
            System.out.println(s + ":" + jsonObject.get(s));
        }
    }

    @Around("webLog()")
    public Object logHandler(ProceedingJoinPoint joinPoint) throws Throwable
    {
        System.out.println("开始执行" + joinPoint.getSignature() + "方法");
        long totalMilliSeconds = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Object result= null;
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attributes.getRequest();
        // 记录下请求内容
        String url = req.getRequestURL().toString();
        String httpMethod = req.getMethod();
        String ip = req.getRemoteAddr();
        System.out.println("请求URL : " + url);
        System.out.println("HTTP_METHOD : " + httpMethod);
        System.out.println("IP : " + ip);

        //获取目标方法的参数信息
        Object[] obj = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();

        //代理的是哪一个方法
        String methodName = signature.getName();

        MethodSignature methodSignature = (MethodSignature) signature;

        // 入参
        StringBuilder params = new StringBuilder();

        // 获取方法参数名称
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        int length = paramNames.length;
        for(int i=0; i<length; i++)
        {
            params.append(paramNames[i]).append("=").append(paramValues[i]).append("</br>");
        }
        long costTime = System.currentTimeMillis() - totalMilliSeconds;
        System.out.println("请求时间：" + df.format(new Date()) + "  请求耗时：" + costTime + "ms" + "  请求方法：" + methodName + "  请求参数: " + params.toString());
        try
        {
            // 执行controller方法
            result = joinPoint.proceed();
        }
        catch (Throwable throwable)
        {
            String exception = throwable.getClass()+":"+throwable.getMessage();
            System.out.println("出现异常" + exception);

        }
        return result;
    }

}
