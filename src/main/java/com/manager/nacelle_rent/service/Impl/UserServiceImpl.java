package com.manager.nacelle_rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.ElectricStateMapper;
import com.manager.nacelle_rent.dao.ProjectMapper;
import com.manager.nacelle_rent.dao.UserMapper;
import com.manager.nacelle_rent.utils.mapUtils;
import com.manager.nacelle_rent.entity.User;
import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.service.UserService;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ElectricStateMapper electricStateMapper;
    @Value("${APP_SECRET_KEY}")
    private String APP_SECRET_KEY;
    @Value("${MY_PACKAGE_NAME}")
    private String MY_PACKAGE_NAME;
    @Value("${CONFIGURATION_LIST_TOPIC}")
    private String CONFIGURATION_LIST_TOPIC;

    @Override
    public User webLoad(String webAdminId) {
            return userMapper.getUserById(webAdminId);
    }
    @Override
    public User androidLoad(String userPhone) {
        return userMapper.getUserByPhone(userPhone);
    }
    @Override
    public User getUserInfo(String userId){return userMapper.getUserInfo(userId);}
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
    public int registerAndroidUser(User user){
        if(userMapper.getUserByPhone(user.getUserPhone()) != null){
            return 0;  //用户名已经存在
        }
        try{
            //System.out.println(userMapper.getMinId().getUserId());
            int nowId = Integer.parseInt(userMapper.getMinId().getUserId());
            user.setUserId(Integer.toString(nowId - 1));
            userMapper.registerUser(user);
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
    public int updateQualifications(String userId, int picNum){
        String qualificationImage = userMapper.getUserInfo(userId) == null ? "" : userMapper.getUserInfo(userId).getUserPerm();
        qualificationImage = qualificationImage == null ? "" : qualificationImage;
        if(qualificationImage.equals("")) {
            for (int i = 0; i < picNum; i++) {
                qualificationImage += userId + "_" + (i + 1) + ".jpg" + ",";
            }
            try {
                userMapper.updateQualifications(qualificationImage, userId);
                return 0;
            } catch (Exception e) {
                return 1;
            }
        }else {////增加图片
            String[] imageBefore = qualificationImage.split(",");
            String imageLast = imageBefore[imageBefore.length-1];
            int j = imageLast.indexOf("_");
            int k = imageLast.indexOf(".");
            String num = imageLast.substring(j+1,k);
            int numNow = Integer.parseInt(num);
            for (int i = 0; i < picNum; i++) {
                qualificationImage += userId + "_" + (numNow + i + 1) + ".jpg" + ",";
            }
            try {
                userMapper.updateQualifications(qualificationImage, userId);
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
    public boolean handleRegister(String multipleUserId, String handleResult){
        String[] multipleUserIdArray = new String[1];
        if(multipleUserId.contains(",")){
            multipleUserIdArray = multipleUserId.split(",");
        }else{
            multipleUserIdArray[0] = multipleUserId;
        }
        try{
            if(handleResult.equals("refuse")){
                userMapper.deleteUser(multipleUserIdArray);
            }else if(handleResult.equals("pass")){
                userMapper.updateRegisterState(multipleUserIdArray);
                sendMessageToAlias();
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
            String storageList = project.getBoxList();
            String[] storage = null;
            if(storageList != null)
                storage = storageList.split(",");
            String id = project.getAdminAreaId();
            try {
                if(projectMapper.getProjectDetail(projectId).getProjectState().equals("3"))
                    return false;
                if (handleResult.equals("refuse")) {
                    projectMapper.updateState(projectId,2);
                } else if (handleResult.equals("pass")) {
                    projectMapper.updateState(projectId,3);
                    if(storage != null){
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],3);
                        }
                    }
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
            String storageList = project.getBoxList();
            String[] storage = null;
            if(storageList != null)
                storage = storageList.split(",");
            String id = project.getAdminAreaId();
            try {
                if (handleResult.equals("refuse")) {
                    projectMapper.updateState(projectId,1);
                } else if (handleResult.equals("pass")) {
                    projectMapper.updateState(projectId,2);
                    if(storage != null){
                        for(int i = 0 ; i < storage.length ; i++){
                            electricStateMapper.updateStateOut(storage[i],1);
                        }
                    }
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
            Project project = projectMapper.getProjectDetail(projectId);
            String storageList = project.getBoxList()==null?"":project.getBoxList();
            String[] storage  = storageList.split(",");
            String id = project.getAdminAreaId()==null?"":project.getAdminAreaId();
            try {
                if (handleResult.equals("refuse")) {
                    electricStateMapper.updateStateOut(storeId, 5);
                } else if (handleResult.equals("pass")) {
                    if(!id.equals("")){
                        if(storageList.contains(storeId)){
                            try {
                                storageList = storageList.replace(storeId + ",", "");
//                                projectMapper.increaseBox(projectId, storageList);
                                electricStateMapper.updateStateOut(storeId, 5);
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
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "项目结束申请";
        String description = "通过";
        //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
    }
    //////吊篮入库申请同通过通知
    private void sendStorageToAlias(String alias) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET_KEY);
        String messagePayload = "这是一条消息";
        String title = "吊篮入库申请";
        String description = "通过";
        //alias非空白, 不能包含逗号, 长度小于128
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(-1)     // 使用默认提示音提示
                .build();
        sender.sendToAlias(message, alias, 3); //根据alias, 发送消息到指定设备上
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
    public void sendRepairMessage(String projectId, String deviceId) throws Exception {
        Constants.useOfficial();
        Map<String,String> map = new HashMap<String, String>();
        Sender sender = new Sender(APP_SECRET_KEY);
        String alias = projectMapper.getProjectDetail(projectId) == null ? "" : projectMapper.getProjectDetail(projectId).getAdminProjectId();
        String messagePayload = "这是一条消息";
        String title = "报修";
        String description = "报修吊篮ID";
        //alias非空白, 不能包含逗号, 长度小于128
        map.put("type","4");
        map.put("deviceId",deviceId);
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
}
