package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.dtos.FuzzyAlgorithmSecondDataDto;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmSecondData {
    //fuzzy_algorithm_second_data主键
    @TableId(type = IdType.AUTO)
    private long fasId;
    //外键->fuzzy_algorithm_first_data主键
    private long fafId;
    //别名（一级->此次分析命名的名称;二级->一级里某个指标的名称）
    private String alias;
    //规模
    private int scale;
    //指标名称数组
    private String names;
    //权重数组
    private byte[] proportion;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/24 19:04
     *@Description AdminSecondDataDtp->AdminSecondData
     */
    public FuzzyAlgorithmSecondData(FuzzyAlgorithmSecondDataDto dto) {
        fafId = dto.getFafId();
        scale = dto.getScale();
        alias = dto.getAlias();
        names = dto.getNames();
        proportion = ArrayUtil.doubleArrayToByteArray(dto.getProportion());
    }

    public FuzzyAlgorithmSecondData() {
    }
}
