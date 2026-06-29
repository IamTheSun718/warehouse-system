package com.demo.service;

import com.demo.mapper.InboundOrderMapper;
import com.demo.model.InboundOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InboundOrderService {

    @Autowired
    private InboundOrderMapper inboundOrderMapper;

    @Autowired
    private MaterialService materialService;

    public List<InboundOrder> findAll() {
        return inboundOrderMapper.selectList(null);
    }

    public InboundOrder save(InboundOrder order) {
        if (order.getId() == null) {
            order.setOrderNo("IN-" + System.currentTimeMillis());
            order.setInboundTime(LocalDateTime.now());
            order.setStatus("已入库");
            inboundOrderMapper.insert(order);
            if (order.getMaterialId() != null) {
                materialService.updateStock(order.getMaterialId(), order.getQuantity());
            }
        } else {
            inboundOrderMapper.updateById(order);
        }
        return order;
    }
}
