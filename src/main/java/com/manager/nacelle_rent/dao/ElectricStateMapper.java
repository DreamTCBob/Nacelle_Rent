package com.manager.nacelle_rent.dao;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.entity.ElectricBoxState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ElectricStateMapper {
    List<ElectricBoxState> getStoreList(int flag);
    List<ElectricBoxState> getAllDevice();
    void updateStateIn(@Param("deviceId") String deviceId, @Param("storageState") int storageState, @Param("storeIn") String storeIn);
    void updateStateOut(@Param("deviceId") String deviceId, @Param("storageState") int storageState);
    void updateProject(@Param("deviceId") String deviceId, @Param("projectId") String projectId);
    void updateWorkState(@Param("deviceId") String deviceId, @Param("workingState") int workingState);
    void createWorkBox(@Param("deviceId") String deviceId, @Param("projectId") String projectId);
    void deleteWorkBox(@Param("deviceId") String deviceId);
    int sum(@Param("storageState") int storageState);///获取不同状态的吊篮总数
    int sumOfElectricBox();
    ElectricBoxState getBoxLog(@Param("deviceId") String deviceId);
}
