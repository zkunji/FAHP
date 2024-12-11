package com.example.ahp.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.entity.pojos.*;
import com.example.ahp.mapper.*;
import com.google.gson.Gson;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

public class RedisUtil {
    /**
     * @Author keith
     * @Date 2024/3/30 14:56
     * @Description 尝试根据uid从redis和数据库获取用户信息
     */
    public static User getUserInfoFromRedis(long uid, StringRedisTemplate redisTemplate, UserMapper userMapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.UserInfo + uid);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getUid, uid);
            User user = userMapper.selectOne(qw);
            if (user == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.UserInfo + uid, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.UserInfo + uid, new Gson().toJson(user));
            }
            return user;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, User.class);
        }
    }

    /**
     * @Author keith
     * @Date 2024/3/30 23:59
     * @Description 根据afd_id集合获取ahpFirstData数据(保证数据库存在数据)
     */
    public static List<AhpFirstData> getAhpFirstDatasFromRedis(List<AhpFirstData> list, StringRedisTemplate redisTemplate, AhpFirstDataMapper ahpFirstDataMapper) {
        ArrayList<AhpFirstData> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            //遍历
            //尝试从redis获取数据
            long afdId = list.get(i).getAfdId();
            //对象字符串
            String jsonStr = redisTemplate.opsForValue().get(RedisConstants.AhpFirstDataInfo + afdId);
            //对象
            AhpFirstData ahpFirstData;
            if (jsonStr == null) {
                //redis无数据,尝试从数据库获取信息
                LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
                qw.eq(AhpFirstData::getAfdId, afdId);
                ahpFirstData = ahpFirstDataMapper.selectOne(qw);
                //数据库肯定有数据(afd_id是从数据库获得的),将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpFirstDataInfo + afdId, new Gson().toJson(ahpFirstData));
            } else {
                //redis有数据
                ahpFirstData = new Gson().fromJson(jsonStr, AhpFirstData.class);
            }
            resultList.add(ahpFirstData);
        }
        return resultList;
    }

    /**
     * @Author keith
     * @Date 2024/3/30 23:59
     * @Description 根据afdId获取ahpFirstData数据
     */
    public static AhpFirstData getAhpFirstDataFromRedis(long afdId, StringRedisTemplate redisTemplate, AhpFirstDataMapper ahpFirstDataMapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.AhpFirstDataInfo + afdId);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
            qw.eq(AhpFirstData::getAfdId, afdId);
            AhpFirstData ahpFirstData = ahpFirstDataMapper.selectOne(qw);
            if (ahpFirstData == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpFirstDataInfo + afdId, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpFirstDataInfo + afdId, new Gson().toJson(ahpFirstData));
            }
            return ahpFirstData;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, AhpFirstData.class);
        }
    }

    /**
     * @Author keith
     * @Date 2024/3/31 11:14
     * @Description 根据asdId获取ahpSecondData数据
     */
    public static AhpSecondData getAhpSecondDataFromRedis(long asdId, StringRedisTemplate redisTemplate, AhpSecondDataMapper ahpSecondDataMapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.AhpSecondDataInfo + asdId);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<AhpSecondData> qw = new LambdaQueryWrapper<>();
            qw.eq(AhpSecondData::getAsdId, asdId);
            AhpSecondData ahpSecondData = ahpSecondDataMapper.selectOne(qw);
            if (ahpSecondData == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpSecondDataInfo + asdId, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpSecondDataInfo + asdId, new Gson().toJson(ahpSecondData));
            }
            return ahpSecondData;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, AhpSecondData.class);
        }
    }

    /**
     * @Author keith
     * @Date 2024/3/31 11:14
     * @Description 根据afdId、asdId、secondAlias获取ahpSecondComment数据
     */
    public static AhpSecondComment getAhpSecondCommentFromRedis(long afdId, long asdId, String secondAlias, StringRedisTemplate redisTemplate, AhpSecondCommentsMapper ahpSecondCommentsMapper) {
        //先尝试从redis获取信息
        String key = afdId + "_"+ asdId + secondAlias ;
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.AhpSecondCommentInfo + key);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<AhpSecondComment> qw = new LambdaQueryWrapper<>();
            qw.eq(AhpSecondComment::getAfdId, afdId).eq(AhpSecondComment::getAsdId, asdId).eq(AhpSecondComment::getAlias, secondAlias);
            //获取二级指标评语集数据
            AhpSecondComment comment = ahpSecondCommentsMapper.selectOne(qw);
            if (comment == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpSecondCommentInfo + key, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.AhpSecondCommentInfo + key, new Gson().toJson(comment));
            }
            return comment;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, AhpSecondComment.class);
        }
    }

    /**
     * @Author keith
     * @Date 2024/3/31 23:04 根据inquiryTypeId获取数据
     * @Description
     */
    public static InquiryType getInquiryTypeFromRedis(int inquiryTypeId, StringRedisTemplate redisTemplate, InquiryTypeMapper inquiryTypeMapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.InquiryTypeInfo + inquiryTypeId);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<InquiryType> qw = new LambdaQueryWrapper<>();
            qw.eq(InquiryType::getInquiryTypeId, inquiryTypeId);
            //获取二级指标评语集数据
            InquiryType inquiryType = inquiryTypeMapper.selectOne(qw);
            if (inquiryType == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.InquiryTypeInfo + inquiryTypeId, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.InquiryTypeInfo + inquiryTypeId, new Gson().toJson(inquiryType));
            }
            return inquiryType;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, InquiryType.class);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/28 22:07
     *@Description 根据fuzzyAlgorithmFirstDataId获取模糊算法一级数据
     */
    public static FuzzyAlgorithmFirstData getFuzzyAlgorithmFirstDataFromRedis(long fafId, StringRedisTemplate redisTemplate, FuzzyAlgorithmFirstDataMapper mapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.FuzzyAlgorithmFirstDataInfo + fafId);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<FuzzyAlgorithmFirstData> qw = new LambdaQueryWrapper<>();
            qw.eq(FuzzyAlgorithmFirstData::getFafId, fafId);
            //获取模糊算法一级数据
            FuzzyAlgorithmFirstData data = mapper.selectOne(qw);
            if (data == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmFirstDataInfo + fafId, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmFirstDataInfo + fafId, new Gson().toJson(data));
            }
            return data;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, FuzzyAlgorithmFirstData.class);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/28 23:45
     *@Description 根据fasId获取模糊算法二级数据
     */
    public static FuzzyAlgorithmSecondData getFuzzyAlgorithmSecondDataFromRedis(long fasId, StringRedisTemplate redisTemplate, FuzzyAlgorithmSecondDataMapper mapper) {
        //先尝试从redis获取信息
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.FuzzyAlgorithmSecondDataInfo + fasId);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<FuzzyAlgorithmSecondData> qw = new LambdaQueryWrapper<>();
            qw.eq(FuzzyAlgorithmSecondData::getFasId, fasId);
            //获取模糊算法一级数据
            FuzzyAlgorithmSecondData data = mapper.selectOne(qw);
            if (data == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmSecondDataInfo + fasId, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmSecondDataInfo + fasId, new Gson().toJson(data));
            }
            return data;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, FuzzyAlgorithmSecondData.class);
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/29 0:15
     *@Description 根据fafId、fasId、secondMetrics获取二级模糊算法评价数据
     */
    public static FuzzyAlgorithmSecondComment getFuzzyAlgorithmSecondCommentFromRedis(long fafId, long fasId, String secondMetrics, StringRedisTemplate redisTemplate, FuzzyAlgorithmSecondCommentMapper mapper) {
        //先尝试从redis获取信息
        String key = fafId + "_"+ fasId + secondMetrics ;
        String jsonStr = redisTemplate.opsForValue().get(RedisConstants.FuzzyAlgorithmSecondCommentInfo + key);
        if (jsonStr == null) {
            //redis无数据,尝试从数据库获取信息
            LambdaQueryWrapper<FuzzyAlgorithmSecondComment> qw = new LambdaQueryWrapper<>();
            qw.eq(FuzzyAlgorithmSecondComment::getFafId, fafId).eq(FuzzyAlgorithmSecondComment::getFasId, fasId).eq(FuzzyAlgorithmSecondComment::getAlias, secondMetrics);
            //获取二级指标评语集数据
            FuzzyAlgorithmSecondComment comment = mapper.selectOne(qw);
            if (comment == null) {
                //数据库无数据,放一个"null"数据到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmSecondCommentInfo + key, "null");
            } else {
                //数据库有数据,将数据保存到redis
                redisTemplate.opsForValue().set(RedisConstants.FuzzyAlgorithmSecondCommentInfo + key, new Gson().toJson(comment));
            }
            return comment;
        } else if ("null".equals(jsonStr)) {
            //redis查询为空
            return null;
        } else {
            //redis查询到数据
            return new Gson().fromJson(jsonStr, FuzzyAlgorithmSecondComment.class);
        }
    }
}
