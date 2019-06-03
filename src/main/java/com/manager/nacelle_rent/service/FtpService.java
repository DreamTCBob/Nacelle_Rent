package com.manager.nacelle_rent.service;
import java.io.IOException;
import java.io.InputStream;

public interface FtpService {
    int createImageFile(String fileName, InputStream inputStream) throws IOException;
    int createCertFile(String fileName, InputStream inputStream) throws IOException;
    int createProjectFile(String fileName, InputStream inputStream) throws IOException;
    int createStoreInFile(String fileName, InputStream inputStream) throws IOException;
    int downloadFile(String fileName) throws IOException;
}
