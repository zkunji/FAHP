package com.example.ahp.entity.dtos;

import com.example.ahp.entity.pojos.AhpData;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.util.TimeUtil;
import lombok.Data;

import java.util.Date;

@Data
public class AhpDto {
    //ahp数据矩阵规模
    private int scale;
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //指标名称数组
    private String[] names;
    //ahp原始数据
    private double[][] originalData;
    //特征向量
    private String[] eigenvectors;
    //权重值
    private String[] proportion;
    //最大特征值
    private String maxEigenvalue;
    //CI值
    private String ci;
    //RI值
    private double ri;
    //CR值
    private String cr;
    //一致性分析结果
    private boolean result;
    //创建时间
    private String createdTime;

    public AhpDto() {
    }

    public AhpDto(AhpData ahpData) {
        scale = ahpData.getScale();
        names = ArrayUtil.stringToStringArray(ahpData.getNames());
        originalData = ArrayUtil.byteArrayToDoubleArrayMatrix(ahpData.getOriginalData());
        eigenvectors = ArrayUtil.stringToStringArray(ahpData.getEigenvectors());
        proportion = ArrayUtil.stringToStringArray(ahpData.getProportion());
        maxEigenvalue = ahpData.getMaxEigenvalue();
        ci = ahpData.getCi();
        ri = ahpData.getRi();
        cr = ahpData.getCr();
        result = ahpData.isResult();
    }
}
