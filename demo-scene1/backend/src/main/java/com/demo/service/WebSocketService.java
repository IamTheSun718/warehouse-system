package com.demo.service;

import com.demo.handler.PositionWebSocketHandler;
import com.demo.model.GoodsPosition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class WebSocketService {
    @Autowired
    private PositionWebSocketHandler webSocketHandler;

    @Autowired
    private PositionEngine positionEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000)
    public void pushPositions() {
        try {
            Collection<GoodsPosition> positions = positionEngine.getAllPositions().values();
            String json = objectMapper.writeValueAsString(positions);
            webSocketHandler.broadcast(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}