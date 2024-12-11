package com.example.ahp.util;

public class EntropyWeightUtil {
    /**
     * @Author keith
     * @Date 2024/4/27 21:31
     * @Description 熵权法
     */
    public static double[] calculateEntropyWeight(double[][] doubles) {
        int row = doubles.length;
        int column = doubles[0].length;
        //1.归一化(消除量纲的影响,使值落在0-1之间)
        //1.1求每列最大、小值
        double[][] maxAndMinMatrix = new double[2][column]; //第一行的每一个数据指这一列的最大值、第二行的每一个数据指这一列的最小值
        //i->行,j->列
        for (int j = 0; j < column; j++) {
            double max = 0.0;
            double min = 10.0;
            int maxIndex, minIndex;
            maxIndex = minIndex = 0;
            //寻找该列最大、小值及行号
            for (int i = 0; i < row; i++) {
                //判断是否是当前最大值
                if (Double.compare(doubles[i][j], max) > 0) {
                    max = doubles[i][j];
                    maxIndex = i;
                }
                //判断是否是当前最小值
                if (Double.compare(min, doubles[i][j]) > 0) {
                    min = doubles[i][j];
                    minIndex = i;
                }
            }
            //赋值
            maxAndMinMatrix[0][j] = doubles[maxIndex][j];
            maxAndMinMatrix[1][j] = doubles[minIndex][j];
        }
        //1.2进行归一化(x-min)/(max-min)
        double[][] normalizationMatrix = new double[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (maxAndMinMatrix[0][j] == maxAndMinMatrix[1][j]) {
                    //这一列值都相同
                    normalizationMatrix[i][j] = 0.0;
                } else {
                    //这一列值不全相同
                    normalizationMatrix[i][j] = (doubles[i][j] - maxAndMinMatrix[1][j]) / (maxAndMinMatrix[0][j] - maxAndMinMatrix[1][j]);
                }
            }
        }
        //1.3平移
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                normalizationMatrix[i][j] += 0.001;
            }
        }

        //2.计算信息熵(详情见公式)
        double k = -1 / Math.log(row);
        double[] informationEntropyArray = new double[column];
        for (int j = 0; j < column; j++) {
            //计算归一化后一列的总值
            double columnTotal = 0.0;
            for (int i = 0; i < row; i++) {
                columnTotal += normalizationMatrix[i][j];
            }
            //计算该一列每个的P(i,j)概率
            double[] probabilityArray = new double[row];
            for (int i = 0; i < row; i++) {
                probabilityArray[i] = normalizationMatrix[i][j] / columnTotal;
            }
            //计算P(i,j)乘与ln(P(i,j))的求和
            double tmp = 0.0;
            for (int i = 0; i < row; i++) {
                tmp = tmp + probabilityArray[i] * Math.log(probabilityArray[i]);
            }
            //赋值
            informationEntropyArray[j] = tmp * k;
        }

        //3.确定指标权重
        //计算D(j) = 1 - 信息熵(e(j))
        double[] DArray = new double[column];
        double tmpDTotal = 0.0;
        for (int j = 0; j < column; j++) {
            DArray[j] = 1 - informationEntropyArray[j];
            tmpDTotal += DArray[j];
        }
        //计算每一列(指标)的权重(D(j) / D(j)的和)
        double[] metricsWeightArray = new double[column];
        for (int j = 0; j < column; j++) {
            metricsWeightArray[j] = DArray[j] / tmpDTotal;
        }

        //4.计算每位专家的权重
        //用线性加权法得出矩阵(归一化矩阵乘与指标权重)
        double[][] linearlyWeightedMatrix = new double[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                //判断这一列指标的权重值是否等于0
                double epsilon = 1e-10; // 设置的误差范围
                if (Math.abs(metricsWeightArray[j]) < epsilon) {
                    linearlyWeightedMatrix[i][j] = 0.0;
                } else {
                    linearlyWeightedMatrix[i][j] = normalizationMatrix[i][j] * metricsWeightArray[j];
                }
            }
        }
        //计算专家的权重
        double[] expertWeightArray = new double[row];
        double total = 0.0;
        for (int i = 0; i < row; i++) {
            double tmp = 0.0;
            for (int j = 0; j < column; j++) {
                tmp += linearlyWeightedMatrix[i][j];
            }
            expertWeightArray[i] = tmp;
            total += tmp;
        }
        for (int i = 0; i < row; i++) {
            expertWeightArray[i] = expertWeightArray[i] / total;
        }
        return expertWeightArray;
    }
}
