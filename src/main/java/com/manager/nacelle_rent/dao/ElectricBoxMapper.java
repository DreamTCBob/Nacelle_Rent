package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.RealTimeData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElectricBoxMapper {
    RealTimeData getRealTimeDataById(String deviceId);
    void deleteRealTimeDateById(String deviceId);
}
