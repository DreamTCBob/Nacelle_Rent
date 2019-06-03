package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.service.FtpService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;


@Service
public class FtpServiceImpl implements FtpService {
    @Value("${FTPFileURL}")
    private String ftpFileURL;
    @Value("${FTPUserName}")
    private String ftpUserName;
    @Value("${FTPUserPassword}")
    private String ftpUserPassword;
    @Value("${FTPFilePath}")
    private String ftpFilePath;
    @Value("${FTPCertFilePath}")
    private String ftpCertFilePath;
    @Value("${FTPProjectFilePath}")
    private String ftpProjectFilePath;
    @Value("${FTPStoreInFilePath}")
    private String ftpStoreInFilePath;
    @Value("${localFilePath}")
    private String localFilePath;
    @Override
    /**
     * 创建注册头像文件
     * @param fileName
     * @return
     * @throws Exception*/
    public int createImageFile(String fileName, InputStream inputStream) throws IOException{
        if(StringUtils.isEmpty(fileName) || inputStream == null){
            return 0;
        }
        fileName = fileName + ".jpg";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(ftpFileURL,21);
            ftp.login(ftpUserName,ftpUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 2;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(ftpFilePath);
            ftp.changeWorkingDirectory(ftpFilePath);
            ftp.storeFile(fileName,inputStream);
            inputStream.close();
            ftp.logout();
            return 1;
        }catch (IOException ex){
            return 2;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    /**
     * 创建项目开工审核照片
     * @param fileName
     * @return
     * @throws Exception*/
    public int createProjectFile(String fileName, InputStream inputStream) throws IOException{
        if(StringUtils.isEmpty(fileName) || inputStream == null){
            return 0;
        }
        fileName = fileName + ".jpg";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(ftpFileURL,21);
            ftp.login(ftpUserName,ftpUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 2;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(ftpProjectFilePath);
            ftp.changeWorkingDirectory(ftpProjectFilePath);
            ftp.storeFile(fileName,inputStream);
            inputStream.close();
            ftp.logout();
            return 1;
        }catch (IOException ex){
            return 2;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    /**
     * 创建入库审核照片
     * @param fileName
     * @return
     * @throws Exception*/
    public int createStoreInFile(String fileName, InputStream inputStream) throws IOException{
        if(StringUtils.isEmpty(fileName) || inputStream == null){
            return 0;
        }
        fileName = fileName + ".jpg";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(ftpFileURL,21);
            ftp.login(ftpUserName,ftpUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 2;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(ftpStoreInFilePath);
            ftp.changeWorkingDirectory(ftpStoreInFilePath);
            ftp.storeFile(fileName,inputStream);
            inputStream.close();
            ftp.logout();
            return 1;
        }catch (IOException ex){
            return 2;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    @Override
    /**
     * 创建安监证书
     * @param fileName
     * @return
     * @throws Exception*/
    public int createCertFile(String fileName, InputStream inputStream) throws IOException{
        if(StringUtils.isEmpty(fileName) || inputStream == null){
            return 0;
        }
        fileName = fileName + ".jpg";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(ftpFileURL,21);
            ftp.login(ftpUserName,ftpUserPassword);
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return 2;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(ftpCertFilePath);
            ftp.changeWorkingDirectory(ftpCertFilePath);
            ftp.storeFile(fileName,inputStream);
            inputStream.close();
            ftp.logout();
            return 1;
        }catch (IOException ex){
            return 2;
        }finally {
            if(ftp.isConnected()){
                try{
                    ftp.disconnect();
                }catch (IOException ioe){}
            }
        }
    }
    @Override
    /**
     * 读取文件
     * @param fileName
     * @return
     * @throws Exception*/
    public int downloadFile(String fileName) throws IOException {//download文件
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            int flag = 0;
            ftp.connect(ftpFileURL, 21);
            ftp.login(ftpUserName, ftpUserPassword);//登陆
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return 0;
            }
            ftp.setControlEncoding("GBK");//文件编码格式
            ftp.setFileTransferMode(ftp.STREAM_TRANSFER_MODE);//设置传输方式
            ftp.changeWorkingDirectory(ftpFilePath);//工作文件夹
            FTPFile[] fs = ftp.listFiles();//列出所有的文件
            for(FTPFile file : fs){//判断是否存在指定文件
                if(file.getName().equals(fileName)){
                    File localFile = new File(localFilePath + fileName);
                    OutputStream outputStream = new FileOutputStream(localFile);
                    ftp.retrieveFile(file.getName(),outputStream);//复制文件
                    outputStream.close();
                    flag = flag + 1;
                }
            }
            ftp.logout();
            if(flag != 0)
                return 1;
            else return 0;
//            return 1;
        } catch (IOException ex) {
            return 0;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
    }
}

