package com.example.ahp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ahp.common.result.Result;
import com.example.ahp.common.result.ResultInfo;
import com.example.ahp.entity.dtos.EntropyWeightDto;
import com.example.ahp.entity.pojos.Admin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface AdminService extends IService<Admin> {
    Result register(Map<String, String> params);

    Result login(Map<String, String> params);

    Result analyse(Map<String, Object> params,long adminId);

    ResultInfo searchByPagination(Map<String, Object> params);

    ResultInfo getUserInfo(Map<String, Object> params);

    Result getAhpFirstDataMarked(long uid);

    Result insertEntropyWeight(EntropyWeightDto dto);

    Result queryFuzzyAlgorithmFirstDataDetails(int pageNum, int pageSize);

    Result queryFuzzyAlgorithmFirstSubject(long fafId);

    Result updateAdminPwd(String new_pwd,long adminId);
}
