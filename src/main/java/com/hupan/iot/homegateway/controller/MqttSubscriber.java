package com.hupan.iot.homegateway.controller;

import com.google.gson.JsonObject;
import com.hupan.iot.homegateway.constant.MqttConstant;
import com.hupan.iot.homegateway.entity.LightState;
import com.hupan.iot.homegateway.service.NightLightService;
import com.hupan.iot.homegateway.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttSubscriber {

    private final String STATE_KEY = "state";
    private final String STATE_ON = "on";
    private final String STATE_OFF = "off";

    @Autowired
    private NightLightService nightLightService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        String payload = message.getPayload().toString();
        log.info("mqtt rec, topic:{}, payload:{}", topic, payload);

        if(topic.equals(MqttConstant.TOPIC_SWITCH1)){
            processSwitch1(payload);
        }
    }

    private void processSwitch1(String payload){
        try {
            JsonObject jsonObject = GsonUtil.getPARSER().parse(payload).getAsJsonObject();
            if(!jsonObject.has(STATE_KEY)){
                return;
            }
            String value = jsonObject.get(STATE_KEY).getAsString();
            if(STATE_ON.equals(value)){
                nightLightService.changeState(LightState.LIGHT_ON, 0);
            } else if (STATE_OFF.equals(value)){
                nightLightService.changeState(LightState.LIGHT_OFF, 0);
            }
        } catch (Exception e){
            log.error("process switch1 exception: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }
}
