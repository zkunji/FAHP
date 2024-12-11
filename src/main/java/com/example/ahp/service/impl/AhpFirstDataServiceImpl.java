package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.AhpSecondCommentDto;
import com.example.ahp.entity.dtos.AhpSecondDataDto;
import com.example.ahp.entity.dtos.InquiryTypeDto;
import com.example.ahp.entity.pojos.AhpSecondComment;
import com.example.ahp.entity.pojos.AhpSecondData;
import com.example.ahp.mapper.AhpSecondCommentsMapper;
import com.example.ahp.mapper.AhpSecondDataMapper;
import com.example.ahp.mapper.InquiryTypeMapper;
import com.example.ahp.util.AhpUtil;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpFirstDataDto;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.mapper.AhpFirstDataMapper;
import com.example.ahp.service.AhpFirstDataService;
import com.example.ahp.util.MinioUtil;
import com.example.ahp.util.RedisUtil;
import com.google.gson.Gson;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
@Transactional
public class AhpFirstDataServiceImpl extends ServiceImpl<AhpFirstDataMapper, AhpFirstData> implements AhpFirstDataService {
    @Autowired
    AhpFirstDataMapper ahpFirstDataMapper;
    @Autowired
    AhpSecondDataMapper ahpSecondDataMapper;
    @Autowired
    AhpSecondCommentsMapper ahpSecondCommentsMapper;
    @Autowired
    InquiryTypeMapper inquiryTypeMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MinioClient minioClient;

    /**
     * @Author keith
     * @Date 2023/12/24 16:48
     * @Description ahp一级分析
     */
    @Override
    public Result analyse(AhpFirstDataDto dto) {
        //1.进行分析
        AhpFirstData ahpFirstData = new AhpFirstData(AhpUtil.weightValueFirstAnalysis(ArrayUtil.doubleArrayMatrixUppercase(dto.getOriginalData())));
        //2.判断一致性是否成功
        //2.1一致性通过
        ahpFirstData.setAlias(dto.getAlias());
        ahpFirstData.setNames(ArrayUtil.stringArrayToString(dto.getNames()));
        ahpFirstData.setUid(dto.getUid());
        try {
            //flag:>0:异常;1:不通过;2:通过
            int flag = 0;
            if (ahpFirstData.isResult()) {
                // 将数据存到数据库
                flag = ahpFirstDataMapper.insert(ahpFirstData);
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
                AhpFirstDataDto ahpFirstDataDto = new AhpFirstDataDto(ahpFirstData);
                return Result.fail("一致性不通过!", ahpFirstDataDto);
            } else {
                // 插入数据成功
                AhpFirstDataDto ahpFirstDataDto = new AhpFirstDataDto(ahpFirstData);
                return Result.success(ahpFirstDataDto);
            }
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            // 手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 返回异常结果
            return Result.fail("服务器异常,分析失败");
        }
    }

    /**
     * @Author keith
     * @Date 2023/12/24 16:49
     * @Description 根据(用户主键&分页信息)查询一级分析数据
     */
    @Override
    public ResultInfo searchByUidAndPagination(Map<String, Object> params, long uid) {
        //获取参数
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        //生成分页对象
        Page<AhpFirstData> page = new Page<>(pageNum, pageSize);
        //设置查询条件
        LambdaQueryWrapper<AhpFirstData> wrapper = new LambdaQueryWrapper<>();
        //1.从数据库查询外键集合
        wrapper.select(AhpFirstData::getAfdId).eq(AhpFirstData::getUid, uid);
        List<AhpFirstData> list = ahpFirstDataMapper.selectPage(page, wrapper).getRecords();
        //2.从redis和数据库获取ahpFirstData数据
        list = RedisUtil.getAhpFirstDatasFromRedis(list, redisTemplate, ahpFirstDataMapper);
        //3.未查询到该用户的ahp分析记录
        if (list.size() == 0) {
            return ResultInfo.fail("查询为空!");
        }
        //4.查询到该用户的ahp分析记录
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        for (AhpFirstData ahpFirstData : list) {
            AhpFirstDataDto dto = new AhpFirstDataDto(ahpFirstData);
            HashMap<String, Object> map = new HashMap<>();
            map.put("afd_id", dto.getAfdId());
            map.put("alias", dto.getAlias());
            map.put("created_time", dto.getCreatedTime());
            map.put("submit", dto.getSubmit());
            resultList.add(map);
        }
        //5.查询数据总条数
        LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
        qw.eq(AhpFirstData::getUid, uid);
        Integer count = ahpFirstDataMapper.selectCount(qw);
        return ResultInfo.success(count, resultList);
    }

