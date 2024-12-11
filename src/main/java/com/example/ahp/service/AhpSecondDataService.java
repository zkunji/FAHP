package com.example.ahp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.entity.dtos.AhpSecondDataDto;
import com.example.ahp.entity.pojos.AhpSecondData;

import java.util.Map;

public interface AhpSecondDataService extends IService<AhpSecondData> {
    Result analyse(AhpSecondDataDto dto);

    Result queryAhpSecondData(long afdId, String firstName);

    Result updateOriginalData(Map<String, Object> map);
}
