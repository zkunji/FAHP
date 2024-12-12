package com.example.ahp.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.ahp.common.result.Result;
import com.example.ahp.service.AlibabaAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhangkunji
 * @date 2024/12/12
 * @Description
 */

@RestController
@RequestMapping("/aiService")
public class AlibabaAIController {

    private final AlibabaAIService aiService;

    @Autowired
    public AlibabaAIController(AlibabaAIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public Result askAQuestion(@RequestBody String question) throws NoApiKeyException, InputRequiredException {
        return aiService.call(question);
    }
}
