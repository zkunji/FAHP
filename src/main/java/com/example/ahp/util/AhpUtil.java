package com.example.ahp.util;

import com.example.ahp.entity.pojos.AhpData;

public class AhpUtil {
    private Double[] target;
    //3-16阶RI值
    private static Double[] RIs = new Double[]{0.0,0.0,0.52,0.89,1.12,
            1.26,1.36,1.41,1.46,1.49,1.52,1.54,1.56,1.58,1.59,1.5943};
    /**
     *@Author kkke
     *@Date 2023/11/20 16:47
     *@Description Ahp层次分析算法
     */
    public static AhpData weightValueFirstAnalysis(Double[][] data){
        int length = data.length;
        AhpData ahpData = new AhpData();
        //1.计算每一列之和
        Double[] sum = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for (Double[] datum : data) {
                tmp += datum[i];
            }
            sum[i] = tmp;
        }
        //2.计算按列归一化矩阵
        Double[][] normalizedMatrix = new Double[length][length];
        for(int i=0;i<length;i++){
            for(int j=0;j<length;j++){
                normalizedMatrix[i][j] = data[i][j]/sum[j];
            }
        }
        //3.计算特征向量矩阵
        Double[] new_eigenVectors = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp = tmp + normalizedMatrix[i][j];
            }
            new_eigenVectors[i] = tmp;
        }
        //4.计算每一个指标权重值
        Double[] wightValue = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp += normalizedMatrix[i][j];
            }
            wightValue[i] = tmp/length;
        }
        //5.计算每一个指标A*w
        Double[] eigenVectors = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp = tmp + data[i][j]*wightValue[j];
            }
            eigenVectors[i] = tmp;
        }
        //6.计算最大特征值
        Double maximumEigenvalue = 0.0;
        for(int i=0;i<length;i++){
            maximumEigenvalue += eigenVectors[i]/wightValue[i];
        }
        maximumEigenvalue = maximumEigenvalue/length;
        //7.计算CI
        double CI = (maximumEigenvalue-length)/(length-1);
        //8.RI值
        double RI = AhpUtil.RIs[length-1];
        //9.计算CR
        double CR = CI/RI;
        //10.一致性分析
        boolean result = Double.doubleToLongBits(CR) < Double.doubleToLongBits(0.1)
                && Double.doubleToLongBits(CR) >= Double.doubleToLongBits(0.0);
        //11.赋值
        String tmp = "";
        //11.1规格
        ahpData.setScale(length);
        //11.2原始数据
        byte[] bytes = ArrayUtil.doubleMatrixArrayToByteArray(ArrayUtil.doubleArrayMatrixLowercase(data));
        ahpData.setOriginalData(bytes);
        //11.3特征向量
        tmp = "";
        for(int i=0;i<length;i++){
            tmp = tmp + ArrayUtil.reserveThree(new_eigenVectors[i]) + ",";
        }
        ahpData.setEigenvectors(tmp.substring(0,tmp.length()-1));
        //11.4权重值
        tmp = "";
        for(int i=0;i<length;i++){
            tmp = tmp + ArrayUtil.reserveThree(wightValue[i] * 100) + "%,";
        }
        ahpData.setProportion(tmp.substring(0,tmp.length()-1));
        //11.5最大特征值
        ahpData.setMaxEigenvalue(ArrayUtil.reserveThree(maximumEigenvalue));
        //11.6CI值
        ahpData.setCi(ArrayUtil.reserveThree(CI));
        //11.7RI值
        ahpData.setRi(RI);
        //11.8CR值
        ahpData.setCr(ArrayUtil.reserveThree(CR));
        //11.9一致性分析结果
//        ahpData.setResult(result);
        ahpData.setResult(true);
        //11.10指标个数<=2,重新赋值ci,ri,cr,result值
        if(ahpData.getScale()<=2){
            ahpData.setCi("0.000");
            ahpData.setRi(0.000);
            ahpData.setCr("null");
            ahpData.setResult(true);
        }
        return ahpData;
    }
}
