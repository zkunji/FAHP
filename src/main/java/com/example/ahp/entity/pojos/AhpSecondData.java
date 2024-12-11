package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AhpSecondData extends AhpData{
    //ahp二级标识
    @TableId(type = IdType.AUTO)
    private long asdId;
    //ahp一级标识
    private long afdId;

    public AhpSecondData() {
    }

    public AhpSecondData(AhpData ahpData) {
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
