package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.result.FuzzyAlgorithmResult;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.*;
import com.example.ahp.entity.pojos.*;
import com.example.ahp.mapper.*;
import com.example.ahp.util.*;
import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.common.result.Result;
import com.example.ahp.service.AdminService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    AdminMapper adminMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    AhpFirstDataMapper ahpFirstDataMapper;
    @Autowired
    AhpSecondDataMapper ahpSecondDataMapper;
    @Autowired
    FuzzyAlgorithmFirstDataMapper fuzzyAlgorithmFirstDataMapper;
    @Autowired
    FuzzyAlgorithmSecondDataMapper fuzzyAlgorithmSecondDataMapper;
    @Autowired
    FuzzyAlgorithmSecondCommentMapper fuzzyAlgorithmSecondCommentMapper;
    @Autowired
    AhpSecondCommentsMapper ahpSecondCommentsMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * @Author keith
     * @Date 2024/1/1 20:56
     * @Description 管理员注册
     */
    @Override
    public Result register(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String re_password = params.get("re_password");
        //判断输入是否为空
        if (account == null || "".equals(account) || password == null || "".equals(password) || re_password == null || "".equals(re_password)) {
            return Result.fail("输入错误");
        }
        //判断两次密码输入是否相同
        if (!password.equals(re_password)) {
            return Result.fail("两次输入的密码不相同");
        }
        //判断管理员名是否被注册
        if (adminMapper.queryByAccount(account) != null) {
            return Result.fail("账号已被注册");
        }
        //生成管理员注册实体信息
        String salt = RandomUtil.saltGeneration(6);
        Admin admin = new Admin(account, (new Sha256Hash(password, salt).toHex()), salt);
        return adminMapper.insert(admin) == 1 ? Result.success("注册成功") : Result.fail("注册失败");
    }

    /**
     * @Author keith
     * @Date 2024/1/1 21:05
     * @Description 管理员登录
     */
    @Override
    public Result login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        //判断输入是否为空
        if (account == null || "".equals(account) || password == null || "".equals(password)) {
            return Result.fail("输入错误");
        }
        //管理员信息
        Admin admin = adminMapper.queryByAccount(account);
        //账号不存在、密码错误
        if (admin == null || !admin.getPassword().equals(new Sha256Hash(password, admin.getSalt()).toHex())) {
            return Result.fail("账号或密码不正确");
        }
        //生成token，并保存到redis
        String adminIdToken = RedisConstants.AdminIdToken + admin.getAdminId();
        String token = TokenGenerator.generateValue();
        redisTemplate.opsForValue().set(adminIdToken, token);
        redisTemplate.expire(adminIdToken, AhpConstants.UidTokenExpire, TimeUnit.HOURS);
        //将token与uid返回
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("adminId", admin.getAdminId().toString());
        return Result.success(map);
    }

    /**
     * @Author keith
     * @Date 2024/1/24 15:11
     * @Description 根据一级指标外键集合以及占比权重进行综合性分析
     */
    @Override
    public Result analyse(Map<String, Object> params, long adminId) {
        //1.获取参数
        //ahp_first_data主键集合
        List<Integer> exchangeIntegerList = (List<Integer>) params.get("ids");
        long[] uidArray = exchangeIntegerList.stream()
                .map(Integer::longValue)
                .mapToLong(Long::longValue)
                .toArray();
        for (long uid : uidArray) {
            //判断该专家是否提交ahp分析数据
            LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
            qw.select(AhpFirstData::getAfdId).eq(AhpFirstData::getUid, uid).eq(AhpFirstData::getSubmit, 1);
            AhpFirstData ahpFirstData = ahpFirstDataMapper.selectOne(qw);
            if (ahpFirstData == null) {
                //查找该专家信息(数据存在)
                User user = RedisUtil.getUserInfoFromRedis(uid, redisTemplate, userMapper);
                return Result.fail("专家:"+ user.getUsername() + "未提交ahp分析数据");
            }
        }
        long[] afdIdArray = new long[uidArray.length];
         for(int i=0;i<uidArray.length;i++){
             //根据主键查找对应的分析数据
             LambdaQueryWrapper<AhpFirstData> qw = new LambdaQueryWrapper<>();
             qw.select(AhpFirstData::getAfdId).eq(AhpFirstData::getUid,uidArray[i]).eq(AhpFirstData::getSubmit,1);
             AhpFirstData ahpFirstData = ahpFirstDataMapper.selectOne(qw);
             ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(ahpFirstData.getAfdId(),redisTemplate,ahpFirstDataMapper);
             //数据肯定存在
             afdIdArray[i] = ahpFirstData.getAfdId();
         }
        //ahp_first_data每个主键的数据占比多少
        List<Double> exchangeDoubleList = (List<Double>) params.get("percentages");
        double[] percentagesArray = exchangeDoubleList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        //adminFirstData对象的alias属性
        String adminFirstDataAlias = (String) params.get("alias");
        if (adminFirstDataAlias == null) {
            adminFirstDataAlias = "";
        }

        //2.根据占比计算综合之后的一级、二级指标的权重,根据评语集计算模糊矩阵,赋值
        //2.1处理管理员一级对象数据
        //2.1.1AhpFirstData->AdminFirstDataDto部分数据赋值
        FuzzyAlgorithmFirstDataDto fuzzyAlgorithmFirstDataDto = new FuzzyAlgorithmFirstDataDto(RedisUtil.getAhpFirstDataFromRedis(afdIdArray[0],redisTemplate,ahpFirstDataMapper));
        fuzzyAlgorithmFirstDataDto.setUidArray(uidArray);
        fuzzyAlgorithmFirstDataDto.setPercentages(percentagesArray);
        fuzzyAlgorithmFirstDataDto.setAlias(adminFirstDataAlias);
        fuzzyAlgorithmFirstDataDto.setAdminId(adminId);
        //2.1.2计算综合后一级权重
        int firstScale = fuzzyAlgorithmFirstDataDto.getScale();
        double[] proportionArray = new double[firstScale];
        for (int i = 0; i < afdIdArray.length; i++) {
            //ahp_first_data表主键
            long id = afdIdArray[i];
            //该位专家赋值权重占比多少
            double percentage = percentagesArray[i];
            //获得该位专家对一级指标权重赋分后的占比
            double[] tmpProportionArray = ArrayUtil.proportionStringToDoubleArray(RedisUtil.getAhpFirstDataFromRedis(id,redisTemplate,ahpFirstDataMapper).getProportion());
            //该位专家的一级指标权重赋值占比乘与赋予给该位专家的权重
            for (int j = 0; j < tmpProportionArray.length; j++) {
                //加上先前的专家的权重
                proportionArray[j] = proportionArray[j] + tmpProportionArray[j] * percentage;
            }
        }
        fuzzyAlgorithmFirstDataDto.setProportion(ArrayUtil.doubleArrayReserveThree(proportionArray));
        //将管理员一级对象数据插入数据库获取主键
        FuzzyAlgorithmFirstData fuzzyAlgorithmFirstData = new FuzzyAlgorithmFirstData(fuzzyAlgorithmFirstDataDto);
        int insert = fuzzyAlgorithmFirstDataMapper.insert(fuzzyAlgorithmFirstData);
        if(insert == 0){
            //手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("分析失败");
        }

        //2.2处理管理员二级对象数据
        String[] firstNames = RedisUtil.getAhpFirstDataFromRedis(afdIdArray[0],redisTemplate,ahpFirstDataMapper).getNames().split(",");
        //插入数据库的数据
        ArrayList<FuzzyAlgorithmSecondDataDto> fuzzyAlgorithmSecondDataDtoList = new ArrayList<>();
        //i->一级指标个数,j->专家个数,k->该一级指标下二级指标的个数
        for (int i = 0; i < firstScale; i++) {
            //暂时缓存adminSecondData数据
            FuzzyAlgorithmSecondDataDto fuzzyAlgorithmSecondDataDto = null;
            for (int j = 0; j < afdIdArray.length; j++) {
                //该位专家赋值权重占比多少
                double percentage = percentagesArray[j];
                //准备查询条件
                LambdaQueryWrapper<AhpSecondData> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, afdIdArray[j]).eq(AhpSecondData::getAlias, firstNames[i]);
                //根据一级指标外键、一级指标名称查找二级指标数据
                AhpSecondData ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondDataMapper.selectOne(wrapper).getAsdId(),redisTemplate,ahpSecondDataMapper);
                if (j == 0) {
                    //赋值
                    fuzzyAlgorithmSecondDataDto = new FuzzyAlgorithmSecondDataDto(ahpSecondData);
                    fuzzyAlgorithmSecondDataDto.setFafId(fuzzyAlgorithmFirstData.getFafId());
                    //设置大小为该一级指标下二级指标的个数
                    proportionArray = new double[fuzzyAlgorithmSecondDataDto.getScale()];
                }
                double[] tmpProportionArray = ArrayUtil.proportionStringToDoubleArray(ahpSecondData.getProportion());
                //该位专家的一级指标权重赋值占比乘与赋予给该位专家的权重
                for (int k = 0; k < tmpProportionArray.length; k++) {
                    //加上先前的专家的权重
                    proportionArray[k] = proportionArray[k] + tmpProportionArray[k] * percentage;
                }
            }
            fuzzyAlgorithmSecondDataDto.setProportion(ArrayUtil.doubleArrayReserveThree(proportionArray));
            fuzzyAlgorithmSecondDataDtoList.add(fuzzyAlgorithmSecondDataDto);
        }
        //将adminSecondData数据插入数据库
        ArrayList<FuzzyAlgorithmSecondData> fuzzyAlgorithmSecondDataList = new ArrayList<>();
        for (FuzzyAlgorithmSecondDataDto dto : fuzzyAlgorithmSecondDataDtoList) {
            FuzzyAlgorithmSecondData fuzzyAlgorithmSecondData = new FuzzyAlgorithmSecondData(dto);
            insert = fuzzyAlgorithmSecondDataMapper.insert(fuzzyAlgorithmSecondData);
            if(insert == 0){
                //手动触发事务回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.fail("分析失败");
            }
            fuzzyAlgorithmSecondDataList.add(fuzzyAlgorithmSecondData);
        }

        //2.3处理管理员二级评语集数据
        //i->一级指标个数;j->专家个数;k->该一级指标下二级指标的个数
        HashMap<Integer, double[][]> matrixMap = new HashMap<>();
        for (int i = 0; i < firstScale; i++) {
            //二级指标名称数组
            String[] secondNames = null;
            //二级指标对象
            AhpSecondData ahpSecondData = null;
            //评价人数,依次为(优秀、良好、警戒、危险);行数:二级指标个数,列数:4
            int[][] comments_number = null;
            for (int j = 0; j < afdIdArray.length; j++) {
                if (j == 0) {
                    //准备查询条件
                    LambdaQueryWrapper<AhpSecondData> wrapper = new LambdaQueryWrapper<>();
                    wrapper.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, afdIdArray[j]).eq(AhpSecondData::getAlias, firstNames[i]);
                    //根据一级指标外键、一级指标名称查找二级指标数据
                    ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondDataMapper.selectOne(wrapper).getAsdId(),redisTemplate,ahpSecondDataMapper);
                    //赋值
                    secondNames = ahpSecondData.getNames().split(",");
                    //设置大小
                    comments_number = new int[secondNames.length][4];
                }
                for (int k = 0; k < secondNames.length; k++) {
                    //准备查询条件
                    LambdaQueryWrapper<AhpSecondData> wrapper = new LambdaQueryWrapper<>();
                    wrapper.select(AhpSecondData::getAsdId).eq(AhpSecondData::getAfdId, afdIdArray[j]).eq(AhpSecondData::getAlias, firstNames[i]);
                    //根据一级指标外键、一级指标名称查找二级指标数据
                    ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondDataMapper.selectOne(wrapper).getAsdId(),redisTemplate,ahpSecondDataMapper);
                    //查询二级指标评语集
                    AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(afdIdArray[j], ahpSecondData.getAsdId(), secondNames[k], redisTemplate, ahpSecondCommentsMapper);
                    //该位专家对此二级指标的评分
                    String level = comment.getLevel();
                    //判断是哪种评价水平
                    if ("优秀".equals(level)) {
                        comments_number[k][0]++;
                    } else if ("良好".equals(level)) {
                        comments_number[k][1]++;
                    } else if ("警戒".equals(level)) {
                        comments_number[k][2]++;
                    } else if ("危险".equals(level)) {
                        comments_number[k][3]++;
                    }
                }
            }
            //将adminSecondComment数据插入数据库
            for (int k = 0; k < secondNames.length; k++) {
                FuzzyAlgorithmSecondCommentDto fuzzyAlgorithmSecondCommentDto = new FuzzyAlgorithmSecondCommentDto();
                fuzzyAlgorithmSecondCommentDto.setFafId(fuzzyAlgorithmFirstData.getFafId());
                fuzzyAlgorithmSecondCommentDto.setFasId(fuzzyAlgorithmSecondDataList.get(i).getFasId());
                fuzzyAlgorithmSecondCommentDto.setAlias(secondNames[k]);
                System.arraycopy(comments_number[k], 0, fuzzyAlgorithmSecondCommentDto.getCommentsNumber(), 0, 4);
                FuzzyAlgorithmSecondComment fuzzyAlgorithmSecondComment = new FuzzyAlgorithmSecondComment(fuzzyAlgorithmSecondCommentDto, afdIdArray.length);
                //插入
                insert = fuzzyAlgorithmSecondCommentMapper.insert(fuzzyAlgorithmSecondComment);
                if(insert == 0){
                    //手动触发事务回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.fail("分析失败");
                }
            }
            //准备模糊矩阵
            matrixMap.put(i, ArrayUtil.intArrayDivisionToFuzzyMatrix(comments_number, afdIdArray.length));
        }

        //3.计算管理员一级指标数据最终地评价结果(按照最大隶属原则的评价结果)、评语集矩阵、最终评价程度
        //3.1计算评语集矩阵
        double[][] comments_matrix = new double[firstScale][4];
        for (int i = 0; i < firstScale; i++) {
            //获取管理员二级指标对象的权重数组
            double[] secondProportion = fuzzyAlgorithmSecondDataDtoList.get(i).getProportion();
            //一个一级指标的评语集
            double[] result = ArrayUtil.doubleArrayMultiplyDoubleMatrix(secondProportion, matrixMap.get(i));
            //复制
            System.arraycopy(result, 0, comments_matrix[i], 0, result.length);
        }
        //赋值
        fuzzyAlgorithmFirstData.setCommentsMatrix(ArrayUtil.doubleArrayToByteArray(ArrayUtil.doubleArrayReserveThree(ArrayUtil.doubleMatrixTwoToOne(comments_matrix))));
        //3.2计算评价结果(按照最大隶属原则的评价结果)
        String[] comments = new String[firstScale];
        for (int i = 0; i < firstScale; i++) {
            int flag = 0;
            double max = 0.0;
            for (int j = 0; j < 4; j++) {
                if (Double.compare(max, comments_matrix[i][j]) < 0) {
                    max = comments_matrix[i][j];
                    flag = j;
                }
            }
            //判断最大隶属度是哪种评价水平
            if (flag == 0) {
                comments[i] = "优秀";
            } else if (flag == 1) {
                comments[i] = "良好";
            } else if (flag == 2) {
                comments[i] = "警戒";
            } else if (flag == 3) {
                comments[i] = "危险";
            }
        }
        //赋值
        StringBuilder tmp = new StringBuilder();
        for (String comment : comments) {
            tmp.append(comment).append(",");
        }
        fuzzyAlgorithmFirstData.setComments(tmp.substring(0, tmp.length() - 1));
        //3.3计算最终评价程度
        double[] result = null;
        //获取管理员一级指标对象的权重数组
        double[] firstProportion = fuzzyAlgorithmFirstDataDto.getProportion();
        //最终评价程度数组
        result = ArrayUtil.doubleArrayMultiplyDoubleMatrix(firstProportion, comments_matrix);
        //赋值
        fuzzyAlgorithmFirstData.setCommentsResult(ArrayUtil.doubleArrayToByteArray(result));
        //3.4更新管理员一级指标数据
        int update = fuzzyAlgorithmFirstDataMapper.updateById(fuzzyAlgorithmFirstData);
        if(update == 0){
            //手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("分析失败");
        }

        //4.返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        //4.1评语集矩阵
        ArrayList<HashMap<String, Object>> commentsMatrixList = new ArrayList<>();
        for(int i=0;i<firstScale;i++){
            HashMap<String, Object> map = new HashMap<>();
            //一级指标名称
            map.put("first_name",firstNames[i]);
            //对应的评语集数组
            double[] targetArray = new double[4];
            System.arraycopy(comments_matrix[i], 0, targetArray, 0, 4);
            map.put("comments",ArrayUtil.doubleArrayReserveThree(targetArray));
            //评价结果
            map.put("single_result",comments[i]);
            commentsMatrixList.add(map);
        }
        resultMap.put("first_param",commentsMatrixList);
        //4.2最终评分得分
        resultMap.put("second_param",ArrayUtil.doubleArrayReserveThree(result));
        //4.3adminFirstData表的主键
        resultMap.put("admin_first_data_id", fuzzyAlgorithmFirstData.getFafId());
        return Result.success(resultMap);
    }

    /**
     *@Author keith
     *@Date 2024/1/2 19:31
     *@Description 根据分页条件查询一级分析数据
     */
    @Override
    public ResultInfo searchByPagination(Map<String, Object> params) {
        //获取参数
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        //生成分页对象
        Page<AhpFirstData> page = new Page<>(pageNum, pageSize);
        //设置查询条件
        LambdaQueryWrapper<AhpFirstData> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(AhpFirstData::getAfdId).eq(AhpFirstData::getSubmit, 1);
        //查询
        List<AhpFirstData> list = ahpFirstDataMapper.selectPage(page, wrapper).getRecords();
        //1.未查询到(所有)用户已提交的ahp分析记录
        if (list.isEmpty()) {
            return ResultInfo.fail("没有专家提交分析记录,查询为空!");
        }
        //2.查询到(所有)用户的提交的ahp分析记录
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();
        for (AhpFirstData ahpFirstData : list) {
            //获得数据
            ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(ahpFirstData.getAfdId(),redisTemplate,ahpFirstDataMapper);
            //一级数据存储(数据肯定存在)
            AhpFirstDataDto dto = new AhpFirstDataDto(ahpFirstData);
            HashMap<String, Object> map = new HashMap<>();
            map.put("afd_id",dto.getAfdId());
            map.put("alias",dto.getAlias());
            map.put("created_time",dto.getCreatedTime());
            //相关用户数据存储(数据肯定存在)
            User user = RedisUtil.getUserInfoFromRedis(ahpFirstData.getUid(), redisTemplate, userMapper);
            map.put("username",user.getUsername());
            map.put("uid",user.getUid());
            resultList.add(map);
        }
        return ResultInfo.success(list.size(),resultList);
    }

    /**
     *@Author keith
     *@Date 2024/1/1 21:48
     *@Description 根据分页条件获取用户信息
     */
    @Override
    public ResultInfo getUserInfo(Map<String, Object> params) {
        //获取参数
        Integer pageNum = (Integer)params.get("pageNum");
        Integer pageSize = (Integer)params.get("pageSize");
        //生成分页对象
        Page<User> page = new Page<>(pageNum,pageSize);
        //查询
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.select(User::getUid);
        List<User> list = userMapper.selectPage(page,qw).getRecords();
        //1.未查询到用户信息
        if(list.isEmpty()){
            return ResultInfo.fail("查询为空!");
        }
        //2.查询到该用户信息
        long total = page.getTotal();
        System.out.println(total);
        //查询记录数
        ArrayList<Map<String, Object>> resultMap = new ArrayList<>();
        for(User user: list){
            user = RedisUtil.getUserInfoFromRedis(user.getUid(), redisTemplate, userMapper);
            UserDto userDto = null;
            if (user != null) {
                userDto = new UserDto(user);
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("uid",userDto.getUid());
            map.put("account",userDto.getAccount());
            map.put("username",userDto.getUsername());
            map.put("sex",userDto.getSex());
            map.put("phone",userDto.getPhone());
            map.put("email",userDto.getEmail());
            //判断此用户是否提交了一级分析数据
            LambdaQueryWrapper<AhpFirstData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AhpFirstData::getUid,user.getUid()).eq(AhpFirstData::getSubmit,1);
            Integer count = ahpFirstDataMapper.selectCount(wrapper);
            if(count >= 1){
                //已提交
                map.put("if_submit",1);
            }else{
                //未提交
                map.put("if_submit",0);
            }
            //判断此用户是否完成评审任务考核指标体系打分
            if(userDto.getAssessment() != null){
                map.put("if_scoring",1);
            }else{
                map.put("if_scoring",0);
            }
            resultMap.add(map);
        }
        //3.获取用户总数
        return ResultInfo.success(userMapper.selectCount(null),resultMap);
    }

    /**
     *@Author keith
     *@Date 2024/3/30 16:04
     *@Description 根据uid获取某个用户提交的分析数据
     */
    @Override
    public Result getAhpFirstDataMarked(long uid) {
        //查询数据
        LambdaQueryWrapper<AhpFirstData> tmp_qw = new LambdaQueryWrapper<>();
        tmp_qw.select(AhpFirstData::getAfdId).eq(AhpFirstData::getUid,uid).eq(AhpFirstData::getSubmit,1);
        AhpFirstData ahpFirstData = ahpFirstDataMapper.selectOne(tmp_qw);
        if(ahpFirstData == null){
            return Result.fail("该用户不存在提交的ahp分析数据");
        }
        long afdId = ahpFirstData.getAfdId();
        //数据肯定存在
        ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId,redisTemplate,ahpFirstDataMapper);
        //1.一级指标数据处理
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
            //获取二级指标数据(数据肯定存在)
            AhpSecondData ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(ahpSecondDataMapper.selectOne(qw).getAsdId(),redisTemplate,ahpSecondDataMapper);
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

        //3.二级指标评语集数据处理
        for (int i = 0; i < resultList.size(); i++) {
            //获得先前的数据(数据肯定存在)
            Map<String, Object> map = resultList.get(i);
            //获取二级指标评语集数据
            AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(afdId, Long.parseLong(map.get("asdId").toString()), map.get("secondAlias").toString(),redisTemplate,ahpSecondCommentsMapper);
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
     *@Author keith
     *@Date 2024/4/28 18:29
     *@Description 专家评审任务考核指标体系打分(熵权法)
     */
    @Override
    public Result insertEntropyWeight(EntropyWeightDto dto) {
        //获取每位专家评审任务考核指标体系打分的数据
        double[][] doubles = new double[dto.getUidArray().length][17];
        long[] uids = dto.getUidArray();
        for (int i=0;i<uids.length;i++) {
            User user = RedisUtil.getUserInfoFromRedis(uids[i], redisTemplate, userMapper);
            if(user == null || user.getAssessment() == null){
                return Result.fail("用户:{"+user.getUid()+user.getUsername()+"}未完成评审任务考核指标体系打分");
            }
            // 将oneDimArray整体复制到twoDimArray的第一行
            System.arraycopy(ArrayUtil.byteArrayToDoubleArray(user.getAssessment()), 0, doubles[i], 0, ArrayUtil.byteArrayToDoubleArray(user.getAssessment()).length);
        }
        //使用熵权法
        double[] expertWeight = EntropyWeightUtil.calculateEntropyWeight(doubles);
        for(int i=0;i<expertWeight.length;i++){
            expertWeight[i] = Double.parseDouble(ArrayUtil.reserveThree(expertWeight[i]));
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("expert_weight",expertWeight);
        return Result.success(resultMap);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 21:12
     *@Description 获取模糊算法数据的外键(FuzzyAlgorithmFirstDataId)集合、用户信息和用户权重
     */
    @Override
    public Result queryFuzzyAlgorithmFirstDataDetails(int pageNum, int pageSize) {          //1.先从数据库获取faf外键集合
        //生成分页对象
        Page<FuzzyAlgorithmFirstData> page = new Page<>(pageNum,pageSize);
        //查询
        LambdaQueryWrapper<FuzzyAlgorithmFirstData> qw = new LambdaQueryWrapper<>();
        qw.select(FuzzyAlgorithmFirstData::getFafId);
        List<FuzzyAlgorithmFirstData> list = fuzzyAlgorithmFirstDataMapper.selectPage(page,qw).getRecords();

        //2.获取相关用户信息
        //从redis获取模糊算法一级完整数据
        ArrayList<Map<String, Object>> resultMap = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            //一个map
            HashMap<String, Object> map = new HashMap<>();
            //从redis获取数据
            long fafId = list.get(i).getFafId();
            FuzzyAlgorithmFirstData firstData = RedisUtil.getFuzzyAlgorithmFirstDataFromRedis(fafId, redisTemplate, fuzzyAlgorithmFirstDataMapper);
            if(firstData == null){
                return Result.fail("数据不存在");
            }
            FuzzyAlgorithmFirstDataDto firstDataDto = new FuzzyAlgorithmFirstDataDto(firstData);
            //专家数
            int length = firstDataDto.getUidArray().length;
            //根据uid获取username
            String[] usernames = new String[length];
            for (int j=0;j<length;j++) {
                long uid = firstDataDto.getUidArray()[j];
                User user = RedisUtil.getUserInfoFromRedis(uid, redisTemplate, userMapper);
                if(user == null){
                    return Result.fail("用户:"+uid+"数据不存在");
                }
                usernames[j] = user.getUsername();
            }
            //一个map的数据
            map.put("faf_id",fafId);
            map.put("uids",firstDataDto.getUidArray());
            map.put("usernames",usernames);
            map.put("expert_weights",firstDataDto.getPercentages());
            resultMap.add(map);
        }
        return Result.success(resultMap);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 21:11
     *@Description 根据外键获取模糊算法主要数据内容
     */
    @Override
    public Result queryFuzzyAlgorithmFirstSubject(long fafId) {
        //1.综合过后的一级、二级指标名称和权重
        FuzzyAlgorithmResult fuzzyAlgorithmResult = new FuzzyAlgorithmResult();
        //获取一级数据
        FuzzyAlgorithmFirstData firstData = RedisUtil.getFuzzyAlgorithmFirstDataFromRedis(fafId, redisTemplate, fuzzyAlgorithmFirstDataMapper);
        if(firstData==null){
            return Result.fail("fafId:"+fafId+"的数据不存在");
        }
        FuzzyAlgorithmFirstDataDto firstDataDto = new FuzzyAlgorithmFirstDataDto(firstData);
        //存放一级权重
        fuzzyAlgorithmResult.getMetricsWeight().put("first_weight",firstDataDto.getProportion());
        String[] firstMetrics = firstDataDto.getNames().split(",");
        int firstLength = firstMetrics.length;
        HashMap<String, FuzzyAlgorithmSecondDataDto> metricsMapping = new HashMap<>();
        //查找二级权重
        for(int i=0;i<firstLength;i++){
            //先根据fafId和一级指标名称获取二级指标数据外键
            LambdaQueryWrapper<FuzzyAlgorithmSecondData> qw = new LambdaQueryWrapper<>();
            qw.select(FuzzyAlgorithmSecondData::getFasId).eq(FuzzyAlgorithmSecondData::getFafId,fafId).eq(FuzzyAlgorithmSecondData::getAlias,firstMetrics[i]);
            FuzzyAlgorithmSecondData secondData = fuzzyAlgorithmSecondDataMapper.selectOne(qw);
            if(secondData == null){
                return Result.fail("一级指标:"+firstMetrics[i]+"数据不存在");
            }
            secondData = RedisUtil.getFuzzyAlgorithmSecondDataFromRedis(secondData.getFasId(), redisTemplate, fuzzyAlgorithmSecondDataMapper);
            FuzzyAlgorithmSecondDataDto secondDataDto = new FuzzyAlgorithmSecondDataDto(secondData);
            int tmp = i + 1;
            //存放二级权重
            fuzzyAlgorithmResult.getMetricsWeight().put("second"+tmp+"_weight",secondDataDto.getProportion());
            //存放一级名称与二级模糊数据的映射
            metricsMapping.put(firstMetrics[i],secondDataDto);
        }

        //2.评价结果人数和模糊矩阵
        //i->一级指标,j->二级指标
        for(int i=0;i<firstLength;i++){
            //二级模糊算法数据
            FuzzyAlgorithmSecondDataDto secondDataDto = metricsMapping.get(firstMetrics[i]);
            //二级指标名称
            String[] secondMetrics = secondDataDto.getNames().split(",");
            //二级指标长度
            int secondLength = secondMetrics.length;
            //评价人数数组
            int[][] commentsNumber = new int[secondLength][4];
            //模糊矩阵
            double[][] fuzzyMatrix = new double[secondLength][4];
            for(int j=0;j<secondLength;j++){
                //获取二级模糊算法评价数据
                FuzzyAlgorithmSecondComment secondComment = RedisUtil.getFuzzyAlgorithmSecondCommentFromRedis(fafId, secondDataDto.getFasId(), secondMetrics[j], redisTemplate, fuzzyAlgorithmSecondCommentMapper);
                if(secondComment == null){
                    return Result.fail("数据不存在");
                }
                FuzzyAlgorithmSecondCommentDto secondCommentDto = new FuzzyAlgorithmSecondCommentDto(secondComment);
                //评价结果人数
                System.arraycopy(secondCommentDto.getCommentsNumber(), 0, commentsNumber[j], 0, secondCommentDto.getCommentsNumber().length);
                //模糊矩阵(做保留三位小数的处理)
                double[] tmpFuzzyMatrix = secondCommentDto.getFuzzyMatrix();
                for(int k=0;k<tmpFuzzyMatrix.length;k++){
                    tmpFuzzyMatrix[k] = Double.parseDouble(ArrayUtil.reserveThree(tmpFuzzyMatrix[k]));
                }
                System.arraycopy(tmpFuzzyMatrix, 0, fuzzyMatrix[j], 0, tmpFuzzyMatrix.length);
            }
            //存放数据模糊矩阵
            fuzzyAlgorithmResult.getFuzzyMatrix().put(firstMetrics[i],fuzzyMatrix);
            //存放评价人数数据
            fuzzyAlgorithmResult.setCommentsNumber(ArrayUtil.mergeTwoIntMetrix(fuzzyAlgorithmResult.getCommentsNumber(),commentsNumber));
        }

        //3.处理评语集矩阵数据
        //评价结果
        String[] singleResult = firstDataDto.getComments().split(",");
        //评语集矩阵
        double[][] commentsMatrix = firstDataDto.getCommentsMatrix();
        for(int i=0;i<firstLength;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("single_result",singleResult[i]);
            double[] tmpDoubleArray = new double[4];
            System.arraycopy(commentsMatrix[i], 0, tmpDoubleArray, 0, commentsMatrix[i].length);
            map.put("comments",tmpDoubleArray);
            //存放数据
            fuzzyAlgorithmResult.getCommentsMatrix().put(firstMetrics[i],map);
        }

        //4.最终评价结果
        fuzzyAlgorithmResult.setCommentsResult(firstDataDto.getCommentsResult());
        return Result.success(fuzzyAlgorithmResult);
    }

    @Override
    public Result updateAdminPwd(String new_pwd,long adminId) {
        //先查询盐
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.eq(Admin::getAdminId,adminId);
        Admin admin = adminMapper.selectOne(qw);
        if(admin == null){
            return Result.fail("管理员信息不存在");
        }
        //修改密码
        LambdaUpdateWrapper<Admin> uw = new LambdaUpdateWrapper<>();
        uw.eq(Admin::getAdminId,adminId).set(Admin::getPassword,new Sha256Hash(new_pwd, admin.getSalt()).toHex());
        int flag = adminMapper.update(null, uw);
        if(flag == 1){
            return Result.success("修改成功");
        }else{
            return Result.fail("修改失败");
        }
    }
}
