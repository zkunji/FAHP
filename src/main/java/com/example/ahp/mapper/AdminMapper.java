package com.example.ahp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ahp.entity.pojos.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("select * from admin where account = #{account}")
    Admin queryByAccount(@Param("account")String account);
}
