package com.example.ahp.service;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.Question;

/**
 * @author Zhangkunji
 * @date 2024/11/27
 * @Description
 */
public interface AlibabaAIService extends IService<Question> {
    Result call(String question) throws NoApiKeyException, InputRequiredException;

    Result startNewDialogue();

    Result viewHistory();
}
