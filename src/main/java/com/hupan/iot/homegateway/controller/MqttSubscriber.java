package com.hupan.iot.homegateway.controller;

import com.hupan.iot.homegateway.constant.MqttConstant;
import com.hupan.iot.homegateway.entity.LightState;
import com.hupan.iot.homegateway.service.NightLightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttSubscriber {

    @Autowired
    private NightLightService nightLightService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        String payload = message.getPayload().toString();
        log.info("mqtt rec, topic:{}, payload:{}", topic, payload);
        if(topic.equals(MqttConstant.TOPIC_SWITCH1)){
            if(payload.equals("{\"state\":\"on\"}")){
                nightLightService.changeState(LightState.LIGHT_ON, 0);
            } else if(payload.equals("{\"state\":\"off\"}")){
                nightLightService.changeState(LightState.LIGHT_OFF, 0);
            }
        }
    }
}
