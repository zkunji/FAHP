package com.example.ahp.util;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public class ArrayUtil {
    private static DecimalFormat dfThree = new DecimalFormat("0.000");
    private static DecimalFormat dfTwo = new DecimalFormat("0.00");

    /**
     * @Author kkke
     * @Date 2023/11/20 16:41
     * @Description 保留三位小数
     */
    public static String reserveThree(double number) {
        return dfThree.format(number);
    }

    /**
     * @Author keith
     * @Date 2024/1/24 21:03
     * @Description 保留两位位小数
     */
    public static String reserveTwo(double number) {
        return dfTwo.format(number);
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:42
     * @Description 二维double矩阵数组转一维double数组
     */
    public static double[] doubleMatrixTwoToOne(double[][] array) {
        int rows = array.length;
        int columns = array[0].length;
        double[] resultArray = new double[rows * columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(array[i], 0, resultArray, i * columns, columns);
        }
        return resultArray;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:42
     * @Description 一维double数组转二维double矩阵数组
     */
    public static double[][] doubleMatrixOneToTwo(double[] array, int scale) {
        double[][] doubles = new double[scale][scale];
        for (int i = 0; i < array.length; i++) {
            doubles[i / scale][i % scale] = array[i];
        }
        return doubles;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:43
     * @Description 二维double矩阵数组转字节数组
     */
    public static byte[] doubleMatrixArrayToByteArray(double[][] data) {
        int length = data.length;
        byte[] bytes = new byte[length * length * 8];
        ByteBuffer.wrap(bytes).asDoubleBuffer().put(doubleMatrixTwoToOne(data));
        return bytes;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:43
     * @Description 字节数组转二维double矩阵数组
     */
    public static double[][] byteArrayToDoubleArrayMatrix(byte[] bytes) {
        double[] doubles = byteArrayToDoubleArray(bytes);
        return doubleMatrixOneToTwo(doubles, (int) Math.sqrt((double) bytes.length / 8));
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:44
     * @Description 二维Double矩阵数组转二维double矩阵数组
     */
    public static double[][] doubleArrayMatrixLowercase(Double[][] data) {
        int length = data.length;
        double[][] doubles = new double[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                doubles[i][j] = data[i][j];
            }
        }
        return doubles;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:44
     * @Description 二维double矩阵数组转二维Double矩阵数组
     */
    public static Double[][] doubleArrayMatrixUppercase(double[][] data) {
        int length = data.length;
        Double[][] doubles = new Double[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                doubles[i][j] = data[i][j];
            }
        }
        return doubles;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:44
     * @Description String[]数组转String，用‘，’分割开
     */
    public static String stringArrayToString(String[] ss) {
        String joinedString = String.join(",", ss);
        joinedString = joinedString.replaceAll("\\s", "");
        return joinedString;
    }

    /**
     * @Author kkke
     * @Date 2023/11/20 16:45
     * @Description String转String[]数组，用‘，’分隔
     */
    public static String[] stringToStringArray(String str) {
        String[] ss = str.split(",");
        return ss;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 15:42
     * @Description 将权重字符串转为double数组
     */
    public static double[] proportionStringToDoubleArray(String proportionStr) {
        //字符串转为数组
        String[] split = proportionStr.split(",");
        //返回结果
        double[] result = new double[split.length];
        for (int i = 0; i < split.length; i++) {
            String tmp = split[i];
            //去除%
            tmp = tmp.substring(0, tmp.length() - 1);
            //移位、赋值
            result[i] = Double.parseDouble(tmp) / 100;
        }
        return result;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 16:47
     * @Description double数组转byte数组
     */
    public static byte[] doubleArrayToByteArray(double[] doubles) {
        byte[] bytes = new byte[doubles.length * 8];
        ByteBuffer.wrap(bytes).asDoubleBuffer().put(doubles);
        return bytes;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 18:31
     * @Description long数组转byte数组
     */
    public static byte[] longArrayToByteArray(long[] longs) {
        byte[] bytes = new byte[longs.length * 8];
        ByteBuffer.wrap(bytes).asLongBuffer().put(longs);
        return bytes;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 20:10
     * @Description int数组转byte数组
     */
    public static byte[] intArrayToByteArray(int[] ints) {
        byte[] bytes = new byte[ints.length * 4];
        ByteBuffer.wrap(bytes).asIntBuffer().put(ints);
        return bytes;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 20:17
     * @Description int[][]数组根据除数转为double[][]数组
     */
    public static double[][] intArrayDivisionToFuzzyMatrix(int[][] ints, int number) {
        double[][] doubles = new double[ints.length][ints[0].length];
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[0].length; j++) {
                doubles[i][j] = ints[i][j] / (double) number;
            }
        }
        return doubles;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 20:33
     * @Description 一维double数组与二维double数组相乘
     */
    public static double[] doubleArrayMultiplyDoubleMatrix(double[] array, double[][] matrix) {
        double[] result = new double[matrix[0].length];
        for (int column = 0; column < matrix[0].length; column++) {
            double tmp = 0.0;
            for (int row = 0; row < matrix.length; row++) {
                tmp = tmp + array[row] * matrix[row][column];
            }
            result[column] = tmp;
        }
        return result;
    }

    /**
     * @Author keith
     * @Date 2024/1/24 22:26
     * @Description double数组保留三位小数
     */
    public static double[] doubleArrayReserveThree(double[] doubles) {
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = Double.parseDouble(reserveThree(doubles[i]));
        }
        return doubles;
    }

    /**
     * @Author keith
     * @Date 2024/1/25 13:15
     * @Description 字节数组转long数组
     */
    public static long[] byteArrayToLongArray(byte[] bytes) {
        long[] longs = new long[bytes.length / 8];
        ByteBuffer.wrap(bytes).asLongBuffer().get(longs);
        return longs;
    }

    /**
     * @Author keith
     * @Date 2024/1/25 13:16
     * @Description 字节数组转double数组
     */
    public static double[] byteArrayToDoubleArray(byte[] bytes) {
        double[] doubles = new double[bytes.length / 8];
        ByteBuffer.wrap(bytes).asDoubleBuffer().get(doubles);
        return doubles;
    }

    /**
     * @Author keith
     * @Date 2024/1/25 14:18
     * @Description 字节数组转二维double数组
     */
    public static double[][] byteArrayToTwoDimensionalDoubleArray(byte[] bytes, int column) {
        double[] doubles = new double[bytes.length / 8];
        ByteBuffer.wrap(bytes).asDoubleBuffer().get(doubles);
        int row = doubles.length / column;
        double[][] result = new double[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                result[i][j] = doubles[i * column + j];
            }
        }
        return result;
    }

    /**
     *@Author keith
     *@Date 2024/1/26 15:21
     *@Description byte数组转int数组
     */
    public static int[] byteArrayToIntArray(byte[] bytes) {
        int[] ints = new int[bytes.length / 4];
        ByteBuffer.wrap(bytes).asIntBuffer().get(ints);
        return ints;
    }

    /**
     *@Author keith
     *@Date 2024/5/1 18:07
     *@Description 合并两个int二维数组(列数要求相同,第一个数组可能为空)
     */
    public static int[][] mergeTwoIntMetrix(int[][] firstMetrix,int[][] secondMetrix){
        //第一个数组为空
        if(firstMetrix == null){
            return secondMetrix;
        }

        //扩容并把副数组融入到主数组
        int firstMetrixRow = firstMetrix.length;
        int secondMetrixRow = secondMetrix.length;
        int column = firstMetrix[0].length;
        int[][] targetMetrix = new int[firstMetrixRow + secondMetrixRow][column];
        //先合并第一个数组
        for(int i=0;i<firstMetrixRow;i++){
            System.arraycopy(firstMetrix[i], 0, targetMetrix[i], 0, column);
        }
        //合并第二个数组
        for(int i=0;i<secondMetrixRow;i++){
            System.arraycopy(secondMetrix[i], 0, targetMetrix[firstMetrixRow+i], 0, column);
        }
        return targetMetrix;
    }

    /**
     *@Author keith
     *@Date 2024/5/3 22:08
     *@Description double矩阵保留三位小数
     */
    public static double[][] doubleMatrixReserveThree(double[][] doubles) {
        for (int i = 0; i < doubles.length; i++) {
            for(int j=0;j<doubles[0].length;j++){
                doubles[i][j] = Double.parseDouble(reserveThree(doubles[i][j]));
            }
        }
        return doubles;
    }
}
