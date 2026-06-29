package com.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("outbound_order")
public class OutboundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long materialId;
    private String materialName;
    private Integer quantity;
    private String unit;
    private String targetDept;
    private String purpose;
    private LocalDateTime outboundTime;
    private String operator;
    private String approver;
    private String status;
}