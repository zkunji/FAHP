package com.example.ahp.service.impl;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpDto;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.entity.pojos.AhpData;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.service.AhpDataService;
import com.example.ahp.util.AhpUtil;
import com.example.ahp.util.ArrayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AhpDataServiceImpl implements AhpDataService {
    /**
     *@Author keith
     *@Date 2024/4/26 14:40
     *@Description 对二维矩阵进行一致性测试
     */
    @Override
    public Result consistencyTest(AhpDto dto) {
        //1.进行分析
        AhpData ahpData = new AhpData(AhpUtil.weightValueFirstAnalysis(ArrayUtil.doubleArrayMatrixUppercase(dto.getOriginalData())));
        //2.返回结果
        ahpData.setNames(ArrayUtil.stringArrayToString(dto.getNames()));
        return Result.success(new AhpDto(ahpData));
    }
}
