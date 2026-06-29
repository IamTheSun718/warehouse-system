package com.demo.model;

import lombok.Data;

@Data
public class SensorData {
    private String sensorId;
    private double temperature;
    private double humidity;
    private String location;
    private long timestamp;
}