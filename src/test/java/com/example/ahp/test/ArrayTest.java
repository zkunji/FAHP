package com.example.ahp.test;

import com.example.ahp.entity.pojos.InquiryType;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.util.TimeUtil;
import org.assertj.core.util.DateUtil;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

public class ArrayTest {
    @Test
    public void doubleArrayToByteArray(){
        double[] doubles = {2.0,1.2};
        byte[] bytes = ArrayUtil.doubleArrayToByteArray(doubles);
        double[] tmp = ArrayUtil.byteArrayToDoubleArray(bytes);
        for(int i=0;i<tmp.length;i++){
            System.out.println(tmp[i]);
        }
    }

    @Test
    public void timeTest(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
//        Date date = timestamp;
//        System.out.println(TimeUtil.toStringDate(date));
        // 数据库时区
        TimeZone dbTimeZone = TimeZone.getTimeZone("UTC");
        // 系统（Java虚拟机）默认时区
        TimeZone defaultTimeZone = TimeZone.getDefault();
        // 计算时区差异
        int timeDifference = defaultTimeZone.getRawOffset() - dbTimeZone.getRawOffset();
        System.out.println(timeDifference);
        // 转换时间
        Date date = new Date(timestamp.getTime() + timeDifference);
        System.out.println("转换后的时间: " + date.toString());
    }


}
