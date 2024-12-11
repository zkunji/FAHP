package com.example.ahp.entity.dtos;


import com.example.ahp.entity.pojos.InquiryType;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.util.TimeUtil;
import lombok.Data;

import java.util.Date;

@Data
public class InquiryTypeDto {
    //唯一标识
    private int inquiryTypeId;
    //命名
    private String alias;
    //一级指标集合
    private String firstMetrics;
    //二级指标集合
    private String[] secondMetrics;
    //创建时间
    private String createdTime;

    public InquiryTypeDto() {
    }

    /**
     *@Author keith
     *@Date 2024/3/31 18:38
     *@Description InquiryType>InquiryTypeDto
     */
    public InquiryTypeDto(InquiryType inquiryType) {
        inquiryTypeId = inquiryType.getInquiryTypeId();
        alias = inquiryType.getAlias();
        firstMetrics = inquiryType.getFirstMetrics();
        //二级指标
        secondMetrics = new String(inquiryType.getSecondMetrics()).split(";");
        createdTime = TimeUtil.toStringDate(inquiryType.getCreatedTime());
    }
}
