package com.example.ahp.test;

public class AhpUtil {
    private Double[] target;
    //3-16阶RI值
    private static Double[] RIs = new Double[]{0.0,0.0,0.52,0.89,1.12,
            1.26,1.36,1.41,1.46,1.49,1.52,1.54,1.56,1.58,1.59,1.5943};
    public static Double[] weightValueAnalysis(Double[][] data) throws Exception {
        int length = data.length;
        //1.计算每一列之和
        Double[] sum = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp += data[j][i];
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
        //3.计算每一个指标权重值
        Double[] wightValue = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp += normalizedMatrix[i][j];
            }
            wightValue[i] = tmp/length;
        }
        //4.计算每一个指标特征向量
        Double[] eigenVectors = new Double[length];
        for(int i=0;i<length;i++){
            double tmp = 0;
            for(int j=0;j<length;j++){
                tmp = tmp + data[i][j]*wightValue[j];
            }
            eigenVectors[i] = tmp;
        }
        //5.计算最大特征值
        Double maximumEigenvalue = 0.0;
        for(int i=0;i<length;i++){
            maximumEigenvalue += eigenVectors[i]/wightValue[i];
        }
        maximumEigenvalue = maximumEigenvalue/length;
        //6.计算CI
        double CI = (maximumEigenvalue-length)/(length-1);
        //7.RI值
        double RI = AhpUtil.RIs[length-1];
        //8.计算CR
        double CR = CI/RI;
        //9.一致性分析
        if(Double.doubleToLongBits(CR) > Double.doubleToLongBits(0.1)){
            throw new Exception("一致性检验不符合");
        }
        return wightValue;
    }
}
