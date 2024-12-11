package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.service.AhpSecondCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ahpSecondComment")
public class AhpSecondCommentController {
    @Autowired
    AhpSecondCommentsService ahpSecondCommentsService;

    /**
     *@Author keith
     *@Date 2024/1/1 14:17
     *@Description 二级指标打分
     */
    @PostMapping("/evaluate")
    public Result insertAhpCommentData(@RequestBody Map<String,Object> params){
        return ahpSecondCommentsService.evaluate(params);
    }

    /**
     *@Author keith
     *@Date 2024/1/1 16:35
     *@Description 根据一级指标外键查询二级指标打分
     */
    @GetMapping("/search/{afd_id}")
    public Result queryAhpCommentDataByAfdId(@PathVariable("afd_id")long afdId){
        return ahpSecondCommentsService.searchByAfdId(afdId);
    }

    /**
     *@Author keith
     *@Date 2024/3/9 14:25
     *@Description 根据一级指标外键、二级指标外键、二级指标名称上传文件
     */
    @PostMapping("/uploadFiles")
    public Result insertFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("afd_id") long afdId,
            @RequestParam("asd_id") long asdId,
            @RequestParam("second_alias") String alias){
        return ahpSecondCommentsService.uploadFiles(files,afdId,asdId,alias);
    }

    /**
     *@Author keith
     *@Date 2024/3/30 15:40
     *@Description 根据一级指标外键、二级指标外键、二级指标名称获取文件
     */
    @GetMapping("/files")
    public Result queryFiles(
            @RequestParam("afd_id") long afdId,
            @RequestParam("asd_id") long asdId,
            @RequestParam("second_alias") String alias){
        return ahpSecondCommentsService.getFiles(afdId,asdId,alias);
    }
}
