package com.manager.nacelle_rent.service;

import com.manager.nacelle_rent.entity.WorkTimeLog;
import java.sql.Timestamp;

import java.sql.Timestamp;
import java.util.List;

public interface WorkTimeLogService {
    void createWorkTimeLog(String userId, String projectId, String deviceId, long timeWork, Timestamp timeStamp);
    List<WorkTimeLog> getWorkerTime(String userId);
}
