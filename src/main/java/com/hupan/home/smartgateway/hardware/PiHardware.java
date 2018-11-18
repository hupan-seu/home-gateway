package com.hupan.home.smartgateway.hardware;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class PiHardware {

    private boolean debugMode;
    private GpioController ioController;
    private GpioPinDigitalInput ioLightSensor;  // 光敏电阻输入

    public PiHardware(boolean debugMode){
        this.debugMode = debugMode;

        if(!debugMode){
            ioController = GpioFactory.getInstance();
            ioLightSensor = ioController.provisionDigitalInputPin(RaspiPin.GPIO_01, "lightSensor");
        }
    }

    public void addKeyListener(GpioPinListenerDigital listener){
        if(debugMode){
            return;
        }
        ioLightSensor.addListener(listener);
    }
}
