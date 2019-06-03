package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.RepairBoxInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RepairInfoMapper {
    void createRepairBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("managerId") String managerId,
                         @Param("reason") String reason, @Param("image") String image);
    void createRepairEndBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId, @Param("managerId") String managerId,
                         @Param("reason") String reason, @Param("image") String image);
    RepairBoxInfo getRepairInfo(@Param("deviceId") String deviceId);
}
