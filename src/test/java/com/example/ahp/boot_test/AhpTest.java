//package com.example.ahp;
//
//import com.example.ahp.Util.AhpUtil;
//import com.example.ahp.entity.pojos.AhpFirstData;
//import com.example.ahp.entity.dtos.AhpFirstDataDto;
//import com.example.ahp.mapper.AhpFirstDataMapper;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AhpApplication.class)
//public class AhpTest {
//    @Autowired
//    public AhpFirstDataMapper ahpFirstDataMapper;
//
//    public Double[][] data = {
//            {1.000, 0.333, 3.000, 5.000, 0.500, 5.000, 0.167, 5.000, 2.000, 4.000, 0.200, 6.000, 0.333},
//            {3.000, 1.000, 5.000, 7.000, 0.250, 7.000, 0.250, 7.000, 4.000, 6.000, 0.333, 8.000, 0.500},
//            {0.333, 0.200, 1.000, 3.000, 0.500, 4.000, 0.143, 4.000, 0.333, 3.000, 0.167, 5.000, 0.167},
//            {0.200, 0.143, 0.333, 1.000, 0.167, 1.000, 0.111, 0.500, 0.250, 0.333, 0.125, 0.500, 0.143},
//            {2.000, 4.000, 2.000, 6.000, 1.000, 6.000, 0.200, 6.000, 3.000, 5.000, 0.250, 7.000, 0.333},
//            {0.200, 0.143, 0.250, 1.000, 0.167, 1.000, 0.111, 0.500, 0.250, 0.333, 0.111, 0.500, 0.125},
//            {6.000, 4.000, 7.000, 9.000, 5.000, 9.000, 1.000, 8.000, 6.000, 7.000, 2.000, 8.000, 3.000},
//            {0.200, 0.143, 0.250, 2.000, 0.167, 2.000, 0.125, 1.000, 0.200, 0.333, 0.143, 3.000, 0.143},
//            {0.500, 0.250, 3.000, 4.000, 0.333, 4.000, 0.167, 5.000, 1.000, 4.000, 0.200, 6.000, 0.200},
//            {0.250, 0.167, 0.333, 3.000, 0.200, 3.000, 0.143, 3.000, 0.250, 1.000, 0.143, 4.000, 0.167},
//            {5.000, 3.000, 6.000, 8.000, 4.000, 9.000, 0.500, 7.000, 5.000, 7.000, 1.000, 8.000, 2.000},
//            {0.167, 0.125, 0.200, 2.000, 0.143, 2.000, 0.125, 0.333, 0.167, 0.250, 0.125, 1.000, 0.125},
//            {3.000, 2.000, 6.000, 7.000, 3.000, 8.000, 0.333, 7.000, 5.000, 6.000, 0.500, 8.000, 1.000}
//    };
//
//    @Test
//    public void saveFirstAhpData(){
////        AhpFirstData ahpFirstData = AhpUtil.weightValueFirstAnalysis(data);
////        ahpFirstDataMapper.insert(ahpFirstData);
//    }
//
//    @Test
//    public void selectFirstAhpData(){
//        AhpFirstData ahpFirstData = ahpFirstDataMapper.selectById(1);
//        AhpFirstDataDto ahpFirstDataDto = new AhpFirstDataDto(ahpFirstData);
//        System.out.println(ahpFirstDataDto);
//    }
//}
