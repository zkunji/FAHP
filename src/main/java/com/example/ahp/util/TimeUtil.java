package com.example.ahp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    /**
     *@Author keith
     *@Date 2024/3/9 15:30
     *@Description 将Date转为字符串"yyyy-MM-dd HH:mm:ss"
     */
    public static String toStringDate(Date date){
        // 指定日期格式
        String pattern = "yyyy-MM-dd HH:mm:ss"; // 例如："2024-03-09 15:30:45"
        // 创建SimpleDateFormat对象并将Date对象格式化为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
