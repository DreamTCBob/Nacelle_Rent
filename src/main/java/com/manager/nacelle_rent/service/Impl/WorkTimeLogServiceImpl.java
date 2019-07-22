package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.dao.WorkTimeLogMapper;
import com.manager.nacelle_rent.entity.WorkTimeLog;
import com.manager.nacelle_rent.service.WorkTimeLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WorkTimeLogServiceImpl implements WorkTimeLogService {
    @Autowired
    private WorkTimeLogMapper workTimeLogMapper;
    @Override
    public void createWorkTimeLog(String userId, String projectId, String deviceId, long timeWork){
        workTimeLogMapper.createWorkTimeLog(userId, projectId, deviceId, timeWork);
    }
    @Override
    public List<WorkTimeLog> getWorkerTime(String userId){
        return workTimeLogMapper.getWorkerTime(userId);
    }
}

