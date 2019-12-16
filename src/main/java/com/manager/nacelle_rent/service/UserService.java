package com.manager.nacelle_rent.service;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface UserService {
    User webLoad(String webAdminId);
    User androidLoad(String userPhone);
    User getUserInfo(String userId);
    int registerAndroidUser(User user);
    int getRegisterState(String userPhone);
    int judgeProAdmin(String userId);
    int deleteUser(String userId);
    int updateQualifications(String userId, int picNum);
    String createWebAdmin(Map<String, String> map);
    String createManageAdmin(Map<String, String> map);
    String getQualifications(String userId);
    String updatePassword(String userId, String oldPassword, String newPassword);
    List<User> getRegisterUnChecked();
    List<User> getAllAccount();
    int getRegisterUnCheckedNum();
    boolean handleRegister(String userId, String handleResult);
    boolean handleProjectBegin(String projectId, String handleResult);
    boolean handleProjectEnd(String projectId, String handleResult);
    boolean handleStoreIn(String projectId, String storeId, String handleResult);
    boolean pushConfigurationList(String pushList, String projectId);
    int createInstaller(String projectId, String userId, String deviceId, String name, String phone, String accountId);
    int updateInstaller(String projectId, String userId,String deviceId, String name, String phone, String accountId);
    int deleteInstaller(String projectId, String userId,String deviceId, String phone);
    int updateInstallState(String projectId, String userId, String deviceId, int state, int type);
    void sendMessageToAreaAdmin(String alias) throws Exception;
    void sendRepairMessage(String projectId, String deviceId, Timestamp startTime) throws Exception;
    List<JSONObject> getInstaller(String projectId, String userId, String deviceId);
    JSONObject getProjectByInstaller(String userId, String projectId, int type);
    JSONObject getProjectInstallInfoByProjectId(String projectId);
}
