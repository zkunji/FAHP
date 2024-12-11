package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("first_index_data_dg")
public class AhpFirstDataDynamicGenerate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ratingName;
    private String firstIndexData;
    private LocalDateTime createTime = LocalDateTime.now();

    public AhpFirstDataDynamicGenerate(Long firstIndexIdDg, String ratingName, String indexName) {
        this.id = firstIndexIdDg;
        this.ratingName = ratingName;
        this.firstIndexData = indexName;

    }
}
