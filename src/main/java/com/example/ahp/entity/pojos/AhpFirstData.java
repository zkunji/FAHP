package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

@Data
public class AhpFirstData extends AhpData{
    //ahp一级唯一标识
    @TableId(type = IdType.AUTO)
    private long afdId;
    //用户标识
    private long uid;
    //查询类型对象的外键
    private int inquiryTypeId = 1;
    //提交状态(1:提交,0:完整数据,-1:不完整数据)
    private int submit = -1;

    public AhpFirstData() {
    }

    public AhpFirstData(AhpData ahpData) {
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
