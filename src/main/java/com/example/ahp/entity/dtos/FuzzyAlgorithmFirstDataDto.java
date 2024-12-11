package com.example.ahp.entity.dtos;

import com.example.ahp.entity.pojos.FuzzyAlgorithmFirstData;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmFirstDataDto {
    //fuzzy_algorithm_first_data表唯一标识
    private long fafId;
    //uid集合
    private long[] uidArray;
    //ahp_first_data每个主键的数据占比多少
    private double[] percentages;
    //规模
    private int scale;
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //指标名称数组
    private String names;
    //权重数组
    private double[] proportion;
    //评价结果(按照最大隶属原则的评价结果)
    private String comments;
    //评语集矩阵(行数:二级指标个数,列数:4的double数组)
    private double[][] commentsMatrix;
    //最终评价结果(4个double值)
    private String commentsResult;
    //外键->admin表主键
    private long adminId;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/24 16:12
     *@Description AhpFirstData->AdminFirstDataDto数据转换
     */
    public FuzzyAlgorithmFirstDataDto(AhpFirstData ahpFirstData) {
        scale = ahpFirstData.getScale();
        names = ahpFirstData.getNames();
    }

    /**
     *@Author keith
     *@Date 2024/1/25 13:12
     *@Description AdminFirstData->AdminFirstDataDto
     */
    public FuzzyAlgorithmFirstDataDto(FuzzyAlgorithmFirstData fuzzyAlgorithmFirstData) {
        fafId = fuzzyAlgorithmFirstData.getFafId();
        uidArray = ArrayUtil.byteArrayToLongArray(fuzzyAlgorithmFirstData.getUidArray());
        percentages = ArrayUtil.byteArrayToDoubleArray(fuzzyAlgorithmFirstData.getPercentages());
        scale = fuzzyAlgorithmFirstData.getScale();
        alias = fuzzyAlgorithmFirstData.getAlias();
        names = fuzzyAlgorithmFirstData.getNames();
        proportion = ArrayUtil.byteArrayToDoubleArray(fuzzyAlgorithmFirstData.getProportion());
        comments = fuzzyAlgorithmFirstData.getComments();
        commentsMatrix = ArrayUtil.byteArrayToTwoDimensionalDoubleArray(fuzzyAlgorithmFirstData.getCommentsMatrix(),4);
        adminId = fuzzyAlgorithmFirstData.getAdminId();
        createdTime = fuzzyAlgorithmFirstData.getCreatedTime();
        //commentsResult赋值
        {
            String tmp = "";
            double[] result = ArrayUtil.byteArrayToDoubleArray(fuzzyAlgorithmFirstData.getCommentsResult());
            for(int i=0;i<result.length;i++){
                tmp = tmp + ArrayUtil.reserveTwo(result[i] * 100) + "%,";
            }
            commentsResult = tmp.substring(0,tmp.length()-1);
        }
    }

    public FuzzyAlgorithmFirstDataDto() {
    }
}
