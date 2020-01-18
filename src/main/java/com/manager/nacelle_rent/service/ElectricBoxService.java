package com.manager.nacelle_rent.service;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.entity.ElectricRes;

import java.util.List;

public interface ElectricBoxService {

    List<ElectricRes> serializeElectricRes(List<ElectricRes> list);
    int createElectricBoxConfig(String deviceId, String type, String number);
    int deleteElectricBoxConfig(String deviceId, String type);
    JSONObject getElectricBoxConfig(String deviceId, int type);

}
