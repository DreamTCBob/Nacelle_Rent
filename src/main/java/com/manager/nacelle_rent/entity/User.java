package com.manager.nacelle_rent.entity;


import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.utils.mapUtils;
import java.util.Date;

public class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userRole;  //用户的角色
    private String userPerm;  //用户的权限
    private String userPhone;
    private String userImage;
    private String createDate;
    private String userSex;
    private String userNative;
    private String userAccount;
    private int userAge;
    private boolean isChecked;
    public static String userToJson(User user){
        String roleName = mapUtils.roleMap.get(user.getUserRole());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 0);
        jsonObject.put("userId",user.getUserId());
        jsonObject.put("userName", user.getUserName());
        jsonObject.put("userPhone",user.getUserPhone());
        jsonObject.put("userImage",user.getUserImage());
        jsonObject.put("userPerm",user.getUserPerm());
        jsonObject.put("userRole",roleName);
        jsonObject.put("createDate",new Date().getTime());
        return jsonObject.toJSONString();
    }
    public static JSONObject jsonObject(User user){
        String roleName = mapUtils.roleMap.get(user.getUserRole());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 0);
        jsonObject.put("userId",user.getUserId());
        jsonObject.put("userAge",user.getUserAge());
        jsonObject.put("userNative",user.getUserNative());
        jsonObject.put("userSex",user.getUserSex());
        jsonObject.put("userName", user.getUserName());
        jsonObject.put("userAccount", user.getUserAccount());
        jsonObject.put("userPerm",user.getUserPerm());
        jsonObject.put("userPhone",user.getUserPhone());
        jsonObject.put("userImage",user.getUserImage());
        jsonObject.put("userRole",roleName);
        jsonObject.put("createDate",new Date().getTime());
        return jsonObject;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserPerm() {
        return userPerm;
    }

    public void setUserPerm(String userPerm) {
        this.userPerm = userPerm;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserNative() {
        return userNative;
    }

    public void setUserNative(String userNative) {
        this.userNative = userNative;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }
}
