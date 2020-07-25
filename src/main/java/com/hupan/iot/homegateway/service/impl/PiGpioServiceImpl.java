package com.hupan.iot.homegateway.service.impl;

import com.hupan.iot.homegateway.service.PiGpioService;
import com.hupan.iot.homegateway.service.listener.LightSensorListener;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@ConditionalOnProperty(name = "homegateway.hardware", havingValue = "true")
public class PiGpioServiceImpl implements PiGpioService {

    private final int HARDWARE_FILTER_MS = 1000;    // 硬件消抖时间

    private GpioController gpioController = GpioFactory.getInstance();
    private GpioPinDigitalInput sensorInput = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01);      // 光敏电阻输入
    private GpioPinDigitalOutput littleOutput = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02);

    private long lastActionTime = 0;                // 上次传感器事件时间，用于消抖
    private LightSensorListener listener;

    @PostConstruct
    public void init() {
        gpioController = GpioFactory.getInstance();
        sensorInput = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01);      // 光敏电阻输入
        littleOutput = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        sensorInput.addListener((GpioPinListenerDigital) this::handleSensorEvent);
        log.info("listen gpio pin");
    }

    @Override
    public void addLightSensorListener(LightSensorListener listener) {
        this.listener = listener;
    }

    @Override
    public void littleOn() {
        littleOutput.high();
    }

    @Override
    public void littleOff() {
        littleOutput.low();
    }

    private void handleSensorEvent(GpioPinDigitalStateChangeEvent event) {

        // 消抖
        long nowTime = System.currentTimeMillis();
        if ((nowTime - lastActionTime) < HARDWARE_FILTER_MS) {
            return;
        }
        lastActionTime = nowTime;

        // 点亮一小会儿
        if ((event.getState() == PinState.HIGH) && (listener != null)) {
            listener.toDark();
        }
    }
}
