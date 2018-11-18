package com.hupan.iot.homegateway.controller;

import com.hupan.iot.homegateway.entity.LightState;
import com.hupan.iot.homegateway.service.NightLightService;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;

@Slf4j
@Component
public class GpioListener {
    private final int HARDWARE_FILTER_MS = 1000;  // 硬件消抖时间
    private final int LIGHT_ON_TIMES = 90;  // 灯点亮持续时间，单位：秒


    private GpioController ioController;
    private GpioPinDigitalInput ioLightSensor;  // 光敏电阻输入
    private long lastActionTime = 0;  // 上次传感器事件时间，用于消抖

    @Value("${homegateway.hardware.debug-mode}")
    private boolean debugMode;  // 测试模式下不关联实际硬件，保证在非树莓派环境上可运行

    @Autowired
    private NightLightService nightLightService;

    @PostConstruct
    public void init(){
        if(debugMode){
            return;
        }
        ioController = GpioFactory.getInstance();
        ioLightSensor = ioController.provisionDigitalInputPin(RaspiPin.GPIO_01, "lightSensor");
        ioLightSensor.addListener(new GpioPinListenerDigital(){
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                boolean toDark = (event.getState() == PinState.HIGH);
                onLightSenorState(toDark);
            }
        });
        log.info("listen gpio pin");
    }

    private void onLightSenorState(boolean toDark){
        // 消抖
        long nowTime = System.currentTimeMillis();
        if((nowTime - lastActionTime) < HARDWARE_FILTER_MS){
            return;
        }
        lastActionTime = nowTime;

        // 是否在触发时间段内
        if(!isRightTime()){
            return;
        }

        // 点亮一小会儿
        if(toDark){
            log.info("light on");
            nightLightService.changeState(LightState.LIGHT_ON_A_WHILE, LIGHT_ON_TIMES);
        }
    }

    private boolean isRightTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if((hours >= 21)||((hours >= 0)&&(hours <= 2))){
            return true;
        } else {
            return false;
        }
    }
}
