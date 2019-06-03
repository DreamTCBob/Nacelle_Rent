package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.dao.UserMapper;
import com.manager.nacelle_rent.entity.ElectricRes;
import com.manager.nacelle_rent.entity.SimpleUser;
import com.manager.nacelle_rent.service.ElectricBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElectricBoxServiceImpl implements ElectricBoxService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ElectricRes> serializeElectricRes(List<ElectricRes> list){
        for( int i = 0 ; i < list.size() ; i++) {
            ElectricRes electricRes = list.get(i);
            String[] projectBuilders = electricRes.getProjectBuilders().split(",");
            ArrayList<SimpleUser> projectBuildersList = new ArrayList<>();
            for(String userId : projectBuilders){
                SimpleUser currentUser = userMapper.getNameById(userId);
                projectBuildersList.add(currentUser);
            }
            electricRes.setProjectBuildersDetail(projectBuildersList);
        }
        return  list;
    }
}
