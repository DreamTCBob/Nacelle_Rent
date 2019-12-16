package com.manager.nacelle_rent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectWorkerMapper {
    boolean increaseWorker(@Param("projectId") String projectId, @Param("userId") String userId);
    boolean deleteWorkerByUserId(@Param("userId") String userId);
    boolean deleteWorkerByProjectId(@Param("projectId") String projectId);
    List<String> getWorkerList(@Param("projectId") String projectId);
    List<String> getWorker(@Param("userId") String userId);
}