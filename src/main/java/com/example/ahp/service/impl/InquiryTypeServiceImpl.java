package com.example.ahp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ahp.entity.pojos.InquiryType;
import com.example.ahp.mapper.InquiryTypeMapper;
import com.example.ahp.service.InquiryTypeIservice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InquiryTypeServiceImpl extends ServiceImpl<InquiryTypeMapper, InquiryType> implements InquiryTypeIservice {
}
