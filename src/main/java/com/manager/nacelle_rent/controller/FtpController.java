package com.manager.nacelle_rent.controller;

import com.alibaba.fastjson.JSONObject;
import com.manager.nacelle_rent.service.FtpService;
import com.manager.nacelle_rent.utils.UserCheckUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

@Api(description = "文件接口")
@RestController
public class FtpController {

    @Autowired
    private FtpService ftpService;

    //文件上传
    @ApiOperation(value = "上传注册头像" ,  notes="参数需要文件、用户电话以及token")
    @PostMapping("/createImageFile")
    public JSONObject createFile(@RequestParam("userPhone") String fileName,///此处把电话号码当作上传file的唯一name
                                 @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) throws Exception{
        JSONObject jsonObject = new JSONObject();
//        String password = request.getHeader("Authorization");
        int flag1 = 1;//(int) UserCheckUtil.checkUser("", password,null).get("result");
        if(flag1 == 1) {
            try {
                InputStream inputStream = multipartFile.getInputStream();
                int flag = ftpService.createImageFile(fileName, inputStream);
                if (flag == 0) {
                    jsonObject.put("error", 1);
                    jsonObject.put("status", "读取文件失败");
                } else if (flag == 1) {
                    jsonObject.put("error", 0);
                    jsonObject.put("status", "上传文件成功");
                } else {
                    jsonObject.put("error", 2);
                    jsonObject.put("status", "上传文件失败");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上传开工的多张照片" ,  notes="参数需要多张照片、项目Id以及token")
    @PostMapping("/createProjectFile")
    public JSONObject createFile(@RequestParam("projectId") String fileName,///此处把项目Id当作上传file的唯一name
                                 @RequestParam("file") List<MultipartFile> multipartFile, HttpServletRequest request) throws Exception{
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag1 = (int) UserCheckUtil.checkUser("", password,null).get("result");
        int flag = 0;
        String filenameIn = "";
        if(flag1 == 1) {
            try {
                for(int i = 0 ; i < multipartFile.size() ; i++) {
                    InputStream inputStream = multipartFile.get(i).getInputStream();
                    filenameIn = fileName + "_" + (i + 1);
                    flag += ftpService.createProjectFile(filenameIn, inputStream);
                    filenameIn = "";
                }
                if (flag == 0) {
                    jsonObject.put("error", 1);
                    jsonObject.put("status", "读取文件失败");
                } else if (flag == multipartFile.size()) {
                    jsonObject.put("error", 0);
                    jsonObject.put("status", "上传文件成功");
                } else {
                    jsonObject.put("error", 2);
                    jsonObject.put("status", "上传文件失败");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上传吊篮的报停照片" ,  notes="参数需要多张照片、项目Id、吊篮Id以及token")
    @PostMapping("/createStoreInFile")
    public JSONObject createStoreInFile(@RequestParam("projectId") String fileName,@RequestParam("storageId") String storageId,///此处把项目Id和吊篮Id当作上传file的唯一name
                                 @RequestParam("file") List<MultipartFile> multipartFile, HttpServletRequest request) throws Exception{
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag1 = (int) UserCheckUtil.checkUser("", password,null).get("result");
        int flag = 0;
        String filenameIn = "";
        if(flag1 == 1) {
            try {
                for(int i = 0 ; i < multipartFile.size() ; i++) {
                    InputStream inputStream = multipartFile.get(i).getInputStream();
                    filenameIn = fileName + "_" + storageId + "_" + (i + 1);
                    flag += ftpService.createStoreInFile(filenameIn, inputStream);
                    filenameIn = "";
                }
                if (flag == 0) {
                    jsonObject.put("error", 1);
                    jsonObject.put("status", "读取文件失败");
                } else if (flag == multipartFile.size()) {
                    jsonObject.put("error", 0);
                    jsonObject.put("status", "上传文件成功");
                } else {
                    jsonObject.put("error", 2);
                    jsonObject.put("status", "上传文件失败");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }

    @ApiOperation(value = "上传安监证书" ,  notes="参数需要文件、项目ID以及token")
    @PostMapping("/createCertFile")
    public JSONObject createCertFile(@RequestParam("projectId") String fileName,///此处把电话号码当作上传file的唯一name
                                 @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) throws Exception{
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag1 = (int) UserCheckUtil.checkUser("", password,null).get("result");
        if(flag1 == 1) {
            try {
                InputStream inputStream = multipartFile.getInputStream();
                int flag = ftpService.createCertFile(fileName, inputStream);
                if (flag == 0) {
                    jsonObject.put("error", 1);
                    jsonObject.put("status", "读取文件失败");
                } else if (flag == 1) {
                    jsonObject.put("error", 0);
                    jsonObject.put("status", "上传文件成功");
                } else {
                    jsonObject.put("error", 2);
                    jsonObject.put("status", "上传文件失败");
                }
            }catch (Exception ex){
                jsonObject.put("isAllowed",false);
            }
        }else {
            jsonObject.put("isAllowed",false);
        }
        return jsonObject;
    }
    //文件读取
    @ApiOperation(value = "下载文件" ,  notes="参数需要用户电话以及token")
    @PostMapping("/downloadFtpFile")
    public JSONObject downloadFile(@RequestParam("userPhone") String fileName, ///此处把电话号码当作查找file的唯一name
                                   HttpServletRequest request) throws Exception{
        JSONObject jsonObject = new JSONObject();
        String password = request.getHeader("Authorization");
        int flag1 = (int) UserCheckUtil.checkUser("", password,null).get("result");
        if(flag1 == 1) {
            try {
                int flag = ftpService.downloadFile(fileName);
                if (flag == 1) {
                    jsonObject.put("isTrue", "文件读取成功");
                } else {
                    jsonObject.put("isFalse", "文件读取失败");
                }
            }catch (Exception e){
                jsonObject.put("isAllowed",false);
            }
        }
        else jsonObject.put("isAllowed",false);
        return jsonObject;
    }
}

