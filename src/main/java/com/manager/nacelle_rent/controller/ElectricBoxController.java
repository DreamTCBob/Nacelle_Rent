package com.manager.nacelle_rent.controller;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.ElectricBoxMapper;
import com.manager.nacelle_rent.dao.ElectricResMapper;
import com.manager.nacelle_rent.entity.ElectricRes;
import com.manager.nacelle_rent.entity.RealTimeData;
import com.manager.nacelle_rent.service.ElectricBoxService;
import com.manager.nacelle_rent.utils.UserCheckUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.BitSet;
import java.util.List;

@Api(description = "电柜接口")
@RestController
public class ElectricBoxController {
    @Autowired
    private ElectricBoxMapper electricBoxMapper;
    @Autowired
    private ElectricResMapper electricResMapper;
    @Autowired
    private ElectricBoxService electricBoxService;

    @ApiOperation(value = "获取指定电柜的实时参数" ,  notes="")
    @GetMapping("/getRealTimeData")
    public JSONObject pushToWeb(HttpServletRequest request, @RequestParam String deviceId) {
        JSONObject jsonObject = new JSONObject();
        //进行身份验证，只能通过cookie认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password,null).get("result");
        if(flag == 1){
            try {
                RealTimeData realTimeData = electricBoxMapper.getRealTimeDataById(deviceId);
                jsonObject.put("realTimeData",realTimeData);
                jsonObject.put("isAllowed",true);
            }catch(Exception ex){
                jsonObject.put("fail","dataError");
                jsonObject.put("isAllowed",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取电柜列表" ,  notes="")
    @GetMapping("/getDeviceList")
    public JSONObject getDeviceList(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        //进行身份验证，只能通过cookie认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password,null).get("result");
        if(flag == 1){
            try {
                List<ElectricRes> deviceList =  electricBoxService.serializeElectricRes(electricResMapper.getDeviceList());
                jsonObject.put("deviceList",deviceList);
            }catch(Exception ex){
                jsonObject.put("fail","dataError");
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "新建安装信息" ,  notes="")
    @PostMapping("/createElectricBoxConfig")
    public JSONObject createElectricBoxConfig(HttpServletRequest request, @RequestParam String deviceId,
                                              @RequestParam String type, @RequestParam String number){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = electricBoxService.createElectricBoxConfig(deviceId, type, number);
            if(result == 1)
                jsonObject.put("create","success");
            else
                jsonObject.put("create","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取安装信息" ,  notes="")
    @GetMapping("/getElectricBoxConfig")
    public JSONObject getElectricBoxConfig(HttpServletRequest request, @RequestParam String deviceId , @RequestParam int type){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            JSONObject result = electricBoxService.getElectricBoxConfig(deviceId, type);
            jsonObject.put("electricBoxConfig",result);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "删除安装信息" ,  notes="")
    @PostMapping("/deleteElectricBoxConfig")
    public JSONObject deleteElectricBoxConfig(HttpServletRequest request, @RequestParam String deviceId, @RequestParam String type){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = electricBoxService.deleteElectricBoxConfig(deviceId, type);
            if(result == 1)
                jsonObject.put("delete","success");
            else
                jsonObject.put("delete/**/","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取吊篮实时工作信息" ,  notes="")
    @GetMapping("/getElectricResInfo")
    public JSONObject getElectricResInfo(HttpServletRequest request, @RequestParam(required = false) String deviceId,
                                         @RequestParam(required = false) String userId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            List<JSONObject> result = electricBoxService.getElectricResInfo(deviceId, userId);
            jsonObject.put("result", result);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

}
