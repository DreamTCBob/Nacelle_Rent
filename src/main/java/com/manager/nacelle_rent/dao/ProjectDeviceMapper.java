package com.manager.nacelle_rent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectDeviceMapper {
    boolean increaseDevice(@Param("projectId") String projectId, @Param("deviceId") String deviceId);
    boolean deleteDeviceByDeviceId(@Param("deviceId") String deviceId);
    boolean deleteDeviceByProjectId(@Param("projectId") String projectId);
    List<String> getDeviceList(@Param("projectId") String projectId);
    List<String> getDevice(@Param("deviceId") String deviceId);
    List<String> getEndDeviceList(@Param("projectId") String projectId);
}