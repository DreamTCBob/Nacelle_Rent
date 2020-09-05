package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.*;
import com.manager.nacelle_rent.entity.*;
import com.manager.nacelle_rent.enums.ElectricBoxStateEnum;
import com.manager.nacelle_rent.enums.ProjectStateEnum;
import com.manager.nacelle_rent.service.ProjectService;
import com.manager.nacelle_rent.service.RedisService;
import com.manager.nacelle_rent.service.UserService;
import com.manager.nacelle_rent.service.WorkTimeLogService;
import com.manager.nacelle_rent.utils.DateUtil;
import com.manager.nacelle_rent.utils.RedisUtil;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectDeviceMapper projectDeviceMapper;
    @Autowired
    private ProjectWorkerMapper projectWorkerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ElectricResMapper electricResMapper;
    @Autowired
    private PreStopMapper preStopMapper;
    @Autowired
    private ElectricStateMapper electricStateMapper;
    @Autowired
    private WorkTimeLogService workTimeLogService;
    @Autowired
    private ElectricBoxMapper electricBoxMapper;
    @Autowired
    private SetUpDataMapper setUpDataMapper;
    @Autowired
    private ExceptionInfoMapper exceptionInfoMapper;
    @Autowired
    private RepairInfoMapper repairInfoMapper;
    @Autowired
    private ConfigurationList configurationList;
    @Autowired
    private RedisService redisService;

    @Value("${FTPFileURL}")
    private String FTPFileURL;
    @Value("${FTPUserName}")
    private String FTPUserName;
    @Value("${FTPUserPassword}")
    private String FTPUserPassword;
    @Value("${FTPPlaneGraph}")
    private String FTPPlaneGraph;
    @Value("${APP_SECRET_KEY}")
    private String APP_SECRET_KEY;
    @Value("${MY_PACKAGE_NAME}")
    private String MY_PACKAGE_NAME;

    private String[] projectStateList = {"草稿","待成立项目部","吊篮安装验收","进行中","已结束","安监证书验收","清单待配置","清单待审核"};
    private String projectId;

    @Override
    public List<JSONObject> getProjectList(int flag){
        List<Project> projectList = projectMapper.getProjectList(flag);
        List<JSONObject> jsonObjects = new ArrayList<>();
        for(int i = 0; i < projectList.size();i++){
            JSONObject jsonObject = new JSONObject();
            Project project = projectList.get(i);
            String boxList = getBasketList(project.getProjectId());
            project.setBoxList(boxList);
            project.setProjectState(projectStateList[Integer.parseInt(project.getProjectState())]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
            project.setCompanyName(projectMapper.searchCompany(project.getProjectId()));
            jsonObject.put("projectDetail", project);
            ProjectSupInfo projectSupInfo = projectMapper.getProjectSupInfo(project.getProjectId()).get(0);
            jsonObject.put("projectSupInfo", projectSupInfo);
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    @Override
    public List<Project> getProjectByVague(String subString){
        List<String> projectIdList = projectMapper.getProjectByVague(subString);
        List<Project> projectList = new ArrayList<>();
        for(String projectId : projectIdList){
            Project project;
            if(projectId.equals("")){
                project = null;
            }else {
                if(redisService.getProject(projectId) != null){
                    project = redisService.getProject(projectId);
                }else {
                    project = projectMapper.getProjectDetail(projectId);
                    if(project != null){
                        redisService.setProject(project);
                    }
                }
                String companyName = projectMapper.searchCompany(projectId);
                if(project != null)
                    project.setCompanyName(companyName);
                projectList.add(project);
            }
        }
        return projectList;
    }

    @Override
    public List<Project> getProjectListByKey(String keyWord, int flag, int pageNum){
        if(!keyWord.equals("") && flag != 0){
            List<Project> projectList;
            switch (flag){
                case 1://////按照地区检索项目列表
                {
                    int pageNumM = (pageNum - 1) * 5;
                    projectList = projectMapper.getProjectListByRegion(keyWord, pageNumM);
                    break;
                }
                case 11://////按地区检索全部
                {
                    projectList = projectMapper.getProjectListByRegionAll(keyWord);
                    break;
                }
                case 2://////分页查询
                {
                    int pageNumM = (pageNum - 1) * 5;
                    projectList = projectMapper.getProjectListByPage(pageNumM);
                    break;
                }
                case 30:///////某地区已结束的项目
                {
                    List<Project> projectListByRegionAll = projectMapper.getProjectListByRegionAll(keyWord);
                    List<Project> rightProjectList = judgeProjectState(projectListByRegionAll,0);
                    int startNum = (pageNum - 1) * 5;
                    projectList = new ArrayList<>();
                    for(int i = startNum ; i < (startNum + 5) ; i++){
                        if(i >= rightProjectList.size()) continue;
                        projectList.add(rightProjectList.get(i));
                    }
                    break;
                }
                case 31:///////某地区安装中的项目
                {
                    List<Project> projectListByRegionAll = projectMapper.getProjectListByRegionAll(keyWord);
                    List<Project> rightProjectList = judgeProjectState(projectListByRegionAll,1);
                    int startNum = (pageNum - 1) * 5;
                    projectList = new ArrayList<>();
                    for(int i = startNum ; i < (startNum + 5) ; i++){
                        if(i >= rightProjectList.size()) continue;
                        projectList.add(rightProjectList.get(i));
                    }
                    break;
                }
                case 32:///////某地区使用中的项目
                {
                    List<Project> projectListByRegionAll = projectMapper.getProjectListByRegionAll(keyWord);
                    List<Project> rightProjectList = judgeProjectState(projectListByRegionAll,2);
                    int startNum = (pageNum - 1) * 5;
                    projectList = new ArrayList<>();
                    for(int i = startNum ; i < (startNum + 5) ; i++){
                        if(i >= rightProjectList.size()) continue;
                        projectList.add(rightProjectList.get(i));
                    }
                    break;
                }
                default: {
                    projectList = null;
                }
            }
            return projectList;
        }else return null;
    }
    private List<Project> judgeProjectState(List<Project> projectList, int state){
        List<Project> projects = new ArrayList<>();
        switch (state){
            case 0:///已结束
            {
                for(Project project : projectList){
                    List<String> endDeviceList = projectDeviceMapper.getEndDeviceList(project.getProjectId());
                    if(endDeviceList.size() != 0) projects.add(project);
                }
                break;
            }
            case 1:///安装中
            {
                for(Project project : projectList){
                    List<String> deviceList = projectDeviceMapper.getDeviceList(project.getProjectId());
                    for(String deviceId : deviceList){
                        if(deviceId.equals("A") || deviceId.equals("B")) continue;
                        int deviceState = electricStateMapper.getBoxLog(deviceId).getStorageState();
                        if(deviceState == ElectricBoxStateEnum.getCode("待安装").getCode() || deviceState == ElectricBoxStateEnum.getCode("正在安装").getCode() || deviceState == ElectricBoxStateEnum.getCode("安装审核中").getCode())
                            projects.add(project);
                    }
                }
                break;
            }
            case 2:///使用中
            {
                for (Project project : projectList) {
                    List<String> deviceList = projectDeviceMapper.getDeviceList(project.getProjectId());
                    for (String deviceId : deviceList) {
                        if (deviceId.equals("A") || deviceId.equals("B")) continue;
                        int deviceState = electricStateMapper.getBoxLog(deviceId).getStorageState();
                        if (deviceState == ElectricBoxStateEnum.getCode("待上传安监证书").getCode()
                                || deviceState == ElectricBoxStateEnum.getCode("使用中").getCode()
                                || deviceState == ElectricBoxStateEnum.getCode("待报停").getCode()
                                || deviceState == ElectricBoxStateEnum.getCode("报停审核").getCode())
                            projects.add(project);
                    }
                }
                break;
            }
            default: projects = null;
        }
        if (projects == null)
            return null;
        else{
            Set<Project> set = new HashSet<>();
            List<Project> list = new ArrayList<>();
            for (Project element : projects) {
                if (set.add(element)) {
                    list.add(element);
                }
            }
            projects.clear();
            projects.addAll(list);
            return projects;
        }
    }
    @Override
    public List<JSONObject> getProjectListAll(){
        List<Project> projectList = projectMapper.getProjectListAll();
        List<JSONObject> list = new ArrayList<>();
        for(int i = 0; i < projectList.size();i++){
            if(!projectList.get(i).getProjectState().equals("0")) {
                JSONObject jsonObject = new JSONObject();
                Project project = projectList.get(i);
//                User user = userMapper.getUserById(project.getAdminAreaId());
                User user;
                if(redisService.getUser(project.getAdminAreaId()) != null){
                    user = redisService.getUser(project.getAdminAreaId());
                }else {
                    user = userMapper.getUserInfo(project.getAdminAreaId());
                    if(user != null){
                        redisService.setUser(user);
                    }
                }
                User user1;
                if(redisService.getUser(project.getAdminRentId()) != null){
                    user1 = redisService.getUser(project.getAdminRentId());
                }else {
                    user1 = userMapper.getUserInfo(project.getAdminRentId());
                    if(user1 != null){
                        redisService.setUser(user1);
                    }
                }
                User user2;
                if(redisService.getUser(project.getAdminProjectId()) != null){
                    user2 = redisService.getUser(project.getAdminProjectId());
                }else {
                    user2 = userMapper.getUserInfo(project.getAdminProjectId());
                    if(user2 != null){
                        redisService.setUser(user2);
                    }
                }
                String[] boxList = getBasketList(project.getProjectId()).split(",");
                String[] worker = getWorkerList(project.getProjectId()).equals("") ? null : getWorkerList(project.getProjectId()).split(",");
                switch (projectList.get(i).getProjectState()){
                    case "21":
                        project.setProjectState(projectStateList[5]);
                        break;
                    case "11":
                        project.setProjectState(projectStateList[6]);
                        break;
                    case "12":
                        project.setProjectState(projectStateList[7]);
                        break;
                    default:
                        project.setProjectState(projectStateList[Integer.parseInt(project.getProjectState())]);
                }
                if (project.getProjectEnd() == null) {
                    project.setProjectEnd("—");
                }
                jsonObject.put("projectId",project.getProjectId());
                jsonObject.put("projectName",project.getProjectName());
                if(boxList == null) jsonObject.put("boxList","");
                else jsonObject.put("boxList",boxList);
                if(worker == null) jsonObject.put("worker","");
                else jsonObject.put("worker",worker);
                jsonObject.put("adminAreaId",project.getAdminAreaId());
                jsonObject.put("adminRentId",project.getAdminRentId());
                jsonObject.put("adminProjectId",project.getAdminProjectId());
                if(user == null) jsonObject.put("adminAreaUser","");
                else jsonObject.put("adminAreaUser",user);
                if(user1 == null) jsonObject.put("adminRentUser","");
                else jsonObject.put("adminRentUser",user1);
                if(user2 == null) jsonObject.put("adminProjectUser","");
                else jsonObject.put("adminProjectUser",user2);
                jsonObject.put("projectBuilders",project.getProjectBuilders());
                jsonObject.put("projectCertUrl",project.getProjectCertUrl());
                jsonObject.put("projectContractUrl",project.getProjectContractUrl());
                jsonObject.put("projectEnd",project.getProjectEnd());
                jsonObject.put("projectEndUrl",project.getProjectEndUrl());
                jsonObject.put("projectStart",project.getProjectStart());
                jsonObject.put("projectState",project.getProjectState());
                jsonObject.put("storeOut",project.getStoreOut());
                jsonObject.put("coordinate",project.getCoordinate());
                jsonObject.put("regionManager",project.getRegionManager());
                jsonObject.put("marketSalesman", project.getMarketSalesman());
                jsonObject.put("perfectState", project.getPerfectState());
                jsonObject.put("planeState", project.getPlaneState());
                jsonObject.put("companyName", projectMapper.searchCompany(project.getProjectId()));
                ProjectSupInfo projectSupInfo = projectMapper.getProjectSupInfo(project.getProjectId()).get(0);
                jsonObject.put("projectSupInfo", projectSupInfo);
                list.add(jsonObject);
            }
        }
        return list;
    }

    @Override
    public List<JSONObject> getProjectList2(int flag){
        List<JSONObject> returnList = new ArrayList<>();
        List<Project> projectList = projectMapper.getProjectList(ProjectStateEnum.getCode("吊篮安装验收").getCode());
        List<Project> projectList1 = projectMapper.getProjectList(ProjectStateEnum.getCode("安监证书验收").getCode());
        for(int i = 0; i < projectList.size();i++){
            JSONObject jsonObject = new JSONObject();
            Project project = projectList.get(i);
            String boxList = getBasketList(project.getProjectId());
            project.setBoxList(boxList);
            project.setProjectState(projectStateList[Integer.parseInt(project.getProjectState())]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
            String device = project.getBoxList() == null?"":project.getBoxList();
            String[] deviceList = device.split(",");
            for(String deviceId : deviceList){
                if (deviceId == null || deviceId.equals("")) break;
                if(electricStateMapper.getBoxLog(deviceId).getStorageState() != 2)
                    device = device.replace(deviceId + ",","");
            }
            String[] deviceList1 = device.split(",");
            String image = project.getStoreOut() == null?"":project.getStoreOut();
            String[] imageList = image.split(",");
            jsonObject.put("projectId",project.getProjectId());
            jsonObject.put("projectName",project.getProjectName());
            jsonObject.put("adminAreaName",userMapper.getUserById(project.getAdminAreaId()).getUserName());
            jsonObject.put("adminAreaId",project.getAdminAreaId());
            jsonObject.put("projectState",project.getProjectState());
            jsonObject.put("type", 1);
            jsonObject.put("deviceList",deviceList1);
            jsonObject.put("imageList", imageList);
            jsonObject.put("certificate",project.getProjectCertUrl().split(","));
            jsonObject.put("uploadTime",project.getProjectStart());
            jsonObject.put("coordinate",project.getCoordinate());
            jsonObject.put("regionManager",project.getRegionManager());
            jsonObject.put("marketSalesman", project.getMarketSalesman());
            jsonObject.put("projectBuilders", project.getProjectBuilders());
            jsonObject.put("companyName", projectMapper.searchCompany(project.getProjectId()));
            jsonObject.put("perfectState", project.getPerfectState());
            jsonObject.put("planeState", project.getPlaneState());
            ProjectSupInfo projectSupInfo = projectMapper.getProjectSupInfo(project.getProjectId()).get(0);
            jsonObject.put("projectSupInfo", projectSupInfo);
            returnList.add(jsonObject);
        }
        for(int i = 0; i < projectList1.size();i++){
            JSONObject jsonObject = new JSONObject();
            Project project = projectList1.get(i);
            String boxList = getBasketList(project.getProjectId());
            project.setBoxList(boxList);
            project.setProjectState(projectStateList[5]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
            String device = project.getBoxList() == null?"":project.getBoxList();
            String[] deviceList = device.split(",");
            for(String deviceId : deviceList){
                if(electricStateMapper.getBoxLog(deviceId).getStorageState() != 21)
                    device = device.replace(deviceId + ",","");
            }
            String[] deviceList1 = device.split(",");
            String image = project.getStoreOut() == null?"":project.getStoreOut();
            String[] imageList = image.split(",");
            jsonObject.put("projectId",project.getProjectId());
            jsonObject.put("projectName",project.getProjectName());
            jsonObject.put("adminAreaName",userMapper.getUserById(project.getAdminAreaId()).getUserName());
            jsonObject.put("adminAreaId",project.getAdminAreaId());
            jsonObject.put("projectState",project.getProjectState());
            jsonObject.put("type", 1);
            jsonObject.put("deviceList",deviceList1);
            jsonObject.put("imageList", imageList);
            jsonObject.put("certificate",project.getProjectCertUrl().split(","));
            jsonObject.put("uploadTime",project.getProjectStart());
            jsonObject.put("coordinate",project.getCoordinate());
            jsonObject.put("regionManager",project.getRegionManager());
            jsonObject.put("marketSalesman", project.getMarketSalesman());
            jsonObject.put("projectBuilders", project.getProjectBuilders());
            jsonObject.put("perfectState", project.getPerfectState());
            jsonObject.put("planeState", project.getPlaneState());
            jsonObject.put("companyName", projectMapper.searchCompany(project.getProjectId()));
            ProjectSupInfo projectSupInfo = projectMapper.getProjectSupInfo(project.getProjectId()).get(0);
            jsonObject.put("projectSupInfo", projectSupInfo);
            returnList.add(jsonObject);
        }
        return returnList;

    }

    @Override
    public List<JSONObject> getStoreList(int flag){
        List<JSONObject> returnList = new ArrayList<>();
        List<ElectricBoxState> list;
        if(flag == 0) list = electricStateMapper.getAllDevice();
        else list = electricStateMapper.getStoreList(flag);
        for(int i = 0; i < list.size();i++){
            try {
                JSONObject jsonObject = new JSONObject();
                ElectricBoxState electricBoxState = list.get(i);
                if (electricBoxState.getStoreIn() == null) {
                    electricBoxState.setStoreIn("");
                }
                String projectId = projectDeviceMapper.getDevice(electricBoxState.getDeviceId()).size() == 0 ? "" :
                        projectDeviceMapper.getDevice(electricBoxState.getDeviceId()).get(0);
                Project project;
                if(projectId.equals("")){
                    project = null;
                }else {
                    if(redisService.getProject(projectId) != null){
                        project = redisService.getProject(projectId);
                    }else {
                        project = projectMapper.getProjectDetail(projectId);
                        if(project != null){
                            redisService.setProject(project);
                        }
                    }
                }
                double longitude = electricBoxMapper.getRealTimeDataById(electricBoxState.getDeviceId()) == null ? 0 :
                        electricBoxMapper.getRealTimeDataById(electricBoxState.getDeviceId()).getLongitude();
                double latitude = electricBoxMapper.getRealTimeDataById(electricBoxState.getDeviceId()) == null ? 0 :
                        electricBoxMapper.getRealTimeDataById(electricBoxState.getDeviceId()).getLatitude();
                String projectName = project == null ? "" : project.getProjectName();
                String userId = project == null ? "" : project.getAdminAreaId();
                String userName = userMapper.getUserById(userId) == null ? "" : userMapper.getUserById(userId).getUserName();
                String res;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                res = simpleDateFormat.format(electricBoxState.getDate());
                jsonObject.put("longitude",longitude);
                jsonObject.put("latitude",latitude);
                jsonObject.put("projectId", projectId);
                jsonObject.put("projectName", projectName);
                jsonObject.put("userId", userId);
                jsonObject.put("userName", userName);
                jsonObject.put("deviceId", electricBoxState.getDeviceId());
                jsonObject.put("uploadTime", res);
                jsonObject.put("alarm",electricBoxState.getAlarm());
                jsonObject.put("verificationImage", electricBoxState.getStoreIn().split(",")[0]);
                jsonObject.put("electricBoxState",electricBoxState.getStorageState());
                jsonObject.put("workingState",electricBoxState.getWorkingState());
                jsonObject.put("cameraId", userMapper.getAllParts(electricBoxState.getDeviceId()) == null ? "" : userMapper.getAllParts(electricBoxState.getDeviceId()).get("camera_id"));
                returnList.add(jsonObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return returnList;

    }

    @Override
    public JSONObject getAllProjectByAdmin(String userId){
        List<Project> projectList = projectMapper.getAllProjectByAdmin(userId);
        JSONObject jsonObject = new JSONObject();
        List<Project> installingProject = new ArrayList<>();
        List<Project> operatingProject = new ArrayList<>();
        List<Project> endProject = new ArrayList<>();
        for(Project project : projectList){
            String companyName = projectMapper.searchCompany(project.getProjectId()) == null ? "" : projectMapper.searchCompany(project.getProjectId());
            project.setCompanyName(companyName);
            List<String> deviceList = projectDeviceMapper.getDeviceList(project.getProjectId());
            for(String deviceId : deviceList){
                if(deviceId.equals("A") || deviceId.equals("B")) continue;
                int deviceState = electricStateMapper.getBoxLog(deviceId).getStorageState();
                if(deviceState == ElectricBoxStateEnum.getCode("待安装").getCode() || deviceState == ElectricBoxStateEnum.getCode("正在安装").getCode() || deviceState == ElectricBoxStateEnum.getCode("安装审核中").getCode())
                    installingProject.add(project);
                if(deviceState == ElectricBoxStateEnum.getCode("待上传安监证书").getCode()
                        || deviceState == ElectricBoxStateEnum.getCode("使用中").getCode()
                        || deviceState == ElectricBoxStateEnum.getCode("待报停").getCode()
                                || deviceState == ElectricBoxStateEnum.getCode("报停审核").getCode())
                    operatingProject.add(project);
            }
//            List<String> endDeviceList = projectDeviceMapper.getEndDeviceList(project.getProjectId());
            if(project.getProjectState().charAt(0) == '1' || project.getProjectState().charAt(0) == '2') installingProject.add(project);
            if(Integer.parseInt(project.getProjectState()) == ProjectStateEnum.getCode("已结束").getCode()) endProject.add(project);
        }
        jsonObject.put("installingProjectList",new ArrayList<>(new HashSet<>(installingProject)));
        jsonObject.put("operatingProjectList",new ArrayList<>(new HashSet<>(operatingProject)));
        jsonObject.put("endProjectList",new ArrayList<>(new HashSet<>(endProject)));
        return jsonObject;
    }

    @Override
    public String getBasketList(String projectId){
        String basketList = "";
        List<String> device = projectDeviceMapper.getDeviceList(projectId);
        for(String deviceId : device){
            if(!deviceId.equals("A") && !deviceId.equals("B"))
                basketList += deviceId + ",";
        }
        return basketList;
    }

    @Override
    public String getWorkerList(String projectId){
        String workerList = "";
        List<String> worker = projectWorkerMapper.getWorkerList(projectId);
        for(String userId : worker){
            workerList += userId + ",";
        }
        return workerList;
    }

//    @Override
//    public String getProjectId(String userId){
//        List<Project> project = projectMapper.getProjectId(userId);
//        if(project.size()!=0)
//            return project.get(0).getProjectId();
//        else return "";
//    }

    @Override
    public String getProjectIdByAdmin(String userId){
        if(projectMapper.getProjectIdByAdmin(userId)!=null)
            return projectMapper.getProjectIdByAdmin(userId).getProjectId();
        else return "";
    }

    @Override
    public String pushConfigurationList(Map<String, String> list){
        int sixMetersNum = Integer.parseInt(list.get("sixMetersNum"));
        try {
            projectMapper.createConfigurationList(list.get("projectId"), sixMetersNum);
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","1");
        jsonObject.put("partsList",configurationList.configurationListToString(sixMetersNum));
        return jsonObject.toJSONString();
    }

    @Override
    public String handleConfigurationList(Map<String, String> list){
        String projectId = list.get("projectId");
        int sixMetersNum = Integer.parseInt(list.get("sixMetersNum"));
        try {
            projectMapper.createConfigurationList(projectId, sixMetersNum);
        }catch (Exception e){
            return "fail";
        }
        return list.toString();
    }

    @Override
    public String getConfigurationList(String projectId){
        try {
            JSONObject jsonObject = new JSONObject();
            int num = projectMapper.getConfigurationList(projectId);
            jsonObject.put("partsList",configurationList.configurationListToString(num));
            return jsonObject.toJSONString();
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @Override
    public List<User> getUserList(String projectId){
        String areaAdmin = null;
        String rentAdmin = null;
        String worker = null;
        Project project;
        if(projectId.equals("") || projectId == null){
            project = null;
        }else {
            if(redisService.getProject(projectId) != null){
                project = redisService.getProject(projectId);
            }else {
                project = projectMapper.getProjectDetail(projectId);
                if(project != null){
                    redisService.setProject(project);
                }
            }
        }
        if(project != null)
            areaAdmin = project.getAdminAreaId();
        if(project != null)
            rentAdmin = project.getAdminRentId();
        if(project != null)
            worker = getWorkerList(project.getProjectId());
        List<User> userList = new ArrayList<>();
        userList.add(userService.getUserInfo(areaAdmin));
        userList.add(userService.getUserInfo(rentAdmin));
        if(worker != null) {
            String[] splitWorker = worker.split(",");
            for (int i = 0; i < splitWorker.length; i++) {
                userList.add(userService.getUserInfo(splitWorker[i]));
            }
        }
        return userList;
    }
    @Override
    public JSONObject getProjectDetail(String projectId) throws Exception{
        // --AND project_info.admin_area_id = user_info.user_id
        JSONObject jsonObject = new JSONObject();
        /**
         * Redis缓存测试
         */
        ProjectSupInfo projectSupInfo = projectMapper.getProjectSupInfo(projectId).get(0);
        Project projectDetail;
//        if(redisService.getProject(projectId) != null){
//            projectDetail = redisService.getProject(projectId);
//        }else {
//            projectDetail = projectMapper.getProjectDetail(projectId);
//            if(projectDetail != null) {
//                redisService.setProject(projectDetail);
//            }
//        }
        projectDetail = projectMapper.getProjectDetail(projectId);
        if(projectDetail == null) return null;
        User adminRentUser = userMapper.getUserInfo(projectDetail.getAdminRentId());
        User adminAreaUser = userMapper.getUserInfo(projectDetail.getAdminAreaId());
        if(!projectDetail.getProjectStart().contains("-"))
            projectDetail.setProjectStart(DateUtil.timeToDate(projectDetail.getProjectStart()));
        projectDetail.setAdminRentUser(adminRentUser);
        projectDetail.setAdminAreaUser(adminAreaUser);
        projectDetail.setCompanyName(projectMapper.searchCompany(projectDetail.getProjectId()));
        String deviceList = getBasketList(projectId);
        projectDetail.setBoxList(deviceList);
        String worker = getWorkerList(projectId);
        projectDetail.setWorker(worker);
        if(projectDetail.getProjectState() == null) {
            projectDetail.setProjectState("0");
            redisService.setProject(projectDetail);
        }else redisService.setProject(projectDetail);
        switch (projectDetail.getProjectState()){
            case "21":
                projectDetail.setProjectState(projectStateList[5]);
                break;
            case "11":
                projectDetail.setProjectState(projectStateList[6]);
                break;
            case "12":
                projectDetail.setProjectState(projectStateList[7]);
                break;
            default:
                projectDetail.setProjectState(projectStateList[Integer.parseInt(projectDetail.getProjectState())]);
                break;
        }
//        if(projectDetail.getProjectState().equals("21"))
//            projectDetail.setProjectState(projectStateList[5]);
//        else if(projectDetail.getProjectState().equals("11"))
//            projectDetail.setProjectState(projectStateList[6]);
//        else if(projectDetail.getProjectState().equals("12"))
//            projectDetail.setProjectState(projectStateList[7]);
//        else
//            projectDetail.setProjectState(projectStateList[Integer.parseInt(projectDetail.getProjectState())]);
        if(projectDetail.getProjectEnd() == null){
            projectDetail.setProjectEnd("—");
        }
        User projectAdmin = userMapper.getUserInfo(projectDetail.getAdminProjectId());
        String projectAdminName = projectAdmin == null ? "" : projectAdmin.getUserName();
        List<ElectricRes> electricRes = electricResMapper.getElectricRes(projectId);
        jsonObject.put("projectAdminName", projectAdminName);
        jsonObject.put("projectDetail",projectDetail);
        jsonObject.put("electricRes",electricRes);
        jsonObject.put("projectSupInfo", projectSupInfo);
        return jsonObject;
    }
    @Override
    public List<JSONObject> getAlarmInfo(Integer alarmType, String startTime, String endTime, String projectId, String deviceId, int page){
        List<JSONObject> jsonObjects = new ArrayList<>();
        int pageNumM = (page - 1) * 10;
        List<Map<String, Object>> mapAll = projectMapper.getAlarmInfo(alarmType, startTime, endTime, projectId, deviceId, pageNumM);
        for (Map<String, Object> map : mapAll) {
            JSONObject jsonObject = new JSONObject();
            jsonObjects.add(new JSONObject(map));
        }
        return jsonObjects;
    }
    @Override
    public List<JSONObject> getElectricBoxStop(int type, String value){
        List<JSONObject> jsonObjects = new ArrayList<>();
        switch (type) {
            case 1:///按照项目号获取
                List<Map<String, Object>> maps = projectMapper.getElectricBoxStopByProjectId(value);
                for (Map<String, Object> map : maps) {
                    jsonObjects.add(new JSONObject(map));
                }
                break;
        }
        return jsonObjects;
    }
    @Override
    public List<JSONObject> getPlaneGraphInfo(String projectId, String buildingNum, int type) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        switch (type){
            case 1:
                List<Map<String, Object>> maps = projectMapper.getProjectPlaneGraphInfo(projectId);
                for (Map<String, Object> map : maps) {
                    JSONObject jsonObject = new JSONObject(map);
                    String buildingId = (String) map.get("building_id");
                    List<Map<String, Object>> maps1 = projectMapper.getPlaneGraphInfo(projectId, buildingId);
                    String deviceList = "";
                    for (Map<String, Object> map1 : maps1) {
                        String device = (String) map1.get("device_id");
                        if (!device.equals("A") && !device.equals("B")) {
                            deviceList += device + ",";
                        }
                    }
                    jsonObject.put("deviceList", deviceList);
                    jsonObjects.add(jsonObject);
                }
                break;
            case 2:
                List<Map<String, Object>> maps1 = projectMapper.getPlaneGraphInfo(projectId, buildingNum);
                for (Map<String, Object> map : maps1) {
                    JSONObject jsonObject = new JSONObject(map);
                    String deviceId = jsonObject.getString("device_id");
                    if (!deviceId.equals("A") && !deviceId.equals("B")) {
                        int projectState = projectMapper.getProjectDetail(projectId) == null ? 0 : Integer.parseInt(projectMapper.getProjectDetail(projectId).getProjectState());
                        int deviceState = electricStateMapper.getBoxLog(deviceId) == null ? 0 : electricStateMapper.getBoxLog(deviceId).getStorageState();
                        String siteNo = electricStateMapper.getSiteNo(deviceId);
                        if (siteNo == null || siteNo.equals("")) {
                            siteNo = "未录入";
                        }
                        jsonObject.put("site_no", siteNo);
                        jsonObject.put("project_state", projectState);
                        jsonObject.put("basket_state", deviceState);
                        jsonObject.put("basket_id", deviceId);
                    }
                    jsonObjects.add(jsonObject);
                }
                break;
        }
        return jsonObjects;
    }
    @Override
    public boolean checkProject(String projectId){
        try{
            String projectIdCheck = projectMapper.checkProjectId(projectId);
            return projectIdCheck.equals(projectId);
        }catch(Exception ex){
            return false;
        }
    }
    @Override
    public boolean editProjectDepartment(String projectId,String adminAreaId,String adminProjectId){
        try{
            if(userMapper.judgeProAdmin(adminProjectId) != null) return false;
            if(projectMapper.editProjectDepartment(projectId,adminAreaId,adminProjectId)) {
                userMapper.createProjectAdmin(projectId, adminProjectId);
                projectMapper.updateState(projectId, ProjectStateEnum.getCode("清单待配置").getCode());
                Project project = projectMapper.getProjectDetail(projectId);
                redisService.setProject(project);
                return true;
            }
            return false;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    @Override
    public String increaseBox(String projectId, String boxId){
//        try{projectDeviceMapper.getDevice(boxId);}catch (Exception e){
//            e.printStackTrace();
//        }
        boxId = boxId.replaceAll("\\\\s*|\\t|\\r|\\n","");
        if(projectDeviceMapper.getDevice(boxId).size() == 0){ // 当前吊篮不在任何项目中
            if(electricStateMapper.getBoxLog(boxId) == null){
                electricStateMapper.createWorkBox(boxId, projectId);
            }
            try{
                electricStateMapper.updateStateOut(boxId, ElectricBoxStateEnum.getCode("待安装").getCode());
                electricStateMapper.updateProject(boxId,projectId);
                projectMapper.increaseBox(projectId);
                Project project = projectMapper.getProjectDetail(projectId);
                redisService.setProject(project);
                if(projectDeviceMapper.increaseDevice(projectId, boxId))
                    return "新增吊篮成功";
                else return "新增吊篮失败";
            }catch (Exception e){
                e.printStackTrace();
                return "新增吊篮失败";
            }
        }else return "吊篮已存在项目中";
    }
    @Override
    public String increaseWorker(String projectId, String userId){
        if(projectWorkerMapper.getWorker(userId).size() == 0){
            if(userMapper.getUserInfo(userId) != null){
                try {
                    projectWorkerMapper.increaseWorker(projectId, userId);
                    projectMapper.increaseWorker(projectId);
                    Project project = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project);
                    return "新增工人成功";
                }catch (Exception e){
                    e.printStackTrace();
                    return "新增工人失败";
                }
            }
            else return "新增工人失败";
        }
        else return "新增工人失败";
    }
    @Override
    public boolean beginWork(String projectId, String userId, String boxId){
        String userList = getWorkerList(projectId);
        String boxList = getBasketList(projectId);
        if(electricResMapper.getElectricBoxState(userId) != null) return false;//判断工人是否在工作
        if(electricResMapper.getDevice(boxId).size() > 1) return false;//每个吊篮最多有两个人
        String[] box = boxList.split(",");
        int flag = 0;
        for(String s : box){
            if(s.equals(boxId))
                flag = 1;
        }
//        String userNow;
//        if(electricResMapper.getDevice(boxId)!=null)
//            userNow = electricResMapper.getDevice(boxId).getProjectBuilders();
//        else userNow = "";
//        String[] userUpdate;
        if(userList.contains(userId) && flag == 1){
//            if(electricResMapper.getDevice(boxId) == null) {////////////吊篮不在运行，更新吊篮运行时表
            try {
                electricStateMapper.updateWorkState(boxId, 1);
                electricResMapper.createWorkBox(boxId, projectId, userId);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            }else{////////工人直接上吊篮，不用更新吊篮运行时表
//                userNow = userId + "," + userNow ;
//                userUpdate = userNow.split(",");
//                userNow = "";
//                for(int i = 0 ; i < userUpdate.length ; i++){
//                    if(!userUpdate[i].equals("") && !userNow.contains(userUpdate[i])){
//                        userNow += userUpdate[i] + ",";
//                    }
//                }try {
//                    electricResMapper.updateWorker(boxId, userNow);
//                    return true;
//                }catch (Exception e){
//                    return false;
//                }
//            }
        }
        else return false;
    }
    @Override
    public boolean endWork(String projectId, String userId, String boxId){
        if(electricResMapper.getElectricBoxState(userId) == null || !electricResMapper.getElectricBoxState(userId).getDeviceId().equals(boxId)) return false;
        String boxList = getBasketList(projectId);
        Timestamp timeStart = electricResMapper.getElectricBoxState(userId) == null ? null : electricResMapper.getElectricBoxState(userId).getTimeStart();
        long hour = 0;
        if(timeStart != null) {
            long nd = 1000  * 60;
//            long nh = 1000 * 60 * 60;
            Date date = timeStart;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, -8);
            date = cal.getTime();
            Date now = new Date();
            long diff = now.getTime() - date.getTime();
            hour = diff / nd;
        }
        if(boxList.contains(boxId)){
            try {
                electricResMapper.deleteWorkBox(userId);
                if(electricResMapper.getDevice(boxId).size() == 0)
                    electricStateMapper.updateWorkState(boxId, 0);
                workTimeLogService.createWorkTimeLog(userId, projectId, boxId, hour, timeStart);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else return false;
    }
    @Override
    public int storageControl(String projectId, String deviceId, String managerId,String image, int storageState){//////////区域管理员发起报停审核
        ElectricBoxState electricBoxState = electricStateMapper.getBoxLog(deviceId);
        if(electricBoxState.getProjectId().equals(projectId)){
            if(electricBoxState.getStorageState() == storageState){
                return 2;///状态相同，不需要提交申请
            }else{
                if(electricBoxState.getWorkingState()!=1){
                    if(storageState == ElectricBoxStateEnum.getCode("报停审核").getCode()){////入库
                        try{
                            projectMapper.createElectricBoxStop(projectId,deviceId);
                            electricStateMapper.updateStateIn(deviceId, storageState, image);
                            return 0;
                        }catch (Exception e){
                            e.printStackTrace();
                            return 4;
                        }
                    }else {
                        try {
                            projectMapper.createElectricBoxStop(projectId,deviceId);
                            electricStateMapper.updateStateOut(deviceId,storageState);
                            return 0;
                        }catch (Exception e){
                            return 4;
                        }
                    }
                }else return 3;/////电柜正在工作
            }
        }
        else return 1;///管理员信息不对，你没有权限
    }
    @Override
    public int installApply(String projectId, int picNum, String managerId){
        Project project;
        if(redisService.getProject(projectId) != null){
            project = redisService.getProject(projectId);
        }else {
            project = projectMapper.getProjectDetail(projectId);
            if(project != null) {
                redisService.setProject(project);
            }
        }
        String storageList = getBasketList(projectId);
        String[] storage = null;
        if(storageList != null)
            storage = storageList.split(",");
        if(project != null) {
            if (project.getAdminAreaId().equals(managerId)) {
                String[] storeOutTest = new String[picNum];
                String storeOut = "";
                for (int i = 0; i < picNum; i++) {
                    storeOutTest[i] = projectId + "_" + (i + 1) + ".jpg";
                }
                for (int i = 0; i < picNum; i++) {
                    storeOut = storeOut + storeOutTest[i] + ",";
                }
                String projectCertUrl = "";
                try {
                    projectMapper.installApply(projectId, projectCertUrl, storeOut);
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("吊篮安装验收").getCode());
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                    if(storage != null)
                        for(int i = 0 ; i < storage.length ; i++){
                            if(electricStateMapper.getBoxLog(storage[i]).getStorageState() != ElectricBoxStateEnum.getCode("使用中").getCode() && electricStateMapper.getBoxLog(storage[i]).getStorageState() != 0)
                                electricStateMapper.updateStateOut(storage[i],ElectricBoxStateEnum.getCode("待上传安监证书").getCode());
                        }
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;////插入数据库失败
                }
            } else return 3;
        }else return 2;///项目不存在
    }
    @Override
    public int installCheck(String projectId, String deviceId, String managerId, String dealerId, String description, int check){
        String userId = (String) userMapper.getDeviceInstallInfoByDeviceId(projectId, deviceId).get("user_id");
        if (check == 1){
            projectMapper.updateState(projectId,ProjectStateEnum.getCode("吊篮安装验收").getCode());
            Project project1 = projectMapper.getProjectDetail(projectId);
            redisService.setProject(project1);
            electricStateMapper.updateStateOut(deviceId, ElectricBoxStateEnum.getCode("待上传安监证书").getCode());
            projectMapper.uploadInstallCheck(projectId, deviceId, userId, dealerId, description, "通过");
            return 0;
        }else {
            userMapper.updateAllInstallState(projectId, userId, deviceId, 0);
            electricStateMapper.updateStateOut(deviceId, ElectricBoxStateEnum.getCode("正在安装").getCode());
            projectMapper.uploadInstallCheck(projectId, deviceId, userId, dealerId, description, "不通过");
            //发个通知
            try {
                sendMessageToAreaAdmin(userId, deviceId);
                return 0;
            }catch (Exception e){
                return 1;
            }
        }
    }
    private void sendMessageToAreaAdmin(String alias, String deviceId) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap<String, String>();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "通知";
        String description = "吊篮" + deviceId + "安装审核不通过";
        //alias非空白, 不能包含逗号, 长度小于128
        map.put("type","3");
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .extra(map)
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    @Override
    public int beginProject(String projectId, String storageList, String managerId, int check){
        if (check == 0){
            projectMapper.updateState(projectId,ProjectStateEnum.getCode("吊篮安装验收").getCode());
            Project project1 = projectMapper.getProjectDetail(projectId);
            redisService.setProject(project1);
            String[] storage = null;
            if(storageList != null)
                storage = storageList.split(",");
            if(storage != null) {
                for (int i = 0; i < storage.length; i++) {
                    String userId = (String) userMapper.getDeviceInstallInfoByDeviceId(projectId, storage[i]).get("user_id");
                    userMapper.updateAllInstallState(projectId, userId, storage[i], 0);
                    electricStateMapper.updateStateOut(storage[i], ElectricBoxStateEnum.getCode("正在安装").getCode());
                }
            }
            return 0;
        }
        Project project;
        if(redisService.getProject(projectId) != null){
            project = redisService.getProject(projectId);
        }else {
            project = projectMapper.getProjectDetail(projectId);
            if(project != null) {
                redisService.setProject(project);
            }
        }
        String[] storage = null;
        if(storageList != null)
            storage = storageList.split(",");
        if(project != null) {
            if (project.getAdminAreaId().equals(managerId)) {
                String projectCertUrl = project.getProjectCertUrl();
                try {
                    if(storage != null) {
                        for (int i = 0; i < storage.length ; i++){
                            projectCertUrl += projectId + "_" + storage[i] + ".jpg" + ",";
                        }
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],21);
                        }
                    }
                    projectMapper.beginProject(projectId, projectCertUrl);
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("安监证书验收").getCode());
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;////插入数据库失败
                }
            } else return 3;///项目不存在
        }else return 2;///没有权限
    }
    @Override
    public int prepareEnd(String projectId, String storageList){///租方管理员通知区域管理员验收项目
        storageList = storageList==null?"":storageList;
        String[] storage = storageList.split(",");
        try {
            for(int i = 0 ; i < storage.length ; i++){
                electricStateMapper.updateStateOut(storage[i],ElectricBoxStateEnum.getCode("待报停").getCode());
            }
            return 0; /////////成功
        }catch (Exception e){
            return 1; /////////改变状态失败
        }
    }
    @Override
    public int applyEnd(String projectId, int picNum, String managerId){///租方管理员通知区域管理员验收项目
        Project project = projectMapper.getProjectDetail(projectId);
        String projectEndUrl = "";
        String storageList = project.getBoxList()==null?"":project.getBoxList();
        String[] storage = storageList.split(",");
        if(project.getProjectState().equals("3")) {
            if (project.getAdminAreaId() != null) {
                if (project.getAdminAreaId().equals(managerId)) {
                    try {
                        for (int i = 0; i < picNum; i++) {
                            projectEndUrl += projectId + "_" + (i + 1) + ".jpg" + ",";
                        }
                        projectMapper.updateProjectEnd(projectId, projectEndUrl);
                        Project project1 = projectMapper.getProjectDetail(projectId);
                        redisService.setProject(project1);
                        for (int i = 0 ; i < storage.length ; i++) {
                            electricStateMapper.updateStateOut(storage[i],ElectricBoxStateEnum.getCode("报停审核").getCode());
                        }
                        return 0; /////////成功
                    } catch (Exception e) {
                        return 1; /////////发起申请失败
                    }
                } else return 3;////////没有权限
            } else return 2;////////该项目不存在区域管理员
        }else return 4;///////项目已经停止
    }
    @Override
    public int createCompany(Map<String,String> map){
        String companyName = map.get("companyName");
        String taxNum = map.get("taxNum");
        String projectId = map.get("projectId");
        try {
            projectMapper.createCompany(companyName,projectId);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }
    @Override
    public int createExceptionBox(String deviceId, String projectId, String managerId, String reason){
        try {
            if(projectId.equals(""))
                return 2;///////吊篮不存在项目中
            if(electricStateMapper.getBoxLog(deviceId) == null)
                return 3;///////吊兰已出库
            if(electricStateMapper.getBoxLog(deviceId).getStorageState() != 0)
                return 4;///////吊篮未处于报停状态
            exceptionInfoMapper.createExceptionBox(deviceId,projectId,managerId,reason);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;/////插入失败
        }
    }
    @Override
    public int createProjectSupInfo(ProjectSupInfo projectSupInfo){
        try {
            projectMapper.createProjectSupInfo(projectSupInfo);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }
    @Override
    public int createRepairBox(Map<String, String> repair){
        String imageStart = "";
        String deviceId = repair.get("deviceId");
        String projectId = repair.get("projectId");
        String reason = repair.get("reason");
        String managerId = repair.get("managerId");
        repair.remove("deviceId");
        repair.remove("projectId");
        repair.remove("reason");
        repair.remove("managerId");
        Timestamp startTime = new Timestamp(new Date().getTime());
        for(int i = 0 ; i < repair.size() ; i++){
            imageStart += repair.get("pic" + "_" + (i+1)) + ",";
        }
        try {
            if(projectId.equals(""))
                return 2;///////吊篮不存在项目中
            if(electricStateMapper.getBoxLog(deviceId) == null)
                return 3;///////吊兰已出库
            repairInfoMapper.createRepairBox(deviceId,projectId,managerId,reason,imageStart,startTime);
            userService.sendRepairMessage(projectId,deviceId,startTime);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;/////插入失败
        }
    }
    @Override
    public int createRepairEndBox(Map<String, String> repair){
        String imageEnd = "";
        String deviceId = repair.get("deviceId");
        String projectId = repair.get("projectId");
        String description = repair.get("description");
        String dealerId = repair.get("dealerId");
        repair.remove("deviceId");
        repair.remove("projectId");
        repair.remove("description");
        repair.remove("dealerId");
        Timestamp endTime = new Timestamp(new Date().getTime());
        for(int i = 0 ; i < repair.size() ; i++){
            imageEnd += repair.get("pic" + "_" + (i+1)) + ",";
        }
        try {
            if(projectId.equals(""))
                return 2;///////吊篮不存在项目中
            if(electricStateMapper.getBoxLog(deviceId) == null)
                return 3;///////吊兰已出库
            repairInfoMapper.createRepairEndBox(deviceId,projectId,dealerId,description,imageEnd,endTime);
//            repairInfoMapper.deleteRepairBox(deviceId);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;/////插入失败
        }
    }
    @Override
    public int createInstallInfo(String projectId, String userId, String deviceList){
        String[] deviceArray = deviceList.split(",");
        if(deviceArray.length == 0) return 0;
        User user;
        if(redisService.getUser(userId) != null){
            user = redisService.getUser(userId);
        }else {
            user = userMapper.getUserInfo(userId);
            if(user != null){
                redisService.setUser(user);
            }
        }
        if(user == null) return 0;
        if(!user.getUserRole().equals("InstallTeam")) return 0;
        for(String deviceId : deviceArray){
            if(userMapper.getDeviceInstallInfoByDeviceId(projectId, deviceId) != null) return  0;
            if(projectMapper.insertInstallInfo(userId, projectId, deviceId)) {
                electricStateMapper.updateStateOut(deviceId, ElectricBoxStateEnum.getCode("正在安装").getCode());
            }
        }
        return 1;
    }
    @Override
    public int deleteProject(String projectId){
        try {
            String[] deleteProject = new String[1];
            deleteProject[0] = projectId;
            projectMapper.deleteProject(deleteProject);
            projectWorkerMapper.deleteWorkerByProjectId(projectId);
            redisService.delProject(projectId);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    @Override
    public int uploadPlaneGraph(InputStream file, String projectId, int num, int type) {
        if(projectId.equals("") || file == null){
            return 0;
        }
        String fileName = "";
        if(type == 1) {
            fileName = "plane_graph" + "_" + "all" + ".jpg";
        }else {
            fileName = "plane_graph" + "_" + num + ".jpg";
        }
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(FTPFileURL,21);
            ftp.login(FTPUserName,FTPUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 0;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(FTPPlaneGraph + projectId + "/");
            ftp.changeWorkingDirectory(FTPPlaneGraph + projectId + "/");
            ftp.storeFile(fileName,file);
            file.close();
            ftp.logout();
            return 1;
        }catch (IOException ex){
            return 0;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    @Override
    public int uploadProjectContract(InputStream file, String projectId, int type) {
        if(projectId.equals("") || file == null){
            return 0;
        }
        String fileName = type == 1 ? projectId + ".jpg" : projectId + ".pdf";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(FTPFileURL,21);
            ftp.login(FTPUserName,FTPUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 0;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(FTPPlaneGraph + projectId + "/");
            ftp.changeWorkingDirectory(FTPPlaneGraph + projectId + "/");
            ftp.storeFile(fileName,file);
            file.close();
            ftp.logout();
            projectMapper.updateProjectContractUrl(projectId, fileName);
            return 1;
        }catch (IOException ex){
            return 0;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    @Override
    public int getRepairBoxNum(String projectId){
        return repairInfoMapper.getRepairBoxNum(projectId);
    }
    @Override
    public List<RepairBoxInfo> getRepairBox(String projectId){
        return repairInfoMapper.getRepairInfo(projectId);
    }
    @Override
    public List<RepairBoxInfo> getRepairBoxOne(String deviceId){
        return repairInfoMapper.getRepairInfoOne(deviceId);
    }
    @Override
    public List<RepairBoxInfo> getRepairEndBoxOne(String deviceId){
        return repairInfoMapper.getRepairEndInfoOne(deviceId);
    }
    @Override
    public ElectricBoxState getBoxLog(String boxId){
        ElectricBoxState electricBoxState = electricStateMapper.getBoxLog(boxId);
        return electricBoxState;
    }
    @Override
    public JSONObject getStoreInInfo(String projectId, String deviceId, String managerId){
        JSONObject jsonObject = new JSONObject();
        Project project;
        if(projectId.equals("") || projectId == null){
            project = null;
        }else {
            if(redisService.getProject(projectId) != null){
                project = redisService.getProject(projectId);
            }else {
                project = projectMapper.getProjectDetail(projectId);
                if(project != null){
                    redisService.setProject(project);
                }
            }
        }
        String projectName = project == null ? "" : project.getProjectName();
        projectName = projectName == null ? "" : projectName;
        String userName = userMapper.getUserById(managerId).getUserName();
        userName = userName == null ? "" : userName;
        String verificationImage = electricStateMapper.getBoxLog(deviceId).getStoreIn();
        verificationImage = verificationImage == null ? "" : verificationImage;
        String[] returnImage = verificationImage.split(";");
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        res = simpleDateFormat.format(new Date().getTime());
        jsonObject.put("type", 2);
        jsonObject.put("projectId",projectId);
        jsonObject.put("projectName",projectName);
        jsonObject.put("userId",managerId);
        jsonObject.put("userName",userName);
        jsonObject.put("deviceId",deviceId);
        jsonObject.put("uploadTime",res);
        jsonObject.put("verificationImage",returnImage);
        return jsonObject;
    }
    @Override
    public JSONObject getBeginWorkInfo(String projectId, int picNum, String managerId){
        JSONObject jsonObject = new JSONObject();
        Project project;
        if(projectId.equals("") || projectId == null){
            project = null;
        }else {
            if(redisService.getProject(projectId) != null){
                project = redisService.getProject(projectId);
            }else {
                project = projectMapper.getProjectDetail(projectId);
                if(project != null){
                    redisService.setProject(project);
                }
            }
        }
        String projectName =project == null ? "" : project.getProjectName();
        String userName = userMapper.getUserById(managerId).getUserName();
        String device = getBasketList(projectId);
        String[] deviceList = device.split(",");
        for(int i = 0 ; i < deviceList.length ; i++){
            if(electricStateMapper.getBoxLog(deviceList[i]).getStorageState() != ElectricBoxStateEnum.getCode("待上传安监证书").getCode()){
                device = device.replace(deviceList[i] + ",", "");
            }
        }
        String[] devicePush = device.split(",");
        String image =project == null ? "" : project.getStoreOut() == null?"":project.getStoreOut();
        String[] imageList = image.split(",");
        String certificate =project == null ? "" : project.getProjectCertUrl();
        String[] certificateList = certificate.split(",");
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        res = simpleDateFormat.format(new Date().getTime());
        jsonObject.put("projectId",projectId);
        jsonObject.put("projectName",projectName);
        jsonObject.put("adminAreaName",managerId);
        jsonObject.put("adminAreaId",userName);
        jsonObject.put("type", 1);
        jsonObject.put("deviceList",devicePush);
        jsonObject.put("imageList", imageList);
        jsonObject.put("certificate",certificateList);
        jsonObject.put("uploadTime",res);
        return jsonObject;
    }////吊篮安装审核信息
    @Override
    public JSONObject getStoreCertInfo(String projectId, int picNum, String managerId){
        JSONObject jsonObject = new JSONObject();
        Project project;
        if(projectId.equals("") || projectId == null){
            project = null;
        }else {
            if(redisService.getProject(projectId) != null){
                project = redisService.getProject(projectId);
            }else {
                project = projectMapper.getProjectDetail(projectId);
                if(project != null){
                    redisService.setProject(project);
                }
            }
        }
        String projectName =project == null ? "" : project.getProjectName();
        String userName = userMapper.getUserById(managerId).getUserName();
        String device = getBasketList(projectId) == null?"":project.getBoxList();
        String[] deviceList = device.split(",");
        for(String deviceId : deviceList){
            if(electricStateMapper.getBoxLog(deviceId).getStorageState() != 21)
                device = device.replace(deviceId + ",","");
        }
        String[] deviceList1 = device.split(",");
        String image = project.getStoreOut() == null?"":project.getStoreOut();
        String[] imageList = image.split(",");
        String certificate = project.getProjectCertUrl();
        String[] certificateList = certificate.split(",");
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        res = simpleDateFormat.format(new Date().getTime());
        jsonObject.put("projectId",projectId);
        jsonObject.put("projectName",projectName);
        jsonObject.put("adminAreaName",managerId);
        jsonObject.put("adminAreaId",userName);
        jsonObject.put("type", 1);
        jsonObject.put("deviceList",deviceList1);
        jsonObject.put("imageList", imageList);
        jsonObject.put("certificate",certificateList);
        jsonObject.put("uploadTime",res);
        return jsonObject;
    }////吊篮安监证书审核信息
    @Override
    public JSONObject getEndWorkInfo(String projectId, int picNum, String managerId){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();/////项目信息
        JSONObject jsonObject2 = new JSONObject();/////管理员信息
        JSONObject jsonObject3 = new JSONObject();/////所有的吊篮ID
        JSONObject jsonObject4 = new JSONObject();/////所有的吊篮照片url
        Project project;
        if(projectId.equals("") || projectId == null){
            project = null;
        }else {
            if(redisService.getProject(projectId) != null){
                project = redisService.getProject(projectId);
                project.setBoxList(getBasketList(projectId));
            }else {
                project = projectMapper.getProjectDetail(projectId);
                if(project != null){
                    redisService.setProject(project);
                }
            }
        }
        String projectName = project.getProjectName();
        String userName = userMapper.getUserById(managerId).getUserName();
        String deviceList = project.getBoxList()==null?"":project.getBoxList();
        String[] device = deviceList.split(",");
        String imageList = project.getProjectEndUrl()==null?"":project.getProjectEndUrl();
        String[] image = imageList.split(",");
        String certificate = project.getProjectCertUrl();
        String[] certificateList = certificate.split(",");
        jsonObject1.put("projectId",projectId);
        jsonObject1.put("projectName",projectName);
        jsonObject2.put("userId",managerId);
        jsonObject2.put("userName",userName);
        for(int i = 0 ; i < device.length ; i++){
            jsonObject3.put("device_" + i,device[i]);
        }
        for(int i = 0 ; i < image.length ; i++){
            jsonObject4.put("sitePhoto_" + i,image[i]);
        }
        jsonObject.put("project",jsonObject1);
        jsonObject.put("adminArea",jsonObject2);
        jsonObject.put("device",jsonObject3);
        jsonObject.put("sitePhoto", jsonObject4);
        jsonObject.put("certificate",certificateList);
        return jsonObject;
    }
    @Override
    public JSONObject getStorageSum(int userFlag){
        JSONObject jsonObject = new JSONObject();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();///7天后可以使用的吊篮数目
        calendar.add(Calendar.DATE, 7);
        Calendar calendar1 = Calendar.getInstance();///15天后可以使用的吊篮数目
        calendar1.add(Calendar.DATE, 15);
        Calendar calendar2 = Calendar.getInstance();///3天后可以使用的吊篮数目
        calendar2.add(Calendar.DATE, 3);
        Calendar calendar3 = Calendar.getInstance();///全部可以使用的吊篮数目
        calendar3.add(Calendar.DATE, 1000);
        Timestamp timestampNow = new Timestamp(date.getTime());
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        Timestamp timestamp1 = new Timestamp(calendar1.getTime().getTime());
        Timestamp timestamp2 = new Timestamp(calendar2.getTime().getTime());
        Timestamp timestamp3 = new Timestamp(calendar3.getTime().getTime());
//        System.out.println(timestamp);
//        System.out.println(timestamp1);
        //jsonObject.put("usedSum",electricStateMapper.sum(1));///可用的吊篮数目
        switch (userFlag) {
            case 1:
                preStopMapper.deleteOutDate(timestampNow);
                try {
                    jsonObject.put("usedSum", preStopMapper.all());///吊篮总数
                }catch (Exception e){
                    jsonObject.put("usedSum", 0);
                }
                break;
            case 7:
                jsonObject.put("totalPreStop", preStopMapper.sum(timestamp, timestampNow));
                List<JSONObject> jsonObjects = getPreStopInfo(timestamp,timestampNow);
                jsonObject.put("preStopBasket",jsonObjects);
                break;
            case 15:
                jsonObject.put("totalPreStop", preStopMapper.sum(timestamp1, timestampNow));
                List<JSONObject> jsonObjects1 = getPreStopInfo(timestamp1,timestampNow);
                jsonObject.put("preStopBasket",jsonObjects1);
                break;
            case 3:
                jsonObject.put("totalPreStop", preStopMapper.sum(timestamp2, timestampNow));
                List<JSONObject> jsonObjects2 = getPreStopInfo(timestamp2,timestampNow);
                jsonObject.put("preStopBasket",jsonObjects2);
                break;
            case 0:
                jsonObject.put("totalPreStop", preStopMapper.sum(timestamp3, timestampNow));
                List<JSONObject> jsonObjects3 = getPreStopInfo(timestamp3,timestampNow);
                jsonObject.put("preStopBasket",jsonObjects3);
                break;

        }
        return jsonObject;
    }///获取不同状态的吊篮数目
    @Override
    public JSONObject getStorageInfo(String deviceId){
        JSONObject jsonObject = new JSONObject();
        ElectricBoxState electricBoxState = electricStateMapper.getBoxLog(deviceId);
        String projectId = projectDeviceMapper.getDevice(deviceId).get(0);
        Project project;
        if(projectId != null && !projectId.equals("")) {
            try {
                project = (Project) getProjectDetail(projectId).get("projectDetail");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else project = null;
        return jsonObject;
    }
    private List<JSONObject> getPreStopInfo(Timestamp timestamp, Timestamp timestamp2){
        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<PreStop> preStops = preStopMapper.getPreStopInfo(timestamp, timestamp2);
        if(preStops != null) {
            for (PreStop preStop : preStops) {
                Project project;
                if(preStop.getProjectId().equals("") || preStop.getProjectId() == null){
                    project = null;
                }else {
                    if(redisService.getProject(preStop.getProjectId()) != null){
                        project = redisService.getProject(preStop.getProjectId());
                    }else {
                        project = projectMapper.getProjectDetail(preStop.getProjectId());
                        if(project != null){
                            redisService.setProject(project);
                        }
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("projectName",project.getProjectName());
                jsonObject.put("preStopBasketNum",preStop.getNum());
                jsonObjectList.add(jsonObject);
            }
        }
        return jsonObjectList;
    }
    @Override
    public JSONObject getWorker(int type, String deviceId){
        JSONObject jsonObject = new JSONObject();
        List<ElectricRes> electricResList = electricResMapper.getDevice(deviceId);
        int i = 1;
        for(ElectricRes electricRes : electricResList){
            User user;
            if(redisService.getUser(electricRes.getProjectBuilders()) != null){
                user = redisService.getUser(electricRes.getProjectBuilders());
            }else {
                user = userMapper.getUserInfo(electricRes.getProjectBuilders());
                if(user != null){
                    redisService.setUser(user);
                }
            }
            jsonObject.put("worker" + i , user);
            i++;
        }
        return jsonObject;
    }
    @Override
    public JSONObject getProjectSupInfo(String projectId) {
        List<ProjectSupInfo> projectSupInfoList  = projectMapper.getProjectSupInfo(projectId);
        JSONObject jsonObject = new JSONObject();
        if (projectSupInfoList.size() != 0){
            ProjectSupInfo projectSupInfo = projectSupInfoList.get(0);
            jsonObject.put(projectId, projectSupInfo);
        }
        return jsonObject;
    }
    @Override
    public int storageIn(String projectId, String deviceId){
        ElectricBoxState electricBoxState = electricStateMapper.getBoxLog(deviceId);
        List<String> isInProject = projectDeviceMapper.getDevice(deviceId);
        if(isInProject == null) return 2;
        if(isInProject.size() == 0) return 2;
        if(electricBoxState != null) {
            if(electricBoxState.getStorageState() == ElectricBoxStateEnum.getCode("报停审核").getCode() ||
                    electricBoxState.getStorageState() == ElectricBoxStateEnum.getCode("待安装").getCode() ||
                    electricBoxState.getStorageState() == 0) {
                try {
//                    Project project = projectMapper.getProjectDetail(projectId);
//                    electricBoxMapper.deleteRealTimeDateById(deviceId);
//                    setUpDataMapper.deleteSetUpData(deviceId);
//                    electricStateMapper.deleteWorkBox(deviceId);
                    projectDeviceMapper.deleteDeviceByDeviceId(deviceId);
//                    String storageList = project == null ? "" : getBasketList(projectId);
//                    String storageNew = "";
//                    if (!storageList.equals("")) {
//                        storageNew = storageList.replace(deviceId + ",", "");
//                    }
                    projectMapper.decreaseBox(projectId);
                    Project project = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project);
                    return 0;
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return 3;//////删除失败
                }
            }else return 1;////吊篮未报停
        }else return 2;////吊篮不存在
    }
    @Override
    public int uploadPlaneGraphInfo(JSONObject info, String projectId, String buildingNum, int type) {
        switch (type){
            case 1:
                for(String buildingId :  info.keySet()){
                    if(projectMapper.getProjectPlaneGraphOne(projectId, buildingId).size() != 0){
                        projectMapper.deleteProjectPlaneGraphOne(projectId, buildingId);
                        projectMapper.uploadProjectPlaneGraphInfo(projectId, buildingId, (String) info.get(buildingId));
                        continue;
                    }
                    if(!projectMapper.uploadProjectPlaneGraphInfo(projectId, buildingId, (String) info.get(buildingId))) return 0;
                }
                projectMapper.updatePlaneState(projectId, 1);
                return 1;
            case 2:
                for(String deviceId : info.keySet()){
                    String buildInfo = (String) info.get(deviceId);
                    String[] locationInfo = buildInfo.split(":");
                    String locationId = locationInfo[0];
                    String location = locationInfo[1];
                    if(locationId.equals("A") || locationId.equals("B")) {
                        if(projectMapper.getPlaneGraphAB(projectId, locationId, buildingNum, locationId) != null) continue;
                        if(!projectMapper.uploadPlaneGraphInfo(projectId, buildingNum, deviceId, locationId, location)) return 0;
                    }else {
                        List<String> deviceIdBefore = projectMapper.judgeDeviceLocationId(projectId, buildingNum, locationId);
                        if (deviceIdBefore.size() != 0) {
                            for (String deviceIdBe :deviceIdBefore) {
                                if (!StringUtils.isBlank(deviceIdBe)) {
                                    projectMapper.updateDeviceLocationId(projectId, buildingNum, "0", deviceIdBe);
                                }
                            }
                        }
                        if(!projectMapper.updatePlaneGraphInfo(projectId, buildingNum, deviceId, locationId, location)) return 0;
                    }
                }
                return 1;
        }
        return 0;
    }
    @Override
    public int updateSiteNo(String deviceId, String siteNo) {
        try {
            electricStateMapper.updateSiteNo(deviceId, siteNo);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }
    @Override
    public JSONObject getSiteNo(String deviceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("siteNo", electricStateMapper.getSiteNo(deviceId));
        return jsonObject;
    }
    @Override
    public JSONObject getInstallInfo(String deviceId) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = userMapper.getAllParts(deviceId);//绑定设备信息
        List<Map<String, Object>> list1 = userMapper.getInstallCaptainInfo(deviceId);//获取安装队伍信息
        for (Map<String, Object> installTeam : list1){
            String installTeamId = (String) installTeam.get("user_id");
            List<Map<String, Object>> list = userMapper.getInstallerInfo(deviceId, installTeamId);//获取安装队伍人员信息
            jsonObject.put("installTeam_" + installTeamId, list);
        }
        jsonObject.put("deviceInfo", map);
        jsonObject.put("installTeamInfo", list1);
        return jsonObject;
    }

    @Override
    public JSONObject getDeviceAlarmDetail(String deviceId, String startTime, String endTime) {
        JSONObject jsonObject = new JSONObject();
        String projectId = projectDeviceMapper.getDevice(deviceId).get(0);
        Project project = projectMapper.getProjectDetail(projectId);
        if (project == null) {
            return null;
        }

        String projectName = project.getProjectName();
        jsonObject.put("projectName", projectName);

        String rentAdmin = project.getAdminRentId();
        String areaAdmin = project.getAdminAreaId();
        String projectAdmin = project.getAdminProjectId();

        User rentUser = userMapper.getUserInfo(rentAdmin);
        jsonObject.put("rentUser", rentUser);
        User areaUser = userMapper.getUserInfo(areaAdmin);
        jsonObject.put("areaUser", areaUser);
        User projectUser = userMapper.getUserInfo(projectAdmin);
        jsonObject.put("projectUser", projectUser);

        String siteNo = electricStateMapper.getSiteNo(deviceId);
        jsonObject.put("siteNo", siteNo);

        int alarmCountAll = projectMapper.getAlarmCountAllByDeviceId(deviceId);
        int alarmCountMonth = projectMapper.getAlarmCountMonthByDeviceId(deviceId, startTime, endTime);
        jsonObject.put("alarmCountAll", alarmCountAll);
        jsonObject.put("alarmCountMonth", alarmCountMonth);

        return jsonObject;
    }

    @Override
    public boolean deleteDevice(String deviceId) {
        electricStateMapper.deleteWorkBox(deviceId);
        List<String> list = projectDeviceMapper.getDevice(deviceId);
        String projectId = list == null ? "" :list.get(0);
        if (!projectId.equals("")) {
            projectMapper.decreaseBox(projectId);
        }
        return true;
    }
}
