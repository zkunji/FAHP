package com.example.ahp.test;

public class Test {
    public static void main(String[] args) {
        //1.准备数据
        double[][] data = {
                {1.000, 0.250, 2.000, 0.333},
                {4.000, 1.000, 8.000, 2.000},
                {0.500, 0.125, 1.000, 0.200},
                {3.000, 0.500, 5.000, 1.000}
        };
        //2计算每一列之和
        double[] sum = new double[4];
        for(int i=0;i<4;i++){
            double tmp = 0;
            for(int j=0;j<4;j++){
                tmp += data[j][i];
            }
            sum[i] = tmp;
        }
        //3.计算按列归一化矩阵
        double[][] normalizedMatrix = new double[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                normalizedMatrix[i][j] = data[i][j]/sum[j];
            }
        }
        //4.计算每一个指标权重值
        double[] wightValue = new double[4];
        for(int i=0;i<4;i++){
            double tmp = 0;
            for(int j=0;j<4;j++){
                tmp += normalizedMatrix[i][j];
            }
            wightValue[i] = tmp/4;
        }
        //5.计算每一个指标特征向量
        double[] eigenVectors = new double[4];
        for(int i=0;i<4;i++){
            double tmp = 0;
            for(int j=0;j<4;j++){
                tmp = tmp + data[i][j]*wightValue[j];
            }
            eigenVectors[i] = tmp;
        }
        //6.计算最大特征值
        double maximumEigenvalue = 0;
        for(int i=0;i<4;i++){
            maximumEigenvalue += eigenVectors[i]/wightValue[i];
        }
        maximumEigenvalue = maximumEigenvalue/4;
        //7.计算CI
        double CI = (maximumEigenvalue-4)/(4-1);
        //8.RI值
        double RI = 0.89;
        //9.计算CR
        double CR = CI/RI;
        System.out.println("权重值：");
        for(int i=0;i<4;i++){
            System.out.println(wightValue[i]+" ");
        }
//        System.out.println();
//        System.out.println("特征向量：");
//        for(int i=0;i<4;i++){
//            System.out.println(eigenVectors[i]+" ");
//        }
//        System.out.println();
    }
}
