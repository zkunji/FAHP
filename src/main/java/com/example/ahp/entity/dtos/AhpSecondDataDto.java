package com.example.ahp.entity.dtos;

import com.example.ahp.util.ArrayUtil;
import com.example.ahp.entity.pojos.AhpSecondData;
import com.example.ahp.util.TimeUtil;
import lombok.Data;

@Data
public class AhpSecondDataDto extends AhpDto{
    private long afdId;
    private long asdId;

    public AhpSecondDataDto() {

    }

    /**
     *@Author keith
     *@Date 2023/12/24 16:58
     *@Description 二级数据转换
     */
    public AhpSecondDataDto(AhpSecondData ahpSecondData){
        this.asdId = ahpSecondData.getAsdId();
        setScale(ahpSecondData.getScale());
        setAlias(ahpSecondData.getAlias());
        setNames(ArrayUtil.stringToStringArray(ahpSecondData.getNames()));
        setOriginalData(ArrayUtil.doubleMatrixReserveThree(ArrayUtil.byteArrayToDoubleArrayMatrix(ahpSecondData.getOriginalData())));
        setEigenvectors(ArrayUtil.stringToStringArray(ahpSecondData.getEigenvectors()));
        setProportion(ArrayUtil.stringToStringArray(ahpSecondData.getProportion()));
        setMaxEigenvalue(ahpSecondData.getMaxEigenvalue());
        setCi(ahpSecondData.getCi());
        setRi(ahpSecondData.getRi());
        setCr(ahpSecondData.getCr());
        setResult(ahpSecondData.isResult());
        if(ahpSecondData.getCreatedTime() != null){
            setCreatedTime(TimeUtil.toStringDate(ahpSecondData.getCreatedTime()));
        }
        this.afdId = ahpSecondData.getAfdId();
    }
}
