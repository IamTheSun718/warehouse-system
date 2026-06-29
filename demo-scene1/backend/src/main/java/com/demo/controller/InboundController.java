package com.demo.controller;

import com.demo.model.InboundOrder;
import com.demo.service.InboundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inbound")
@CrossOrigin(origins = "*")
public class InboundController {

    @Autowired
    private InboundOrderService inboundOrderService;

    @GetMapping
    public List<InboundOrder> list() {
        return inboundOrderService.findAll();
    }

    @PostMapping
    public InboundOrder create(@RequestBody InboundOrder order) {
        return inboundOrderService.save(order);
    }
}
