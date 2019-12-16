package com.manager.nacelle_rent.utils;
import org.apache.shiro.codec.Base64;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

/**
 * 文件上传工具包
 */
public class FileUtil {
    private static String localFilePath = "d:/";
//    private static String localFilePath = "/var/ftp/nacelleRent/contract/";
    /**
     *
     * @param file 文件
     * @return
     */
    public static boolean base64ToPdf(String file, String fileName){
        try {
            // Base64解码
            byte[] b = Base64.decode(file);
            for (int i = 0; i < b.length; ++i) {
                // 调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            String imgFilePath = localFilePath+ fileName + ".pdf";
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean writeFile(String filePath, String fileString){
        File myFilePath = new File(localFilePath+filePath+".txt");
        try {
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(myFilePath));
            bw.write(fileString);
            bw.flush();
            bw.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readFile(String filePath){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(/*localFilePath+*/filePath));
            String tempString = null;
            StringBuffer resultString = new StringBuffer();
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                resultString.append(tempString);
                line++;
            }
            reader.close();
            return resultString.toString();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
