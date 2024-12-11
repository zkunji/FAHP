package com.example.ahp.util;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MinioUtil {
    /**
     * 文件上传
     * @param file 文件
     * @return Boolean
     */
    public static boolean upload(MultipartFile file,String bucket,String objectName, MinioClient minioClient) throws IOException {
        String originalFilename = file.getOriginalFilename();
        InputStream stream = null;
        if (originalFilename == null || "".equals(originalFilename)){
            throw new RuntimeException();
        }
        try {
            stream = file.getInputStream();
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(stream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            //文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(stream!=null){
                stream.close();
            }
        }
        return true;
    }

    /**
     * 预览图片
     * @param fileName
     * @return
     */
    public static String preview(String fileName,String bucket,MinioClient minioClient){
        // 查看文件地址
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder().bucket(bucket).object(fileName).method(Method.GET).build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     * @param bucketName
     * @param fileName
     * @return
     */
    public static boolean removeFile(String bucketName,String fileName,MinioClient minioClient) {
        try {
            //判断桶是否存在
            boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (res) {
                //删除文件
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName)
                        .object(fileName).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除文件失败");
            return false;
        }
        System.out.println("删除文件成功");
        return true;
    }
//
//    /**
//     * 查看存储bucket是否存在
//     * @return boolean
//     */
//    public Boolean bucketExists(String bucketName) {
//        Boolean found;
//        try {
//            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return found;
//    }
//
//    /**
//     * 创建存储bucket
//     * @return Boolean
//     */
//    public Boolean makeBucket(String bucketName) {
//        try {
//            minioClient.makeBucket(MakeBucketArgs.builder()
//                    .bucket(bucketName)
//                    .build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 删除存储bucket
//     * @return Boolean
//     */
//    public Boolean removeBucket(String bucketName) {
//        try {
//            minioClient.removeBucket(RemoveBucketArgs.builder()
//                    .bucket(bucketName)
//                    .build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 获取全部bucket
//     */
//    public List<Bucket> getAllBuckets() {
//        try {
//            List<Bucket> buckets = minioClient.listBuckets();
//            return buckets;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}

