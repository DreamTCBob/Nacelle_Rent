package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.*;
import com.manager.nacelle_rent.entity.*;
import com.manager.nacelle_rent.service.ProjectService;
import com.manager.nacelle_rent.service.UserService;
import com.manager.nacelle_rent.service.WorkTimeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    private ProjectMapper projectMapper;
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

    private String[] projectStateList = {"草稿","待成立项目部","吊篮已安装","计费中","已结束","证书待审核","清单待配置","清单待审核"};

    @Override
    public List<Project> getProjectList(int flag){
        List<Project> projectList = projectMapper.getProjectList(flag);
        for(int i = 0; i < projectList.size();i++){

            Project project = projectList.get(i);
            project.setProjectState(projectStateList[Integer.parseInt(project.getProjectState())]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
        }
        return projectList;
    }

    @Override
    public List<JSONObject> getProjectListAll(){
        List<Project> projectList = projectMapper.getProjectListAll();
        List<JSONObject> list = new ArrayList<>();
        for(int i = 0; i < projectList.size();i++){
            if(!projectList.get(i).getProjectState().equals("0")) {
                JSONObject jsonObject = new JSONObject();
                Project project = projectList.get(i);
                User user = userMapper.getUserById(project.getAdminAreaId());
                User user1 = userMapper.getUserById(project.getAdminRentId());
                User user2 = userMapper.getUserById(project.getAdminProjectId());
                String[] boxList = project.getBoxList().equals("") ? null : project.getBoxList().split(",");
                String[] worker = project.getWorker().equals("")? null : project.getWorker().split(",");
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
                list.add(jsonObject);
            }
        }
        return list;
    }

    @Override
    public List<JSONObject> getProjectList2(int flag){
        List<JSONObject> returnList = new ArrayList<>();
        List<Project> projectList = projectMapper.getProjectList(flag);
        List<Project> projectList1 = projectMapper.getProjectList(21);
        for(int i = 0; i < projectList.size();i++){
            JSONObject jsonObject = new JSONObject();
            Project project = projectList.get(i);
            project.setProjectState(projectStateList[Integer.parseInt(project.getProjectState())]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
            String device = project.getBoxList() == null?"":project.getBoxList();
            String[] deviceList = device.split(",");
            String image = project.getStoreOut() == null?"":project.getStoreOut();
            String[] imageList = image.split(",");
            jsonObject.put("projectId",project.getProjectId());
            jsonObject.put("projectName",project.getProjectName());
            jsonObject.put("adminAreaName",userMapper.getUserById(project.getAdminAreaId()).getUserName());
            jsonObject.put("adminAreaId",project.getAdminAreaId());
            jsonObject.put("projectState",project.getProjectState());
            jsonObject.put("type", 1);
            jsonObject.put("deviceList",deviceList);
            jsonObject.put("imageList", imageList);
            jsonObject.put("certificate",project.getProjectCertUrl().split(","));
            jsonObject.put("uploadTime",project.getProjectStart());
            returnList.add(jsonObject);
        }
        for(int i = 0; i < projectList1.size();i++){
            JSONObject jsonObject = new JSONObject();
            Project project = projectList1.get(i);
            project.setProjectState(projectStateList[5]);
            if(project.getProjectEnd() == null){
                project.setProjectEnd("—");
            }
            String device = project.getBoxList() == null?"":project.getBoxList();
            String[] deviceList = device.split(",");
            String image = project.getStoreOut() == null?"":project.getStoreOut();
            String[] imageList = image.split(",");
            jsonObject.put("projectId",project.getProjectId());
            jsonObject.put("projectName",project.getProjectName());
            jsonObject.put("adminAreaName",userMapper.getUserById(project.getAdminAreaId()).getUserName());
            jsonObject.put("adminAreaId",project.getAdminAreaId());
            jsonObject.put("projectState",project.getProjectState());
            jsonObject.put("type", 1);
            jsonObject.put("deviceList",deviceList);
            jsonObject.put("imageList", imageList);
            jsonObject.put("certificate",project.getProjectCertUrl().split(","));
            jsonObject.put("uploadTime",project.getProjectStart());
            returnList.add(jsonObject);
        }
        return returnList;

    }

    @Override
    public List<JSONObject> getStoreList(int flag){
        List<JSONObject> returnList = new ArrayList<>();
        List<ElectricBoxState> list = electricStateMapper.getStoreList(flag);
        for(int i = 0; i < list.size();i++){
            try {
                JSONObject jsonObject = new JSONObject();
                ElectricBoxState electricBoxState = list.get(i);
                if (electricBoxState.getStoreIn() == null) {
                    electricBoxState.setStoreIn("");
                }
                Project project = projectMapper.getProjectIdByStore(electricBoxState.getDeviceId());
                String projectId = project == null ? "" : project.getProjectId();
                String projectName = project == null ? "" : project.getProjectName();
                String userId = projectMapper.getProjectDetail(electricBoxState.getProjectId()).getAdminAreaId();
                String userName = userMapper.getUserById(userId).getUserName();
                String res;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                res = simpleDateFormat.format(electricBoxState.getDate());
                jsonObject.put("projectId", projectId);
                jsonObject.put("projectName", projectName);
                jsonObject.put("userId", userId);
                jsonObject.put("userName", userName);
                jsonObject.put("deviceId", electricBoxState.getDeviceId());
                jsonObject.put("uploadTime", res);
                jsonObject.put("verificationImage", electricBoxState.getStoreIn().split(",")[0]);
                returnList.add(jsonObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return returnList;

    }
    @Override
    public List<Project> getAllProjectByAdmin(String userId){
        List<Project> projectList = projectMapper.getAllProjectByAdmin(userId);
        return projectList;
    }

    @Override
    public String getBasketList(String projectId){
        String basketList = null;
        Project project = projectMapper.getProjectDetail(projectId);
        if(project != null)
            basketList = project.getBoxList();
        return basketList;
    }

    @Override
    public String getProjectId(String userId){
        List<Project> project = projectMapper.getProjectId(userId);
        if(project.size()!=0)
            return project.get(0).getProjectId();
        else return "";
    }

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
        Project project = projectMapper.getProjectDetail(projectId);
        if(project != null)
            areaAdmin = project.getAdminAreaId();
        if(project != null)
            rentAdmin = project.getAdminRentId();
        if(project != null)
            worker = project.getWorker();
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
        Project projectDetail = projectMapper.getProjectDetail(projectId);
        if(projectDetail.getProjectState().equals("21"))
            projectDetail.setProjectState(projectStateList[5]);
        else if(projectDetail.getProjectState().equals("11"))
            projectDetail.setProjectState(projectStateList[6]);
        else if(projectDetail.getProjectState().equals("12"))
            projectDetail.setProjectState(projectStateList[7]);
        else
            projectDetail.setProjectState(projectStateList[Integer.parseInt(projectDetail.getProjectState())]);
        if(projectDetail.getProjectEnd() == null){
            projectDetail.setProjectEnd("—");
        }
        List<ElectricRes> electricRes = electricResMapper.getElectricRes(projectId);
        jsonObject.put("projectDetail",projectDetail);
        jsonObject.put("electricRes",electricRes);
        return jsonObject;
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
            if(projectMapper.editProjectDepartment(projectId,adminAreaId,adminProjectId)) {
                userMapper.createProjectAdmin(projectId, adminProjectId);
                projectMapper.updateState(projectId, 11);
                return true;
            }
            return false;
        }catch(Exception ex){
            return false;
        }
    }
    @Override
    public String increaseBox(String projectId, String boxId){
        Project project = projectMapper.getProjectDetail(projectId);
        if(projectMapper.checkStorage(boxId)==null) {///////判断吊篮是否已经在项目中
            String boxL;
            String storageId = boxId;
            String returnRe = "";
            String returnRe1 = "";
            boxL = projectMapper.getProjectDetail(projectId).getBoxList();
            if (boxL == null)
                boxL = "";
            String projectState = projectMapper.getProjectDetail(projectId).getProjectState();////获取项目当前状态，以便修改不同情况下的新增吊篮
            String[] test = boxId.split(",");
            String[] beTest = boxL.split(",");
            int k = 0;
            for (int i = 0; i < test.length; i++) {//////测试是否存在重复数据
                for (int j = 0; j < beTest.length; j++) {
                    if (beTest[j].equals(test[i])) {
                        boxId = boxId.replace(test[i], "");
                        returnRe = returnRe + test[i] + ",";
                        k = k + 1;
                    }
                }
            }
            String[] resultBoxId = boxId.split(",");//更新修改后的数据
            for (int i = 0; i < resultBoxId.length; i++) {//////////查看是否存在此吊篮
                if (electricStateMapper.getBoxLog(resultBoxId[i]) == null) {
                    electricStateMapper.createWorkBox(resultBoxId[i], projectId);
//                boxId = boxId.replace(resultBoxId[i],"");
                    returnRe1 = returnRe1 + resultBoxId[i] + ",";
                    k = k + 1;
                }
            }
            resultBoxId = boxId.split(",");
            boxId = "";
            for (int i = 0; i < resultBoxId.length; i++) { //准备插入数据
                if (!resultBoxId[i].equals(""))
                    boxId = boxId + resultBoxId[i] + ",";
            }
            String boxList;
            if (!boxId.equals("")) {/////////boxList为最终插入数据
                boxList = boxL + boxId;
            } else boxList = boxL;
            String[] boxState = boxList.split(",");
            if (k == 0) {//插入数据并返回提示
                try {
                    if (projectState.equals("0") || projectState.equals("1") || projectState.equals("2"))
                        for (int i = 0; i < boxState.length; i++) {
                            electricStateMapper.updateStateOut(boxState[i], 1);
                            electricStateMapper.updateProject(boxState[i],projectId);
                        }
                    else if ((projectState.equals("3")))
                        for (int i = 0; i < boxState.length; i++) {
                            electricStateMapper.updateStateOut(boxState[i], 1);
                            electricStateMapper.updateProject(boxState[i],projectId);
                        }
                    projectMapper.increaseBox(projectId, boxList);
                    return "新增吊篮成功";
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "新增吊篮失败";
                }
            } else {
                try {
                    if (projectState.equals("0") || projectState.equals("1") || projectState.equals("2"))
                        for (int i = 0; i < boxState.length; i++) {
                            electricStateMapper.updateStateOut(boxState[i], 1);
                            electricStateMapper.updateProject(boxState[i],projectId);
                        }
                    else if ((projectState.equals("3")))
                        for (int i = 0; i < boxState.length; i++) {
                            electricStateMapper.updateStateOut(boxState[i], 1);
                            electricStateMapper.updateProject(boxState[i],projectId);
                        }
                    projectMapper.increaseBox(projectId, boxList);
                    if (returnRe.equals(""))
                        return "新增吊篮成功," + returnRe1 + "不存在此吊篮！已添加！";
                    else if (returnRe1.equals(""))
                        return "新增吊篮成功," + returnRe + "已存在项目中！";
                    else return "新增吊篮成功," + returnRe + "已存在项目中！   " + returnRe1 + "不存在此吊篮！已添加！";
                } catch (Exception ex) {
                    return "新增吊篮失败";
                }
            }
        }else return "吊篮已存在项目中";
    }
    @Override
    public String increaseWorker(String projectId, String userId){
        String userL;
        String returnRe = "";
        String returnRe1 = "";
        userL = projectMapper.getProjectDetail(projectId).getWorker();
        String[] test = userId.split(",");
        String[] beTest = userL.split(",");
        int k = 0;
        for (int i = 0; i < test.length; i++) {//////测试是否存在重复数据
            for (int j = 0; j < beTest.length; j++) {
                if (beTest[j].equals(test[i])) {
                    userId = userId.replace(test[i], "");
                    returnRe = returnRe + test[i] + ",";
                    k = k + 1;
                }
            }
        }
        String[] resultUserId = userId.split(",");//更新修改后的数据
        for (int i = 0; i < resultUserId.length; i++) {//////////查看是否存在此工人
            User user = userMapper.getUserById(resultUserId[i]);
            if (user == null || !user.getUserRole().contains("worker")) {
                userId = userId.replace(resultUserId[i], "");
                returnRe1 = returnRe1 + resultUserId[i] + ",";
                k = k + 1;
            }
        }
        resultUserId = userId.split(",");
        userId = "";
        for (int i = 0; i < resultUserId.length; i++) { //准备插入数据
            if (!resultUserId[i].equals(""))
                userId = userId + resultUserId[i] + ",";
        }
        String userList;
        if (!userId.equals("")) {/////////boxList为最终插入数据
            userList = userL + userId;
        } else userList = userL;
        if (k == 0) {//插入数据并返回提示
            try {
                projectMapper.increaseWorker(projectId, userList);
                return "新增工人成功";
            } catch (Exception ex) {
                return "新增工人失败";
            }
        } else {
            try {
                projectMapper.increaseWorker(projectId, userList);
                if (returnRe.equals(""))
                    return "新增工人成功," + "     不存在的工人为：" + returnRe1;
                else if (returnRe1.equals(""))
                    return "新增工人成功," + "    已存在项目中的工人为：" + returnRe;
                else return "新增工人成功," + "已存在项目中的工人为：" + returnRe + "    不存在的工人为：" + returnRe1;
            } catch (Exception ex) {
                return "新增工人失败";
            }
        }
    }
    @Override
    public boolean beginWork(String projectId, String userId, String boxId){
        String userList = projectMapper.getProjectDetail(projectId).getWorker();
        String boxList = projectMapper.getProjectDetail(projectId).getBoxList();
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
        String boxList = projectMapper.getProjectDetail(projectId).getBoxList();
        Timestamp timeStart = electricResMapper.getElectricBoxState(userId) == null ? null : electricResMapper.getElectricBoxState(userId).getTimeStart();
        long hour = 0;
        if(timeStart != null) {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            Date date = timeStart;
            Date now = new Date();
            long diff = now.getTime() - date.getTime();
            hour = (diff % nd / nh) + 1;
        }
        if(boxList.contains(boxId)){
            try {
                electricResMapper.deleteWorkBox(userId);
                if(electricResMapper.getDevice(boxId).size() == 0)
                    electricStateMapper.updateWorkState(boxId, 0);
                workTimeLogService.createWorkTimeLog(userId, projectId, boxId, hour);
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
                    if(storageState == 5){////入库
                        try{
                            electricStateMapper.updateStateIn(deviceId, storageState, image);
                            return 0;
                        }catch (Exception e){
                            e.printStackTrace();
                            return 4;
                        }
                    }else {
                        try {
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
        Project project = projectMapper.getProjectDetail(projectId);
        String storageList = projectMapper.getProjectDetail(projectId).getBoxList();
        String[] storage = null;
        if(storageList != null)
            storage = storageList.split(",");
        if(project.getAdminAreaId().equals(managerId)) {
            if (project != null) {
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
                    projectMapper.updateState(projectId,2);
                    if(storage != null)
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],2);
                        }
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;////插入数据库失败
                }
            } else return 2;///项目不存在
        }else return 3;
    }
    @Override
    public int beginProject(String projectId, String storageList, String managerId){
        Project project = projectMapper.getProjectDetail(projectId);
        String[] storage = null;
        if(storageList != null)
            storage = storageList.split(",");
        if(project.getAdminAreaId().equals(managerId)) {
            if (project != null) {
                String projectCertUrl = project.getProjectCertUrl();
                try {
                    if(storage != null) {
                        for (int i = 0; i < storage.length ; i++){
                            projectCertUrl += projectId + "_" + storage[i] + ".jpg" + ",";
                        }
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],2);
                        }
                    }
                    projectMapper.beginProject(projectId, projectCertUrl);
                    projectMapper.updateState(projectId,21);
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 1;////插入数据库失败
                }
            } else return 2;///项目不存在
        }else return 3;///没有权限
    }
    @Override
    public int prepareEnd(String projectId, String storageList){///租方管理员通知区域管理员验收项目
        storageList = storageList==null?"":storageList;
        String[] storage = storageList.split(",");
        try {
            for(int i = 0 ; i < storage.length ; i++){
                electricStateMapper.updateStateOut(storage[i],4);
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
                        for (int i = 0 ; i < storage.length ; i++) {
                            electricStateMapper.updateStateOut(storage[i],5);
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
            if(electricStateMapper.getBoxLog(deviceId).getStorageState() != 5)
                return 4;///////吊篮未处于报停状态
            exceptionInfoMapper.createExceptionBox(deviceId,projectId,managerId,reason);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;/////插入失败
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
    public int deleteProject(String projectId){
        try {
            String[] deleteProject = new String[1];
            deleteProject[0] = projectId;
            projectMapper.deleteProject(deleteProject);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
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
        String projectName = projectMapper.getProjectDetail(projectId).getProjectName();
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
        Project project = projectMapper.getProjectDetail(projectId);
        String projectName = project.getProjectName();
        String userName = userMapper.getUserById(managerId).getUserName();
        String device = project.getBoxList() == null?"":project.getBoxList();
        String[] deviceList = device.split(",");
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
        jsonObject.put("deviceList",deviceList);
        jsonObject.put("imageList", imageList);
        jsonObject.put("certificate",certificateList);
        jsonObject.put("uploadTime",res);
        return jsonObject;
    }////吊篮安装审核信息
    @Override
    public JSONObject getStoreCertInfo(String projectId, int picNum, String managerId){
        JSONObject jsonObject = new JSONObject();
        Project project = projectMapper.getProjectDetail(projectId);
        String projectName = project.getProjectName();
        String userName = userMapper.getUserById(managerId).getUserName();
        String device = project.getBoxList() == null?"":project.getBoxList();
        String[] deviceList = device.split(",");
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
        jsonObject.put("deviceList",deviceList);
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
        Project project = projectMapper.getProjectDetail(projectId);
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
                jsonObject.put("usedSum", preStopMapper.all());///吊篮总数
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
    private List<JSONObject> getPreStopInfo(Timestamp timestamp, Timestamp timestamp2){
        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<PreStop> preStops = preStopMapper.getPreStopInfo(timestamp, timestamp2);
        if(preStops != null) {
            for (PreStop preStop : preStops) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("projectName",projectMapper.getProjectDetail(preStop.getProjectId()).getProjectName());
                jsonObject.put("preStopBasketNum",preStop.getNum());
                jsonObjectList.add(jsonObject);
            }
        }
        return jsonObjectList;
    }
    @Override
    public int storageIn(String projectId, String deviceId){
        ElectricBoxState electricBoxState = electricStateMapper.getBoxLog(deviceId);
        if(electricBoxState != null) {
            if(electricBoxState.getStorageState() == 5 || electricBoxState.getStorageState() == 1 || electricBoxState.getStorageState() == 0) {
                try {
                    Project project = projectMapper.getProjectDetail(projectId);
                    electricBoxMapper.deleteRealTimeDateById(deviceId);
                    setUpDataMapper.deleteSetUpData(deviceId);
                    electricStateMapper.deleteWorkBox(deviceId);
                    String storageList = project == null ? "" : project.getBoxList();
                    String storageNew = "";
                    if (!storageList.equals("")) {
                        storageNew = storageList.replace(deviceId + ",", "");
                    }
                    projectMapper.increaseBox(projectId, storageNew);
                    return 0;
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return 3;//////删除失败
                }
            }else return 1;////吊篮未报停
        }else return 2;////吊篮不存在
    }
}
