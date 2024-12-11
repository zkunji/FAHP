package com.example.ahp.controller;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.UserDto;
import com.example.ahp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {
    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * @Author keith
     * @Date 2023/12/24 11:45
     * @Description 注册
     */
    @PostMapping("/register")
    public Result insertUserInfo(@RequestBody Map<String, String> params) {
        return userService.register(params);
    }

    /**
     * @Author keith
     * @Date 2023/12/24 12:01
     * @Description 登录
     */
    @PostMapping("/login")
    public Result queryUserInfo(@RequestBody Map<String, String> params) {
        return userService.login(params);
    }

    /**
     * @Author keith
     * @Date 2023/12/30 12:18
     * @Description 根据uid获取用户信息{uid,用户账号,用户名,头像,性别,电话,邮箱，创建时间}
     */
    @GetMapping("/userinfo")
    public Result queryUserInfoByUid(@RequestHeader("uid") String uid) {
        return userService.getInfo(Long.parseLong(uid));
    }


    /**
     * @Author keith
     * @Date 2023/12/30 11:14
     * @Description 更新密码
     */
    @PatchMapping("/updatepwd")
    public Result updateUserPwd(@RequestBody Map<String, String> params, @RequestHeader("uid") String uid) {
        return userService.updatePwd(params, Long.parseLong(uid));
    }

    /**
     * @Author keith
     * @Date 2024/1/23 23:31
     * @Description 更改用户信息
     */
    @PutMapping("/userinfo")
    public Result updateUsername(@RequestBody Map<String, String> params, @RequestHeader("uid") String uid) {
        return userService.updateUserinfo(params, Long.parseLong(uid));
    }

    /**
     *@Author keith
     *@Date 2024/4/28 17:39
     *@Description 专家评审任务考核指标体系打分(新增)
     */
    @PostMapping("/assessment")
    public Result insertAssessment(@RequestBody UserDto userDto){
        return userService.insertAssessment(userDto);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 17:41
     *@Description 专家评审任务考核指标体系打分(修改)
     */
    @PutMapping("/assessment")
    public Result updateAssessment(@RequestBody UserDto userDto){
        return userService.updateAssessment(userDto);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 18:12
     *@Description 专家评审任务考核指标体系打分(查询)
     */
    @GetMapping("/assessment")
    public Result queryAssessment(@RequestParam("uid")long uid){
        return userService.queryAssessment(uid);
    }
}
