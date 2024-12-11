package com.example.ahp.mapper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ahp.entity.pojos.AhpFirstDataDynamicGenerate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AhpFirstDataDynamicGenerateMapper extends BaseMapper<AhpFirstDataDynamicGenerate> {

}
