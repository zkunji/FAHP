package com.example.ahp.entity.pojos;

import com.example.ahp.entity.dtos.FuzzyAlgorithmSecondCommentDto;
import com.example.ahp.util.ArrayUtil;
import lombok.Data;

import java.util.Date;

@Data
public class FuzzyAlgorithmSecondComment {
    //一级指标外键
    private long fafId;
    //二级指标外键
    private long fasId;
    //二级指标别名
    private String alias;
    //评价人数,依次为(优秀、良好、警戒、危险)
    private byte[] commentsNumber;
    //模糊矩阵
    private byte[] fuzzyMatrix;
    //创建时间
    private Date createdTime;

    /**
     *@Author keith
     *@Date 2024/1/24 20:09
     *@Description AdminSecondCommentDto->AdminSecondComment
     */
    public FuzzyAlgorithmSecondComment(FuzzyAlgorithmSecondCommentDto dto, int number) {
        fafId = dto.getFafId();
        fasId = dto.getFasId();
        alias = dto.getAlias();
        commentsNumber = ArrayUtil.intArrayToByteArray(dto.getCommentsNumber());
        double[] doubles = new double[4];
        for(int i=0;i<4;i++){
            doubles[i] = dto.getCommentsNumber()[i]/(double)number;
        }
        fuzzyMatrix = ArrayUtil.doubleArrayToByteArray(doubles);
    }

    public FuzzyAlgorithmSecondComment() {
    }
}
