package com.manager.nacelle_rent.dao;

import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.entity.ProjectSupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectMapper {
    int sumOfProject();
    int getSumOfAlarmInfo(@Param("alarmType") Integer alarmType, @Param("startTime") String startTime,
                          @Param("endTime") String endTime, @Param("projectId") String projectId,
                          @Param("deviceId") String deviceId);
    Integer getWorkTimeOneDay(@Param("date") String date, @Param("userId") String userId, @Param("projectId") String projectId);
    Integer getWorkTimeOneWorker(@Param("date") String date, @Param("projectId") String projectId,
                                 @Param("deviceId") String deviceId, @Param("siteNo") String siteNo);
    Integer getNacelleWorkTimeOnDay(@Param("date") String date, @Param("deviceId") String deviceId,
                                    @Param("siteNo") String siteNo, @Param("projectId") String projectId);
    Integer getWorkTimeAll(@Param("startTime") String startTime, @Param("endTime") String endTime,
                           @Param("userId") String userId, @Param("projectId") String projectId);
    Integer getNacelleWorkTimeAll(@Param("startTime") String startTime, @Param("endTime") String endTime,
                           @Param("userId") String deviceId, @Param("siteNo") String siteNo, @Param("projectId") String projectId);
    Integer getDeviceWorkTime(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                  @Param("userId") String deviceId, @Param("projectId") String projectId);
    int getConfigurationList(@Param("projectId") String projectId);
    int getAlarmCountAllByDeviceId(@Param("deviceId") String deviceId);
    int getAlarmCountMonthByDeviceId(@Param("deviceId") String deviceId, @Param("startTime") String startTime, @Param("endTime") String endTime);
    List<Project> getProjectList(int flag);
    List<Project> getProjectListByRegion(@Param("regionKey") String regionKey, @Param("pageNum") int pageNum);
    List<Project> getProjectListByRegionAll(@Param("regionKey") String regionKey);
    List<Project> getProjectListByPage(@Param("pageNum") int pageNum);
    List<Project> getProjectListAll();
    List<Project> getAllProjectByAdmin(String adminAreaId);
    List<ProjectSupInfo> getProjectSupInfo(@Param("projectId") String projectId);
    List<Map<String, Object>> getAlarmInfo(@Param("alarmType") Integer alarmType, @Param("startTime") String startTime,
                                           @Param("endTime") String endTime, @Param("projectId") String projectId,
                                           @Param("deviceId") String deviceId, @Param("pageSize") Integer pageSize, @Param("offset") Integer offset);
    List<Map<String, Object>> getElectricBoxStopByProjectId(@Param("projectId") String projectId);
    List<Map<String, Object>> getPlaneGraphInfo(@Param("projectId") String projectId, @Param("buildingNum") String buildingNum);
    List<Map<String, Object>> getProjectPlaneGraphInfo(@Param("projectId") String projectId);
    List<Map<String, Object>> getPlaneGraphAB(@Param("projectId") String projectId, @Param("deviceId") String deviceId,
                                              @Param("buildingId") String buildingId, @Param("locationId") String locationId);
    List<Map<String, Object>> getProjectPlaneGraphOne(@Param("projectId") String projectId ,
                                              @Param("buildingId") String buildingId);
    List<Map<String, Object>> getWorkerOneDayInfo(@Param("date") String date, @Param("userId") String userId, @Param("projectId") String projectId);
    List<Map<String, Object>> getNacelleWorkOneDayInfo(@Param("date") String date, @Param("deviceId") String deviceId,
                                                       @Param("siteNo") String siteNo, @Param("projectId") String projectId);
    List<Map<String, Object>> getNacelleWorkerDetail(@Param("projectId") String projectId, @Param("deviceId") String deviceId, @Param("siteNo") String siteNo,
                                                        @Param("startTime") String startTime, @Param("endTime") String endTime);
    List<Map<String, Object>> getWorkerWorkInfo(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                @Param("userId") String userId, @Param("projectId") String projectId);
    List<Map<String, Object>> getNacelleWorkLogAll(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                @Param("deviceId") String deviceId, @Param("siteNo") String siteNo, @Param("projectId") String projectId);
    List<String> getProjectByVague(@Param("subString") String subString);
    List<String> getDevice(@Param("subString") String subString);
    List<String> getSiteNoList(@Param("deviceId") String deviceId, @Param("projectId") String projectId);
    List<String> getDeviceWorkList(@Param("projectId") String projectId, @Param("startTime") String startTime, @Param("endTime") String endTime);
    Project getProjectDetail(String projectId);
    String getProjectName(String projectId);
    String checkProjectId(String projectId);
    String searchCompany(@Param("projectId") String projectId);
//    List<Project> getProjectId(String userId);
    Project getProjectIdByAdmin(String userId);
    List<String> judgeDeviceLocationId(@Param("projectId") String projectId, @Param("buildingId") String buildingId, @Param("locationId") String locationId);
    List<String> getWorkersFromWorkLog(@Param("projectId") String projectId, @Param("startTime") String startTime, @Param("endTime") String endTime);
    void updateDeviceLocationId(@Param("projectId") String projectId, @Param("buildingId") String buildingId, @Param("locationId") String locationId, @Param("deviceId") String deviceId);
    boolean increaseBox(@Param("projectId") String projectId);
    boolean decreaseBox(@Param("projectId") String projectId);
    boolean increaseWorker(@Param("projectId") String projectId);
    boolean decreaseWorker(@Param("projectId") String projectId);
    boolean insertInstallInfo(@Param("userId") String userId, @Param("projectId") String projectId, @Param("deviceId") String deviceId);
    boolean editProjectDepartment(@Param("projectId") String projectId,@Param("adminAreaId") String adminAreaId,@Param("adminProjectId") String adminProjectId);
    boolean uploadPlaneGraphInfo(@Param("projectId") String projectId, @Param("buildingId") String buildingId, @Param("deviceId") String deviceId,
                                 @Param("locationId") String locationId, @Param("location") String location);
    boolean updatePlaneGraphInfo(@Param("projectId") String projectId, @Param("buildingId") String buildingId, @Param("deviceId") String deviceId,
                                 @Param("locationId") String locationId, @Param("location") String location);
    boolean uploadProjectPlaneGraphInfo(@Param("projectId") String projectId, @Param("buildingId") String buildingId, @Param("location") String location);
    void createProject(@Param("projectId") String projectId,@Param("projectName") String projectName,@Param("projectStart") String projectStart,
                       @Param("projectContractUrl") String projectContractUrl, @Param("adminAreaId") String adminAreaId, @Param("adminRentId") String adminRentId);
    void updateProject(@Param("projectId") String projectId, @Param("projectName") String projectName, @Param("projectStart") String projectStart,
                       @Param("projectContractUrl") String projectContractUrl, @Param("adminAreaId") String adminAreaId,@Param("projectState") int projectState,@Param("projectBuilders") String projectBuilders);
    void installApply(@Param("projectId") String projectId, @Param("projectCertUrl") String projectCertUrl, @Param("storeOut") String storeOut);
    void beginProject(@Param("projectId") String projectId, @Param("projectCertUrl") String projectCertUrl);
    void updateState(@Param("projectId") String projectId, @Param("projectState") int projectState);
    void updateKeyWord(@Param("projectId") String projectId, @Param("owner") String owner, @Param("region") String region,
                       @Param("coordinate") String coordinate, @Param("servicePeriod") String servicePeriod, @Param("regionManager") String regionManager,
                       @Param("marketSalesman") String marketSalesman, @Param("perfectState") int perfectState);
    void updateProjectEnd(@Param("projectId") String projectId, @Param("projectEndUrl") String projectEndUrl);
    void createCompany(@Param("companyName") String companyName, @Param("projectId") String projectId);
    void createConfigurationList(@Param("projectId") String projectId, @Param("sixMetersNum") int sixMetersNum);
    void createElectricBoxStopInfo(@Param("projectId") String projectId, @Param("managerId") String managerId, @Param("number") int number);
    void createElectricBoxStop(@Param("projectId") String projectId, @Param("deviceId") String deviceId);
    void deleteProject(String[] multipleProjectId);
    void createProjectSupInfo(ProjectSupInfo projectSupInfo);
    void deleteProjectPlaneGraphOne(@Param("projectId") String projectId ,
                                    @Param("buildingId") String buildingId);
    void deletePlaneGraphByBuildingInfo(@Param("projectId") String projectId, @Param("buildingId") String buildingId);
    void deletePlaneGraphOne(@Param("projectId") String projectId, @Param("deviceId") String deviceId);
    void updatePerfectState(@Param("projectId") String projectId, @Param("perfectState") int perfectState);
    void updatePlaneState(@Param("projectId") String projectId, @Param("planeState") int planeState);
    void updateProjectContractUrl(@Param("projectId") String projectId, @Param("projectContractUrl") String projectContractUrl);
    void uploadInstallCheck(@Param("projectId") String projectId, @Param("deviceId") String deviceId,
                            @Param("managerId") String managerId, @Param("dealerId") String dealerId,
                            @Param("description") String description, @Param("record") String record);
}