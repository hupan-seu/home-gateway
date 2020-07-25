package com.hupan.iot.homegateway.mqtt;

import com.hupan.iot.homegateway.StartApplication;
import com.hupan.iot.homegateway.service.NightLightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StartApplication.class)
public class MqttClientTest {

    @Autowired
    private NightLightService nightLightService;

    @Test
    public void testMqttPublish() {
        System.out.println("ahahah");
//        nightLightService.changeLightState(LightState.LIGHT_ON_A_WHILE, 60);
        //mqttPublishClient.sendToMqtt("hahah", 1, "hshshshs");
    }
}
