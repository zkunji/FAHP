package com.example.ahp.service.impl;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.Question;
import com.example.ahp.mapper.AlibabaAIMapper;
import com.example.ahp.service.AlibabaAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zhangkunji
 * @date 2024/11/27
 * @Description
 */

@Service
public class AlibabaAIServiceImpl extends ServiceImpl<AlibabaAIMapper, Question> implements AlibabaAIService {
    @Autowired
    AlibabaAIMapper alibabaAIMapper;

    private final String APP_ID = "466b0dd21f434203a30cb3ea0b851275";

    private static ApplicationParam param = null;

    @Override
    public Result call(String question) throws NoApiKeyException, InputRequiredException {
        try {
            if (param == null) {
                param = problemPreprocessing(question);
            } else {
                param.setPrompt(question);
            }
            Application application = new Application();
            ApplicationResult result = application.call(param);
            param.setSessionId(result.getOutput().getSessionId());
            System.out.println("test"+result.getRequestId());

            Question history = new Question(
                    result.getRequestId(),
                    question,
                    result.getOutput().getText(),
                    result.getOutput().getFinishReason()
            );
            int res = alibabaAIMapper.insert(history);
            if (res > 0) {
                System.out.println("对话保存成功");
            }
            return Result.success(result);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            return Result.fail(e.getMessage());
        }

    }

    @Override
    public Result startNewDialogue() {
        try {
            param = null;
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.success("新建对话成功");
    }

    @Override
    public Result viewHistory() {
        return null;
    }

    public ApplicationParam problemPreprocessing(String question) {
        return ApplicationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .appId(APP_ID)
                .prompt(question)
                .build();
    }
}