    /**
     * @Author keith
     * @Date 2024/1/1 15:52
     * @Description 提交此次分析
     */
    @Override
    public Result submit(long afdId, long uid) {
        //1.查询此条afdId数据的submit是否>=0(完整状态)
        AhpFirstData redisData0 = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
        if (redisData0 == null) {
            return Result.fail("设置为提交状态的数据不存在");
        }
        if (redisData0.getSubmit() == -1) {
            return Result.fail("设置为提交状态的数据不完整");
        }
        if (redisData0.getSubmit() == 1) {
            return Result.success("数据已设置为提交状态");
        }
        //2.找出先前被设置为1的数据,将其置为0
        LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
        qw.select(AhpFirstData::getAfdId).eq(AhpFirstData::getSubmit, 1).eq(AhpFirstData::getUid, uid);
        AhpFirstData ahpFirstData1 = ahpFirstDataMapper.selectOne(qw);
        try {
            //2.1更新数据库和redis的数据
            if (ahpFirstData1 != null) {
                //先前有置为1的数据
                //修改数据库数据
                LambdaUpdateWrapper<AhpFirstData> uw = new LambdaUpdateWrapper<>();
                uw.eq(AhpFirstData::getAfdId, ahpFirstData1.getAfdId()).set(AhpFirstData::getSubmit, 0);
                int flag = ahpFirstDataMapper.update(null, uw);
                if (flag == 0) {
                    return Result.fail("先前提交的数据设置为未提交失败");
                }
                //修改redis数据
                ahpFirstData1 = RedisUtil.getAhpFirstDataFromRedis(ahpFirstData1.getAfdId(), redisTemplate, ahpFirstDataMapper);
                ahpFirstData1.setSubmit(0);
                redisTemplate.opsForValue().set(RedisConstants.AhpFirstDataInfo + ahpFirstData1.getAfdId(),new Gson().toJson(ahpFirstData1));
            }
            //3.把此次afdId的数据设置为提交状态
            LambdaUpdateWrapper<AhpFirstData> uw = new LambdaUpdateWrapper<>();
            uw.eq(AhpFirstData::getAfdId, redisData0.getAfdId()).set(AhpFirstData::getSubmit, 1);
            ahpFirstDataMapper.update(null, uw);
            //3.1修改redis数据
            redisData0.setSubmit(1);
            redisTemplate.opsForValue().set(RedisConstants.AhpFirstDataInfo + redisData0.getAfdId(),new Gson().toJson(redisData0));
        } catch (Exception e) {
            e.printStackTrace();
            //手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("服务器异常,更新数据失败");
        }
        return Result.success("数据已设置为提交状态");
    }

