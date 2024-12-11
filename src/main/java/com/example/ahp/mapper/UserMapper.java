package com.example.ahp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ahp.entity.pojos.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where account = #{account}")
    User queryByAccount(@Param("account") String account);
}
