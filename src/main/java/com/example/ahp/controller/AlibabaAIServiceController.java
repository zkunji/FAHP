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
 * @date 2024/11/27
 * @Description
 */

@RestController
@RequestMapping("/ai")
public class AlibabaAIServiceController {
    @Autowired
    AlibabaAIService alibabaAIService;

    @PostMapping
    public Result aiReply(@RequestBody String question, String DBId) throws NoApiKeyException, InputRequiredException {
        return alibabaAIService.call(question, DBId);
    }

    @PostMapping("/new_dialogue")
    public Result startNewDialogue() {
        return alibabaAIService.startNewDialogue();
    }

}
