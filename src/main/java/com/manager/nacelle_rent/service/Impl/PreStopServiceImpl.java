package com.manager.nacelle_rent.service.Impl;

import com.manager.nacelle_rent.dao.PreStopMapper;
import com.manager.nacelle_rent.service.PreStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;


@Service
public class PreStopServiceImpl implements PreStopService{
    @Autowired
    private PreStopMapper preStopMapper;
    @Override
    public int createPreStop(int days, int num , String projectId){
        if(projectId != null){
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, days);
                Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
                preStopMapper.createPreStop(timestamp,days, num, projectId);
                return 0;///成功
            }catch (Exception e){
                return 1;///数据库插入异常
            }
        }else return 2;///项目id不对
    }
}

