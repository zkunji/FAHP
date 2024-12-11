package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.common.constant.AhpConstants;
import com.example.ahp.common.constant.RedisConstants;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.AhpSecondCommentDto;
import com.example.ahp.entity.pojos.AhpFirstData;
import com.example.ahp.entity.pojos.AhpSecondComment;
import com.example.ahp.entity.pojos.AhpSecondData;
import com.example.ahp.mapper.AhpFirstDataMapper;
import com.example.ahp.mapper.AhpSecondCommentsMapper;
import com.example.ahp.mapper.AhpSecondDataMapper;
import com.example.ahp.service.AhpSecondCommentsService;
import com.example.ahp.util.MinioUtil;
import com.example.ahp.util.RedisUtil;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class AhpSecondCommentsServiceImpl extends ServiceImpl<AhpSecondCommentsMapper, AhpSecondComment> implements AhpSecondCommentsService {
    @Autowired
    AhpSecondCommentsMapper ahpSecondCommentsMapper;
    @Autowired
    AhpSecondDataMapper ahpSecondDataMapper;
    @Autowired
    AhpFirstDataMapper ahpFirstDataMapper;
    @Autowired
    MinioClient minioClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * @Author keith
     * @Date 2024/1/1 14:18
     * @Description 二级指标打分
     */
    @Override
    public Result evaluate(Map<String, Object> params) {
        //1.数据处理
        //一级指标外键
        Integer afdId = (Integer) params.get("afd_id");
        AhpFirstData ahpFirstData = RedisUtil.getAhpFirstDataFromRedis(afdId, redisTemplate, ahpFirstDataMapper);
        if(ahpFirstData == null){
            return Result.fail("未给一级指标赋值权重");
        }
        //二级指标打分对象存储
        List<AhpSecondComment> list = new ArrayList<>();
        //获取二级指标打分对象map
        List<Map<String, Object>> dtoList = (List<Map<String, Object>>) params.get("asd_comments");
        for (Map<String, Object> map : dtoList) {
            //创建二级指标打分对象
            AhpSecondCommentDto dto = new AhpSecondCommentDto();
            //一级指标权重外键
            dto.setAfdId(afdId);
            //二级指标权重外键
            dto.setAsdId((Integer) map.get("asd_id"));
            //二级指标名
            dto.setAlias((String) map.get("alias"));
            AhpSecondData ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(dto.getAsdId(), redisTemplate, ahpSecondDataMapper);
            if(ahpSecondData == null){
                return Result.fail("未给二级指标:"+dto.getAlias()+"赋值权重");
            }
            //二级指标打分水平
            dto.setLevel((String) map.get("level"));
            //二级指标打分评语集
            if (map.get("comment") != null) {
                dto.setComment((String) map.get("comment"));
            } else {
                dto.setComment("");
            }
            //判断评价水平和评语集是否符合要求
            if (!"优秀".equals(dto.getLevel()) && (dto.getComment() == null || "".equals(dto.getComment()))) {
                return Result.fail("二级指标:"+dto.getAlias()+"的评价水平不为优秀,需要补充原因");
            }
            //转换
            list.add(new AhpSecondComment(dto));
        }
        //2.数据存储
        try {
            //2.1数据库数据处理
            boolean flag = this.saveBatch(list);
            if (!flag) {
                return Result.fail("插入数据失败");
            }
            //2.2将此次整体分析设置为完整数据状态(0)
            LambdaUpdateWrapper<AhpFirstData> uw = new LambdaUpdateWrapper<>();
            uw.eq(AhpFirstData::getAfdId, afdId).set(AhpFirstData::getSubmit, 0);
            ahpFirstDataMapper.update(null, uw);
            //删除redis数据
            redisTemplate.delete(RedisConstants.AhpFirstDataInfo + afdId);
            return Result.success("插入数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            //手动触发事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail("插入数据失败");
        }
    }

    /**
     * @Author keith
     * @Date 2024/1/1 16:48
     * @Description 根据一级指标外键查询二级指标打分
     */
    @Override
    public Result searchByAfdId(long afdId) {
        //封装返回结果
        List<Map<String, Object>> target = new ArrayList<>();
        //根据一级指标外键查询包括哪些二级指标外键
        for (Long asdId : ahpSecondCommentsMapper.selectAsdIdByAfdId(afdId)) {
            //一级map
            HashMap<String, Object> firstMap = new HashMap<>();
            //根据二级指标外键获取此一级指标名称->firstAlias
            AhpSecondData ahpSecondData = RedisUtil.getAhpSecondDataFromRedis(asdId, redisTemplate, ahpSecondDataMapper);
            //数据肯定存在
            firstMap.put("firstAlias", ahpSecondData.getAlias());
            //一级List
            ArrayList<Map<String, Object>> firstList = new ArrayList<>();
            //根据二级指标外键获取一个一级指标下每个二级指标打分 数据
            for (String secondAlias : ahpSecondData.getNames().split(",")) {
                //获得数据
                AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(afdId, asdId, secondAlias,redisTemplate,ahpSecondCommentsMapper);
                //二级map
                HashMap<String, Object> secondMap = new HashMap<>();
                //二级指标名称
                secondMap.put("secondAlias",secondAlias);
                if(comment!=null){
                    //数据格式转换
                    AhpSecondCommentDto dto = new AhpSecondCommentDto(comment);
                    //评分水平
                    secondMap.put("level", dto.getLevel());
                    //文本评语集
                    secondMap.put("comment", dto.getComment());
                    //存储到一级list
                }
                firstList.add(secondMap);
            }
            //存储结果
            firstMap.put("secondComment", firstList);
            target.add(firstMap);
        }
        return Result.success(target);
    }

    /**
     * @Author keith
     * @Date 2024/3/9 14:25
     * @Description 根据一级指标外键和二级指标外键上传文件
     */
    @Override
    public Result uploadFiles(MultipartFile[] files, long afdId, long asdId, String alias) {
        //先查询此AhpSecondComment对象是否存在
        AhpSecondComment secondComment = RedisUtil.getAhpSecondCommentFromRedis(afdId, asdId, alias, redisTemplate, ahpSecondCommentsMapper);
        if (secondComment != null && files.length != 0) {
            try {
                //上传文件并得到url
                List<String> list = savePhotos(files, new AhpSecondCommentDto(secondComment));
                if(list == null){
                    return Result.fail("上传文件失败");
                }
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        tmp.append(list.get(i));
                        continue;
                    }
                    tmp.append(",").append(list.get(i));
                }
                //准备fileUrl
                String filesUrl = "";
                if (secondComment.getFiles() != null && !"".equals(secondComment.getFiles())) {
                    filesUrl = secondComment.getFiles() + ",";
                }
                filesUrl += tmp.toString();
                //更新数据库
                LambdaUpdateWrapper<AhpSecondComment> uw = new LambdaUpdateWrapper<>();
                uw.eq(AhpSecondComment::getAfdId, afdId).eq(AhpSecondComment::getAsdId, asdId).eq(AhpSecondComment::getAlias, alias).set(AhpSecondComment::getFiles, filesUrl);
                int flag = ahpSecondCommentsMapper.update(null, uw);
                if(flag == 0){
                    return Result.fail("数据库异常,插入数据失败");
                }
                //更新redis(删除数据)
                secondComment.setFiles(filesUrl);
                String key = afdId+"_"+asdId+alias;
                redisTemplate.delete(RedisConstants.AhpSecondCommentInfo+key);
                //返回结果
                return Result.success(filesUrl.split(","));
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail("上传失败");
            }
        }
        return Result.fail("上传失败");
    }

    /**
     * @Author keith
     * @Date 2024/3/30 15:40
     * @Description 根据一级指标外键、二级指标外键、二级指标名称获取文件
     */
    @Override
    public Result getFiles(long afdId, long asdId, String alias) {
        //查询数据
        AhpSecondComment comment = RedisUtil.getAhpSecondCommentFromRedis(afdId, asdId, alias, redisTemplate, ahpSecondCommentsMapper);
        if(comment==null){
            return Result.fail("数据不存在");
        }
        String[] files = new AhpSecondCommentDto(comment).getFiles();
        return Result.success(getPhotos(files));
    }

    /**
     * @Author keith
     * @Date 2024/1/18 14:42
     * @Description 将文件存储到minio服务里并返回图片url
     */
    private List<String> savePhotos(MultipartFile[] files, AhpSecondCommentDto dto) throws IOException {
        //存储到Minio上图片的filename
        String filename = dto.getAfdId() + "_" + dto.getAsdId() + "_" + dto.getAlias() + "_";
        //所有图片组成的url
        List<String> urlList = new ArrayList<>();
        //Minio操作
        int i = 1;
        //判断是否已经存储过
        if (dto.getFiles() != null) {
            String lastFile = dto.getFiles()[dto.getFiles().length - 1];
            String[] ss = lastFile.split("_");
            i = Integer.parseInt(ss[ss.length - 1]) + 1;
        }
        for (int j = 0; j < files.length; j++, i++) {
            //将图片存储到Minio
            String url = filename + i;
            boolean flag = MinioUtil.upload(files[j], AhpConstants.MinioFilesBucket, url, minioClient);
            if(!flag){
                //某一次上传文件失败
                for(int k =0;k<urlList.size();k++){
                    //删除之前已上传文件
                    boolean ifRemove = MinioUtil.removeFile(AhpConstants.MinioFilesBucket, urlList.get(k), minioClient);
                }
                //终止上传,返回null
                urlList = null;
                break;
            }
            //添加url
            urlList.add(url);
        }
        return urlList;
    }

    /**
     * @Author keith
     * @Date 2024/1/20 15:08
     * @Description 根据图片filename获得具体的url集合
     */
    private String[] getPhotos(String[] photos) {
        for (int i = 0; i < photos.length; i++) {
            photos[i] = MinioUtil.preview(photos[i], AhpConstants.MinioFilesBucket, minioClient);
        }
        return photos;
    }
}
