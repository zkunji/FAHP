package com.example.ahp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.UserDto;
import com.example.ahp.entity.pojos.User;
import org.apache.shiro.realm.Realm;

import java.util.Map;

public interface UserService extends IService<User> {
    User selectByAccount(String account);

    Result updatePwd(Map<String, String> params, long uid);

    Result getInfo(long uid);

    Result updateUserinfo(Map<String, String> params, long uid);

    Result register(Map<String, String> params);

    Result login(Map<String, String> params);

    Result insertAssessment(UserDto userDto);

    Result updateAssessment(UserDto userDto);

    Result queryAssessment(long uid);
}
