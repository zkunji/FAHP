package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpSecondDataDto;
import com.example.ahp.service.AhpSecondDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ahpSecondData")
public class AhpSecondDataController {
    @Autowired
    AhpSecondDataService ahpSecondDataService;

    /**
     *@Author kkke
     *@Date 2023/11/20 16:48
     *@Description 二级指标进行层次分析
     */
    @PostMapping("/analyse")
    public Result insertAhpSecondAnalyseData(@RequestBody AhpSecondDataDto dto){
        return ahpSecondDataService.analyse(dto);
    }

    /**
     *@Author keith
     *@Date 2024/4/26 16:30
     *@Description 根据一级指标外键和一级指标名称查询二级数据(矩阵和分析结果)
     */
    @GetMapping("")
    public Result queryAhpSecondData(@RequestParam("afd_id")long afdId,@RequestParam("first_name")String firstName){
        return ahpSecondDataService.queryAhpSecondData(afdId,firstName);
    }

    /**
     *@Author keith
     *@Date 2024/4/26 16:42
     *@Description 修改二维矩阵原始数据
     */
    @PutMapping("/originalData")
    public Result updateOriginalData(@RequestBody Map<String,Object> map){
        return ahpSecondDataService.updateOriginalData(map);
    }
}
