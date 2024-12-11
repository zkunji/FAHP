package com.example.ahp.entity.dtos;

import lombok.Data;

@Data
public class EntropyWeightDto {
    private int id;
    private long[] uidArray;
    private double[] expertWeight;
}
