package com.manager.nacelle_rent.utils;

import java.util.HashMap;

public class mapUtils {
    public static HashMap<String,String> roleMap = new HashMap<String,String>(){{
        put("rentAdmin","租方管理员");
        put("areaAdmin","区域管理员");
        put("commonWebAdmin","普通管理员");
        put("superWebAdmin","超级管理员");
        put("worker","施工作业人员");
        put("inspector","巡检人员");
        put("worker_1","施工作业人员");
        put("worker_2","施工作业人员");
        put("worker_3","施工作业人员");
        put("salesWebAdmin","营销部管理员");
        put("engineeringWebAdmin","工程部管理员");
        put("accountingWebAdmin","财务部管理员");
        put("InstallTeam","安装队伍");
        put("curtain_electricWorker", "施工作业人员");
        put("curtain_glassWorker", "施工作业人员");
        put("curtain_glueWorker", "施工作业人员");
        put("coating_painter", "施工作业人员");
        put("curtain_stoneWorker","施工作业人员");
        put("coating_realStone", "施工作业人员");
        put("other_others", "施工作业人员");
        put("manager","管理员");
        put("govAdmin", "政府监管人员");
    }};
}
