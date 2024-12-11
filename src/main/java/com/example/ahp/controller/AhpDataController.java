package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpDto;
import com.example.ahp.service.AhpDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ahpData")
public class AhpDataController {
    @Autowired
    AhpDataService ahpDataService;

    /**
     *@Author keith
     *@Date 2024/4/26 14:40
     *@Description 对二维矩阵进行一致性测试
     */
    @GetMapping("/consistency-test")
    public Result consistencyTest(@RequestBody AhpDto dto){
        return ahpDataService.consistencyTest(dto);
    }
}
