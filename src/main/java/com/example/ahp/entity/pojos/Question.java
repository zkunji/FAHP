package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Zhangkunji
 * @date 2024/11/27
 * @Description
 */

@Data
public class Question {

    @TableId(type = IdType.AUTO)
    private String requestId;
    private String ask;
    private String answer;
    private String finishReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dialogueTime;

    public Question(String requestId, String ask, String answer, String finishReason) {
        this.requestId = requestId;
        this.ask = ask;
        this.answer = answer;
        this.finishReason = finishReason;
    }
}
