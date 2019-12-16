package com.manager.nacelle_rent.dao;


import com.manager.nacelle_rent.entity.SimpleUser;
import com.manager.nacelle_rent.entity.User;
import javafx.beans.binding.ObjectExpression;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    //根据ID获取web账户登录的信息
    User getUserById(@Param("userId") String userId);

    User getUserInfo(@Param("userId") String userId);

    String judgeProAdmin(@Param("userId") String userId);

    SimpleUser getNameById(@Param("userId") String userId);

    User getUserByPhone(@Param("userPhone") String userPhone);

    List<User> getUserPhoneDepartment(@Param("userPhone") String userPhone);

    List<User> getUserNameDepartment(@Param("userPhone") String userPhone);

    //得到用户的最小ID
    User getMinId();

    //得到所有区域管理员的id和姓名
    List<User> getAllAreaAdmin();

    //得到所有租方管理员的id和姓名
    List<User> getAllRentAdmin();

    //区域管理员、租方管理员、工作人员注册，默认是未认证状态
    void registerUser(User user);

    //获得所有尚未经过审核的账号信息
    List<User>  getRegisterUnChecked();
    //获取所有审核过的账号信息
    List<User>  getAllAccount();

    //获得所有尚未经审核的账号数目
    int getRegisterUnCheckedNum();
    int sumOfUser();
    int sumOfDevice(@Param("projectId") String projectId);
    int sumOfDeviceUn(@Param("projectId") String projectId);
    //根据WEB返回结果处理注册审核
    void deleteUser(String[] multipleUserId);
    void updateRegisterState(String[] multipleUserId);
    void updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword);

    void createWebAdmin(@Param("userRole") String userRole, @Param("userName") String userName, @Param("userPassword") String userPassword,
                        @Param("userId") String userId, @Param("userPhone") String userPhone, @Param("isChecked") int isChecked);

    void createManageAdmin(@Param("userRole") String userRole, @Param("userName") String userName, @Param("userPassword") String userPassword,
                        @Param("userId") String userId, @Param("userPhone") String userPhone, @Param("userPerm") String userPerm, @Param("isChecked") int isChecked);

    void createProjectAdmin(@Param("projectId") String projectId, @Param("userId") String userId);

    void updateQualifications(@Param("qualificationImage") String qualificationImage, @Param("userId") String userId);

    boolean createInstaller(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId,
                            @Param("name") String name, @Param("phone") String phone, @Param("accountId") String accountId);
    boolean updateInstaller(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId,
                            @Param("name") String name, @Param("phone") String phone, @Param("accountId") String accountId);
    boolean deleteInstaller(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId, @Param("phone") String phone);
    boolean updateAllInstallState(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId,@Param("state") int state);
    boolean updatePicInstallState(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId,@Param("state") int state);

    List<Map<String, Object>> getInstaller(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId);
    List<Map<String, Object>> getDeviceListByInstaller(@Param("projectId") String projectId, @Param("userId") String userId);
    List<Map<String, Object>> getProjectInstallInfoByProjectId(@Param("projectId") String projectId);
    Map<String, Object> getAllParts(@Param("deviceId") String deviceId);
    Map<String, Object> getDeviceInstallInfo(@Param("projectId") String projectId, @Param("userId") String userId, @Param("deviceId") String deviceId);
    List<String> getProjectByInstaller(@Param("userId") String userId);

}
