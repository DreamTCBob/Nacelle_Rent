package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.RealTimeData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface ElectricBoxMapper {
    RealTimeData getRealTimeDataById(String deviceId);
    void deleteRealTimeDateById(String deviceId, String type);
    boolean deleteElectricBoxConfigByHoist(@Param("deviceId") String deviceId);
    boolean deleteElectricBoxConfigByCamera(@Param("deviceId") String deviceId);
    boolean deleteElectricBoxConfigBySafeLock(@Param("deviceId") String deviceId);
    boolean updateElectricBoxConfigByHoist(@Param("deviceId") String deviceId,@Param("hoistId") String hoistId);
    boolean updateElectricBoxConfigByCamera(@Param("deviceId") String deviceId,@Param("cameraId") String cameraId);
    boolean updateElectricBoxConfigBySafeLock(@Param("deviceId") String deviceId,@Param("safeLockId") String safeLockId);

}
