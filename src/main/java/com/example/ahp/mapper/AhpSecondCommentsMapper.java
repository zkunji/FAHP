package com.example.ahp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ahp.entity.pojos.AhpSecondComment;
import com.example.ahp.entity.pojos.User;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AhpSecondCommentsMapper extends BaseMapper<AhpSecondComment> {
    @Select("SELECT DISTINCT asd_id FROM ahp_second_comment WHERE afd_id = #{afdId}")
    List<Long> selectAsdIdByAfdId(@Param("afdId") long afdId);
}
