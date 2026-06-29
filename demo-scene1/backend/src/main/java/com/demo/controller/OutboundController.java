package com.demo.controller;

import com.demo.model.OutboundOrder;
import com.demo.service.OutboundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/outbound")
@CrossOrigin(origins = "*")
public class OutboundController {

    @Autowired
    private OutboundOrderService outboundOrderService;

    @GetMapping
    public List<OutboundOrder> list() {
        return outboundOrderService.findAll();
    }

    @PostMapping
    public OutboundOrder create(@RequestBody OutboundOrder order) {
        return outboundOrderService.save(order);
    }
}
