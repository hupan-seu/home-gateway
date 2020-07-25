package com.hupan.iot.homegateway.service.impl;

import com.hupan.iot.homegateway.service.PiGpioService;
import com.hupan.iot.homegateway.service.listener.LightSensorListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "homegateway.hardware", havingValue = "false", matchIfMissing = true)
public class PiGpioServiceDebugImpl implements PiGpioService {

    @Override
    public void addLightSensorListener(LightSensorListener listener) {
    }

    @Override
    public void littleOn() {
    }

    @Override
    public void littleOff() {
    }
}
