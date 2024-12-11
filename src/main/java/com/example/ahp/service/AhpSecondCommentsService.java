package com.example.ahp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.AhpSecondComment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AhpSecondCommentsService extends IService<AhpSecondComment> {

    Result evaluate(Map<String, Object> params);

    Result searchByAfdId(long afdId);

    Result uploadFiles(MultipartFile[] files, long afdId, long asdId, String alias);

    Result getFiles(long afdId, long asdId, String alias);
}
