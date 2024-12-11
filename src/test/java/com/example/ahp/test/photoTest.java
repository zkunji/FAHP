package com.example.ahp.test;

import com.example.ahp.AhpApplication;
import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.util.ImageUtil;
import com.example.ahp.util.MinioUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AhpApplication.class)
public class photoTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void photoToString(){
        String filePath = "D:\\test\\photo\\user.png";
        String base64String = ImageUtil.convertImageToBase64(filePath);
        System.out.println(base64String);
    }

//    @Test
////    public void uploadPhoto(){
////        String photoString = "iVBORw0KGgoAAAANSUhEUgAAACMAAAAmCAYAAABOFCLqAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAKrSURBVFhH7ZdZTxpRFIC/Yd9EUXFDpUrqglXbdHloH9o0TfqT+9DHpmmbNqY2KDWARFOpVEQFAYFZeiFg2urEmUlseOBLCMPNzD3fPXPm3EEqFosaPYKt890T9GX06Mvo0ZfRoy+jh+UOrCoytfMzSpU6ipghEBzC5/HgclpfnyUZTVO4KJ+QSW6ylc1Tl+HuvUdEZ6YYD/mx26TOmeawJFPOb5P+nuDN2w2Oq412ZnyBcZYePmb96RNWw35sknkhkzkVUZUqe+kMW5s75AqnVCoVatUKxaMf7KZSJL6lqDc1kb3OJSYwJ9OKINc4zB2zf3CMLH53Y2pag9JJgdzuPg1FRe2Mm8F8ZuQm1ab4KMqlSBdFqdNonKOoFtIiMF/6YsmtWNfF01qZ0sQJ1lwsyIi6bD0sttbBP0jtMTGl+dptY05GEqe7/EyMDDI14mtLXSLZ8QZDjE7O4HZIFlZpOjMiutNHZDZKbG4Wv0sE7Qq5g4QnIiwvzAkZOxaebPML0DSJ4ekFpmNxBj1igm5Q/xgTkxEexMLtMStlY6rpqarM2c8dvrz/zNZ2inS+gNytV7uLUHiGmTuLvHr9nNnwAF6nvX2dUQzJqHKdUvEX+YN9EokE6cweR4Uzyk2xD/yBU9STZyDEYnyFpZVlYtEI4WAAp8PYDTAgo1Eu5Mhsb5JMJtlIZqmIzUi3l4hicXmDzK2ss7Z6n/WleUaCHnHrbi4iAzJNsl8/8undB5KHp2JTvNrsriLhcLoZja7x8sUzlufHcNlvzo4BGZl8NkVqJ81hqSY2xZtVWkg2O4HhaeLxRSLjQzj/6gPXY+g2NS5qXFRr7T3HmEoLCbvIjs/nxe1yGOqDhgr4f2GlUd4afRk9+jJ69GX06C2Zpni5br279gI2VbXyp+I2gN9VDhb8Ds3+ugAAAABJRU5ErkJggg==";
////        MultipartFile file = ImageUtil.convertBase64ToMultipartFile(photoString);
////        MinioUtil.upload(file, AhpConstants.MinioPhotoCommentBucket,"123_二级指标");
////    }
//
//    @Test
//    public void previewPhoto(){
//        String preview = MinioUtil.preview("123_二级指标", AhpConstants.MinioPhotoCommentBucket);
//        System.out.println(preview);
//    }

    @Test
    public void testBeanExistence() {
        boolean exists = applicationContext.containsBeanDefinition("minioUtil");
        if (exists) {
            System.out.println("minioClient 存在于容器中");
        } else {
            System.out.println("minioClient 不存在于容器中");
        }
    }
}
