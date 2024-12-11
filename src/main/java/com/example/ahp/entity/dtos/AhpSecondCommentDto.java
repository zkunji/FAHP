package com.example.ahp.entity.dtos;

import com.example.ahp.entity.pojos.AhpSecondComment;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class AhpSecondCommentDto {
    //一级指标外键
    private long afdId;
    //二级指标外键
    private long asdId;
    //二级指标别名
    private String alias;
    //评价水平:优秀、良好、警戒、危险
    private String level;
    //具体扣分点(优秀不用填)
    private String comment;
    //文件集
    private String[] files;

    /**
     * @Author keith
     * @Date 2024/1/1 16:42
     * @Description 二级打分数据转换
     */
    public AhpSecondCommentDto() {
    }

    public AhpSecondCommentDto(AhpSecondComment secondComment) {
        afdId = secondComment.getAfdId();
        asdId = secondComment.getAsdId();
        alias = secondComment.getAlias();
        level = secondComment.getLevel();
        comment = new String(secondComment.getComment());
        if(secondComment.getFiles()!=null && !"".equals(secondComment.getFiles())){
            files = secondComment.getFiles().split(",");
        }
    }
}
