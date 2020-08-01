package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.*;
import com.manager.nacelle_rent.entity.ElectricBoxState;
import com.manager.nacelle_rent.entity.UserCheckedRecord;
import com.manager.nacelle_rent.enums.ElectricBoxStateEnum;
import com.manager.nacelle_rent.enums.ProjectStateEnum;
import com.manager.nacelle_rent.service.ProjectService;
import com.manager.nacelle_rent.service.RedisService;
import com.manager.nacelle_rent.utils.mapUtils;
import com.manager.nacelle_rent.entity.User;
import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.service.UserService;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ElectricStateMapper electricStateMapper;
    @Autowired
    private ElectricBoxMapper electricBoxMapper;
    @Autowired
    private ProjectWorkerMapper projectWorkerMapper;
    @Autowired
    private RedisService redisService;
    @Value("${APP_SECRET_KEY}")
    private String APP_SECRET_KEY;
    @Value("${MY_PACKAGE_NAME}")
    private String MY_PACKAGE_NAME;
    @Value("${CONFIGURATION_LIST_TOPIC}")
    private String CONFIGURATION_LIST_TOPIC;

    @Override
    public User webLoad(String webAdminId) {
        User user;
        if(redisService.getUser(webAdminId) != null){
            user = redisService.getUser(webAdminId);
        }else {
            user = userMapper.getUserInfo(webAdminId);
            if(user != null){
                redisService.setUser(user);
            }
        }
        return user;
    }
    @Override
    public User androidLoad(String userPhone) {
        User user;
        if(userPhone.length() == 11) {
            if (redisService.getUser(userPhone) != null) {
                user = redisService.getUser(userPhone);
            } else {
                user = userMapper.getUserByPhone(userPhone);
                if (user != null) {
                    redisService.setUser(user);
                }
            }
        }else return userMapper.getUserByPhone(userPhone);
        return user;
    }
    @Override
    public User getUserInfo(String userId){
        User userInfo;
        if(redisService.getUser(userId) != null){
            userInfo = redisService.getUser(userId);
        }else {
            userInfo = userMapper.getUserInfo(userId);
            if(userInfo != null){
                redisService.setUser(userInfo);
            }
        }
        return userInfo;
    }
    @Override
    public String createWebAdmin(Map<String, String> map){
        String userRole = map.get("userRole");
        String userPassword = map.get("userPassword");
        String userName = map.get("userName");
        String userPhone = map.get("userPhone");
        int nowId = Integer.parseInt(userMapper.getMinId().getUserId());
        String userId = Integer.toString(nowId - 1);
        try {
            if(userMapper.getUserByPhone(userPhone) != null)
                return "repeat";
            userMapper.createWebAdmin(userRole,userName,userPassword,userId,userPhone, 1);
            User user = userMapper.getUserInfo(userId);
            redisService.setUser(user);
            return userId;
        }catch (Exception e){
            return "fail";
        }
    }
    @Override
    public String createManageAdmin(Map<String, String> map){
        String userRole = map.get("userRole");
        String userPassword = map.get("userPassword");
        String userName = map.get("userName");
        String userPhone = map.get("userPhone");
        String userPerm = map.get("userPerm");
        int nowId = Integer.parseInt(userMapper.getMinId().getUserId());
        String userId = Integer.toString(nowId - 1);
        try {
            if(userMapper.getUserByPhone(userPhone) != null)
                return "repeat";
            userMapper.createManageAdmin(userRole,userName,userPassword,userId,userPhone,userPerm, 1);
            User user = userMapper.getUserInfo(userId);
            redisService.setUser(user);
            return userId;
        }catch (Exception e){
            return "fail";
        }
    }
    @Override
    public String getQualifications(String userId){
        try {
            return userMapper.getUserInfo(userId).getUserPerm();
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }
    @Override
    public String updatePassword(String userId, String oldPassword, String newPassword){
        try {
            User user;
            if(redisService.getUser(userId) != null){
                user = redisService.getUser(userId);
            }else {
                user = userMapper.getUserInfo(userId);
                if(user != null){
                    redisService.setUser(user);
                }
            }
            if(user.getUserPassword().equals(oldPassword)) {
                userMapper.updatePassword(userId, newPassword);
                User user1 = userMapper.getUserInfo(userId);
                redisService.setUser(user1);
                return "success";
            }else return "IncorrectPassword";
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }
    @Override
    public String updateWebAccountInfo(String userId, String oldPassword, int type, Map<String, Object> info){
        try {
            User user;
            if(redisService.getUser(userId) != null){
                user = redisService.getUser(userId);
            }else {
                user = userMapper.getUserInfo(userId);
                if(user != null){
                    redisService.setUser(user);
                }
            }
            if (type == 4){
                userMapper.updateUserRole(userId, (String) info.get("userRole"));
                User user4 = userMapper.getUserInfo(userId);
                redisService.setUser(user4);
                return "success";
            }
            if(user.getUserPassword().equals(oldPassword)) {
                switch (type){
                    case 1://修改密码
                        userMapper.updatePassword(userId, (String) info.get("newPassword"));
                        User user1 = userMapper.getUserInfo(userId);
                        redisService.setUser(user1);
                        break;
                    case 2://修改手机号
                        userMapper.updateUserPhone(userId, (String) info.get("userPhone"));
                        User user2 = userMapper.getUserInfo(userId);
                        redisService.setUser(user2);
                        break;
                    case 3://修改用户名
                        userMapper.updateUserName(userId, (String) info.get("userName"));
                        User user3 = userMapper.getUserInfo(userId);
                        redisService.setUser(user3);
                        break;
                }
                return "success";
            }else return "IncorrectPassword";
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }
    @Override
    public int registerAndroidUser(User user){
        if(userMapper.getUserByPhone(user.getUserPhone()) != null){
            return 0;  //用户名已经存在
        }
        try{
            //System.out.println(userMapper.getMinId().getUserId());
            int nowId = Integer.parseInt(userMapper.getMinId().getUserId());
            user.setUserId(Integer.toString(nowId - 1));
            userMapper.registerUser(user);
            redisService.setUser(user);
            return 1;  //成功写进数据库
        }catch (Exception ex){
            System.out.print(ex.toString());
            return 2;  //网络故障
        }
    }
    @Override
    public int getRegisterState(String userPhone) {
        userPhone = userPhone.split(",")[0];
        User user = userMapper.getUserByPhone(userPhone);
        if(user == null)
            return 0;
        else if(user.isChecked())
            return 2;
        else
            return 1;
    }
    @Override
    public int judgeProAdmin(String userId){
        if(userMapper.judgeProAdmin(userId)!=null)
            return 1;
        else return 0;
    }
    @Override
    public int deleteUser(String userId){
        try {
            String[] deleteUser = new String[1];
            deleteUser[0] = userId;
            String[] deleteUser1 = userId.split(",");
            for(String user : deleteUser1){
                User userInfo  = userMapper.getUserInfo(user);
                String projectId = projectWorkerMapper.getWorker(user).isEmpty() ? "" : projectWorkerMapper.getWorker(user).get(0);
                if (userInfo.getUserRole().contains("orker") || userInfo.getUserRole().equals("coating_painter") || userInfo.getUserRole().equals("coating_realStone")){
                    if(!projectId.equals("")) {
                        projectMapper.decreaseWorker(projectId);
                        projectWorkerMapper.deleteWorkerByUserId(user);
                    }
                }
                String userPhone = redisService.getUser(user) == null ? "" : redisService.getUser(user).getUserPhone();
                redisService.delUser(userPhone);
                redisService.delUser(user);
            }
            userMapper.deleteUser(deleteUser);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }
    @Override
    public int deleteUserForAndroid(String userId){
        try {
            String[] deleteUser1 = userId.split(",");
            for(String user : deleteUser1){
                User userInfo  = userMapper.getUserInfo(user);
                String projectId = projectWorkerMapper.getWorker(user).isEmpty() ? "" : projectWorkerMapper.getWorker(user).get(0);
                if (userInfo.getUserRole().contains("orker") || userInfo.getUserRole().equals("coating_painter") || userInfo.getUserRole().equals("coating_realStone")){
                    if(!projectId.equals("")) {
                        projectMapper.decreaseWorker(projectId);
                        projectWorkerMapper.deleteWorkerByUserId(user);
                    }
                }
            }
            return 1;
        }catch (Exception e){
            return 0;
        }
    }
    @Override
    public int updateQualifications(String userId, String type, int picNum){
        String qualificationImage = userMapper.getUserInfo(userId) == null ? null : userMapper.getUserInfo(userId).getUserPerm();
        qualificationImage = qualificationImage == null ? "" : qualificationImage;
        if(qualificationImage.equals("")) {
//            for (int i = 0; i < picNum; i++) {
//                qualificationImage += userId + "_" + (i + 1) + ".jpg" + ",";
//            }
            qualificationImage = type + "_" + picNum;
            try {
                userMapper.updateQualifications(qualificationImage, userId);
                User user = userMapper.getUserInfo(userId);
                redisService.setUser(user);
                return 0;
            } catch (Exception e) {
                return 1;
            }
        }else {////增加或者修改
            List<String> list = new ArrayList<>();
            String[] imageBefore = qualificationImage.split(",");
            for(String stringTypeAndNum : imageBefore){
                String[] typeAndNumStrings = stringTypeAndNum.split("_");
                if(!type.equals(typeAndNumStrings[0])) list.add(stringTypeAndNum);
            }
            list.add(type + "_" + picNum);
            String newQualifications = String.join(",", list);
//            String imageLast = imageBefore[imageBefore.length-1];
//            int j = imageLast.indexOf("_");
//            int k = imageLast.indexOf(".");
//            String num = imageLast.substring(j+1,k);
//            int numNow = Integer.parseInt(num);
//            for (int i = 0; i < picNum; i++) {
//                qualificationImage += userId + "_" + (numNow + i + 1) + ".jpg" + ",";
//            }
            try {
                userMapper.updateQualifications(newQualifications, userId);
                User user = userMapper.getUserInfo(userId);
                redisService.setUser(user);
                return 0;
            } catch (Exception e) {
                return 1;
            }
        }
    }
    @Override
    public List<User> getRegisterUnChecked(){
        return userMapper.getRegisterUnChecked();
    }
    @Override
    public int getRegisterUnCheckedNum(){
        return userMapper.getRegisterUnCheckedNum();
    }
    @Override
    public boolean handleRegister(String multipleUserId, String handleResult, String verifier){
        String[] multipleUserIdArray = new String[1];
        if(multipleUserId.contains(",")){
            multipleUserIdArray = multipleUserId.split(",");
        }else{
            multipleUserIdArray[0] = multipleUserId;
        }
        try{
            if(handleResult.equals("refuse")){
                for (String userId : multipleUserIdArray) {
                    User user = userMapper.getUserInfo(userId);
                    UserCheckedRecord userCheckedRecord = new UserCheckedRecord();
                    userCheckedRecord.setCreateDate(user.getCreateDate());
                    userCheckedRecord.setResult("拒绝");
                    userCheckedRecord.setUserId(userId);
                    userCheckedRecord.setUserName(user.getUserName());
                    userCheckedRecord.setUserPhone(user.getUserPhone());
                    userCheckedRecord.setUserRole(user.getUserRole());
                    userCheckedRecord.setVerifier(verifier);
                    userMapper.insertUserCheckedRecord(userCheckedRecord);
                }
                userMapper.deleteUser(multipleUserIdArray);
            }else if(handleResult.equals("pass")){
                userMapper.updateRegisterState(multipleUserIdArray);
                for(String userId : multipleUserIdArray){
                    User user = userMapper.getUserInfo(userId);
                    UserCheckedRecord userCheckedRecord = new UserCheckedRecord();
                    userCheckedRecord.setCreateDate(user.getCreateDate());
                    userCheckedRecord.setResult("通过");
                    userCheckedRecord.setUserId(userId);
                    userCheckedRecord.setUserName(user.getUserName());
                    userCheckedRecord.setUserPhone(user.getUserPhone());
                    userCheckedRecord.setUserRole(user.getUserRole());
                    userCheckedRecord.setVerifier(verifier);
                    userMapper.insertUserCheckedRecord(userCheckedRecord);
                    redisService.setUser(user);
                }
//                sendMessageToAlias();
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }
    @Override
    public boolean handleProjectBegin(String projectId, String handleResult){
        if(projectId != null && handleResult != null) {
            Project project = projectMapper.getProjectDetail(projectId);
            String storageList = projectService.getBasketList(projectId);
            String[] storage = null;
            if(storageList != null)
                storage = storageList.split(",");
            String id = project.getAdminAreaId();
            try {
                if(projectMapper.getProjectDetail(projectId).getProjectState().equals("3"))
                    return false;
                if (handleResult.equals("refuse")) {
                    projectMapper.updateState(projectId, ProjectStateEnum.getCode("吊篮安装验收").getCode());
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                } else if (handleResult.equals("pass")) {
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("进行中").getCode());
                    if(storage != null){
                        for(int i = 0 ; i < storage.length ; i++){
                            if(electricStateMapper.getBoxLog(storage[i]).getStorageState() == 21)
                                electricStateMapper.updateStateOut(storage[i], ElectricBoxStateEnum.getCode("使用中").getCode());
                        }
                    }
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                    sendBeginToAlias(id,projectId);
                }
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }else return false;
    }
    @Override
    public boolean handleProjectEnd(String projectId, String handleResult){
        if(projectId != null && handleResult != null) {
            Project project = projectMapper.getProjectDetail(projectId);
            String storageList = projectService.getBasketList(projectId);
            String[] storage = null;
            if(storageList != null)
                storage = storageList.split(",");
            String id = project.getAdminAreaId();
            try {
                if (handleResult.equals("refuse")) {
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("待成立项目部").getCode());
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                } else if (handleResult.equals("pass")) {
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("吊篮安装验收").getCode());
                    if(storage != null){
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],ElectricBoxStateEnum.getCode("待安装").getCode());
                        }
                    }
                    Project project1 = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project1);
                    sendEndToAlias(id);
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        }else return false;
    }
    @Override
    public boolean handleStoreIn(String projectId, String storeId, String handleResult){
        if(projectId != null && handleResult != null) {
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
            String storageList = projectService.getBasketList(projectId)==null?"":projectService.getBasketList(projectId);
//            String[] storage  = storageList.split(",");
            String id = project.getAdminAreaId()==null?"":project.getAdminAreaId();
            try {
                if (handleResult.equals("refuse")) {
                    electricStateMapper.updateStateOut(storeId, ElectricBoxStateEnum.getCode("使用中").getCode());
                } else if (handleResult.equals("pass")) {
                    if(!id.equals("")){
                        if(storageList.contains(storeId)){
                            try {
                                storageList = storageList.replace(storeId + ",", "");
//                                projectMapper.increaseBox(projectId, storageList);
                                electricStateMapper.updateStateOut(storeId, 0);
                                sendStorageToAlias(id);
                            }catch (Exception e){
                                return false;
                            }
                        }
                    }
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        }else return false;
    }
    //////报警推送
    private void sendMessageToAlias() throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "报警";
        String description = "着火啦";
        String alias = "9989";    //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    //////项目申请开始通过通知
    private void sendBeginToAlias(String alias, String projectId) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "项目开始申请";
        String description = "通过";
        map.put("type","3");
        map.put("projectId",projectId);
        map.put("projectName",projectMapper.getProjectDetail(projectId).getProjectName());
        //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .extra(map)
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    //////项目申请结束通过通知
    private void sendEndToAlias(String alias) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "项目结束申请";
        String description = "通过";
        map.put("type","3");
        //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .extra(map)
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    //////吊篮入库申请同通过通知
    private void sendStorageToAlias(String alias) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "吊篮入库申请";
        String description = "通过";
        map.put("type","3");
        //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .extra(map)
                .build();
//        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    //////租方管理员申请报停
    @Override
    public boolean pushConfigurationList(String pushList, String projectId){

        try {
            sendConfigurationList(pushList, projectId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void sendConfigurationList(String pushList, String projectId) throws Exception{
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        Map<String,String> map = new HashMap<String, String>();
        String title = "配置清单";
        String description = "项目"+ projectId +"配置清单信息";
        map.put("projectId",projectId);
        map.put("type","5");
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(projectId)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .extra(map)
                .build();
        sender.broadcast(message, CONFIGURATION_LIST_TOPIC,0); //根据topic, 发送消息到指定设备上
//        sender.sendToAlias(message,"9962",0);
//        sender.broadcastAll(message,3);
    }
    @Override
    public void sendMessageToAreaAdmin(String alias) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap<String, String>();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "申请";
        String description = "项目报停";
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
    public void sendRepairMessage(String projectId, String deviceId, Timestamp startTime) throws Exception {
        Constants.useOfficial();
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
        Map<String,String> map = new HashMap<String, String>();
        Sender sender = new Sender(APP_SECRET_KEY);
        String alias = project == null ? "" : project.getAdminProjectId();
        String projectName = project == null ? "" : project.getProjectName();
        String messagePayload = "这是一条消息";
        String title = "报修";
        String description = "报修吊篮ID" + deviceId;
        //alias非空白, 不能包含逗号, 长度小于128
        map.put("type","4");
        map.put("deviceId",deviceId);
        map.put("projectId",projectId);
        map.put("time",startTime.toString());
        map.put("projectName",projectName);
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
    public List<User> getAllAccount(){
        List<User> list = userMapper.getAllAccount();
        for(User user:list){
            user.setUserRole(mapUtils.roleMap.get(user.getUserRole()));
        }
        return list;
    }

    @Override
    public List<UserCheckedRecord> getUserCheckedRecord(int type){
        List<UserCheckedRecord> userCheckedRecordList;
        switch (type){
            case 1:
                userCheckedRecordList = userMapper.getUserCheckedRecord();
                break;
            default:
                userCheckedRecordList = userMapper.getUserCheckedRecord();
                break;
        }
        for (UserCheckedRecord userCheckedRecord : userCheckedRecordList){
            userCheckedRecord.setUserRole(mapUtils.roleMap.get(userCheckedRecord.getUserRole()));
        }
        return userCheckedRecordList;
    }

    @Override
    public int createInstaller(String projectId, String userId, String deviceId, String name, String phone, String accountId) {
        if(userMapper.createInstaller(projectId, userId, deviceId, name, phone, accountId)) return 1;
        return 0;
    }

    @Override
    public int updateInstaller(String projectId, String userId, String deviceId, String name, String phone, String accountId) {
        if(userMapper.updateInstaller(projectId,userId,deviceId,name,phone,accountId)) return 1;
        return 0;
    }

    @Override
    public List<JSONObject> getInstaller(String projectId, String userId, String deviceId) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        List<Map<String, Object>> maps = userMapper.getInstaller(projectId, userId , deviceId);
        for (Map<String, Object> map : maps) {
            jsonObjects.add(new JSONObject(map));
        }
        return jsonObjects;
    }

    @Override
    public JSONObject getProjectByInstaller(String userId, String projectId, int type) {
        JSONObject jsonObject = new JSONObject();
        switch (type){
            case 1:
                List<String> list = userMapper.getProjectByInstaller(userId);
                Set<String> strings = new HashSet<>(list);
                List<String> projectList = new ArrayList<>(strings);
                for(String id : projectList){
                    JSONObject jsonObject1 = new JSONObject();
                    Project projectDetail;
                    if(redisService.getProject(id) != null){
                        projectDetail = redisService.getProject(id);
                    }else {
                        projectDetail = projectMapper.getProjectDetail(id);
                        if(projectDetail != null) {
                            redisService.setProject(projectDetail);
                        }
                    }
                    jsonObject1.put(id, projectDetail);
                    jsonObject1.put(id + "allNum", userMapper.sumOfDevice(id));
                    jsonObject1.put(id + "installedSum",userMapper.sumOfDeviceUn(id));
                    jsonObject.put(id, jsonObject1);
                }
                break;
            case 2:
                List<Map<String, Object>> mapList = userMapper.getDeviceListByInstaller(projectId, userId);
                for (Map<String, Object> map : mapList) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put((String)map.get("device_id"), new JSONObject(map));
                    int user = userMapper.getInstaller(projectId, userId, (String)map.get("device_id")).size() == 0 ? 0 : 1;
                    Map<String, Object> device = userMapper.getAllParts((String)map.get("device_id"));
                    jsonObject1.put(map.get("device_id") + "_userState", user);
                    if(device == null || device.isEmpty()) jsonObject1.put(map.get("device_id") + "_deviceState", 0);
                    else jsonObject1.put(map.get("device_id") + "_deviceState", device.size() == 4 ? 1 : 0);
                    jsonObject1.put(map.get("device_id") + "_state", electricStateMapper.getBoxLog((String) map.get("device_id")).getStorageState());
                    jsonObject1.put(map.get("device_id") + "_flag",  (Integer) userMapper.getDeviceInstallInfoByDeviceId(projectId, (String) map.get("device_id")).get("flag"));
                    jsonObject1.put(map.get("device_id") + "_siteNo", electricStateMapper.getSiteNo((String) map.get("device_id")));
                    jsonObject.put((String) map.get("device_id"), jsonObject1);
                }
                break;
        }
        return jsonObject;
    }

    @Override
    public int deleteInstaller(String projectId, String userId,String deviceId, String phone) {
        if(userMapper.deleteInstaller(projectId, userId, deviceId, phone)) return 1;
        return 0;
    }

    @Override
    public int updateInstallState(String projectId, String userId, String deviceId, int state, int type) {
        switch (type){
            case 1:
                if(userMapper.getDeviceInstallInfo(projectId, userId, deviceId).get("pic_flag").equals("0") || userMapper.getInstaller(projectId,userId,deviceId) == null ||
                userMapper.getAllParts(deviceId) == null || userMapper.getAllParts(deviceId).size() != 4) return 0;
                if(userMapper.updateAllInstallState(projectId, userId, deviceId, state)) {
                    electricStateMapper.updateStateOut(deviceId,12);
                    return 1;
                }
                else return 0;
            case 2:
                if(userMapper.updatePicInstallState(projectId, userId, deviceId, state)) return 1;
                else return 0;
        }
        return 0;
    }

    @Override
    public JSONObject getProjectInstallInfoByProjectId(String projectId) {
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> mapList = userMapper.getProjectInstallInfoByProjectId(projectId);
        for (Map<String, Object> map : mapList) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put((String)map.get("device_id"), new JSONObject(map));
            int user = userMapper.getInstaller(projectId, (String) map.get("user_id") == null ?  "0" : (String) map.get("user_id"), (String)map.get("device_id")).size() == 0 ? 0 : 1;
            Map<String, Object> device = userMapper.getAllParts((String)map.get("device_id"));
            jsonObject1.put(map.get("device_id") + "_userState", user);
            if(device == null || device.isEmpty()) jsonObject1.put(map.get("device_id") + "_deviceState", 0);
            else jsonObject1.put(map.get("device_id") + "_deviceState", device.size() == 4 ? 1 : 0);
            ElectricBoxState electricBoxState = electricStateMapper.getBoxLog((String) map.get("device_id"));
            if(electricBoxState != null) jsonObject1.put(map.get("device_id") + "_stateInPro", electricBoxState.getStorageState());
            else jsonObject1.put(map.get("device_id") + "_stateInPro", 999);
            jsonObject1.put(map.get("device_id") + "_flag",  (Integer) userMapper.getDeviceInstallInfoByDeviceId(projectId, (String) map.get("device_id")).get("flag"));
            jsonObject1.put("installTeamName",userMapper.getUserInfo((String) map.get("user_id")) == null ? "" : userMapper.getUserInfo((String) map.get("user_id")));
            jsonObject1.put(map.get("device_id") + "_siteNo", electricBoxState == null ? "" : electricBoxState.getSiteNo());
            jsonObject.put((String) map.get("device_id"), jsonObject1);
        }
        return jsonObject;
    }

    @Override
    public List<User> getAllWebAccount() {
        List<User> list = userMapper.getAllWebAccount();
        for(User user:list){
            user.setUserRole(mapUtils.roleMap.get(user.getUserRole()));
        }
        return list;
    }
}
