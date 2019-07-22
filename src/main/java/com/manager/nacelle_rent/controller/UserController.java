package com.manager.nacelle_rent.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.manager.nacelle_rent.dao.ElectricResMapper;
import com.manager.nacelle_rent.dao.ProjectMapper;
import com.manager.nacelle_rent.dao.UserMapper;
import com.manager.nacelle_rent.entity.ElectricRes;
import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.entity.User;
import com.manager.nacelle_rent.service.ProjectService;
import com.manager.nacelle_rent.service.UserService;
import com.manager.nacelle_rent.utils.JwtUtil;
import com.manager.nacelle_rent.utils.UserCheckUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(description = "用户接口")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ElectricResMapper electricResMapper;
    @Autowired
    private ProjectMapper projectMapper;

    private static Logger logger=Logger.getLogger(UserController.class); // 获取logger实例

    @ApiOperation(value = "安卓用户注册" ,  notes="不需要进行身份验证")
    @PostMapping("/checkRegister")
    public JSONObject pushToWeb(@RequestBody User user) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        String imageUrl = user.getUserPhone() + ".jpg";
        user.setUserImage(imageUrl);
        //将信息插入数据库
       // if(FileUtil.upload(file,imageUrl)){
        int resultFlag = userService.registerAndroidUser(user);
        jsonObject2 = User.jsonObject(user);
        jsonObject1.put("apply",jsonObject2);
        String push = jsonObject1.toJSONString();
        System.out.println(push);
        if(resultFlag == 0) {
            //回复客户端已经有相同的id
            jsonObject.put("message","exist");
        }else if(resultFlag == 1){
            try {
                //通知前端更新
                //WebSocketServer.sendInfo(userString,"superWebAdmin");
                messagingTemplate.convertAndSend("/topic/subscribeTest", push);
                //回复客户端已提交申请请等待
                jsonObject.put("message","success");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(userString);
        }else{
            jsonObject.put("message","fail");
            //插入失败,网络故障
        }
     //  }
        return jsonObject;
    }

    @ApiOperation(value = "用户登陆" ,  notes="")
    @PostMapping("/login")
    public JSONObject loadAndroidAdmin(@RequestBody(required = false) User user, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        String userToken = request.getHeader("Authorization");
        String password = null;
        String userPhone = null;
        String userId = null;
        if(user != null){
            password = user.getUserPassword();
            userPhone = user.getUserPhone();
            userId = user.getUserId();
        }
        //既没有密码也没有token，登录失败
        if(password == null && (userToken==null || userToken.equals("null"))){
            jsonObject.put("isLogin", false);
            return jsonObject;
        }

        boolean loginWithPhone = false;
        if(password != null && userPhone!=null){
            //如果有userPhone且密码不为空，则用userPhone登录
            userPhone = userPhone + ",1";
            System.out.println("用userPhone登录");
            loginWithPhone = true;
        }else if(password != null && userId!=null){
            //如果有userId且密码不为空，则用userId登录
            System.out.println("用userId登录");
            userId = userId + ",0";
        }
        else{
            //否则用token登录
            password = userToken;
        }

        JSONObject subjectResult = UserCheckUtil.checkUser(loginWithPhone?userPhone:userId, password, null);
        int flag = (int)subjectResult.get("result");
        System.out.println(flag);
        if (flag == 1) {
            if(loginWithPhone) {
                if(userService.getRegisterState(userPhone) == 2) {
                    if(userToken == null || userToken.equals("null")){
                        User userInfo = (User)subjectResult.get("userInfo");
                        jsonObject.put("userInfo",userInfo);
                        jsonObject.put("token",JwtUtil.getAndroidToken(userInfo));
                    }
                    jsonObject.put("registerState","0");
                    jsonObject.put("isLogin", true);
                    logger.info(userPhone +" 登录");
                }else if (userService.getRegisterState(userPhone) == 1){
                    jsonObject.put("registerState","1");
                    jsonObject.put("isLogin", false);
                }else {
                    jsonObject.put("registerState","2");
                    jsonObject.put("isLogin", false);
                }
            }else{
                if(userToken == null || userToken.equals("null")){
                    User userInfo = (User)subjectResult.get("userInfo");
                    jsonObject.put("token",JwtUtil.getToken(userInfo));
                }
                jsonObject.put("isLogin", true);
                logger.info(userId +" 登录");
            }
        } else {
            System.out.println("登陆失败");
            jsonObject.put("registerState", "3");
            jsonObject.put("isLogin", false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "解析token中的基本信息（用户名、角色）" ,  notes="")
    @PostMapping("/getUserInfo")
    public JSONObject getProjectInfo(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        //进行身份验证，只能通过cookie认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password,null).get("result");
        if(flag == 1){
            try {
                Map<String, Claim> claims = JwtUtil.verifyToken(password);
                String role = claims.get("userRole").asString();
                String name = claims.get("userName").asString();
                jsonObject.put("userRole",role);
                jsonObject.put("userName",name);
            }catch(Exception ex){
                jsonObject.put("isAllowed",false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取所有区域管理员的信息" ,  notes="只对超管开放")
    @GetMapping("/getAreaList")
    public JSONObject getAreaAdmin(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        //String password = CookieUtil.getCookie(request, "token");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            List<User> areaAdminList = userMapper.getAllAreaAdmin();
            jsonObject.put("resultList",areaAdminList);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取指定人员的信息,模糊搜索" ,  notes="只对超管开放")
    @GetMapping("/getUserByPhoneOrName")
    public JSONObject getUserByPhoneOrName(HttpServletRequest request, @RequestParam String information){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            List<User> user;
            List<User> userReturn = new ArrayList<>();
            if(information.contains("1"))
                user = userMapper.getUserPhoneDepartment(information);
            else user = userMapper.getUserNameDepartment(information);
            if(user != null)
                for(User user1 : user){
                    if(user1.getUserRole().contains("worker"))
                        userReturn.add(user1);
                }
            jsonObject.put("User",userReturn);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取所有租方管理员的信息" ,  notes="只对超管开放")
    @GetMapping("/getRentList")
    public JSONObject getRentAdmin(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        //String password = CookieUtil.getCookie(request, "token");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            List<User> areaAdminList = userMapper.getAllRentAdmin();
            jsonObject.put("resultList",areaAdminList);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取所有没有审核的注册数目" ,  notes="只对超管开放")
    @GetMapping("/getUnchecked")
    public JSONObject getUnCheckedNum(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();

        //进行身份验证，只能通过cookie认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            //int unCheckedNum = userService.getRegisterUnCheckedNum();
            List<User> unCheckedArray = userService.getRegisterUnChecked();
            int unCheckedNum = unCheckedArray.size();
            jsonObject.put("isAllowed",true);
            jsonObject.put("unCheckedNum",unCheckedNum);
            jsonObject.put("unCheckedArray", unCheckedArray);
            System.out.println(jsonObject);
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "处理审核信息" ,  notes="只对超管开放")
    @PostMapping(value="/handleRegister")
    public JSONObject handleRegister(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            if(userService.handleRegister(map.get("multipleUserId"),map.get("handleResult"))){
                jsonObject.put("handleSubmit","success");
            }
        }else{
            jsonObject.put("handleSubmit","fail");
        }
        return jsonObject;
    }

    @ApiOperation(value = "处理项目开始的审核信息(吊篮出库审核)" ,  notes="只对超管开放")
    @PostMapping(value="/handleProjectBegin")
    public JSONObject handleProjectBegin(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            if(userService.handleProjectBegin(map.get("projectId"),map.get("handleResult"))){
                jsonObject.put("handleSubmit","success");
            }
        }else{
            jsonObject.put("handleSubmit","fail");
        }
        return jsonObject;
    }

    @ApiOperation(value = "处理项目结束的审核信息" ,  notes="只对超管开放")
    @PostMapping(value="/handleProjectEnd")
    public JSONObject handleProjectEnd(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            if(userService.handleProjectEnd(map.get("projectId"),map.get("handleResult"))){
                jsonObject.put("handleSubmit","success");
            }
        }else{
            jsonObject.put("handleSubmit","fail");
        }
        return jsonObject;
    }

    @ApiOperation(value = "处理吊篮入库的的审核信息" ,  notes="只对超管开放")
    @PostMapping(value="/handleStoreIn")
    public JSONObject handleStoreIn(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            if(userService.handleStoreIn(map.get("projectId"),map.get("storeId"),map.get("handleResult"))){
                jsonObject.put("handleSubmit","success");
            }
        }else{
            jsonObject.put("handleSubmit","fail");
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取所有审核过的账号信息" ,  notes="只对超管开放")
    @GetMapping("/getAllAccount")
    public JSONObject getAllAccount(HttpServletRequest request){
        JSONObject jsonObject=new JSONObject();

        //进行身份验证，只能通过JWT认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            List<User> allAccount = userService.getAllAccount();
            jsonObject.put("isAllowed",true);
            jsonObject.put("allAccount", allAccount);
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @PostMapping("/deleteAccount")
    public JSONObject deleteAccount(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();

        //进行身份验证，只能通过JWT认证
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            try{
                jsonObject.put("isAllowed",true);
               // userMapper.deleteUser(map.get("multipleUserId"));
                jsonObject.put("idDelete", true);
            }catch (Exception ex){
                jsonObject.put("idDelete", false);
            }
        }else{
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @GetMapping("/quitLoad")
    public JSONObject quitWebAdmin(HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        //CookieUtil.writeCookie(response,"token","1",0);
        jsonObject.put("quitSuccess", true);
        return jsonObject;
    }

    @ApiOperation(value = "安卓获取人员信息" ,  notes="")
    @PostMapping("/androidGetUserInfo")
    public JSONObject getUser(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if (flag == 1) {
            if(userId!=null) {
                User userInfo = userMapper.getUserInfo(userId);
                Project project = null;
                String projectId = "";
                String projectName = "";
                if(userInfo.getUserRole().contains("worker"))
                    projectId = projectService.getProjectId(userId);
                else if(userInfo.getUserRole().equals("rentAdmin")) {
                    if(projectMapper.getProjectIdByAdmin(userId)!=null)
                        projectId = projectMapper.getProjectIdByAdmin(userId).getProjectId();
                }
                if(!projectId.equals(""))
                    project = projectMapper.getProjectDetail(projectId);
                if(project != null)
                    projectName = project.getProjectName();
                if(userInfo.getUserRole().contains("worker")) {///////如果是工人就返回状态
                    ElectricRes electricRes = electricResMapper.getElectricBoxState(userId);
                    if (electricRes != null)
                        jsonObject.put("userState", 1);///////1就是要离开，就是现在正在工作
                    else jsonObject.put("userState", 0);
                }
                jsonObject.put("isLogin", true);
                jsonObject.put("inProject",projectId);
                jsonObject.put("projectName",projectName);
                jsonObject.put("userInfo",userInfo);
            } else {
                jsonObject.put("userId","空");
                jsonObject.put("isLogin", true);
            }
        } else {
            System.out.println("认证失败");
            jsonObject.put("isLogin", false);
        }
        System.out.println(jsonObject);
        return jsonObject;
    }

    /*
    ///////////////////////////////////////5.15新增内容，web用户注册
     */

    @ApiOperation(value = "新建web用户" ,  notes="只对超管开放")
    @PostMapping("/createWebAdmin")
    public JSONObject createWebAdmin(HttpServletRequest request, @RequestBody Map<String,String> map){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, "superWebAdmin").get("result");
        if(flag == 1){
            String result = userService.createWebAdmin(map);
            if(result.equals("fail"))
                jsonObject.put("fail",result);
            else
                jsonObject.put("success",result);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "判断是否是项目管理员" ,  notes="")
    @PostMapping("/judgeProAdmin")
    public JSONObject judgeProAdmin(HttpServletRequest request, @RequestParam String userId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            int result = userService.judgeProAdmin(userId);
            if(result == 1)
                jsonObject.put("projectAdmin",1);
            else
                jsonObject.put("projectAdmin",0);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "更新资质证书照片" ,  notes="工人")
    @PostMapping("/updateQualifications")
    public JSONObject updateQualifications(HttpServletRequest request, @RequestParam String userId, @RequestParam int picNum){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            int result = userService.updateQualifications(userId, picNum);
            switch (result) {
                case 0:
                    jsonObject.put("update", "success");
                    break;
                case 1:
                    jsonObject.put("update", "failed");
            }
            jsonObject.put("isLogin",true);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "获取资质证书照片" ,  notes="工人")
    @PostMapping("/getQualifications")
    public JSONObject getQualifications(HttpServletRequest request, @RequestParam String userId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            String result = userService.getQualifications(userId);
            switch (result) {
                case "fail":
                    jsonObject.put("get", "failed");
                    break;
                default:
                    jsonObject.put("get", "success");
                    jsonObject.put("info",result);
                    break;
            }
            jsonObject.put("isLogin",true);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    /*
    ///////////////////////////////////////7.21新增内容，修改密码
     */
    @ApiOperation(value = "修改密码" ,  notes="对所有人开放")
    @PostMapping("/updatePassword")
    public JSONObject updatePassword(HttpServletRequest request, @RequestParam String userId, @RequestParam String oldPassword, @RequestParam String newPassword){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            String result = userService.updatePassword(userId, oldPassword, newPassword);
            jsonObject.put("result", result);
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "删除用户" ,  notes="")
    @PostMapping("/deleteUser")
    public JSONObject deleteUser(HttpServletRequest request, @RequestParam String userId){
        JSONObject jsonObject=new JSONObject();
        String password = request.getHeader("Authorization");
        int flag = (int)UserCheckUtil.checkUser("", password, null).get("result");
        if(flag == 1){
            jsonObject.put("isLogin",true);
            int result = userService.deleteUser(userId);
            if(result == 1)
                jsonObject.put("delete","success");
            else
                jsonObject.put("delete","fail");
        }else{
            jsonObject.put("isLogin",false);
        }
        return jsonObject;
    }
}
