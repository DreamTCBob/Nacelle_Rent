package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.PreStop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface PreStopMapper {
    void createPreStop(@Param("date") Timestamp date, @Param("days") int days, @Param("num") int num, @Param("projectId") String projectId);
    void deleteOutDate(@Param("dateNow") Timestamp dateNow);
    int sum(@Param("dateAfter") Timestamp dateAfter, @Param("dateNow") Timestamp dateNow);///获取不同时间段内可使用的吊篮总数
    int all();
    List<PreStop> getPreStopInfo(@Param("dateAfter") Timestamp dateAfter, @Param("dateNow") Timestamp dateNow);
}
