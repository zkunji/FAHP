package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.EntropyWeightDto;
import com.example.ahp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class SysAdminController {
    @Autowired
    AdminService adminService;

    /**
     *@Author keith
     *@Date 2024/1/1 20:55
     *@Description 管理员注册
     */
    @PostMapping("/register")
    public Result insertAdminInfo(@RequestBody Map<String,String> params){
        return adminService.register(params);
    }

    /**
     *@Author keith
     *@Date 2024/1/1 21:04
     *@Description 管理员登录
     */
    @PostMapping("/login")
    public Result queryAdminInfo(@RequestBody Map<String,String> params){
        return adminService.login(params);
    }

    /**
     *@Author keith
     *@Date 2024/5/3 11:57
     *@Description 修改密码
     */
    @PutMapping("/password")
    public Result updateAdminPwd(@RequestBody Map<String,String> params,@RequestHeader("adminId")long adminId){
        return adminService.updateAdminPwd(params.get("new_pwd"),adminId);
    }

    /**
     *@Author keith
     *@Date 2024/1/24 15:11
     *@Description 根据一级指标外键集合以及占比权重进行综合性分析
     */
    @PostMapping("/analyse")
    public Result insertAdminAnalyseData(@RequestBody Map<String, Object> params,@RequestHeader("adminId")String adminId){
        return adminService.analyse(params,Long.parseLong(adminId));
    }

    /**
     *@Author keith
     *@Date 2024/1/2 19:31
     *@Description 根据分页条件查询某位专家一级分析数据(?接口还在用吗)
     */
    @PostMapping("/searchAll-pagination")
    public ResultInfo queryAnalyseDataByPagination(@RequestBody Map<String, Object>params){
        return adminService.searchByPagination(params);
    }

    /**
     *@Author keith
     *@Date 2024/1/1 21:48
     *@Description 根据分页条件获取用户信息
     */
    @PostMapping("/userinfo")
    public ResultInfo queryUserInfoByPagination(@RequestBody Map<String,Object>params) {
        return adminService.getUserInfo(params);
    }

    /**
     *@Author keith
     *@Date 2024/3/30 16:04
     *@Description 根据uid获取某个用户提交的分析数据
     */
    @GetMapping("/users/{uid}/integrated_data/marked")
    public Result queryAhpFirstDataMarked(@PathVariable("uid") long uid){
        return adminService.getAhpFirstDataMarked(uid);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 18:29
     *@Description 专家评审任务考核指标体系打分(熵权法)
     */
    @PostMapping("/entropyWeight")
    public Result insertEntropyWeight(@RequestBody EntropyWeightDto dto){
        return adminService.insertEntropyWeight(dto);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 21:12
     *@Description 获取模糊算法数据的外键(FuzzyAlgorithmFirstDataId)集合、用户信息和用户权重
     */
    @GetMapping("/fuzzyAlgorithmDataDetails")
    public Result queryFuzzyAlgorithmFirstDataDetails(@RequestParam("pageNum")int pageNum,@RequestParam("pageSize")int pageSize){
        return adminService.queryFuzzyAlgorithmFirstDataDetails(pageNum,pageSize);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 21:11
     *@Description 根据外键获取模糊算法主要数据内容
     */
    @GetMapping("/fuzzyAlgorithmDataSubject")
    public Result queryFuzzyAlgorithmFirstSubject(@RequestParam("faf_id")long fafId){
        return adminService.queryFuzzyAlgorithmFirstSubject(fafId);
    }
}
