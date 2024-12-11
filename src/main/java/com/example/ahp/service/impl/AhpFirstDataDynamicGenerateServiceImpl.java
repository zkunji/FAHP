package com.example.ahp.service.impl;

import com.example.ahp.common.result.Result;
import com.example.ahp.entity.pojos.AhpFirstDataDynamicGenerate;
import com.example.ahp.mapper.AhpFirstDataDynamicGenerateMapper;
import com.example.ahp.service.AhpFirstDataDynamicGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AhpFirstDataDynamicGenerateServiceImpl implements AhpFirstDataDynamicGenerateService {
    private static final Logger logger = LoggerFactory.getLogger(AhpFirstDataDynamicGenerateServiceImpl.class);

    @Autowired
    private AhpFirstDataDynamicGenerateMapper ahpFirstDataDynamicGenerateMapper;

    @Override
    @Transactional
    public Result addFirstIndexData(AhpFirstDataDynamicGenerate dataMap) {
        try {
            // 安全地获取并转换数据
            if (dataMap == null) {
                logger.error("Data entry with key 'data' is missing or not of the expected type.");
                return Result.fail("操作失败：缺少关键数据或数据类型不匹配");
            }

            // 插入数据并检查结果
            int result = ahpFirstDataDynamicGenerateMapper.insert(dataMap);
            if (result > 0) {
                return Result.success(dataMap);
            } else {
                logger.error("Failed to insert data into the database.");
                return Result.fail("操作失败：数据库插入操作异常");
            }
        } catch (ClassCastException e) {
            logger.error("Failed to cast data to AhpFirstDataDynamicGenerate: {}", e.getMessage());
            return Result.fail("操作失败：数据类型转换异常");
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return Result.fail("操作失败：系统异常");
        }
    }
    @Override
    public Result showHistoryData() {
        try {
            List<AhpFirstDataDynamicGenerate> historyData = ahpFirstDataDynamicGenerateMapper.selectList(null);
            if (historyData != null && !historyData.isEmpty()) {
                return Result.success(historyData);
            } else {
                return Result.fail("查询无数据");
            }
        } catch (Exception e) {
            // 记录错误日志
            logger.error("查询历史数据时发生错误: {}", e.getMessage(), e);
            return Result.fail("查询历史数据失败");
        }
    }


}

