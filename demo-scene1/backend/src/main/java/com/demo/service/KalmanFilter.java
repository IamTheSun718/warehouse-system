package com.demo.service;

public class KalmanFilter {
    private double Q = 0.1;
    private double R = 0.3;
    private double P = 1;
    private double K;
    private double x;

    public double filter(double measurement) {
        double x_pred = x;
        double P_pred = P + Q;
        K = P_pred / (P_pred + R);
        x = x_pred + K * (measurement - x_pred);
        P = (1 - K) * P_pred;
        return x;
    }
}
