package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.dtos.FuzzyAlgorithmFirstDataDto;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmFirstData {
    //fuzzy_algorithm_first_data表唯一标识
    @TableId(type = IdType.AUTO)
    private long fafId;
    //uid集合
    private byte[] uidArray;
    //ahp_first_data每个主键的数据占比多少(专家占比权重)
    private byte[] percentages;
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //规模
    private int scale;
    //指标名称数组
    private String names;
    //一级指标权重数组
    private byte[] proportion;
    //评价结果(按照最大隶属原则的评价结果)
    private String comments;
    //评语集矩阵(行数:二级指标个数,列数:4的double数组)
    private byte[] commentsMatrix;
    //最终评价程度(4个double值)
    private byte[] commentsResult;
    //外键->admin表主键
    private long adminId;
    //查询类型对象的外键
    private int inquiryTypeId = 1;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/24 16:44
     *@Description adminFirstDataDto->adminFirstData
     */
    public FuzzyAlgorithmFirstData(FuzzyAlgorithmFirstDataDto fuzzyAlgorithmFirstDataDto) {
        uidArray = ArrayUtil.longArrayToByteArray(fuzzyAlgorithmFirstDataDto.getUidArray());
        percentages = ArrayUtil.doubleArrayToByteArray(fuzzyAlgorithmFirstDataDto.getPercentages());
        scale = fuzzyAlgorithmFirstDataDto.getScale();
        alias = fuzzyAlgorithmFirstDataDto.getAlias();
        names = fuzzyAlgorithmFirstDataDto.getNames();
        proportion = ArrayUtil.doubleArrayToByteArray(fuzzyAlgorithmFirstDataDto.getProportion());
        adminId = fuzzyAlgorithmFirstDataDto.getAdminId();
    }

    public FuzzyAlgorithmFirstData() {
    }
}
