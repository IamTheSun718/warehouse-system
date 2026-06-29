package com.demo.controller;

import com.demo.model.GoodsPosition;
import com.demo.service.PositionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/api/rtls")
@CrossOrigin(origins = "*")
public class PositionController {

    @Autowired
    private PositionEngine positionEngine;

    @GetMapping("/positions")
    public Collection<GoodsPosition> getPositions() {
        return positionEngine.getAllPositions().values();
    }
}
