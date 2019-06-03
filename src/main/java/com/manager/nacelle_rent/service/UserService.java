package com.manager.nacelle_rent.service;

import com.manager.nacelle_rent.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User webLoad(String webAdminId);
    User androidLoad(String userPhone);
    User getUserInfo(String userId);
    int registerAndroidUser(User user);
    int getRegisterState(String userPhone);
    int judgeProAdmin(String userId);
    int updateQualifications(String userId, int picNum);
    String createWebAdmin(Map<String, String> map);
    String getQualifications(String userId);
    List<User> getRegisterUnChecked();
    List<User> getAllAccount();
    int getRegisterUnCheckedNum();
    boolean handleRegister(String userId, String handleResult);
    boolean handleProjectBegin(String projectId, String handleResult);
    boolean handleProjectEnd(String projectId, String handleResult);
    boolean handleStoreIn(String projectId, String storeId, String handleResult);
    boolean pushConfigurationList(String pushList, String projectId);
    void sendMessageToAreaAdmin(String alias) throws Exception;
    void sendRepairMessage(String projectId, String deviceId) throws Exception;
}
