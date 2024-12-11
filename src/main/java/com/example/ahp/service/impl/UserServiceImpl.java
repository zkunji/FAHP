package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.UserDto;
import com.example.ahp.entity.pojos.User;
import com.example.ahp.mapper.UserMapper;
import com.example.ahp.service.UserService;
import com.example.ahp.util.ArrayUtil;
import com.example.ahp.util.RandomUtil;
import com.example.ahp.util.RedisUtil;
import com.example.ahp.util.TokenGenerator;
import com.google.gson.Gson;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * @Author keith
     * @Date 2023/12/24 16:51
     * @Description 根据账号查询
     */
    @Override
    public User selectByAccount(String account) {
        return userMapper.queryByAccount(account);
    }

    /**
     * @Author keith
     * @Date 2023/12/30 11:17
     * @Description 更新密码
     */
    @Override
    public Result updatePwd(Map<String, String> params, long uid) {
        //旧密码
        String old_pwd = params.get("old_pwd");
        //新密码
        String new_pwd = params.get("new_pwd");
        //再次输入新密码
        String re_pwd = params.get("re_pwd");
        //判断输入是否为空
        if (old_pwd == null || old_pwd.isEmpty() || new_pwd == null || new_pwd.isEmpty() || re_pwd == null || "".equals(re_pwd)){
            return Result.fail("输入错误");
        }
        //查询用户旧信息
        User old_user = RedisUtil.getUserInfoFromRedis(uid,redisTemplate,userMapper);
        if(old_user==null){
            return Result.fail("该用户未注册");
        }
        //比对旧密码
        if(!old_user.getPassword().equals(new Sha256Hash(old_pwd, old_user.getSalt()).toHex())){
            return Result.fail("旧密码不正确");
        }
        //比对新密码的两次输入
        if (!new_pwd.equals(re_pwd)){
            return Result.fail("新密码两次输入不一致");
        }
        old_user.setPassword(new Sha256Hash(new_pwd, old_user.getSalt()).toHex());
        //更新数据
        int flag = 0;
        try{
            flag = userMapper.updateById(old_user);
            if(flag==1){
                //更新成功,删除redis
                redisTemplate.delete(RedisConstants.UserInfo+uid);
                return Result.success("密码更新成功");
            }else{
                //更新失败
                return Result.fail("密码更新失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("服务器异常");
        }
    }

    /**
     *@Author keith
     *@Date 2023/12/30 12:22
     *@Description 获取用户相关信息
     */
    @Override
    public Result getInfo(long uid) {
        //从redis、数据库获取数据
        User user = RedisUtil.getUserInfoFromRedis(uid,redisTemplate,userMapper);
        if(user==null){
            return Result.fail("用户未注册");
        }else{
            return Result.success(new UserDto(user));
        }
    }

    /**
     *@Author keith
     *@Date 2024/1/23 23:18
     *@Description 更新用户信息
     */
    @Override
    public Result updateUserinfo(Map<String, String> params, long uid) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUid,uid);
        //用户昵称
        String username = params.get("username");
        if(username!=null){
            wrapper.set(User::getUsername,username);
        }
        //用户电话
        String phone = params.get("phone");
        if(phone!=null){
            wrapper.set(User::getPhone,phone);
        }
        //用户头像
        String avatar = params.get("avatar");
        if(avatar!=null){
            byte[] bytes = avatar.getBytes();
            wrapper.set(User::getAvatar,bytes);
        }
        //用户邮箱
        String email = params.get("email");
        if(email!=null){
            wrapper.set(User::getEmail,email);
        }
        //用户性别
        String sex = params.get("sex");
        if(sex!=null){
            wrapper.set(User::getSex,sex);
        }
        int flag = userMapper.update(null, wrapper);
        if(flag == 0){
            return Result.fail("更新失败");
        }
        //删除redis
        redisTemplate.delete(RedisConstants.UserInfo+uid);
        return Result.success("更新成功");
    }

    /**
     *@Author keith
     *@Date 2023/12/24 11:45
     *@Description 注册
     */
    @Override
    public Result register(Map<String, String> params) {
        //获取参数
        String account = params.get("account");
        String password = params.get("password");
        String re_password = params.get("re_password");
        //判断输入是否为空
        if(account==null || Objects.equals(account, "") || password==null || Objects.equals(password, "") || re_password==null|| Objects.equals(re_password, "")){
            return Result.fail("输入错误");
        }
        //判断两次密码输入是否相同
        if(!password.equals(re_password)){
            return Result.fail("两次输入的密码不相同");
        }
        //判断用户名是否被注册
        if((selectByAccount(account)) != null){
            return Result.fail("账号已被注册");
        }
        //生成用户注册实体信息
        String salt = RandomUtil.saltGeneration(6);
        User user = new User(account, (new Sha256Hash(password, salt).toHex()), salt);
        //将数据保存到数据库
        int insert = userMapper.insert(user);
        if(insert==1){
            //将数据保存到redis
            user.setCreatedTime(new Date());
            String json = new Gson().toJson(user);
            redisTemplate.opsForValue().set(RedisConstants.UserInfo+user.getUid(),json);
            return Result.success("注册成功");
        }else{
            return Result.fail("注册失败");
        }
    }

    /**
     *@Author keith
     *@Date 2023/12/24 12:01
     *@Description 登录
     */
    @Override
    public Result login(Map<String, String> params) {
        //获取参数
        String password = params.get("password");
        String account = params.get("account");
        //判断输入是否为空
        if(account==null || Objects.equals(account, "") || password==null || Objects.equals(password, "")){
            return Result.fail("输入错误");
        }
        //用户信息
        User user = selectByAccount(account);
        //账号不存在、密码错误
        if(user == null || !user.getPassword().equals(new Sha256Hash(password, user.getSalt()).toHex())) {
            return Result.fail("账号或密码不正确");
        }
        //生成token，并保存到redis
        String uidToken = RedisConstants.UidToken + user.getUid();
        String token = TokenGenerator.generateValue();
        redisTemplate.opsForValue().set(uidToken, token);
        redisTemplate.expire(uidToken, AhpConstants.UidTokenExpire, TimeUnit.HOURS);
        //将token与uid返回
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("uid",user.getUid().toString());
        return Result.success(map);
    }

    /**
     *@Author keith
     *@Date 2024/4/28 17:39
     *@Description 专家评审任务考核指标体系打分(新增)
     */
    @Override
    public Result insertAssessment(UserDto userDto) {
        //1.判断是否已有数据
        User user = RedisUtil.getUserInfoFromRedis(userDto.getUid(), redisTemplate, userMapper);
        if(user == null){
            return Result.fail("不存在用户数据");
        }
        if(user.getAssessment() != null){
            return Result.fail("该专家已完成评审任务考核指标体系打分");
        }

        //2.更新数据库
        LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
        uw.eq(User::getUid,userDto.getUid()).set(User::getAssessment, ArrayUtil.doubleArrayToByteArray(userDto.getAssessment()));
        int flag = userMapper.update(null, uw);
        if(flag == 0){
            return Result.fail("新增数据失败");
        }else{
            //3.删除redis缓存
            redisTemplate.delete(RedisConstants.UserInfo+userDto.getUid());
            return Result.success("新增数据成功");
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/28 17:41
     *@Description 专家评审任务考核指标体系打分(修改)
     */
    @Override
    public Result updateAssessment(UserDto userDto) {
        //1.删除redis缓存
        redisTemplate.delete(RedisConstants.UserInfo+userDto.getUid());

        //2.更新数据库
        LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
        uw.eq(User::getUid,userDto.getUid()).set(User::getAssessment, ArrayUtil.doubleArrayToByteArray(userDto.getAssessment()));
        int flag = userMapper.update(null, uw);
        if(flag == 0){
            return Result.fail("修改数据失败");
        }else{
            return Result.success("修改数据成功");
        }
    }

    /**
     *@Author keith
     *@Date 2024/4/28 18:12
     *@Description 专家评审任务考核指标体系打分(查询)
     */
    @Override
    public Result queryAssessment(long uid) {
        //查询
        User user = RedisUtil.getUserInfoFromRedis(uid, redisTemplate, userMapper);
        if(user == null){
            return Result.fail("查询失败");
        }
        //返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("uid",uid);
        resultMap.put("username",user.getUsername());
        resultMap.put("assessment",ArrayUtil.byteArrayToDoubleArray(user.getAssessment()));
        return Result.success(resultMap);
    }
}
