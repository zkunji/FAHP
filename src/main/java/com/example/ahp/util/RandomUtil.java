package com.example.ahp.util;

import java.util.Random;

public class RandomUtil {
    public static Random random = new Random();

    /**
     *@Author keith
     *@Date 2023/12/21 10:27
     *@Description 生成盐x位小写字母随机盐
     */
    public static String saltGeneration(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a'); // 生成随机的小写字母
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
