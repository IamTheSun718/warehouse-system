package com.demo.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GoodsPosition {
    private String goodsId;
    private String goodsName;
    private double x;
    private double y;
    private double z;
    private String dangerLevel;
    private String status;
    private LocalDateTime updateTime;
}