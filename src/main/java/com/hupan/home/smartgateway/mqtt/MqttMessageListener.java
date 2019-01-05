package com.hupan.home.smartgateway.mqtt;

import com.hupan.home.smartgateway.constant.MqttConstant;
import com.hupan.home.smartgateway.entity.LightState;
import com.hupan.home.smartgateway.service.NightLightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttMessageListener {
    @Autowired
    private NightLightService nightLightService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        String payload = message.getPayload().toString();
        log.info("mqtt rec, topic:{}, payload:{}", topic, payload);
        if(topic.equals(MqttConstant.TOPIC_SWITCH1)){
            if(payload.equals("{\"state\":\"on\"}")){
                nightLightService.changeLightState(LightState.LIGHT_ON, 0);
            } else if(payload.equals("{\"state\":\"off\"}")){
                nightLightService.changeLightState(LightState.LIGHT_OFF, 0);
            }
        }
    }
}
