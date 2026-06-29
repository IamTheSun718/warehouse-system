package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class DataSimulator {
    @Autowired
    private PositionEngine positionEngine;

    private final Random random = new Random();
    private final String[] goodsIds = {"A001", "A002", "A003", "B001", "B002", "C001"};

    @Scheduled(fixedRate = 2000)
    public void simulateUwbData() {
        for (String id : goodsIds) {
            double realX = 20 + Math.sin(System.currentTimeMillis() / 10000.0 + id.hashCode()) * 30;
            double realY = 15 + Math.cos(System.currentTimeMillis() / 15000.0 + id.hashCode()) * 20;

            double d1 = Math.sqrt(realX * realX + realY * realY);
            double d2 = Math.sqrt((realX - 100) * (realX - 100) + realY * realY);
            double d3 = Math.sqrt(realX * realX + (realY - 60) * (realY - 60));

            double noise1 = random.nextGaussian() * 0.3;
            double noise2 = random.nextGaussian() * 0.3;
            double noise3 = random.nextGaussian() * 0.3;

            positionEngine.updatePosition(id, d1 + noise1, d2 + noise2, d3 + noise3);
        }
    }
}
