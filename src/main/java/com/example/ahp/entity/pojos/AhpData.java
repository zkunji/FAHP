package com.example.ahp.entity.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AhpData {
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //ahp数据矩阵规模
    private int scale;
    //指标名称数组
    private String names;
    //ahp原始数据
    private byte[] originalData;
    //特征向量
    private String eigenvectors;
    //权重值
    private String proportion;
    //最大特征值
    private String maxEigenvalue;
    //CI值(一致性指标)
    private String ci;
    //RI值(平均随机一致性指标)
    private double ri;
    //CR值(一致性比率)
    private String cr;
    //一致性分析结果
    private boolean result;
    //创建时间
    private Date createdTime;

    public AhpData() {
    }

    public AhpData(AhpData ahpData) {
        setScale(ahpData.getScale());
        setNames(ahpData.getNames());
        setOriginalData(ahpData.getOriginalData());
        setEigenvectors(ahpData.getEigenvectors());
        setProportion(ahpData.getProportion());
        setMaxEigenvalue(ahpData.getMaxEigenvalue());
        setCi(ahpData.getCi());
        setRi(ahpData.getRi());
        setCr(ahpData.getCr());
        setResult(ahpData.isResult());
    }
}
