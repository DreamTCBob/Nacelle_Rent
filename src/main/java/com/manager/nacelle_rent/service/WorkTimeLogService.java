package com.manager.nacelle_rent.service;

import com.manager.nacelle_rent.entity.WorkTimeLog;

import java.util.List;

public interface WorkTimeLogService {
    void createWorkTimeLog(String userId, String projectId, String deviceId, long timeWork);
    List<WorkTimeLog> getWorkerTime(String userId);
}
