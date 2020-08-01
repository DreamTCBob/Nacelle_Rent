package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.dao.ProjectDeviceInfoMapper;
import com.manager.nacelle_rent.dao.ProjectDeviceMapper;
import com.manager.nacelle_rent.dao.ProjectMapper;
import com.manager.nacelle_rent.entity.ProjectDevice;
import com.manager.nacelle_rent.service.ProjectDeviceService;
import com.manager.nacelle_rent.utils.PageQueryUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectDeviceServiceImpl implements ProjectDeviceService {
    @Resource
    private ProjectDeviceInfoMapper projectDeviceInfoMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Override
    public List<ProjectDevice> getProjectDeviceInfo(int type, String var1, String var2, int pageSize, int pageIndex) {
        List<ProjectDevice> projectDeviceList = new ArrayList<>();
        List<ProjectDevice> returnProjectDeviceList = new ArrayList<>();
        PageQueryUtil pageQueryUtil = new PageQueryUtil();
        pageQueryUtil.setPageIndex(pageIndex);
        pageQueryUtil.setPageSize(pageSize);
        switch (type) {
            case 1://所有的
                projectDeviceList = projectDeviceInfoMapper.getAllDevice(pageQueryUtil.getPageSize(), pageQueryUtil.getOffset());
                for (ProjectDevice projectDevice : projectDeviceList) {
                    projectDevice.setPageIndex(pageIndex);
                    projectDevice.setPageSize(pageSize);
                    projectDevice.setTotal(projectDeviceInfoMapper.totalGetAllDevice());
                }
                returnProjectDeviceList = getProjectName(projectDeviceList);
                break;
            case 2://按照项目ID获取
                projectDeviceList = projectDeviceInfoMapper.getAllDeviceByProjectId(var1, pageQueryUtil.getPageSize(), pageQueryUtil.getOffset());
                returnProjectDeviceList = getProjectName(projectDeviceList);
                for (ProjectDevice projectDevice : projectDeviceList) {
                    projectDevice.setPageIndex(pageIndex);
                    projectDevice.setPageSize(pageSize);
                    projectDevice.setTotal(projectDeviceInfoMapper.totalGetAllDeviceByProjectId(var1));
                }
                break;
            case 3://按照吊篮ID获取
                projectDeviceList = projectDeviceInfoMapper.getAllDeviceByDeviceId(var1, pageQueryUtil.getPageSize(), pageQueryUtil.getOffset());
                returnProjectDeviceList = getProjectName(projectDeviceList);
                for (ProjectDevice projectDevice : projectDeviceList) {
                    projectDevice.setPageIndex(pageIndex);
                    projectDevice.setPageSize(pageSize);
                    projectDevice.setTotal(projectDeviceInfoMapper.totalGetAllDeviceByDeviceId(var1));
                }
                break;
            case 4://按照开始时间段获取
                projectDeviceList = projectDeviceInfoMapper.getAllDeviceByStartTime(var1, var2, pageQueryUtil.getPageSize(), pageQueryUtil.getOffset());
                returnProjectDeviceList = getProjectName(projectDeviceList);
                for (ProjectDevice projectDevice : projectDeviceList) {
                    projectDevice.setPageIndex(pageIndex);
                    projectDevice.setPageSize(pageSize);
                    projectDevice.setTotal(projectDeviceInfoMapper.totalGetAllDeviceByStartTime(var1, var2));
                }
                break;
            case 5://按照结束时间段获取
                projectDeviceList = projectDeviceInfoMapper.getAllDeviceByEndTime(var1, var2, pageQueryUtil.getPageSize(), pageQueryUtil.getOffset());
                returnProjectDeviceList = getProjectName(projectDeviceList);
                for (ProjectDevice projectDevice : projectDeviceList) {
                    projectDevice.setPageIndex(pageIndex);
                    projectDevice.setPageSize(pageSize);
                    projectDevice.setTotal(projectDeviceInfoMapper.totalGetAllDeviceByEndTime(var1, var2));
                }
                break;
        }

        return returnProjectDeviceList;
    }
    private List<ProjectDevice> getProjectName(List<ProjectDevice> projectDeviceList) {
        for (ProjectDevice projectDevice : projectDeviceList) {
            String projectId = projectDevice.getProjectId();
            if (!StringUtils.isBlank(projectId)) {
                String projectName = projectMapper.getProjectName(projectId);
                projectDevice.setProjectName(projectName);
            } else {
                projectDevice.setProjectName("");
            }
        }
        return projectDeviceList;
    }
}
