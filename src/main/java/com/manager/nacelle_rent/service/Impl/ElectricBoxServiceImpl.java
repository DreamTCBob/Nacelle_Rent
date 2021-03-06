package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.ElectricBoxMapper;
import com.manager.nacelle_rent.dao.ElectricResMapper;
import com.manager.nacelle_rent.dao.ElectricStateMapper;
import com.manager.nacelle_rent.dao.UserMapper;
import com.manager.nacelle_rent.entity.ElectricRes;
import com.manager.nacelle_rent.entity.SimpleUser;
import com.manager.nacelle_rent.service.ElectricBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElectricBoxServiceImpl implements ElectricBoxService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ElectricBoxMapper electricBoxMapper;
    @Autowired
    private ElectricResMapper electricResMapper;
    @Autowired
    private ElectricStateMapper electricStateMapper;

    @Override
    public List<ElectricRes> serializeElectricRes(List<ElectricRes> list){
        for( int i = 0 ; i < list.size() ; i++) {
            ElectricRes electricRes = list.get(i);
            String[] projectBuilders = electricRes.getProjectBuilders().split(",");
            ArrayList<SimpleUser> projectBuildersList = new ArrayList<>();
            for(String userId : projectBuilders){
                SimpleUser currentUser = userMapper.getNameById(userId);
                projectBuildersList.add(currentUser);
            }
            electricRes.setProjectBuildersDetail(projectBuildersList);
        }
        return  list;
    }

    @Override
    public int createElectricBoxConfig(String deviceId, String type, String number) {
        switch (type){
            case "hoist":
                if (electricBoxMapper.updateElectricBoxConfigByHoist(deviceId, number)) return 1;
                else return 0;
            case "camera":
                if (electricBoxMapper.updateElectricBoxConfigByCamera(deviceId,number)) return 1;
                else return 0;
            case "safeLock":
                if (electricBoxMapper.updateElectricBoxConfigBySafeLock(deviceId,number)) return 1;
                else return 0;
        }
        return 0;
    }

    @Override
    public JSONObject getElectricBoxConfig(String deviceId, int type) {
        switch (type) {
            case 0: {
                return new JSONObject(userMapper.getAllParts(deviceId));
            }
            case 1: {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cameraId", userMapper.getCameraIdPart(deviceId));
                return jsonObject;
            }
            case 2:{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hoistId", userMapper.getHoistIdPart(deviceId));
                return jsonObject;
            }
            case 3: {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("safeLockId", userMapper.getSafeLockIdPart(deviceId));
                return jsonObject;
            }
            default:
                return null;
        }
    }

    @Override
    public int deleteElectricBoxConfig(String deviceId, String type) {
        switch (type){
            case "hoist":
                if (electricBoxMapper.deleteElectricBoxConfigByHoist(deviceId)) return 1;
                else return 0;
            case "camera":
                if (electricBoxMapper.deleteElectricBoxConfigByCamera(deviceId)) return 1;
                else return 0;
            case "safeLock":
                if (electricBoxMapper.deleteElectricBoxConfigBySafeLock(deviceId)) return 1;
                else return 0;
        }
        return 0;
    }

    @Override
    public List<JSONObject> getElectricResInfo(String deviceId, String userId) {
        List<JSONObject> list = new ArrayList<>();
        List<ElectricRes> electricResList = electricResMapper.getElectricResInfo(deviceId, userId);
        for (ElectricRes electricRes : electricResList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("electricRes", electricRes);
            String siteNo = electricStateMapper.getSiteNo(electricRes.getDeviceId());
            jsonObject.put("siteNo", siteNo);
            String userName = userMapper.getUserName(electricRes.getProjectBuilders());
            jsonObject.put("userName", userName);
            list.add(jsonObject);
        }
        return list;
    }
}
