package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    //用户id
    @TableId(type = IdType.AUTO)
    private Long uid;
    //用户账号
    private String account;
    //用户名
    private String username;
    //用户密码
    private String password;
    //用户盐
    private String salt;
    //用户性别
    private String sex;
    //用户电话
    private String phone;
    //用户邮箱
    private String email;
    //用户头像
    private byte[] avatar;
    //考核指标
    private byte[] assessment;
    //创建时间
    private Date createdTime;

    public User() {
    }

    public User(String account, String password, String salt) {
        this.account = account;
        this.password = password;
        this.salt = salt;
    }
}
