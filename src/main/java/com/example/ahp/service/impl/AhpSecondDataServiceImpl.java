package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.util.AhpUtil;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpSecondDataDto;
import com.example.ahp.entity.pojos.AhpSecondData;
import com.example.ahp.mapper.AhpSecondDataMapper;
import com.example.ahp.service.AhpSecondDataService;
import com.example.ahp.util.RedisUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class AhpSecondDataServiceImpl extends ServiceImpl<AhpSecondDataMapper, AhpSecondData> implements AhpSecondDataService {
    @Autowired
    AhpSecondDataMapper ahpSecondDataMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     *@Author keith
     *@Date 2023/12/24 16:50
     *@Description ahp二级分析
     */
    @Override
    public Result analyse(AhpSecondDataDto dto) {
        //1.进行分析
        AhpSecondData ahpSecondData = new AhpSecondData(AhpUtil.weightValueFirstAnalysis(ArrayUtil.doubleArrayMatrixUppercase(dto.getOriginalData())));
        //2.判断一致性是否成功
        //2.1一致性通过
        ahpSecondData.setAlias(dto.getAlias());
        ahpSecondData.setNames(ArrayUtil.stringArrayToString(dto.getNames()));
        ahpSecondData.setAfdId(dto.getAfdId());
        AhpSecondDataDto ahpSecondDataDto;
        try {
            //flag:>0:异常;1:不通过;2:通过
            int flag = 0;
            if (ahpSecondData.isResult()) {
                // 将数据存到数据库
                flag = ahpSecondDataMapper.insert(ahpSecondData);
                if (flag == 0) {
                    return Result.fail("数据库插入数据异常,分析失败");
                } else {
                    // 设置为通过状态
                    flag = 2;
                }
            } else {
                // 设置为不通过状态
                flag = 1;
            }
            // 返回结果
            if (flag == 1) {
                // 一致性不通过
                ahpSecondDataDto = new AhpSecondDataDto(ahpSecondData);
                return Result.fail("一致性不通过!", ahpSecondDataDto);
            } else {
                // 插入数据成功
                ahpSecondDataDto = new AhpSecondDataDto(ahpSecondData);
                return Result.success(ahpSecondDataDto);
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            // 手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 返回异常结果
            return Result.fail("分析过程中发生异常");
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/26 16:30
     *@Description 根据一级指标外键和一级指标名称查询二级数据(矩阵和分析结果)
     */
    @Override
    public Result queryAhpSecondData(long afdId, String firstName) {
        //先查询出二级指标外键
        LambdaQueryWrapper<AhpSecondData> qw = new LambdaQueryWrapper<>();
        qw.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId,afdId).eq(AhpSecondData::getAlias,firstName);
        AhpSecondData secondData = ahpSecondDataMapper.selectOne(qw);
        if(secondData == null){
            return Result.fail("查询失败");
        }
        //从redis查询数据
        secondData = RedisUtil.getAhpSecondDataFromRedis(secondData.getAsdId(), redisTemplate, ahpSecondDataMapper);
        //返回数据
        return Result.success(new AhpSecondDataDto(secondData));
    }

    /**
     *@Author keith
     *@Date 2024/4/26 16:42
     *@Description 修改二维矩阵原始数据
     */
    @Override
    public Result updateOriginalData(Map<String, Object> map) {
        //1.获取参数
        long asdId = (Integer) map.get("asd_id");
        List<List<Double>> originalDataList = (List<List<Double>>) map.get("original_data");
        // 转换为 double[][] 数组
        double[][] originalData = new double[originalDataList.size()][originalDataList.get(0).size()];
        for (int i = 0; i < originalDataList.size(); i++) {
            List<Double> innerList = originalDataList.get(i);
            for (int j = 0; j < innerList.size(); j++) {
                originalData[i][j] = innerList.get(j);
            }
        }

        //2.进行分析(因为会先进行一致性测试,所以一定通过)
        AhpSecondData newAhpSecondData = new AhpSecondData(AhpUtil.weightValueFirstAnalysis(ArrayUtil.doubleArrayMatrixUppercase(originalData)));

        //3.修改数据库数据
        //3.1准备新数据
        LambdaUpdateWrapper<AhpSecondData> uw = new LambdaUpdateWrapper<>();
        uw.eq(AhpSecondData::getAsdId, asdId);
        uw.set(AhpSecondData::getOriginalData, ArrayUtil.doubleMatrixArrayToByteArray(originalData));
        uw.set(AhpSecondData::getEigenvectors, newAhpSecondData.getEigenvectors());
        uw.set(AhpSecondData::getProportion, newAhpSecondData.getProportion());
        uw.set(AhpSecondData::getMaxEigenvalue, newAhpSecondData.getMaxEigenvalue());
        uw.set(AhpSecondData::getCi, newAhpSecondData.getCi());
        uw.set(AhpSecondData::getCr, newAhpSecondData.getCr());
        //3.2更新
        int flag = ahpSecondDataMapper.update(null, uw);

        //4.删除redis数据
        redisTemplate.delete(RedisConstants.AhpSecondDataInfo + asdId);

        //5.返回数据
        if (flag == 0) {
            return Result.fail("更新数据失败");
        } else {
            return Result.success(new AhpSecondDataDto(RedisUtil.getAhpSecondDataFromRedis(asdId,redisTemplate,ahpSecondDataMapper)));
        }
    }
}
