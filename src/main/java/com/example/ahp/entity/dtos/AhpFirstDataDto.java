package com.example.ahp.entity.dtos;

import com.example.ahp.util.ArrayUtil;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.util.TimeUtil;
import lombok.Data;

import java.text.SimpleDateFormat;

@Data
public class AhpFirstDataDto extends AhpDto{
    //aho一级标识
    private long afdId;
    //用户标识
    private long uid;
    //提交状态
    private int submit;

    public AhpFirstDataDto() {

    }

    /**
     *@Author keith
     *@Date 2023/12/24 16:58
     *@Description 一级数据转化
     */
    public AhpFirstDataDto(AhpFirstData ahpFirstData){
        this.afdId = ahpFirstData.getAfdId();
        setScale(ahpFirstData.getScale());
        setAlias(ahpFirstData.getAlias());
        setNames(ArrayUtil.stringToStringArray(ahpFirstData.getNames()));
        setOriginalData(ArrayUtil.doubleMatrixReserveThree(ArrayUtil.byteArrayToDoubleArrayMatrix(ahpFirstData.getOriginalData())));
        setEigenvectors(ArrayUtil.stringToStringArray(ahpFirstData.getEigenvectors()));
        setProportion(ArrayUtil.stringToStringArray(ahpFirstData.getProportion()));
        setMaxEigenvalue(ahpFirstData.getMaxEigenvalue());
        setCi(ahpFirstData.getCi());
        setRi(ahpFirstData.getRi());
        setCr(ahpFirstData.getCr());
        setResult(ahpFirstData.isResult());
        if(ahpFirstData.getCreatedTime()!=null){
            setCreatedTime(TimeUtil.toStringDate(ahpFirstData.getCreatedTime()));
        }
        setSubmit(ahpFirstData.getSubmit());
        this.uid = ahpFirstData.getUid();
    }
}
