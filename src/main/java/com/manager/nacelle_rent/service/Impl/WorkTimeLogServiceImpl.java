package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.dao.ProjectMapper;
import com.manager.nacelle_rent.dao.WorkTimeLogMapper;
import com.manager.nacelle_rent.entity.WorkTimeLog;
import com.manager.nacelle_rent.service.WorkTimeLogService;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WorkTimeLogServiceImpl implements WorkTimeLogService {
    @Autowired
    private WorkTimeLogMapper workTimeLogMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Override
    public void createWorkTimeLog(String userId, String projectId, String deviceId, long timeWork, Timestamp timeStamp){
        workTimeLogMapper.createWorkTimeLog(userId, projectId, deviceId, timeWork,timeStamp);
    }
    @Override
    public List<WorkTimeLog> getWorkerTime(String userId){
        List<WorkTimeLog> list = workTimeLogMapper.getWorkerTime(userId);
        for (WorkTimeLog workTimeLog : list){
            String projectId = workTimeLog.getProjectId();
            String projectName = projectMapper.getProjectDetail(projectId) == null ? "" : projectMapper.getProjectDetail(projectId).getProjectName();
            workTimeLog.setProjectName(projectName);
        }
        return list;
    }
}

