package com.example.ahp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.entity.pojos.AhpFirstData;

import java.util.Map;

public interface AhpFirstDataService extends IService<AhpFirstData> {
    Result analyse(AhpFirstDataDto dto);
    Result submit(long afdId,long uid);
    ResultInfo searchByUidAndPagination(Map<String, Object> params, long uid);
    Result searchNameProportion(long afdId);
    Result searchProportionComments(long afdId);
    Result deleteAhpDataByAfdIds(long[] afdIds);
    Result queryAhpFirstData(long afdId);
    Result updateOriginalData(Map<String,Object> map);
}
