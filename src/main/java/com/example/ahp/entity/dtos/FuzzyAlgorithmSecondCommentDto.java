package com.example.ahp.entity.dtos;

import com.example.ahp.entity.pojos.FuzzyAlgorithmSecondComment;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmSecondCommentDto {
    //一级指标外键
    private long fafId;
    //二级指标外键
    private long fasId;
    //二级指标别名
    private String alias;
    //评价人数,依次为(优秀、良好、警戒、危险)
    private int[] commentsNumber = new int[4];
    //模糊矩阵
    private double[] fuzzyMatrix;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/26 15:20
     *@Description AdminSecondComment->AdminSecondCommentDto
     */
    public FuzzyAlgorithmSecondCommentDto(FuzzyAlgorithmSecondComment fuzzyAlgorithmSecondComment) {
        fafId = fuzzyAlgorithmSecondComment.getFafId();
        fasId = fuzzyAlgorithmSecondComment.getFasId();
        alias = fuzzyAlgorithmSecondComment.getAlias();
        commentsNumber = ArrayUtil.byteArrayToIntArray(fuzzyAlgorithmSecondComment.getCommentsNumber());
        fuzzyMatrix = ArrayUtil.byteArrayToDoubleArray(fuzzyAlgorithmSecondComment.getFuzzyMatrix());
        createdTime = fuzzyAlgorithmSecondComment.getCreatedTime();
    }

    public FuzzyAlgorithmSecondCommentDto() {
    }
}
