package com.example.ahp.service;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpDto;

public interface AhpDataService {
    Result consistencyTest(AhpDto dto);
}