    /**
     * @Author keith
     * @Date 2024/1/21 0:01
     * @Description 根据一级指标外键查找一级、二级的指标名和权重值
     */
    @Override
    public Result searchNameProportion(long afdId) {
        //根据一级指标外键查找分析数据
        AhpFirstData ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
        if (ahpFirstData == null) {
            return Result.fail("查询不到此数据");
        }
        //list->存储一级、二级的指标名和权重值
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        //获取一级指标名和权重值
        String[] firstDataNames = ahpFirstData.getNames().split(",");
        String[] firstDataProportions = ahpFirstData.getProportion().split(",");
        int firstDataLength = firstDataNames.length;
        //循环存储某一个一级指标及其之下的二级指标的指标名和权重值
        for (int i = 0; i < firstDataLength; i++) {
            //存储一级指标名和权重值
            HashMap<String, Object> firstDataMap = new HashMap<>();
            firstDataMap.put(firstDataNames[i], firstDataProportions[i]);
            //根据一级指标外键和指标名获取二级分析数据
            LambdaQueryWrapper<AhpSecondData> secondDataWrapper = new LambdaQueryWrapper<>();
            secondDataWrapper.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, afdId).eq(AhpSecondData::getAlias, firstDataNames[i]);
            AhpSecondData ahpSecondData = ahpSecondDataMapper.selectOne(secondDataWrapper);
            if (ahpSecondData != null) {
                //从redis获取数据(肯定存在)
                ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondData.getAsdId(), redisTemplate, ahpSecondDataMapper);
                //获取二级指标名和权重值
                String[] secondDataNames = ahpSecondData.getNames().split(",");
                String[] secondDataProportions = ahpSecondData.getProportion().split(",");
                //存储二级指标名和权重值
                HashMap<String, Object> secondDataMap = new HashMap<>();
                for (int j = 0; j < secondDataNames.length; j++) {
                    secondDataMap.put(secondDataNames[j], secondDataProportions[j]);
                }
                firstDataMap.put("secondData", secondDataMap);
            } else {
                //暂无二级指标权重数据,放入空数据返回
                //数据肯定存在
                InquiryTypeDto inquiryTypeDto = new InquiryTypeDto(RedisUtil.getInquiryTypeFromRedis(ahpFirstData.getInquiryTypeId(), redisTemplate, inquiryTypeMapper));
                String[] firstMetrics = inquiryTypeDto.getFirstMetrics().split(",");
                //获得该一级指标对应的索引值
                int index = Arrays.stream(firstMetrics)
                        .filter(firstDataNames[i]::equals)
                        .findFirst()
                        .map(str -> Arrays.asList(firstMetrics).indexOf(str))
                        .orElse(-1);
                HashMap<String, Object> secondDataMap = new HashMap<>();
                for (String secondAlias : inquiryTypeDto.getSecondMetrics()[index].split(",")) {
                    secondDataMap.put(secondAlias, null);
                }
                firstDataMap.put("secondData", secondDataMap);
            }
            //存储到list
            list.add(firstDataMap);
        }
        return Result.success(list);
    }

    /**
     * @Author keith
     * @Date 2024/2/1 12:46
     * @Description 根据一级指标外键查找权重和评语集
     */
    @Override
    public Result searchProportionComments(long afdId) {
        //1.一级指标数据处理
        //根据一级指标外键查询数据
        AhpFirstData ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
        if (ahpFirstData == null) {
            return Result.fail("数据不存在");
        }
        //存放一级指标名称和权重map
        HashMap<String, String> firstMap = new HashMap<>();
        //一级指标权重
        String[] firstProportions = ahpFirstData.getProportion().split(",");
        //一级指标名称
        String[] firstNames = ahpFirstData.getNames().split(",");
        //一级指标个数
        int firstScale = firstNames.length;
        //遍历存储
        for (int i = 0; i < firstScale; i++) {
            firstMap.put(firstNames[i], firstProportions[i]);
        }

        //2.二级指标权重数据处理
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < firstScale; i++) {
            LambdaQueryWrapper<AhpSecondData> qw = new LambdaQueryWrapper<>();
            qw.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, afdId).eq(AhpSecondData::getAlias, firstNames[i]);
            //获取二级指标数据
            AhpSecondData ahpSecondData = ahpSecondDataMapper.selectOne(qw);
            if (ahpSecondData == null) {
                //暂无二级指标权重数据,放入空数据返回
                //数据肯定存在
                InquiryTypeDto inquiryTypeDto = new InquiryTypeDto(RedisUtil.getInquiryTypeFromRedis(ahpFirstData.getInquiryTypeId(), redisTemplate, inquiryTypeMapper));
                String[] firstMetrics = inquiryTypeDto.getFirstMetrics().split(",");
                //获得该一级指标对应的索引值
                int index = Arrays.stream(firstMetrics)
                        .filter(firstNames[i]::equals)
                        .findFirst()
                        .map(str -> Arrays.asList(firstMetrics).indexOf(str))
                        .orElse(-1);
                //找寻对应下的二级指标
                for (String secondAlias : inquiryTypeDto.getSecondMetrics()[index].split(",")) {
                    HashMap<String, Object> secondMap = new HashMap<>();
                    secondMap.put("firstAlias", firstNames[i]);
                    secondMap.put("firstWeight", firstMap.get(firstNames[i]));
                    secondMap.put("secondAlias", secondAlias);
                    secondMap.put("secondWeight", null);
                    secondMap.put("asdId", null);
                    resultList.add(secondMap);
                }
            } else {
                //存在二级指标权重数据
                ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondData.getAsdId(), redisTemplate, ahpSecondDataMapper);
                //数据肯定存在
                //二级指标权重
                String[] secondProportions = ahpSecondData.getProportion().split(",");
                //二级指标名称
                String[] secondNames = ahpSecondData.getNames().split(",");
                //二级指标个数
                int secondScale = secondNames.length;
                //遍历存储
                for (int j = 0; j < secondScale; j++) {
                    HashMap<String, Object> secondMap = new HashMap<>();
                    secondMap.put("firstAlias", firstNames[i]);
                    secondMap.put("firstWeight", firstMap.get(firstNames[i]));
                    secondMap.put("secondAlias", secondNames[j]);
                    secondMap.put("secondWeight", secondProportions[j]);
                    secondMap.put("asdId", ahpSecondData.getAsdId());
                    resultList.add(secondMap);
                }
            }
        }

        //3.二级指标评语集数据处理
        for (int i = 0; i < resultList.size(); i++) {
            //获得先前的数据
            Map<String, Object> map = resultList.get(i);
            if (map.get("asdId") == null) {
                map.put("score", null);
                map.put("comment", null);
                continue;
            }
            //获取二级指标评语集数据
            AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(afdId, Long.parseLong(map.get("asdId").toString()), map.get("secondAlias").toString(), redisTemplate, ahpSecondCommentsMapper);
            if (comment == null) {
                map.put("score", null);
                map.put("comment", null);
                continue;
            }
            //评语集存在
            AhpSecondCommentDto ahpSecondCommentDto = new AhpSecondCommentDto(comment);
            map.put("score", ahpSecondCommentDto.getLevel());
            map.put("comment", ahpSecondCommentDto.getComment());
            //重新存储数据
            resultList.set(i, map);
        }

        //4.返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("afdId", afdId);
        resultMap.put("result", resultList);
        return Result.success(resultMap);
    }

    /**
     * @Author keith
     * @Date 2024/4/4 12:43
     * @Description 根据afdId集合删除数据
     */
    @Override
    public Result deleteAhpDataByAfdIds(long[] afdIds) {
        for (long afdId : afdIds) {
            //查询数据存不存在
            AhpFirstData ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
            if (ahpFirstData != null) {
                //一级数据存在
                try {
                    //1.删除redis数据
                    String[] firstNames = ahpFirstData.getNames().split(",");
                    //删除二级数据和评语集数据
                    for (String firstName : firstNames) {
                        //查询asdId
                        LambdaQueryWrapper<AhpSecondData> secondDataQw = new LambdaQueryWrapper<>();
                        secondDataQw.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, ahpFirstData.getAfdId()).eq(AhpSecondData::getAlias, firstName);
                        AhpSecondData ahpSecondData = ahpSecondDataMapper.selectOne(secondDataQw);
                        if (ahpSecondData == null) {
                            //二级数据为空
                            continue;
                        }
                        ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondData.getAsdId(), redisTemplate, ahpSecondDataMapper);
                        if (ahpSecondData != null) {
                            //二级数据不为空
                            for (String secondName : ahpSecondData.getNames().split(",")) {
                                AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(ahpFirstData.getAfdId(), ahpSecondData.getAsdId(), secondName, redisTemplate, ahpSecondCommentsMapper);
                                if (comment != null) {
                                    //评语集数据不为空
                                    //先删除minio上面的文件集
                                    AhpSecondCommentDto commentDto = new AhpSecondCommentDto(comment);
                                    for (String fileName : commentDto.getFiles()) {
                                        //循环删除文件
                                        boolean ifRemove = MinioUtil.removeFile(AhpConstants.MinioFilesBucket, fileName, minioClient);
                                        if (!ifRemove) {
                                            //手动触发事务回滚
                                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                            return Result.fail("Minio异常,删除文件时出错");
                                        }
                                    }
                                    //再删除redis上的评语集数据
                                    String key = ahpFirstData.getAfdId() + secondName + ahpSecondData.getAsdId();
                                    redisTemplate.delete(key);
                                }
                            }
                            //删除二级数据
                            redisTemplate.delete(RedisConstants.AhpSecondDataInfo + ahpSecondData.getAsdId());
                        }
                    }
                    //删除一级数据
                    redisTemplate.delete(RedisConstants.AhpFirstDataInfo + ahpFirstData.getAfdId());

                    //2.删除数据库的数据
                    int flag = ahpFirstDataMapper.deleteById(afdId);
                    if (flag == 0) {
                        //数据库删除数据失败
                        //手动触发事务回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        //数据库异常
                        return Result.fail("数据库异常,删除数据失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //手动触发事务回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.fail("服务器异常,删除数据失败");
                }
            } else {
                //一级数据不存在
                //手动触发事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.fail("数据afdId为" + afdId + "的数据不存在,删除数据失败");
            }
        }
        return Result.success("删除数据成功");
    }

    /**
     * @Author keith
     * @Date 2024/4/26 15:20
     * @Description 根据一级指标外键查询一级数据(矩阵和分析结果)
     */
    @Override
    public Result queryAhpFirstData(long afdId) {
        AhpFirstData ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
        if (ahpFirstData == null) {
            return Result.fail("为查询到数据");
        } else {
            return Result.success(new AhpFirstDataDto(ahpFirstDataMapper.selectById(afdId)));
        }
    }

    /**
     * @Author keith
     * @Date 2024/4/26 15:15
     * @Description 修改二维矩阵原始数据
     */
    @Override
    public Result updateOriginalData(Map<String, Object> map) {
        //1.获取参数
        long afdId = (Integer) map.get("afd_id");
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
        AhpFirstData newAhpFirstData = new AhpFirstData(AhpUtil.weightValueFirstAnalysis(ArrayUtil.doubleArrayMatrixUppercase(originalData)));

        //3.修改数据库数据
        //3.1准备新数据
        LambdaUpdateWrapper<AhpFirstData> uw = new LambdaUpdateWrapper<>();
        uw.eq(AhpFirstData::getAfdId, afdId);
        uw.set(AhpFirstData::getOriginalData, ArrayUtil.doubleMatrixArrayToByteArray(originalData));
        uw.set(AhpFirstData::getEigenvectors, newAhpFirstData.getEigenvectors());
        uw.set(AhpFirstData::getProportion, newAhpFirstData.getProportion());
        uw.set(AhpFirstData::getMaxEigenvalue, newAhpFirstData.getMaxEigenvalue());
        uw.set(AhpFirstData::getCi, newAhpFirstData.getCi());
        uw.set(AhpFirstData::getCr, newAhpFirstData.getCr());
        //3.2更新
        int flag = ahpFirstDataMapper.update(null, uw);

        //4.删除redis数据
        redisTemplate.delete(RedisConstants.AhpFirstDataInfo + afdId);

        //5.返回数据
        if (flag == 0) {
            return Result.fail("更新数据失败");
        } else {
            return Result.success(new AhpFirstDataDto(Objects.requireNonNull(RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper))));
        }
    }
}
