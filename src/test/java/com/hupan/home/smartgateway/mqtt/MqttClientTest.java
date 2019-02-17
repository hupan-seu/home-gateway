package com.hupan.home.smartgateway.mqtt;

import com.hupan.home.smartgateway.SmartGatewayApplication;
import com.hupan.home.smartgateway.entity.LightState;
import com.hupan.home.smartgateway.service.NightLightService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SmartGatewayApplication.class)
public class MqttClientTest {

    @Autowired
    NightLightService nightLightService;

    @Test
    public void testMqttPublish(){
        System.out.println("ahahah");
        nightLightService.changeLightState(LightState.LIGHT_ON_A_WHILE, 60);
        //mqttPublishClient.sendToMqtt("hahah", 1, "hshshshs");
    }
}
