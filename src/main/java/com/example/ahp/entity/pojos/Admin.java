package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Admin {
    //用户id
    @TableId(type = IdType.AUTO)
    private Long adminId;
    //用户账号
    private String account;
    //用户名
    private String adminName;
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
    //创建时间
    private Date createdTime;

    public Admin(String account, String password, String salt) {
        this.account = account;
        this.password = password;
        this.salt = salt;
    }
}
