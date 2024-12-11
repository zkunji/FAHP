package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.service.AhpFirstDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ahpFirstData")
public class AhpFirstDataController {
    @Autowired
    AhpFirstDataService ahpFirstDataService;
    /**
     *@Author kkke
     *@Date 2023/11/20 16:48
     *@Description 对一级指标进行层次分析
     */
    @PostMapping("/analyse")
    public Result insertAhpFirstAnalyseData(@RequestBody AhpFirstDataDto dto){
        return ahpFirstDataService.analyse(dto);
    }

    /**
     *@Author keith
     *@Date 2024/4/4 12:43
     *@Description 根据afdId集合删除数据
     */
    @DeleteMapping("")
    public Result deleteAhpDataByAfdIds(@RequestBody Map<String,Object> map){
        return ahpFirstDataService.deleteAhpDataByAfdIds(
                ((List<Integer>) map.get("ids")).stream()
                .map(i -> i.longValue())
                .mapToLong(Long::longValue)
                .toArray());
    }

    /**
     *@Author keith
     *@Date 2023/12/24 16:59
     *@Description 根据(用户主键&分页信息)查询一级分析数据
     */
    @PostMapping("/searchAll-pagination-user")
    public ResultInfo queryAhpFirstSuperficialData(@RequestBody Map<String, Object>params, @RequestHeader("uid")long uid){
        return ahpFirstDataService.searchByUidAndPagination(params,uid);
    }

    /**
     *@Author keith
     *@Date 2024/1/1 15:49
     *@Description 提交(让管理员看到这位专家的这次分析记录)
     */
    @GetMapping("/submit/{afd_id}")
    public Result updateSubmit(@PathVariable("afd_id")long afdId,@RequestHeader("uid")long uid){
        return ahpFirstDataService.submit(afdId,uid);
    }

    /**
     *@Author keith
     *@Date 2024/1/20 23:52
     *@Description 根据一级指标外键查找一级、二级数据
     */
    @GetMapping("/searchNameProportion/{afd_id}")
    public Result queryNameProportion(@PathVariable("afd_id")long afdId){
        return ahpFirstDataService.searchNameProportion(afdId);
    }

    /**
     *@Author keith
     *@Date 2024/2/1 12:45
     *@Description 根据一级指标外键查找一级、二级数据和评语集
     */
    @GetMapping("/searchProportionComments/{afd_id}")
    public Result queryProportionComments(@PathVariable("afd_id")long afdId){
        return ahpFirstDataService.searchProportionComments(afdId);
    }

    /**
     *@Author keith
     *@Date 2024/4/26 15:20
     *@Description 根据一级指标外键查询一级数据(矩阵和分析结果)
     */
    @GetMapping("")
    public Result queryAhpFirstData(@RequestParam("afd_id")long afdId){
        return ahpFirstDataService.queryAhpFirstData(afdId);
    }

    /**
     *@Author keith
     *@Date 2024/4/26 15:15
     *@Description 修改二维矩阵原始数据
     */
    @PutMapping("/originalData")
    public Result updateOriginalData(@RequestBody Map<String,Object> map){
        return ahpFirstDataService.updateOriginalData(map);
    }
}
