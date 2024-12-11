package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.AhpFirstDataDynamicGenerate;
import com.example.ahp.service.AhpFirstDataDynamicGenerateService;
import com.example.ahp.service.impl.AhpFirstDataDynamicGenerateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequestMapping("/firstIndex")
@RestController
public class AhpFirstDataDynamicGenerateController {
    @Autowired
    AhpFirstDataDynamicGenerateService ahpFirstDataDynamicGenerateService;

    @PostMapping("/select")
    public Result selectionIndex(@RequestBody AhpFirstDataDynamicGenerate data) {
        return ahpFirstDataDynamicGenerateService.addFirstIndexData(data);
    }

    @GetMapping("/history")
    @ResponseBody
    public Result showHistoryIndex() {

        return ahpFirstDataDynamicGenerateService.showHistoryData();
    }

}
