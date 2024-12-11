package com.example.ahp.entity.dtos;

import com.example.ahp.entity.pojos.FuzzyAlgorithmSecondData;
import com.example.ahp.entity.pojos.AhpSecondData;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmSecondDataDto {
    //外键->fuzzy_algorithm_first_data主键
    private long fafId;
    //fuzzy_algorithm_second_data主键
    private long fasId;
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //规模
    private int scale;
    //指标名称数组
    private String names;
    //权重数组
    private double[] proportion;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/24 16:40
     *@Description ahpSecondData->AdminSecondDataDto
     */
    public FuzzyAlgorithmSecondDataDto(AhpSecondData ahpSecondData) {
        scale = ahpSecondData.getScale();
        alias = ahpSecondData.getAlias();
        names = ahpSecondData.getNames();
    }

    /**
     *@Author keith
     *@Date 2024/1/26 15:09
     *@Description AdminSecondData->AdminSecondDataDto
     */
    public FuzzyAlgorithmSecondDataDto(FuzzyAlgorithmSecondData fuzzyAlgorithmSecondData) {
        fafId = fuzzyAlgorithmSecondData.getFafId();
        fasId = fuzzyAlgorithmSecondData.getFasId();
        alias = fuzzyAlgorithmSecondData.getAlias();
        scale = fuzzyAlgorithmSecondData.getScale();
        names = fuzzyAlgorithmSecondData.getNames();
        proportion = ArrayUtil.byteArrayToDoubleArray(fuzzyAlgorithmSecondData.getProportion());
        createdTime = fuzzyAlgorithmSecondData.getCreatedTime();
    }

    public FuzzyAlgorithmSecondDataDto() {
    }
}
