package com.example.ahp.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FuzzyAlgorithmResult {
    private Map<String,double[]> metricsWeight = new HashMap<>();
    private int[][] commentsNumber;
    private Map<String,double[][]> fuzzyMatrix = new HashMap<>();
    private Map<String,Map<String,Object>> commentsMatrix = new HashMap<>();
    private String commentsResult;
}
