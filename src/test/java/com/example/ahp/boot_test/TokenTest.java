package com.example.ahp.boot_test;

import com.example.ahp.common.constant.RedisConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TokenTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void insertUidToken(){
        System.out.println(redisTemplate);
        redisTemplate.opsForValue().set(RedisConstants.UidToken+0,"123");
    }

    @Test
    public void getUidToken(){
        String uidToken = redisTemplate.opsForValue().get(RedisConstants.UidToken+7);
        System.out.println(uidToken);
    }
}
