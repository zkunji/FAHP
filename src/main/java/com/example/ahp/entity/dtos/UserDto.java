package com.example.ahp.entity.dtos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.pojos.User;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.util.TimeUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;

@Data
public class UserDto {
    //用户id
    private Long uid;
    //用户账号
    private String account;
    //用户名
    private String username;
    //用户性别
    private String sex;
    //用户电话
    private String phone;
    //用户邮箱
    private String email;
    //用户头像
    private String avatar;
    //考核指标
    private double[] assessment;
    //创建时间
    private String createdTime;

    public UserDto() {
    }

    public UserDto(User user) {
        uid = user.getUid();
        account = user.getAccount();
        username = user.getUsername();
        sex = user.getSex();
        phone = user.getPhone();
        email = user.getEmail();
        createdTime = TimeUtil.toStringDate(user.getCreatedTime());
        if(user.getAvatar()!=null && user.getAvatar().length>0){
            avatar = new String(user.getAvatar());
        }
        if(user.getAssessment() != null){
            assessment = ArrayUtil.byteArrayToDoubleArray(user.getAssessment());
        }
    }
}
