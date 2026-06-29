package com.demo.service;

import com.demo.model.GoodsPosition;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PositionEngine {
    private final Map<String, GoodsPosition> positionMap = new ConcurrentHashMap<>();
    private final Map<String, KalmanFilter> filterMap = new ConcurrentHashMap<>();

    private static final double[][] BASE_STATIONS = {{0, 0}, {100, 0}, {0, 60}};

    public void updatePosition(String goodsId, double d1, double d2, double d3) {
        double[] pos = trilateration(d1, d2, d3);
        KalmanFilter filterX = filterMap.computeIfAbsent(goodsId + "_x", k -> new KalmanFilter());
        KalmanFilter filterY = filterMap.computeIfAbsent(goodsId + "_y", k -> new KalmanFilter());
        double smoothX = filterX.filter(pos[0]);
        double smoothY = filterY.filter(pos[1]);

        GoodsPosition gp = positionMap.get(goodsId);
        if (gp == null) {
            gp = new GoodsPosition();
            gp.setGoodsId(goodsId);
            gp.setGoodsName("危化品-" + goodsId.substring(0, 4));
            gp.setStatus("正常");
            gp.setDangerLevel("普通");
            gp.setZ(1.5);
        }
        gp.setX(smoothX);
        gp.setY(smoothY);
        gp.setUpdateTime(java.time.LocalDateTime.now());
        positionMap.put(goodsId, gp);
    }

    private double[] trilateration(double d1, double d2, double d3) {
        double x1 = BASE_STATIONS[0][0], y1 = BASE_STATIONS[0][1];
        double x2 = BASE_STATIONS[1][0], y2 = BASE_STATIONS[1][1];
        double x3 = BASE_STATIONS[2][0], y3 = BASE_STATIONS[2][1];

        double A = 2 * (x2 - x1);
        double B = 2 * (y2 - y1);
        double C = d1*d1 - d2*d2 - x1*x1 + x2*x2 - y1*y1 + y2*y2;
        double D = 2 * (x3 - x2);
        double E = 2 * (y3 - y2);
        double F = d2*d2 - d3*d3 - x2*x2 + x3*x3 - y2*y2 + y3*y3;

        double x = (C*E - F*B) / (A*E - D*B);
        double y = (C*D - F*A) / (B*D - E*A);
        return new double[]{x, y};
    }

    public Map<String, GoodsPosition> getAllPositions() {
        return positionMap;
    }
}