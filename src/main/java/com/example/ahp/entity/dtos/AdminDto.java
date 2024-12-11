package com.example.ahp.entity.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class AdminDto {
    //用户id
    private Long adminId;
    //用户账号
    private String account;
    //用户名
    private String adminName;
    //用户性别
    private String sex;
    //用户电话
    private String phone;
    //用户邮箱
    private String email;
    //用户头像
    private String avatar;
    //创建时间
    private Date createdTime;
}
