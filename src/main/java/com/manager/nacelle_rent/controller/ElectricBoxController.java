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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
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
            }catch(Exception ex){
                jsonObject.put("fail","dataError");
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
}
