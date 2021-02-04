package com.manager.nacelle_rent.utils;

import java.util.HashMap;

public class WorkerUtils {
    public static HashMap<String,String> roleMap = new HashMap<String,String>(){{
        put("worker","其他作业人员");
        put("inspector","巡检人员");
        put("worker_1","其他类-其他作业人员");
        put("worker_2","其他类-其他作业人员");
        put("worker_3","其他类-其他作业人员");
        put("curtain_electricWorker", "幕墙类-电焊工");
        put("curtain_glassWorker", "幕墙类-玻璃铝板工");
        put("curtain_glueWorker", "幕墙类-打胶工");
        put("coating_painter", "涂料类-粉刷工");
        put("curtain_stoneWorker","幕墙类-石材工");
        put("coating_realStone", "涂料类-真石漆工");
        put("other_others", "其他类-其他作业人员");
        put("curtain_glassPlate","幕墙类-玻璃铝板工");
    }};
}
