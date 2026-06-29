package com.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inbound_order")
public class InboundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long materialId;
    private String materialName;
    private Integer quantity;
    private String unit;
    private String supplier;
    private String batchNo;
    private LocalDateTime inboundTime;
    private String operator;
    private String status;
}