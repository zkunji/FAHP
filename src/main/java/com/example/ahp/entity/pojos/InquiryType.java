package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.dtos.InquiryTypeDto;
import lombok.Data;

import java.util.Date;

@Data
public class InquiryType {
    //唯一标识
    @TableId(type = IdType.AUTO)
    private int inquiryTypeId;
    //命名
    private String alias;
    //一级指标集合
    private String firstMetrics;
    //二级指标集合
    private byte[] secondMetrics;
    //创建时间
    private Date createdTime;

    public InquiryType() {
    }

    /**
     *@Author keith
     *@Date 2024/3/31 18:41
     *@Description InquiryTypeDto->InquiryType
     */
    public InquiryType(InquiryTypeDto dto) {
        alias = dto.getAlias();
        firstMetrics = dto.getFirstMetrics();
        //二级指标
        String tmp ="";
        for(int i=0;i<dto.getSecondMetrics().length;i++){
            tmp = tmp + dto.getSecondMetrics()[i]+";";
        }
        tmp = tmp.substring(0,tmp.length()-1);
        secondMetrics = tmp.getBytes();
    }
}
