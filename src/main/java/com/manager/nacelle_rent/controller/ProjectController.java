package com.manager.nacelle_rent.controller;
import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.dao.*;
import com.manager.nacelle_rent.entity.*;
import com.manager.nacelle_rent.enums.ProjectStateEnum;
import com.manager.nacelle_rent.service.*;
import com.manager.nacelle_rent.utils.DateUtil;
import com.manager.nacelle_rent.utils.FileUtil;
import com.manager.nacelle_rent.utils.RedisUtil;
import com.manager.nacelle_rent.utils.UserCheckUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "项目接口")
@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private PreStopService preStopService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private WorkTimeLogService workTimeLogService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ElectricStateMapper electricStateMapper;
    @Autowired
    private ElectricResMapper electricResMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ConfigurationList configurationList;
    @Autowired
    private RedisService redisService;

    @Value("${localFilePath}")
    private String localFilePath;

    @ApiOperation(value = "正式新建项目" ,  notes="只对超管开放")
    @PostMapping("/createProject")
    public JSONObject createProject(HttpServletRequest request, @RequestBody Project project){
        JSONObject jsonObject = new JSONObject();
        String file = project.getProjectContractUrl();
        Map<String, String> map = new HashMap<>();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(projectMapper.getProjectIdByAdmin(project.getAdminRentId())!=null) {
            if(!projectMapper.getProjectIdByAdmin(project.getAdminRentId()).getProjectId().equals(project.getProjectId())) {
                flag = 0;
                jsonObject.put("isAllowed", false);
            }
        }
        if(flag == 1) {
            if(FileUtil.base64ToPdf(file,project.getProjectId())){
                try{
                    if(projectService.checkProject(project.getProjectId())){
                        projectMapper.updateProject(project.getProjectId(), project.getProjectName(), DateUtil.timeToDate(project.getProjectStart()),
                                project.getProjectId() + ".txt", "", 1);
                        projectMapper.createCompany(project.getCompanyName(),project.getProjectId());
                        projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion(),project.getCoordinate(), project.getServicePeriod());
                        map.put("userRole","basketSupervisor");
                        map.put("userPassword","123456");
                        map.put("userName",project.getProjectName());
                        map.put("userPhone",project.getProjectId());
                        userService.createWebAdmin(map);
                        project.setProjectContractUrl(project.getProjectId() + ".txt");
                        redisService.setProject(project);
                        //projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion());
                    }else {
                        projectMapper.createProject(project.getProjectId(),project.getProjectName(), DateUtil.timeToDate(project.getProjectStart()),
                                project.getProjectId()+".txt",project.getAdminAreaId(),project.getAdminRentId());
                        projectMapper.updateProject(project.getProjectId(), project.getProjectName(), DateUtil.timeToDate(project.getProjectStart()),
                                project.getProjectId() + ".txt", "", 1);
                        projectMapper.createCompany(project.getCompanyName(),project.getProjectId());
                        projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion(),project.getCoordinate(), project.getServicePeriod());
                        map.put("userRole","basketSupervisor");
                        map.put("userPassword","123456");
                        map.put("userName",project.getProjectName());
                        map.put("userPhone",project.getProjectId());
                        userService.createWebAdmin(map);
                        project.setProjectContractUrl(project.getProjectId() + ".txt");
                        redisService.setProject(project);
                        //projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion());
                    }
                    jsonObject.put("isCreated",true);
                }catch(Exception e){
                    e.printStackTrace();
                    jsonObject.put("isCreated",false);
                }
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "提交并保存项目草稿" ,  notes="只对超管开放")
    @PostMapping("/editProject")
    public JSONObject editProject(HttpServletRequest request, @RequestBody Project project) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int) UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(projectMapper.getProjectIdByAdmin(project.getAdminRentId())!=null) {
            if(!projectMapper.getProjectIdByAdmin(project.getAdminRentId()).getProjectId().equals(project.getProjectId())) {
                flag = 0;
                jsonObject.put("isAllowed", false);
            }
        }
        if (flag == 1) {
            if (FileUtil.writeFile(project.getProjectId(), project.getProjectContractUrl())){
                try {
                    //如果已经存在，则编辑
                    if(projectService.checkProject(project.getProjectId())){
                        projectMapper.updateProject(project.getProjectId(), project.getProjectName(), DateUtil.timeToDate(project.getProjectStart()),
                                project.getProjectId()+".txt", "",0);
                        projectMapper.createCompany(project.getCompanyName(),project.getProjectId());
                        projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion(),project.getCoordinate(), project.getServicePeriod());
                        project.setProjectContractUrl(project.getProjectId() + ".txt");
                        redisService.setProject(project);
                    }else{
                        //否则新建
                        projectMapper.createProject(project.getProjectId(),project.getProjectName(), DateUtil.timeToDate(project.getProjectStart()),
                                project.getProjectId()+".txt","",project.getAdminRentId());
                        projectMapper.createCompany(project.getCompanyName(),project.getProjectId());
                        projectMapper.updateKeyWord(project.getProjectId(), project.getOwner(), project.getRegion(),project.getCoordinate(), project.getServicePeriod());
                        project.setProjectContractUrl(project.getProjectId() + ".txt");
                        redisService.setProject(project);
                    }
                    jsonObject.put("isEdit", true);
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObject.put("isEdit", false);
                }
            }else{
                jsonObject.put("isAllowed", false);
            }
        }
        return jsonObject;
    }

    @ApiOperation(value = "编辑项目工程部人员" ,  notes="只对超管开放")
    @PostMapping("/editProjectDepartment")
    public JSONObject editProjectDepartment(HttpServletRequest request, @RequestBody Map<String, String> information) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int) UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if (flag == 1) {
            try{
                if(projectService.editProjectDepartment(information.get("projectId"),information.get("adminAreaId"),information.get("adminProjectId")))
                    jsonObject.put("isEdit", true);
                else jsonObject.put("isEdit",false);
            }catch (Exception e){
                jsonObject.put("isEdit", false);
            }
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取项目草稿" ,  notes="只对超管开放")
    @GetMapping("/getCurrentEditProject")
    public JSONObject getCurrentEditProject(HttpServletRequest request, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            try {
                Project projectDetail = (Project) projectService.getProjectDetail(projectId).get("projectDetail");
                String resultString = FileUtil.readFile(localFilePath + projectId + ".txt");
                String userId = projectDetail.getAdminAreaId();
                String userName = userMapper.getNameById(userId) == null ? "" :  userMapper.getNameById(userId).getUserName();
                User adminAreaUser = new User();
                adminAreaUser.setUserName(userName);
                projectDetail.setAdminAreaUser(adminAreaUser);
                User adminRentUser = new User();
                adminRentUser.setUserName(userMapper.getUserById(projectDetail.getAdminRentId()).getUserName());
                projectDetail.setAdminRentUser(adminRentUser);
                projectDetail.setCompanyName(projectMapper.searchCompany(projectId));
                jsonObject.put("projectDetail", projectDetail);
                jsonObject.put("resultString", resultString);
                jsonObject.put("isRead", true);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isRead", false);
            }
        }else{
            jsonObject.put("isAllowed", false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取不同状态下的项目列表，验收状态有两种，发来2状态返回2和21状态数据" ,  notes="")
    @GetMapping("/projectInfo")
    public JSONObject getProjectInfo(HttpServletRequest request, @RequestParam int userFlag) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin,engineeringWebAdmin,salesWebAdmin,accountingWebAdmin").get("result");
        if(flag == 1){
            if(userFlag == 2) {
                List<JSONObject> list = projectService.getProjectList2(userFlag);
                if(list != null){
                    jsonObject.put("projectList",list);
                    System.out.println(list);
                }
            }else if(userFlag == 0){
                List<Project> projectList = projectService.getProjectList(userFlag);
                if (projectList != null) {
                    jsonObject.put("projectList", projectList);
                }
            }else {
                List<JSONObject> projectList = projectService.getProjectListAll();
                if (projectList != null) {
                    jsonObject.put("projectList", projectList);
                }
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取不同状态下的吊篮列表" ,  notes="")
    @GetMapping("/storeInfo")
    public JSONObject getStoreInfo(HttpServletRequest request, @RequestParam int userFlag) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
//            if(userFlag == 5) {
            List<JSONObject> storeList = projectService.getStoreList(userFlag);
            if (storeList != null) {
                jsonObject.put("storeList", storeList);
            }
            jsonObject.put("isAllowed",true);
//            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }
    @ApiOperation(value = "获取指定项目的详细信息" ,  notes="")
    @GetMapping("/projectDetailInfo")
    public JSONObject getProjectDetailInfo(@RequestParam String projectId,
                                           HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try {
                jsonObject = projectService.getProjectDetail(projectId);
                if(jsonObject == null){
                    jsonObject = new JSONObject();
                    jsonObject.put("isAllowed",true);
                    jsonObject.put("isRead", false);
                }else {
                    jsonObject.put("isAllowed", true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("isRead", false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取某一项目的吊篮列表" ,  notes="")
    @GetMapping("/getBasketList")
    public JSONObject getBasketList(@RequestParam String projectId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                String basketList = projectService.getBasketList(projectId);
                if(basketList != null) {
                    if(!basketList.equals("")) {
                        String[] basket = basketList.split(",");
                        for (int i = 0; i < basket.length; i++) {
                            jsonObject.put("storage" + i, electricStateMapper.getBoxLog(basket[i]));
                        }
                        jsonObject.put("basketList", basketList);
                    }else jsonObject.put("basketList", "");
                    jsonObject.put("isAllowed", true);
                }else {
                    jsonObject.put("basketList", "no");
                    jsonObject.put("isAllowed", true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("isRead",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取某一项目的人员列表" ,  notes="")
    @GetMapping("/getUserList")
    public JSONObject getUserList(@RequestParam String projectId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                List<User> userList = projectService.getUserList(projectId);
                jsonObject.put("userList",userList);
                jsonObject.put("isAllowed",true);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("isRead",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取项目中吊篮信息，以便预报停" ,  notes="")
    @GetMapping("/forecastStop")
    public JSONObject forecastStop(@RequestParam String userId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String boxList;
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                String projectId = projectService.getProjectIdByAdmin(userId);
                Project project;
                if(redisService.getProject(projectId) != null){
                    project = redisService.getProject(projectId);
                }else {
                    project = projectMapper.getProjectDetail(projectId);
                    if(project != null){
                        redisService.setProject(project);
                    }
                }
                if(!projectId.equals("")) {
                    if(project != null && projectService.getBasketList(projectId)!=null) {
                        boxList = projectService.getBasketList(projectId);
                        String[] b = boxList.split(",");
                        for (int i = 0; i < b.length; i++) {
                            if (!b[i].equals("")) {
                                ElectricBoxState electricBoxState = projectService.getBoxLog(b[i]);

                                String adminName = userService.getUserInfo(project.getAdminAreaId()).getUserName();
                                electricBoxState.setProjectId(adminName);
                                jsonObject.put("Box" + i, electricBoxState);
                            }
                        }
                        jsonObject.put("projectId", projectId);
                        jsonObject.put("isAllowed", true);
                    }
                    else {
                        jsonObject.put("projectId", projectId);
                        jsonObject.put("isAllowed", true);
                    }
                }else jsonObject.put("not","exist");
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("isRead",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取区域管理员负责的所有项目信息，以便出库" ,  notes="")
    @GetMapping("/getAllProject")
    public JSONObject getAllProject(@RequestParam String userId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                jsonObject.put("isAllowed", true);
                JSONObject jsonObject1 = projectService.getAllProjectByAdmin(userId);
                jsonObject.put("projectList",jsonObject1);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed", true);
                jsonObject.put("isRead",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "安卓扫码开工" ,  notes="")
    @PostMapping("/androidBeginWork")
    public JSONObject beginWork(HttpServletRequest request, @RequestParam String projectId, @RequestParam String userId, @RequestParam String boxId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                boolean result = projectService.beginWork(projectId,userId,boxId);
                if(result) {
                    jsonObject.put("beginWork",true);
                }else
                    jsonObject.put("beginWork", false);
            }catch (Exception ex){
                jsonObject.put("beginWork",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "安卓扫码下工" ,  notes="")
    @PostMapping("/androidEndWork")
    public JSONObject endWork(HttpServletRequest request,
                              @RequestParam String boxId,@RequestParam String userId,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                boolean result = projectService.endWork(projectId, userId, boxId);
                if(electricResMapper.getDevice(boxId).size() == 0)
                    jsonObject.put("hasPeople",false);
                else jsonObject.put("hasPeople",true);
                if(result) {
                    jsonObject.put("endWork",true);
                }else
                    jsonObject.put("endWork", false);
            }catch (Exception ex){
                jsonObject.put("endWork",false);
                jsonObject.put("hadPeople",true);
            }
            jsonObject.put("isAllowed",true);
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "安卓给项目添加吊篮" ,  notes="")
    @PostMapping("/androidIncreaseBasket")
    public JSONObject increaseBasket(HttpServletRequest request, @RequestParam String projectId, @RequestParam String boxId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                String result = projectService.increaseBox(projectId,boxId);
                if(result.equals("新增吊篮失败")) {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase","新增吊篮失败");
                }else if(result.equals("吊篮已存在项目中")) {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase","吊篮已存在项目中");
                }else {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase", result);
                }
            }catch (Exception ex){
                jsonObject.put("increase","新增吊篮失败");
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "安卓给项目添加工人" ,  notes="")
    @PostMapping("/androidIncreaseWorker")
    public JSONObject increaseWorker(HttpServletRequest request, @RequestParam String projectId, @RequestParam String userId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                String result = projectService.increaseWorker(projectId,userId);
                if(result.equals("新增工人失败")) {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase","新增工人失败");
                }else if(result.equals("工人已存在项目中")) {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase","工人已存在项目中");
                }else {
                    jsonObject.put("isAllowed", true);
                    jsonObject.put("increase", result);
                }
            }catch (Exception ex){
                jsonObject.put("increase",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /**
     * 没用了
     * @param request
     * @param projectId
     * @param deviceId
     * @param managerId
     * @param picNum
     * @return
     */
    @ApiOperation(value = "吊篮报停、吊篮状态参数为5" ,  notes="")
    @PostMapping("/storageControl")
    public JSONObject storageControl(HttpServletRequest request, @RequestParam String projectId, @RequestParam String deviceId,
                                     @RequestParam String managerId, @RequestParam int picNum){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                int result = projectService.storageControl(projectId,deviceId,managerId,"",5);
                jsonObject.put("isAllowed",true);
                switch (result){
                    case 0:
                        try {
                            jsonObject.put("update","成功");
                            jsonObject1.put("apply",projectService.getStoreInInfo(projectId,deviceId,managerId));
                            String push = jsonObject1.toJSONString();
                            //通知前端更新
                            //WebSocketServer.sendInfo(userString,"superWebAdmin");
                            messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                            System.out.println(push);
                        }catch (Exception e){
                            jsonObject.put("update","异常");
                        }
                        break;
                    case 1:
                        jsonObject.put("update","没有权限");
                        break;
                    case 2:
                        jsonObject.put("update","same state");
                        break;
                    case 3:
                        jsonObject.put("update","is working");
                        break;
                    case 4:
                        jsonObject.put("update","异常");
                        break;
                    default:jsonObject.put("update","异常");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }
    /**
     * 没用了
     * @return
     */
    @ApiOperation(value = "租方管理员预报停" ,  notes="")
    @PostMapping("/prepareEnd")
    public JSONObject prepareEnd(HttpServletRequest request, @RequestParam String projectId, @RequestParam String storageList){
        JSONObject jsonObject = new JSONObject();
        Project project;
        if(redisService.getProject(projectId) != null){
            project = redisService.getProject(projectId);
        }else {
            project = projectMapper.getProjectDetail(projectId);
            if(project != null){
                redisService.setProject(project);
            }
        }
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                int result = projectService.prepareEnd(projectId,storageList);
                jsonObject.put("isAllowed",true);
                switch (result){
                    case 0:
                        try {
                            if(project.getAdminAreaId()!=null && !project.getAdminAreaId().equals(""))
                                userService.sendMessageToAreaAdmin(project.getAdminAreaId());
                            jsonObject.put("update","申请成功");//返回安卓端发起报停申请成功
                        }catch (Exception e){
                            jsonObject.put("update","异常");
                        }
                        break;
                    case 1:
                        jsonObject.put("update","修改吊篮状态失败");
                        break;
                    default:jsonObject.put("update","异常");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }
    /**
     * 没用了
     * @return
     */
    @ApiOperation(value = "区域管理员预报停" ,  notes="")
    @PostMapping("/applyEnd")
    public JSONObject applyEnd(HttpServletRequest request, @RequestParam String projectId, @RequestParam int picNum, @RequestParam String managerId){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                int result = projectService.applyEnd(projectId,picNum,managerId);
                jsonObject.put("isAllowed",true);
                switch (result){
                    case 0:
                        try {
                            jsonObject.put("update","申请成功");//返回安卓端发起报停申请成功
                            jsonObject1.put("apply",projectService.getEndWorkInfo(projectId,picNum,managerId));////////推送给web的出库核查信息///////////
                            String push = jsonObject1.toJSONString();
                            //通知前端更新
                            //WebSocketServer.sendInfo(userString,"superWebAdmin");
                            messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                            System.out.println(push);
                        }catch (Exception e){
                            jsonObject.put("update","异常");
                        }
                        break;
                    case 1:
                        jsonObject.put("update","发起申请失败");
                        break;
                    case 2:
                        jsonObject.put("update","该项目不存在区域管理员");
                        break;
                    case 3:
                        jsonObject.put("update","您没有权限");
                        break;
                    case 4:
                        jsonObject.put("update","项目已经停止");
                        break;
                    default:jsonObject.put("update","异常");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /*
    **************4.23日修改内容****************
     */
    @ApiOperation(value = "吊篮安装申请" ,  notes="")
    @PostMapping("/installApply")
    public JSONObject installApply(HttpServletRequest request, @RequestParam String projectId, @RequestParam int picNum,
                                   @RequestParam String managerId){
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                int result = projectService.installApply(projectId,picNum,managerId);
                jsonObject.put("isAllowed",true);
                switch (result){
                    case 0:
                        try {
                            jsonObject.put("update",true);//返回安卓端发起出库申请成功
                            jsonObject1.put("apply",projectService.getBeginWorkInfo(projectId,picNum,managerId));////////推送给web的出库核查信息///////////
                            String push = jsonObject1.toJSONString();
                            //通知前端更新
                            //WebSocketServer.sendInfo(userString,"superWebAdmin");
                            messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                            System.out.println(push);
                        }catch (Exception e){
                            jsonObject.put("update","异常");
                        }
                        break;
                    case 1:
                        jsonObject.put("update","修改项目信息失败");
                        break;
                    case 2:
                        jsonObject.put("update","项目不存在");
                        break;
                    case 3:
                        jsonObject.put("update","没有权限");
                        break;
                    default:jsonObject.put("update","异常");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "安监证书申请" ,  notes="")
    @PostMapping("/beginProject")
    public JSONObject beginProject(HttpServletRequest request, @RequestParam String projectId, @RequestParam String storageList, @RequestParam String managerId){
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                int result = projectService.beginProject(projectId, storageList, managerId);
                jsonObject.put("isAllowed",true);
                switch (result){
                    case 0:
                        try {
                            jsonObject.put("update",true);//返回安卓端发起出库申请成功
                            jsonObject1.put("apply",projectService.getStoreCertInfo(projectId,0,managerId));////////推送给web的出库核查信息///////////
                            String push = jsonObject1.toJSONString();
                            //通知前端更新
                            //WebSocketServer.sendInfo(userString,"superWebAdmin");
                            messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                            System.out.println(push);
                        }catch (Exception e){
                            jsonObject.put("update","异常");
                        }
                        break;
                    case 1:
                        jsonObject.put("update","修改项目信息失败");
                        break;
                    case 2:
                        jsonObject.put("update","项目不存在");
                        break;
                    case 3:
                        jsonObject.put("update","没有权限");
                        break;
                    default:jsonObject.put("update","异常");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "租方管理员发起吊篮报停" ,  notes="")
    @PostMapping("/storageEnd")
    public JSONObject storageEnd(HttpServletRequest request, @RequestParam String projectId, @RequestParam String deviceList,
                                     @RequestParam String managerId, @RequestParam int picNum){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        int result;
        String image = "";
        for(int i = 0 ; i < picNum ; i++) {
            image += projectId + "_" + deviceList + "_" + (i+1) + ".jpg" + ";";
        }
        String[] deviceId = null;
        if(deviceList != null)
            deviceId = deviceList.split(",");
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1) {
            try {
                if(deviceId != null) {
                    int sum = deviceId.length;
                    for (int i = 0; i < deviceId.length; i++) {
                        result = projectService.storageControl(projectId, deviceId[i], managerId, image, 5);
                        jsonObject.put("isAllowed", true);
                        switch (result) {
                            case 0:
                                try {
                                    jsonObject.put("update", "成功");
                                    jsonObject1.put("apply", projectService.getStoreInInfo(projectId, deviceId[i], managerId));
                                    String push = jsonObject1.toJSONString();
                                    System.out.println(push);
                                    //通知前端更新
                                    //WebSocketServer.sendInfo(userString,"superWebAdmin");
                                    messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                                } catch (Exception e) {
                                    jsonObject.put("update", "异常");
                                }
                                break;
                            case 1:
                                jsonObject.put("update", "没有权限");
                                sum -= 1;
                                break;
                            case 2:
                                jsonObject.put("update", "same state");
                                sum -= 1;
                                break;
                            case 3:
                                jsonObject.put("update", "is working");
                                sum -= 1;
                                break;
                            case 4:
                                jsonObject.put("update", "异常");
                                sum -= 1;
                                break;
                            default:
                                sum -= 1;
                                jsonObject.put("update", "异常");
                        }
                    }
                    projectMapper.createElectricBoxStopInfo(projectId,managerId,sum);
                }else {
                    jsonObject.put("update",false);
                    jsonObject.put("isAllowed",true);
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",true);
                jsonObject.put("update",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }
    @ApiOperation(value = "新增可报停吊篮信息" ,  notes="")
    @PostMapping("/createPreStop")
    public JSONObject createPreStop(HttpServletRequest request, @RequestParam int days, @RequestParam int num, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                int result = preStopService.createPreStop(days, num, projectId);
                switch (result){
                    case 0:
                        jsonObject.put("increase","成功");
                        break;
                    case 1:
                        jsonObject.put("increase","数据库插入异常");
                        break;
                    case 2:
                        jsonObject.put("increase","项目id异常");
                        break;
                }
                jsonObject.put("isAllowed",true);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("increase","失败");
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /*/

    @ApiOperation(value = "获取可用吊篮的数目以及状态" ,  notes="只对超管开放")
    @PostMapping(value="/getStorageSum")
    public JSONObject getStorageSum(HttpServletRequest request, @RequestParam int userFlag){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            jsonObject.put("info",projectService.getStorageSum(userFlag));
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /*
     **************5.14日修改内容****************
     */

    @ApiOperation(value = "吊篮入库" ,  notes="巡检人员")
    @PostMapping(value="/storageIn")
    public JSONObject storageIn(HttpServletRequest request, @RequestParam String projectId, @RequestParam String deviceId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                int result = projectService.storageIn(projectId, deviceId);
                switch (result){
                    case 0:
                        jsonObject.put("result","入库成功");
                        break;
                    case 1:
                        jsonObject.put("result","吊篮未报停");
                        break;
                    case 2:
                        jsonObject.put("result","吊篮不存在");
                        break;
                    case 3:
                        jsonObject.put("result","入库失败");
                        break;
                }
            } catch (Exception e){
                jsonObject.put("result","入库失败");
            }
            jsonObject.put("isAllowed",true);
        }else{
            jsonObject.put("isAllowed",false);
    }
        return jsonObject;
    }

    @ApiOperation(value = "获取首页所需内容" ,  notes="只对超管开放")
    @GetMapping(value="/getHomePageInfo")
    public JSONObject getHomePageInfo(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin,engineeringWebAdmin,salesWebAdmin,accountingWebAdmin").get("result");

        if(flag == 1){
            jsonObject.put("totalPreStopBasket", projectService.getStorageSum(1).get("usedSum"));
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上报异常吊篮信息" ,  notes="巡检人员")
    @PostMapping(value="/createExceptionBox")
    public JSONObject createExceptionBox(HttpServletRequest request, @RequestParam String deviceId,
                                         @RequestParam String managerId, @RequestParam String reason){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            String projectId = electricStateMapper.getBoxLog(deviceId) == null ? "" : electricStateMapper.getBoxLog(deviceId).getProjectId();
            int result = projectService.createExceptionBox(deviceId, projectId, managerId, reason);
            switch (result){
                case 0:
                    jsonObject.put("create","success");
                    break;
                case 1:
                    jsonObject.put("create","failed");
                    break;
                case 2:
                    jsonObject.put("create","notExistProject");
                    break;
                case 3:
                    jsonObject.put("create","notExist");
                    break;
                case 4:
                    jsonObject.put("create","notStop");
                    break;
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上报吊篮报修信息" ,  notes="巡检人员")
    @PostMapping(value="/createRepairBox")
    public JSONObject createRepairBox(HttpServletRequest request, @RequestBody Map<String, String> repair){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            String deviceId = repair.get("deviceId");
            String projectId = electricStateMapper.getBoxLog(repair.get("deviceId")) == null ? "" : electricStateMapper.getBoxLog(repair.get("deviceId")).getProjectId();
            repair.put("projectId",projectId);
            int result = projectService.createRepairBox(repair);
            switch (result){
                case 0:
                    try {
                        jsonObject.put("create","success");
                    }catch (Exception e){
                        jsonObject.put("create","failed");
                    }
                    break;
                case 1:
                    jsonObject.put("create","failed");
                    break;
                case 2:
                    jsonObject.put("create","notExistProject");
                    break;
                case 3:
                    jsonObject.put("create","notExist");
                    break;
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取修理信息" ,  notes="项目管理员")
    @GetMapping(value="/getRepairBox")
    public JSONObject getRepairBox(HttpServletRequest request, @RequestParam String projectId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            List<RepairBoxInfo> repairBoxInfo = projectService.getRepairBox(projectId);
            List<RepairBoxInfo> repairBoxInfoList = new ArrayList<>();
            for(RepairBoxInfo repairBoxInfo1 : repairBoxInfo){
                if(repairBoxInfo1.getEndTime() == null) {
                    repairBoxInfo1.setStartTimeS(repairBoxInfo1.getStartTime().toString());
                    String endTime = repairBoxInfo1.getEndTime() == null ? "" : repairBoxInfo1.getEndTime().toString();
                    repairBoxInfo1.setEndTimeS(endTime);
                    repairBoxInfoList.add(repairBoxInfo1);
                }
            }
            if(repairBoxInfoList != null){
                jsonObject.put("get","success");
                jsonObject.put("repairInfo",repairBoxInfoList);
            }else {
                jsonObject.put("get","notExit");
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取修理结束信息" ,  notes="项目管理员")
    @GetMapping(value="/getRepairEndBox")
    public JSONObject getRepairEndBox(HttpServletRequest request, @RequestParam String projectId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            List<RepairBoxInfo> repairBoxInfo = projectService.getRepairBox(projectId);
            List<RepairBoxInfo> repairBoxInfoList = new ArrayList<>();
            if(repairBoxInfo.size()!=0) {
                for (RepairBoxInfo repairBoxInfo1 : repairBoxInfo) {
                    if (repairBoxInfo1.getEndTime() != null) {
                        repairBoxInfo1.setStartTimeS(repairBoxInfo1.getStartTime().toString());
                        String endTime = repairBoxInfo1.getEndTime() == null ? "" : repairBoxInfo1.getEndTime().toString();
                        repairBoxInfo1.setEndTimeS(endTime);
                        repairBoxInfoList.add(repairBoxInfo1);
                    }
                }
            }
            if(repairBoxInfoList != null){
                jsonObject.put("get","success");
                jsonObject.put("repairEndInfo",repairBoxInfoList);
            }else {
                jsonObject.put("get","notExit");
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取单个修理信息" ,  notes="项目管理员")
    @GetMapping(value="/getRepairBoxOne")
    public JSONObject getRepairBoxOne(HttpServletRequest request, @RequestParam String deviceId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            List<RepairBoxInfo> repairBoxInfo = projectService.getRepairBoxOne(deviceId);
            for(RepairBoxInfo repairBoxInfo1 : repairBoxInfo){
                repairBoxInfo1.setStartTimeS(repairBoxInfo1.getStartTime().toString());
                String endTime = repairBoxInfo1.getEndTime() == null ? "" : repairBoxInfo1.getEndTime().toString();
                repairBoxInfo1.setEndTimeS(endTime);
            }
            if(repairBoxInfo != null){
                jsonObject.put("get","success");
                jsonObject.put("repairInfo",repairBoxInfo);
            }else {
                jsonObject.put("get","notExit");
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取单个修复信息" ,  notes="项目管理员")
    @GetMapping(value="/getRepairEndBoxOne")
    public JSONObject getRepairEndBoxOne(HttpServletRequest request, @RequestParam String deviceId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            List<RepairBoxInfo> repairBoxInfo = projectService.getRepairEndBoxOne(deviceId);
            List<RepairBoxInfo> repairBoxInfoList = new ArrayList<>();
            for(RepairBoxInfo repairBoxInfo1 : repairBoxInfo){
                if(repairBoxInfo1.getEndTime() != null) {
                    repairBoxInfoList.add(repairBoxInfo1);
                }
            }
            if(repairBoxInfoList != null){
                jsonObject.put("get","success");
                jsonObject.put("repairInfo",repairBoxInfoList);
            }else {
                jsonObject.put("get","notExit");
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上报吊篮修复信息" ,  notes="巡检人员")
    @PostMapping(value="/createRepairEndBox")
    public JSONObject createRepairEndBox(HttpServletRequest request, @RequestBody Map<String, String> repair){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            String projectId = electricStateMapper.getBoxLog(repair.get("deviceId")) == null ? "" : electricStateMapper.getBoxLog(repair.get("deviceId")).getProjectId();
            repair.put("projectId",projectId);
            int result = projectService.createRepairEndBox(repair);
            switch (result){
                case 0:
                    jsonObject.put("create","success");
                    break;
                case 1:
                    jsonObject.put("create","failed");
                    break;
                case 2:
                    jsonObject.put("create","notExistProject");
                    break;
                case 3:
                    jsonObject.put("create","notExist");
                    break;
            }
            jsonObject.put("isAllowed",true);
        } else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取区域管理员负责的所有项目信息，以便出库" ,  notes="")
    @GetMapping("/getProjectByProAdmin")
    public JSONObject getProjectByProAdmin(@RequestParam String userId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                jsonObject.put("isAllowed", true);
                String projectId = userMapper.judgeProAdmin(userId);
                Project project;
                if(projectId == null){
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
//                Project project = projectId == null ? null : projectMapper.getProjectDetail(projectId);
                jsonObject.put("project",project);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed", true);
                jsonObject.put("project",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /*
     **************5.24日修改内容****************
     */

    @ApiOperation(value = "将配置清单发给前端审核" ,  notes="区域管理员")
    @PostMapping("/pushConfigurationList")
    public JSONObject pushConfigurationList(@RequestBody Map<String,String> list,  HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                jsonObject.put("isAllowed", true);
                String resultList = projectService.pushConfigurationList(list);
                JSONObject jsonObject1 = JSONObject.parseObject(resultList);
                if(resultList.equals("fail"))
                    jsonObject.put("push","fail");
                else {
                    projectMapper.updateState(list.get("projectId"), ProjectStateEnum.getCode("清单待审核").getCode());
                    messagingTemplate.convertAndSend("/topic/subscribeTest", jsonObject1);
//                if(userService.pushConfigurationList(resultList,list.get("projectId"))) {
//                    jsonObject.put("push", "success");
//                }
//                else jsonObject.put("push","fail");
                    Project project = projectMapper.getProjectDetail(list.get("projectId"));
                    redisService.setProject(project);
                    jsonObject.put("push", "success");
                }
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed", true);
                jsonObject.put("push","fail");
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "请求某一项目的配置清单" ,  notes="好多人")
    @GetMapping("/getConfigurationList")
    public JSONObject getConfigurationList(@RequestParam String projectId,  HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                jsonObject.put("isAllowed", true);
                String resultList = projectService.getConfigurationList(projectId);
                if(resultList.equals("fail"))
                    jsonObject.put("get","fail");
                else {
                    JSONObject jsonObject1 = JSONObject.parseObject(resultList);
                    jsonObject.put("partsList",jsonObject1.get("partsList"));
                    jsonObject.put("get", "success");
                }
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed", true);
                jsonObject.put("get","fail");
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "将配置清单发给巡检人员" ,  notes="财务处人员")
    @PostMapping("/handleConfigurationList")
    public JSONObject handleConfigurationList(@RequestBody JSONObject data,  HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            String projectId = data.getString("projectId");
            jsonObject.put("isAllowed", true);
            //List<JSONObject> list = data.get("partsList");
            if(userService.pushConfigurationList(data.remove("partsList").toString(),projectId)) {
                try{
                    projectMapper.updateState(projectId,ProjectStateEnum.getCode("吊篮安装验收").getCode());
                    Project project = projectMapper.getProjectDetail(projectId);
                    redisService.setProject(project);
                    jsonObject.put("pass", "success");
                }catch (Exception e){
                    jsonObject.put("pass","fail");
                }
            }
            else jsonObject.put("pass","fail");
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    /*
    **************6.28日修改内容****************
    */

    @ApiOperation(value = "获取某一工人的工时" ,  notes="All")
    @GetMapping("/getWorkerTime")
    public JSONObject getWorkerTime(@RequestParam String userId,  HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                List<WorkTimeLog> workerTime = workTimeLogService.getWorkerTime(userId);
                jsonObject.put("get",workerTime);
                jsonObject.put("isAllowed", true);
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed", true);
                jsonObject.put("get","fail");
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取某一项目的吊篮列表" ,  notes="")
    @GetMapping("/getBasketListByAdmin")
    public JSONObject getBasketListByAdmin(@RequestParam String projectId, HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            try{
                String basketList = projectService.getBasketList(projectId);
                if(basketList != null) {
                    if(!basketList.equals("")) {
                        jsonObject.put("basketList", basketList);
                        String[] basket = basketList.split(",");
                        for (int i = 0; i < basket.length; i++) {
                            if(basket[i].equals("A")||basket[i].equals("B")) continue;
                            if(electricStateMapper.getBoxLog(basket[i]).getStorageState() != 0) {
                                jsonObject.put("storage" + i, electricStateMapper.getBoxLog(basket[i]));
                                basketList = basketList.replace(basket[i] + ",", "");
                            }
                        }
                    }else jsonObject.put("basketList", "");
                    jsonObject.put("isAllowed", true);
                }else {
                    jsonObject.put("basketList", "no");
                    jsonObject.put("isAllowed", true);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                jsonObject.put("isAllowed",true);
                jsonObject.put("isRead",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "删除项目" ,  notes="")
    @PostMapping("/deleteProject")
    public JSONObject deleteProject(HttpServletRequest request, @RequestParam String projectId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = projectService.deleteProject(projectId);
            if(result == 1)
                jsonObject.put("delete","success");
            else
                jsonObject.put("delete","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取单个吊篮信息" ,  notes="")
    @GetMapping("/getStorageInfo")
    public JSONObject getStorageInfo(HttpServletRequest request, @RequestParam String deviceId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            JSONObject jsonObject1 = projectService.getStorageInfo(deviceId);
            jsonObject.put("storageInfo", jsonObject1);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取正在工作的吊篮上的工人" ,  notes="")
    @GetMapping("/getWorker")
    public JSONObject getWorker(HttpServletRequest request, @RequestParam int type, @RequestParam String deviceId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            JSONObject jsonObject1 = projectService.getWorker(type, deviceId);
            jsonObject.put("workerList", jsonObject1);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "按照项目名或地区模糊查找项目(web前端专用)" ,  notes="")
    @GetMapping("/getProjectByVague")
    public JSONObject getProjectByVague(HttpServletRequest request, @RequestParam String subString){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            List<Project> list = projectService.getProjectByVague(subString);
            jsonObject.put("projectList", list);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取不同层级的项目列表(IOS专用)" ,  notes="")
    @GetMapping("/getProjectListByKey")
    public JSONObject getProjectListByKey(HttpServletRequest request, @RequestParam String keyWord, @RequestParam int type, @RequestParam int pageNum){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            List<Project> projectList = projectService.getProjectListByKey(keyWord, type, pageNum);
            jsonObject.put("projectList", projectList);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取指定项目的报修吊篮数目" ,  notes="")
    @GetMapping("/getRepairBoxNum")
    public JSONObject getRepairBoxNum(HttpServletRequest request, @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int num = projectService.getRepairBoxNum(projectId);
            jsonObject.put("repairBoxNum", num);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "新建安装信息" ,  notes="")
    @PostMapping("/createInstallInfo")
    public JSONObject createInstallInfo(HttpServletRequest request, @RequestParam String projectId, @RequestParam String userId, @RequestParam String deviceList){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = projectService.createInstallInfo(projectId,userId,deviceList);
            if(result == 1)
                jsonObject.put("create","success");
            else
                jsonObject.put("create","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取报警记录" ,  notes="")
    @GetMapping("/getAlarmInfo")
    public JSONObject getAlarmInfo(HttpServletRequest request, @RequestParam int type, @RequestParam String value){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            jsonObject.put("alarmInfo", projectService.getAlarmInfo(type, value));
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取报停记录" ,  notes="")
    @GetMapping("/getElectricStopInfo")
    public JSONObject getElectricStopInfo(HttpServletRequest request, @RequestParam int type, @RequestParam String value){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            jsonObject.put("electricBoxStopInfo", projectService.getElectricBoxStop(type, value));
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "前端上传平面图" ,  notes="")
    @PostMapping("/uploadPlaneGraph")
    public JSONObject uploadPlaneGraph(HttpServletRequest request, @RequestParam MultipartFile file, @RequestParam String projectId, @RequestParam int num , @RequestParam int type){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = 0;
            try {
                InputStream inputStream = file.getInputStream();
                result = projectService.uploadPlaneGraph(inputStream, projectId, num, type);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(result == 1)
                jsonObject.put("upload","success");
            else
                jsonObject.put("upload","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "平面图信息" , notes = "")
    @PostMapping("/uploadPlaneGraphInfo")
//    public JSONObject uploadPlaneGraphInfo(HttpServletRequest request, @RequestParam String projectId,
//                                           @RequestParam Map<Object, String> info,@RequestParam String buildingNum, @RequestParam int type){
    public JSONObject uploadPlaneGraphInfo(HttpServletRequest request, @RequestParam Map<String, Object> info){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            JSONObject jsonObject1 = JSONObject.parseObject((String) info.get("info"));
            JSONObject jsonObject2 = jsonObject1.getJSONObject("info");
            jsonObject.put("isLogin",true);
            int result = projectService.uploadPlaneGraphInfo(jsonObject2, jsonObject1.getString("projectId"), jsonObject1.getString("buildingNum"), Integer.parseInt(jsonObject1.getString("type")));
            if(result == 1)
                jsonObject.put("upload","success");
            else
                jsonObject.put("upload","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取平面图信息" ,  notes="")
    @GetMapping("/getPlaneGraphInfo")
    public JSONObject getPlaneGraphInfo(HttpServletRequest request, @RequestParam String projectId, @RequestParam String buildingNum, @RequestParam int type){
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            jsonObject.put("planeGraphInfo", projectService.getPlaneGraphInfo(projectId, buildingNum, type));
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

}
