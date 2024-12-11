package com.example.ahp.boot_test;

import com.example.ahp.AhpApplication;
import com.example.ahp.common.result.Result;
import com.example.ahp.entity.dtos.InquiryTypeDto;
import com.example.ahp.entity.pojos.InquiryType;
import com.example.ahp.mapper.InquiryTypeMapper;
import com.example.ahp.service.AdminService;
import com.example.ahp.util.ArrayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AhpApplication.class)
public class AdminTest {
    @Autowired
    AdminService adminService;
    @Autowired
    InquiryTypeMapper inquiryTypeMapper;


    @Test
    public void inquiryTypeTest(){
        InquiryTypeDto dto = new InquiryTypeDto();
        dto.setAlias("第一代问卷");
        dto.setFirstMetrics("组织架构,决策机制,执行机制,监督机制,协调机制,评价机制,预算控制,收支控制,采购控制,资产控制,工程控制,会计控制,合同控制");
        //二级指标
        String[] strings = {
                "职责分工,三权分立,关键岗位责任制",
                "风险评估,专家论证,审核审批,集体决策",
                "内控实施责任制,权责分工明确,控制制度体系化,信息系统",
                "日常监督,绩效考评,内部审计,纪检监察制度",
                "机构和人员的协调机制,业务流程的协调机制,信息沟通的协调机制",
                "自我评价机制,自我评价反馈",
                "预算编审,预算批复,预算执行,决算与考评",
                "收入预算,收入登记,收入到账确认,支出事项分类,支出事项的执行方式,支出过程控制",
                "采购预算和采购计划的编制与审核,采购组织方式确定,采购方式确定,政府采购代理机构确定,政府采购计划实施,自行采购计划实施,合同签订与备案,组织验收,采购支付",
                "资产配置,资产新购,资产更新,资产处置",
                "工程立项,设计与概预算,工程招标,工程建设,工程竣工验收,工程项目核算",
                "财务收支审批和报销,会计凭证填制和传递,会计账簿登记,财务报告编审和披露",
                "合同控制职责分工,合同订立,合同履行,合同后续管理,合同特殊事项管理"
        };
        dto.setSecondMetrics(strings);
        //转换存储
        InquiryType inquiryType = new InquiryType(dto);
        inquiryTypeMapper.insert(inquiryType);
    }

    @Test
    public void previewInquiryType(){
        InquiryType inquiryType = inquiryTypeMapper.selectById(1);
        InquiryTypeDto dto = new InquiryTypeDto(inquiryType);
        System.out.println(dto.getFirstMetrics());
        for(int i=0;i<13;i++){
            System.out.println(dto.getSecondMetrics()[i]);
        }
    }
}
