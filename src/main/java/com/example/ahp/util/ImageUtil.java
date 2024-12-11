package com.example.ahp.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

public class ImageUtil {
    /**
     *@Author keith
     *@Date 2024/1/19 14:31
     *@Description 根据图片存储地址获取Base64编码后的字符串
     */
    public static String convertImageToBase64(String imagePath) {
        File file = new File(imagePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);
            return Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *@Author keith
     *@Date 2024/1/19 14:36
     *@Description 将Base64编码的字符串转为MultipartFile对象
     */
    public static MultipartFile convertBase64ToMultipartFile(String base64String) {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        // 使用 MockMultipartFile 创建 MultipartFile 对象
        MultipartFile file = new MockMultipartFile("example.jpg","example.jpg","image/jpeg",imageBytes);
        return file;
    }
}
