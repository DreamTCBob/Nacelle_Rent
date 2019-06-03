package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.SetUpData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SetUpDataMapper {
    void updateSetUpData(SetUpData setUpData);
    void updateState(@Param("device_id") String device_id, @Param("state") int state);
    void deleteSetUpData(String device_id);
    SetUpData getSetUpData(String device_id);
    int getState(String device_id);
}
