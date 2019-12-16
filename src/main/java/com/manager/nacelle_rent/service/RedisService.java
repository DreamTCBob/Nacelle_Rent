package com.manager.nacelle_rent.service;


import com.manager.nacelle_rent.entity.Project;
import com.manager.nacelle_rent.entity.User;

public interface RedisService {
    Project getProject(String key);
    void setProject(Project project);
    void delProject(String key);
    User getUser(String key);
    void setUser(User user);
    void delUser(String key);
}
