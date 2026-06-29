package com.demo.service;

import com.demo.mapper.OutboundOrderMapper;
import com.demo.model.OutboundOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboundOrderService {

    @Autowired
    private OutboundOrderMapper outboundOrderMapper;

    @Autowired
    private MaterialService materialService;

    public List<OutboundOrder> findAll() {
        return outboundOrderMapper.selectList(null);
    }

    public OutboundOrder save(OutboundOrder order) {
        if (order.getId() == null) {
            order.setOrderNo("OUT-" + System.currentTimeMillis());
            order.setOutboundTime(LocalDateTime.now());
            order.setStatus("已出库");
            outboundOrderMapper.insert(order);
            if (order.getMaterialId() != null) {
                materialService.updateStock(order.getMaterialId(), -order.getQuantity());
            }
        } else {
            outboundOrderMapper.updateById(order);
        }
        return order;
    }
}
