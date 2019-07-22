package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {
    List<Project> getProjectList(int flag);
    List<Project> getProjectListAll();
    List<Project> getAllProjectByAdmin(String adminAreaId);
    Project getProjectDetail(String projectId);
    String checkProjectId(String projectId);
    String searchCompany(@Param("projectId") String projectId);
    List<Project> getProjectId(String userId);
    Project getProjectIdByStore(String storeId);
    Project checkStorage(String storageId);
    Project getProjectIdByAdmin(String userId);
    boolean increaseBox(@Param("projectId") String projectId, @Param("boxList") String boxList);
    boolean increaseWorker(@Param("projectId") String projectId,@Param("userList") String userList);
    boolean editProjectDepartment(@Param("projectId") String projectId,@Param("adminAreaId") String adminAreaId,@Param("adminProjectId") String adminProjectId);
    void createProject(@Param("projectId") String projectId,@Param("projectName") String projectName,@Param("projectStart") String projectStart,
                       @Param("projectContractUrl") String projectContractUrl, @Param("adminAreaId") String adminAreaId, @Param("adminRentId") String adminRentId);
    void updateProject(@Param("projectId") String projectId, @Param("projectName") String projectName, @Param("projectStart") String projectStart,
                       @Param("projectContractUrl") String projectContractUrl, @Param("adminAreaId") String adminAreaId,@Param("projectState") int projectState);
    void installApply(@Param("projectId") String projectId, @Param("projectCertUrl") String projectCertUrl, @Param("storeOut") String storeOut);
    void beginProject(@Param("projectId") String projectId, @Param("projectCertUrl") String projectCertUrl);
    void updateState(@Param("projectId") String projectId, @Param("projectState") int projectState);
    void updateProjectEnd(@Param("projectId") String projectId, @Param("projectEndUrl") String projectEndUrl);
    void createCompany(@Param("companyName") String companyName, @Param("projectId") String projectId);
    void createConfigurationList(@Param("projectId") String projectId, @Param("sixMetersNum") int sixMetersNum);
    int getConfigurationList(@Param("projectId") String projectId);
    void deleteProject(String[] multipleProjectId);
}