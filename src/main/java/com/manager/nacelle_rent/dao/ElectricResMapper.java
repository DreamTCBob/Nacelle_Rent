package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.ElectricRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElectricResMapper {
    List<ElectricRes> getElectricRes(String projectId);
    List<ElectricRes> getDeviceList();
    List<ElectricRes> getDevice(String deviceId);
    ElectricRes getElectricBoxState(String projectBuilders);
    void createWorkBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("projectBuilders") String projectBuilders);
    void deleteWorkBox(@Param("userId") String userId);
    void updateWorker(@Param("deviceId") String deviceId, @Param("projectBuilders") String projectBuilders);
}
