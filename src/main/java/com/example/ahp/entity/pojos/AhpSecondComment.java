package com.example.ahp.entity.pojos;

import com.baomidou.mybatisplus.annotation.TableId;
import com.example.ahp.entity.dtos.AhpSecondCommentDto;
import lombok.Data;

import java.util.Date;

@Data
public class AhpSecondComment {
    //一级指标外键
    private long afdId;
    //二级指标外键
    private long asdId;
    //二级指标别名
    private String alias;
    //评价水平:优秀、良好、警戒、危险
    private String level;
    //文本评语集
    private byte[] comment;
    //文件集
    private String files;
    //创建时间
    private Date createdTime;

    public AhpSecondComment() {
    }

    public AhpSecondComment(AhpSecondCommentDto dto) {
        afdId = dto.getAfdId();
        asdId = dto.getAsdId();
        alias = dto.getAlias();
        level = dto.getLevel();
        comment = dto.getComment().getBytes();
    }
}
