package com.hupan.iot.homegateway.service;

import com.hupan.iot.homegateway.service.listener.LightSensorListener;

public interface PiGpioService {

    // 添加光敏电阻监听器
    void addLightSensorListener(LightSensorListener listener);

    // 小灯控制
    void littleOn();

    void littleOff();
}
