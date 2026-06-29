package com.demo.service;

import com.demo.model.GoodsPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionEngineTest {

    private PositionEngine positionEngine;

    @BeforeEach
    void setUp() {
        positionEngine = new PositionEngine();
    }

    @Test
    void testKalmanFilter_ShouldSmoothPosition() {
        String goodsId = "TEST-002";

        for (int i = 0; i < 10; i++) {
            double realX = 30 + i * 2;
            double realY = 20 + i * 1.5;

            double noise1 = (Math.random() - 0.5) * 0.6;
            double noise2 = (Math.random() - 0.5) * 0.6;
            double noise3 = (Math.random() - 0.5) * 0.6;

            double d1 = Math.sqrt(realX * realX + realY * realY) + noise1;
            double d2 = Math.sqrt((realX - 100) * (realX - 100) + realY * realY) + noise2;
            double d3 = Math.sqrt(realX * realX + (realY - 60) * (realY - 60)) + noise3;

            positionEngine.updatePosition(goodsId, d1, d2, d3);
        }

        Map<String, GoodsPosition> positions = positionEngine.getAllPositions();
        assertTrue(positions.containsKey(goodsId));

        GoodsPosition pos = positions.get(goodsId);
        assertTrue(pos.getX() > 40 && pos.getX() < 60);
        assertTrue(pos.getY() > 28 && pos.getY() < 42);
    }
}