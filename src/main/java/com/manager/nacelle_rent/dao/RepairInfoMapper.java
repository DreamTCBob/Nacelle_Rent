package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.RepairBoxInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RepairInfoMapper {
    void createRepairBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("managerId") String managerId,
                         @Param("reason") String reason, @Param("imageStart") String imageStart, @Param("startTime") Timestamp startTime);
    void createRepairEndBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("dealerId") String dealerId,
                         @Param("description") String description, @Param("imageEnd") String imageEnd, @Param("endTime") Timestamp endTime);
    void deleteRepairBox(@Param("deviceId") String deviceId);


    List<RepairBoxInfo> getRepairInfo(@Param("projectId") String projectId);

    List<RepairBoxInfo> getRepairEndInfoOne(@Param("deviceId") String deviceId);

    List<RepairBoxInfo> getRepairInfoOne(@Param("deviceId") String deviceId);

    int getRepairBoxNum(@Param("projectId") String projectId);
}
