package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.ProjectDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectDeviceInfoMapper {
    List<ProjectDevice> getAllDevice(@Param("pageSize") int pageSize, @Param("offset") int offset);
    List<ProjectDevice> getAllDeviceByProjectId(@Param("projectId") String projectId,
                                                @Param("pageSize") int pageSize, @Param("offset") int offset);
    List<ProjectDevice> getAllDeviceByDeviceId(@Param("deviceId") String deviceId,
                                               @Param("pageSize") int pageSize, @Param("offset") int offset);
    List<ProjectDevice> getAllDeviceByStartTime(@Param("startTime") String startTime,
                                                @Param("endTime") String endTime,
                                                @Param("pageSize") int pageSize, @Param("offset") int offset);
    List<ProjectDevice> getAllDeviceByEndTime(@Param("startTime") String startTime,
                                              @Param("endTime") String endTime,
                                              @Param("pageSize") int pageSize, @Param("offset") int offset);
    int totalGetAllDevice();
    int totalGetAllDeviceByProjectId(@Param("projectId") String projectId);
    int totalGetAllDeviceByDeviceId(@Param("deviceId") String deviceId);
    int totalGetAllDeviceByStartTime(@Param("startTime") String startTime,
                                     @Param("endTime") String endTime);
    int totalGetAllDeviceByEndTime(@Param("startTime") String startTime,
                                   @Param("endTime") String endTime);
}
