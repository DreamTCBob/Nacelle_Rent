package com.manager.nacelle_rent.dao;


import com.manager.nacelle_rent.entity.SimpleUser;
import com.manager.nacelle_rent.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    //根据WEB返回结果处理注册审核
    void deleteUser(String[] multipleUserId);
    void updateRegisterState(String[] multipleUserId);

    void createWebAdmin(@Param("userRole") String userRole, @Param("userName") String userName, @Param("userPassword") String userPassword,
                        @Param("userId") String userId, @Param("userPhone") String userPhone, @Param("isChecked") int isChecked);

    void createProjectAdmin(@Param("projectId") String projectId, @Param("userId") String userId);

    void updateQualifications(@Param("qualificationImage") String qualificationImage, @Param("userId") String userId);
}
