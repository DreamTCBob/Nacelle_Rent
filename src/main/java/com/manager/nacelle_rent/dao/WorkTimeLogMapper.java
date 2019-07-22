package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.WorkTimeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkTimeLogMapper {
    void createWorkTimeLog(@Param("userId") String userId, @Param("projectId") String projectId, @Param("deviceId") String deviceId, @Param("timeWork") long timeWork);
    List<WorkTimeLog> getWorkerTime(@Param("userId") String userId);
}
