package com.manager.nacelle_rent.utils;

import java.util.HashMap;

public class mapUtils {
    public static HashMap<String,String> roleMap = new HashMap<String,String>(){{
        put("rentAdmin","租方管理员");
        put("areaAdmin","区域管理员");
        put("commonWebAdmin","普通管理员");
        put("superWebAdmin","超级管理员");
        put("worker","施工作业人员");
        put("salesWebAdmin","营销部管理员");
        put("engineeringWebAdmin","工程部管理员");
        put("accountingWebAdmin","财务部管理员");
        put("InstallTeam","安装队伍");
    }};
}
