package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.entity.User;
import com.manager.nacelle_rent.service.RedisService;
import com.manager.nacelle_rent.utils.RedisUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 从Redis中获取项目信息(JSON)
     * @param key
     * @return
     */
    @Override
    public Project getProject(String key){
        JSONObject jsonObject = redisUtil.get(key) == null ? null : (JSONObject) redisUtil.get(key);
        if(jsonObject != null) {
            Project project = new Project();
            project.setProjectId(jsonObject.getString("projectId"));
            project.setProjectName(jsonObject.getString("projectName"));
            project.setProjectState(jsonObject.getString("projectState"));
            project.setProjectStart(jsonObject.getString("projectStart"));
            project.setProjectEnd(jsonObject.getString("projectEnd"));
            project.setAdminRentId(jsonObject.getString("adminRentId"));
            project.setAdminAreaId(jsonObject.getString("adminAreaId"));
            project.setProjectContractUrl(jsonObject.getString("projectContractUrl"));
            project.setProjectCertUrl(jsonObject.getString("projectCertUrl"));
            project.setDeviceNum(Integer.parseInt(jsonObject.getString("deviceNum")));
            project.setProjectBuilders(jsonObject.getString("projectBuilders"));
            project.setWorkerNum(Integer.parseInt(jsonObject.getString("workerNum")));
            project.setStoreOut(jsonObject.getString("storeOut"));
            project.setProjectEndUrl(jsonObject.getString("projectEndUrl"));
            project.setAdminProjectId(jsonObject.getString("adminProjectId"));
            project.setOwner(jsonObject.getString("owner"));
            project.setRegion(jsonObject.getString("region"));
            project.setCoordinate(jsonObject.getString("coordinate"));
            project.setServicePeriod(jsonObject.getString("servicePeriod"));
            project.setRegionManager(jsonObject.getString("regionManager"));
            project.setMarketSalesman(jsonObject.getString("marketSalesman"));
            project.setRemarks(jsonObject.getString("remarks"));
            project.setPerfectState(Integer.parseInt(jsonObject.getString("perfectState")));
            project.setPlaneState(Integer.parseInt(jsonObject.getString("planeState")));
            project.setBoxList(jsonObject.getString("boxList"));
            project.setWorker(jsonObject.getString("worker"));
            project.setCompanyName(jsonObject.getString("companyName"));
            project.setAdminAreaUser((User) jsonObject.get("adminAreaUser"));
            project.setAdminRentUser((User) jsonObject.get("adminRentUser"));
            return project;
        }else return null;
    }

    /**
     * 项目信息写入Redis(JSON)
     * @param project
     * @return
     */
    @Override
    public void setProject(Project project){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", project.getProjectId());
        jsonObject.put("projectName", project.getProjectName());
        jsonObject.put("projectState", project.getProjectState());
        jsonObject.put("projectStart", project.getProjectStart());
        jsonObject.put("projectEnd", project.getProjectEnd());
        jsonObject.put("adminRentId", project.getAdminRentId());
        jsonObject.put("adminAreaId", project.getAdminAreaId());
        jsonObject.put("projectCertUrl", project.getProjectCertUrl());
        jsonObject.put("deviceNum", String.valueOf(project.getDeviceNum()));
        jsonObject.put("workerNum", String.valueOf(project.getWorkerNum()));
        jsonObject.put("projectContractUrl", project.getProjectContractUrl());
        jsonObject.put("projectBuilders", project.getProjectBuilders());
        jsonObject.put("projectEndUrl", project.getProjectEndUrl());
        jsonObject.put("adminProjectId", project.getAdminProjectId());
        jsonObject.put("storeOut", project.getStoreOut());
        jsonObject.put("owner", project.getOwner());
        jsonObject.put("region", project.getRegion());
        jsonObject.put("coordinate",project.getCoordinate());
        jsonObject.put("servicePeriod",project.getServicePeriod());
        jsonObject.put("regionManager",project.getRegionManager());
        jsonObject.put("marketSalesman", project.getMarketSalesman());
        jsonObject.put("remarks", project.getRemarks());
        jsonObject.put("perfectState",String.valueOf(project.getPerfectState()));
        jsonObject.put("planeState",String.valueOf(project.getPlaneState()));
        jsonObject.put("boxList", project.getBoxList());
        jsonObject.put("worker", project.getWorker());
        jsonObject.put("companyName", project.getCompanyName());
        jsonObject.put("adminAreaUser", project.getAdminAreaUser());
        jsonObject.put("adminRentUser", project.getAdminRentUser());
        try {
            redisUtil.set(project.getProjectId(), jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除项目信息
     */
    @Override
    public void delProject(String key){
        redisUtil.del(key);
    }

    /**
     * 从Redis中获取人员信息
     */
    @Override
    public User getUser(String key){
        JSONObject jsonObject = redisUtil.get(key) == null ? null : (JSONObject) redisUtil.get(key);
        if(jsonObject != null){
            User user = new User();
            user.setCreateDate(jsonObject.getString("createDate"));
            user.setUserId(jsonObject.getString("userId"));
            user.setUserName(jsonObject.getString("userName"));
            user.setUserPassword(jsonObject.getString("userPassword"));
            user.setUserRole(jsonObject.getString("userRole"));
            user.setUserPerm(jsonObject.getString("userPerm"));
            user.setUserPhone(jsonObject.getString("userPhone"));
            user.setUserImage(jsonObject.getString("userImage"));
            user.setUserAge(Integer.parseInt(jsonObject.getString("userAge")));
            user.setUserSex(jsonObject.getString("userSex"));
            user.setUserAccount(jsonObject.getString("userAccount"));
            user.setUserNative(jsonObject.getString("userNative"));
            user.setChecked((Boolean) jsonObject.get("isChecked"));
            return user;
        }else return null;
    }

    /**
     * 人员信息写入Redis
     */
    @Override
    public void setUser(User user){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userAge",user.getUserAge());
        jsonObject.put("userSex",user.getUserSex());
        jsonObject.put("userAccount",user.getUserAccount());
        jsonObject.put("userNative",user.getUserNative());
        jsonObject.put("userId", user.getUserId());
        jsonObject.put("userName", user.getUserName());
        jsonObject.put("userPassword", user.getUserPassword());
        jsonObject.put("userRole", user.getUserRole());
        jsonObject.put("userPerm", user.getUserPerm());
        jsonObject.put("userPhone", user.getUserPhone());
        jsonObject.put("userImage", user.getUserImage());
        jsonObject.put("createDate", user.getCreateDate());
        jsonObject.put("isChecked", user.isChecked());
        try {
            redisUtil.set(user.getUserId(), jsonObject);
            if(user.getUserPhone() != null)
                if(user.getUserPhone().length() == 11)
                    redisUtil.set(user.getUserPhone(), jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除人员信息
     */
    @Override
    public void delUser(String key){
        redisUtil.del(key);
    }

}

