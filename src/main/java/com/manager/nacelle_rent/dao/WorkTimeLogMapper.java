package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.WorkTimeLog;
import java.sql.Timestamp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkTimeLogMapper {
    void createWorkTimeLog(@Param("userId") String userId, @Param("projectId") String projectId,
                           @Param("deviceId") String deviceId, @Param("timeWork") long timeWork,
                           @Param("timeStamp")Timestamp timeStamp, @Param("siteNo") String siteNo);
    void createNacelleWorkTimeLog(@Param("projectId") String projectId,
                           @Param("deviceId") String deviceId, @Param("timeWork") long timeWork,
                           @Param("timeStamp")Timestamp timeStamp, @Param("siteNo") String siteNo);
    List<WorkTimeLog> getWorkerTime(@Param("userId") String userId);
}
