package com.manager.nacelle_rent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String timeToDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long timeStamp = Long.parseLong(time);
        return simpleDateFormat.format(new Date(timeStamp));   // 时间戳转换成时间
    }
}
