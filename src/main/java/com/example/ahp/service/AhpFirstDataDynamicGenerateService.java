package com.example.ahp.service;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.AhpFirstDataDynamicGenerate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AhpFirstDataDynamicGenerateService {
    Result addFirstIndexData(AhpFirstDataDynamicGenerate element);

    Result showHistoryData();
}
