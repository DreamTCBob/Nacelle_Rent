package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.ExceptionBoxInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

@Mapper
public interface ExceptionInfoMapper {
    void createExceptionBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("managerId") String managerId, @Param("reason") String reason);
}
