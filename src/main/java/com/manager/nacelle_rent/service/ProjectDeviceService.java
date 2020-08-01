package com.manager.nacelle_rent.service;

import com.manager.nacelle_rent.entity.ProjectDevice;
import java.util.List;

public interface ProjectDeviceService {
    List<ProjectDevice> getProjectDeviceInfo(int type, String var1, String var2, int pageSize, int pageIndex);
}
